package com.oasis25.common.seed;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyDataSeedRunner implements CommandLineRunner {

    private final Environment environment;
    private final DataSource dataSource;

    private static final String EMAIL_PLACEHOLDER = "__SEED_EMAIL__";

    private static final String SEED_SQL_TEMPLATE = """
DO $$
DECLARE
  u_id          BIGINT;
  cat_id        BIGINT;
  preset_id     BIGINT;
  d             DATE;
  i             INT;
  log_created   TIMESTAMP;
  focus_min     INT;
  break_min     INT;
  weather       TEXT;
  emotion       INT;
BEGIN
  SELECT id INTO u_id FROM users WHERE email = '__SEED_EMAIL__';
  IF NOT FOUND THEN
    RAISE NOTICE 'User not found, skipping seed';
    RETURN;
  END IF;

  IF EXISTS (SELECT 1 FROM pomodoro_log WHERE user_id = u_id) THEN
    RAISE NOTICE 'Pomodoro logs already exist for this user, skipping seed';
    RETURN;
  END IF;

  SELECT id INTO cat_id FROM focus_categories WHERE user_id = u_id AND name = '업무';
  IF cat_id IS NULL THEN
    INSERT INTO focus_categories (user_id, name, color, created_at)
    VALUES (u_id, '업무', '#FF5733', CURRENT_TIMESTAMP)
    RETURNING id INTO cat_id;
  END IF;

  SELECT id INTO preset_id FROM pomodoro_preset WHERE user_id = u_id AND name = '기본';
  IF preset_id IS NULL THEN
    INSERT INTO pomodoro_preset (user_id, name, focus_minutes, break_minutes, is_default, created_at)
    VALUES (u_id, '기본', 25, 5, true, CURRENT_TIMESTAMP)
    RETURNING id INTO preset_id;
  END IF;

  FOR d IN
    SELECT generate_series(
      CURRENT_DATE - INTERVAL '2 years',
      CURRENT_DATE - INTERVAL '1 day',
      INTERVAL '1 day'
    )::DATE
  LOOP
    FOR i IN 1..(1 + (EXTRACT(DOY FROM d)::INT % 3)) LOOP
      focus_min := 20 + (i * 5);
      break_min := 5 + ((i % 2) * 5);
      log_created := d + TIME '09:00:00' + (i * INTERVAL '2 hours');
      weather := (ARRAY['CLEAR','CLOUDY','RAIN','SNOW','THUNDERSTORM','FOG','ETC'])
                 [1 + ((EXTRACT(DOW FROM d)::INT + i - 1) % 7)];

      INSERT INTO pomodoro_log
        (user_id, category_id, focus_minutes, break_minutes, completed,
         end_time, weather_condition, temperature,
         elapsed_focus_seconds, elapsed_break_seconds, created_at)
      VALUES
        (u_id, cat_id, focus_min, break_min, true,
         log_created + (focus_min || ' minutes')::INTERVAL, weather,
         15.0 + (i * 3),
         focus_min * 60, break_min * 60, log_created);
    END LOOP;

    emotion := 1 + (EXTRACT(DOW FROM d)::INT % 5);
    INSERT INTO diary (user_id, diary_date, content, emotion_score, created_at, updated_at)
    VALUES (u_id, d, '2년 중 ' || d::TEXT || ' 일기 내용입니다.', emotion, d + TIME '23:00:00', d + TIME '23:00:00')
    ON CONFLICT (user_id, diary_date) DO NOTHING;

    INSERT INTO water_caffeine_log (user_id, log_type, amount, created_at)
    VALUES (u_id, 'WATER', 200 + ((EXTRACT(DOW FROM d)::INT % 3) * 50), d + TIME '10:00:00');

    INSERT INTO water_caffeine_log (user_id, log_type, amount, created_at)
    VALUES (u_id, 'CAFFEINE', 50 + ((EXTRACT(DOW FROM d)::INT % 2) * 50), d + TIME '14:00:00');
  END LOOP;

  FOR i IN 1..10 LOOP
    INSERT INTO feedbacks (user_id, is_good, content, created_at)
    VALUES (u_id, (i % 2 = 0), '더미 피드백 ' || i::TEXT, CURRENT_TIMESTAMP - (i || ' days')::INTERVAL);
  END LOOP;

  RAISE NOTICE 'Seed completed for user_id %', u_id;
END $$;
""";

    @Override
    public void run(String... args) throws Exception {
        String seedEmail = environment.getProperty("SEED_EMAIL", "").trim();
        String seedToken = environment.getProperty("SEED_TOKEN", "").trim();

        if (seedEmail.isEmpty() || seedToken.isEmpty()) {
            log.info("SEED_EMAIL or SEED_TOKEN not set. Skipping dummy data seed.");
            return;
        }

        if (!seedEmail.contains("@") || seedEmail.contains("'")) {
            throw new IllegalArgumentException("Invalid SEED_EMAIL: " + seedEmail);
        }

        String sql = SEED_SQL_TEMPLATE.replace(EMAIL_PLACEHOLDER, seedEmail);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            log.info("Dummy data seed executed for {}", seedEmail);
        } catch (SQLException e) {
            log.error("Failed to seed dummy data", e);
            throw e;
        }
    }
}
