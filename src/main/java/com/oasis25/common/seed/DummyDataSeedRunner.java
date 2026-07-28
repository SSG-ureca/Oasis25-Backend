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

  // 2년치 불규칙한 더미 데이터를 생성합니다.
  // pomodoro_log, water_caffeine_log, feedbacks는 중복 삽입 가능(덧씌우기).
  private static final String SEED_SQL_TEMPLATE = """
      DO $$
      DECLARE
        u_id          BIGINT;
        cat_ids       BIGINT[];
        cat_id        BIGINT;
        preset_id     BIGINT;
        d             DATE;
        i             INT;
        n_logs        INT;
        log_created   TIMESTAMP;
        focus_min     INT;
        break_min     INT;
        elapsed_focus INT;
        elapsed_break INT;
        completed     BOOLEAN;
        start_hour    INT;
        start_min     INT;
        weather       TEXT;
        emotion       INT;
      BEGIN
        SELECT id INTO u_id FROM users WHERE email = '__SEED_EMAIL__';
        IF NOT FOUND THEN
          RAISE NOTICE 'User not found';
          RETURN;
        END IF;

        INSERT INTO focus_categories (user_id, name, color, created_at)
        VALUES
          (u_id, '업무', '#FF5733', CURRENT_TIMESTAMP),
          (u_id, '공부', '#3366FF', CURRENT_TIMESTAMP),
          (u_id, '운동', '#33CC33', CURRENT_TIMESTAMP),
          (u_id, '독서', '#FF9900', CURRENT_TIMESTAMP)
        ON CONFLICT (user_id, name) DO NOTHING;

        SELECT array_agg(id) INTO cat_ids FROM focus_categories WHERE user_id = u_id;

        IF cat_ids IS NULL THEN
          RAISE NOTICE 'No categories available';
          RETURN;
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
          -- 하루 0~5개 뽀모도로
          n_logs := floor(random() * 6)::INT;

          FOR i IN 1..n_logs LOOP
            focus_min := 15 + floor(random() * 46)::INT;
            break_min := 5 + floor(random() * 16)::INT;
            start_hour := 7 + floor(random() * 16)::INT;
            start_min := floor(random() * 60)::INT;
            log_created := d + make_time(start_hour, start_min, 0);
            completed := random() < 0.85;

            IF completed THEN
              elapsed_focus := floor(random() * (focus_min * 60 + 1))::INT;
              elapsed_break := floor(random() * (break_min * 60 + 1))::INT;
            ELSE
              elapsed_focus := 0;
              elapsed_break := 0;
            END IF;

            cat_id := cat_ids[1 + floor(random() * array_length(cat_ids, 1))::INT];
            weather := (ARRAY['CLEAR','CLOUDY','RAIN','SNOW','THUNDERSTORM','FOG','ETC'])
                       [1 + floor(random() * 7)::INT];

            INSERT INTO pomodoro_log
              (user_id, category_id, focus_minutes, break_minutes, completed,
               end_time, weather_condition, temperature,
               elapsed_focus_seconds, elapsed_break_seconds, created_at)
            VALUES
              (u_id, cat_id, focus_min, break_min, completed,
               CASE WHEN completed THEN log_created + (elapsed_focus || ' seconds')::INTERVAL ELSE NULL END,
               weather,
               5 + random() * 30,
               elapsed_focus, elapsed_break, log_created);
          END LOOP;

          -- 70% 확률로 일기 작성
          IF random() < 0.7 THEN
            emotion := 1 + floor(random() * 5)::INT;
            INSERT INTO diary (user_id, diary_date, content, emotion_score, created_at, updated_at)
            VALUES (
              u_id, d,
              '일기 ' || d::TEXT || ' - 더미',
              emotion,
              d + make_time(23, floor(random() * 60)::INT, 0),
              d + make_time(23, floor(random() * 60)::INT, 0)
            )
            ON CONFLICT (user_id, diary_date) DO NOTHING;
          END IF;

          -- 0~3건 물 기록
          FOR i IN 1..floor(random() * 4)::INT LOOP
            INSERT INTO water_caffeine_log (user_id, log_type, amount, created_at)
            VALUES (
              u_id, 'WATER',
              100 + floor(random() * 401)::INT,
              d + make_time(8 + floor(random() * 14)::INT, floor(random() * 60)::INT, 0)
            );
          END LOOP;

          -- 0~2건 카페인 기록
          FOR i IN 1..floor(random() * 3)::INT LOOP
            INSERT INTO water_caffeine_log (user_id, log_type, amount, created_at)
            VALUES (
              u_id, 'CAFFEINE',
              30 + floor(random() * 121)::INT,
              d + make_time(8 + floor(random() * 14)::INT, floor(random() * 60)::INT, 0)
            );
          END LOOP;
        END LOOP;

        -- 5~15건 랜덤 피드백
        FOR i IN 1..(5 + floor(random() * 11)::INT) LOOP
          INSERT INTO feedbacks (user_id, is_good, content, created_at)
          VALUES (
            u_id,
            random() < 0.5,
            '더미 피드백 ' || i::TEXT,
            CURRENT_TIMESTAMP - (floor(random() * 60) || ' days')::INTERVAL
          );
        END LOOP;

        RAISE NOTICE 'Irregular overlay seed completed for user_id %', u_id;
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
      log.info("Irregular dummy data overlay executed for {}", seedEmail);
    } catch (SQLException e) {
      log.error("Failed to seed dummy data", e);
      throw e;
    }
  }
}
