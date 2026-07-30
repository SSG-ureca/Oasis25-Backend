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

  // 2년치 더미 데이터
  // - 감정: 전반적으로 긍정적이면서도 주기+노이즈가 섞인 복잡한 흐름
  // - 최근 30일 집중도: 우상향하도록 일자별 집중 시간/횟수 증가
  // - 날씨별 평균 집중 시간: 날씨에 따라 focus_min/elapsed_focus 차등 적용
  private static final String SEED_SQL_TEMPLATE = """
      DO $$
      DECLARE
        start_date    DATE := CURRENT_DATE - INTERVAL '2 years';
        end_date      DATE := CURRENT_DATE - INTERVAL '1 day';
        total_days    INT := end_date - start_date + 1;
        u_id          BIGINT;
        cat_ids       BIGINT[];
        cat_id        BIGINT;
        preset_id     BIGINT;
        d             DATE;
        day_index     INT;
        relative_30   INT;
        is_last_30    BOOLEAN;
        i             INT;
        j             INT;
        n_logs        INT;
        daily_base_focus DOUBLE PRECISION;
        log_created   TIMESTAMP;
        focus_min     INT;
        break_min     INT;
        elapsed_focus INT;
        elapsed_break INT;
        completed     BOOLEAN;
        start_hour    INT;
        start_min     INT;
        w_rand        DOUBLE PRECISION;
        w_cum         DOUBLE PRECISION;
        weather_idx   INT;
        weather       TEXT;
        focus_factor  DOUBLE PRECISION;
        complete_prob DOUBLE PRECISION;
        temperature   DOUBLE PRECISION;
        emotion_raw   DOUBLE PRECISION;
        emotion       INT;

        weather_types  TEXT[] := ARRAY['CLEAR','CLOUDY','RAIN','SNOW','THUNDERSTORM','FOG','ETC'];
        weather_weights DOUBLE PRECISION[] := ARRAY[0.30, 0.20, 0.15, 0.05, 0.05, 0.15, 0.10]::DOUBLE PRECISION[];
        weather_factors DOUBLE PRECISION[] := ARRAY[1.20, 1.00, 0.75, 0.70, 0.60, 0.90, 0.90]::DOUBLE PRECISION[];
        complete_probs  DOUBLE PRECISION[] := ARRAY[0.95, 0.90, 0.75, 0.70, 0.50, 0.85, 0.85]::DOUBLE PRECISION[];
        temp_bases      DOUBLE PRECISION[] := ARRAY[20.0, 17.0, 13.0, -2.0, 18.0, 9.0, 15.0]::DOUBLE PRECISION[];
        temp_ranges     DOUBLE PRECISION[] := ARRAY[15.0, 13.0, 10.0, 7.0, 10.0, 8.0, 15.0]::DOUBLE PRECISION[];
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

        FOR d IN SELECT generate_series(start_date::TIMESTAMP, end_date::TIMESTAMP, INTERVAL '1 day')::DATE LOOP
          day_index := d - start_date;
          is_last_30 := day_index >= total_days - 30;
          relative_30 := day_index - (total_days - 30);

          -- 감정: 전반적으로 상승(긍정) + 주기성 + 노이즈
          emotion_raw := 3.0
                        + (day_index::DOUBLE PRECISION / total_days) * 1.4
                        + 0.5 * sin(2 * pi() * day_index / 7.0)
                        + 0.3 * sin(2 * pi() * day_index / 30.0)
                        + (random() - 0.5) * 0.8;
          emotion := GREATEST(1, LEAST(5, round(emotion_raw)::INT));

          -- 최근 30일은 집중 시간/횟수를 단계적으로 증가
          IF is_last_30 THEN
            n_logs := 1 + floor(relative_30 / 6.0)::INT + floor(random() * 2)::INT;
            daily_base_focus := 20.0 + relative_30::DOUBLE PRECISION;
          ELSE
            n_logs := floor(random() * 6)::INT;
            daily_base_focus := 15.0
                             + (day_index::DOUBLE PRECISION / total_days) * 15.0
                             + (random() - 0.5) * 8.0;
          END IF;

          -- 일기는 감정 흐름을 반영해 70% 확률로 작성
          IF random() < 0.7 THEN
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

          FOR i IN 1..n_logs LOOP
            -- 가중치 기반 날씨 선택
            w_rand := random();
            w_cum := 0.0;
            weather_idx := 1;
            FOR j IN 1..array_length(weather_types, 1) LOOP
              w_cum := w_cum + weather_weights[j];
              IF w_rand <= w_cum THEN
                weather_idx := j;
                EXIT;
              END IF;
            END LOOP;
            weather := weather_types[weather_idx];
            focus_factor := weather_factors[weather_idx];
            complete_prob := complete_probs[weather_idx];
            temperature := temp_bases[weather_idx] + random() * temp_ranges[weather_idx];

            focus_min := GREATEST(15, LEAST(60, floor(daily_base_focus * focus_factor)::INT));
            break_min := GREATEST(5, LEAST(25, 5 + floor(random() * 21)::INT));
            start_hour := 7 + floor(random() * 16)::INT;
            start_min := floor(random() * 60)::INT;
            log_created := d + make_time(start_hour, start_min, 0);
            completed := random() < complete_prob;

            IF completed THEN
              elapsed_focus := floor((0.55 + random() * 0.40) * focus_min * 60)::INT;
              elapsed_break := floor((0.40 + random() * 0.40) * break_min * 60)::INT;
            ELSE
              elapsed_focus := floor(random() * focus_min * 60 * 0.3)::INT;
              elapsed_break := 0;
            END IF;

            cat_id := cat_ids[1 + floor(random() * array_length(cat_ids, 1))::INT];

            INSERT INTO pomodoro_log
              (user_id, category_id, focus_minutes, break_minutes, completed,
               end_time, weather_condition, temperature,
               elapsed_focus_seconds, elapsed_break_seconds, created_at)
            VALUES
              (u_id, cat_id, focus_min, break_min, completed,
               CASE WHEN completed THEN log_created + (elapsed_focus || ' seconds')::INTERVAL ELSE NULL END,
               weather,
               temperature,
               elapsed_focus, elapsed_break, log_created);
          END LOOP;

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

        RAISE NOTICE 'Rich dummy seed completed for user_id %', u_id;
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
      log.info("Rich dummy data overlay executed for {}", seedEmail);
    } catch (SQLException e) {
      log.error("Failed to seed dummy data", e);
      throw e;
    }
  }
}
