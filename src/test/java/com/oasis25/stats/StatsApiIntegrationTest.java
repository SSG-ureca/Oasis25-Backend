package com.oasis25.stats;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oasis25.auth.dto.LoginRequest;
import com.oasis25.auth.dto.RegisterRequest;
import com.oasis25.diary.dto.DiaryCreateRequest;
import com.oasis25.pomodoro.dto.FocusCategoryCreateRequest;
import com.oasis25.pomodoro.dto.FocusCategoryResponse;
import com.oasis25.pomodoro.dto.PomodoroLogCreateRequest;
import com.oasis25.pomodoro.dto.PomodoroLogResponse;
import com.oasis25.stats.dto.EmotionStatsResponse;
import com.oasis25.stats.dto.TrendStatsResponse;
import com.oasis25.stats.dto.WeatherFocusStatsResponse;
import com.oasis25.stats.dto.WeeklyLogResponse;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StatsApiIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        private String accessToken;

        @BeforeEach
        void setUp() throws Exception {
                RegisterRequest register = new RegisterRequest();
                register.setEmail("stats@example.com");
                register.setPassword("password123");
                register.setNickname("Stats");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(register)))
                                .andExpect(status().isOk());

                LoginRequest login = new LoginRequest();
                login.setEmail("stats@example.com");
                login.setPassword("password123");

                MvcResult result = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login)))
                                .andExpect(status().isOk())
                                .andReturn();

                JsonNode node = objectMapper.readTree(
                                new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8));
                accessToken = node.get("accessToken").asText();
        }

        @Test
        void protectedStatsEndpointsRequireAuthentication() throws Exception {
                mockMvc.perform(get("/api/stats/weekly-logs"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
                mockMvc.perform(get("/api/stats/weather"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
                mockMvc.perform(get("/api/stats/emotions"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
                mockMvc.perform(get("/api/stats/trend"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
        }

        @Test
        void statsFlow() throws Exception {
                FocusCategoryCreateRequest cat = new FocusCategoryCreateRequest();
                cat.setName("Stats");
                cat.setColor("#000000");

                MvcResult catResult = mockMvc.perform(post("/api/pomodoro/categories")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cat)))
                                .andExpect(status().isOk())
                                .andReturn();
                FocusCategoryResponse category = objectMapper.readValue(
                                catResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                FocusCategoryResponse.class);

                createAndCompletePomodoro(category.getId(), 25, "맑음");
                createAndCompletePomodoro(category.getId(), 45, "비");

                LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

                DiaryCreateRequest diary = new DiaryCreateRequest();
                diary.setDiaryDate(today);
                diary.setContent("Good day");
                diary.setEmotionScore(4);

                mockMvc.perform(post("/api/diaries")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(diary)))
                                .andExpect(status().isOk());

                MvcResult weeklyResult = mockMvc.perform(get("/api/stats/weekly-logs")
                                .header("Authorization", "Bearer " + accessToken))
                                .andExpect(status().isOk())
                                .andReturn();
                List<WeeklyLogResponse> weekly = objectMapper.readValue(
                                weeklyResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                new TypeReference<>() {
                                });
                assertThat(weekly).hasSize(2);
                assertThat(weekly).allMatch(r -> r.getCreatedAt() != null && r.getFocusMinutes() != null);

                MvcResult weatherResult = mockMvc.perform(get("/api/stats/weather")
                                .header("Authorization", "Bearer " + accessToken))
                                .andExpect(status().isOk())
                                .andReturn();
                List<WeatherFocusStatsResponse> weather = objectMapper.readValue(
                                weatherResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                new TypeReference<>() {
                                });
                assertThat(weather).hasSize(2);
                Map<String, Double> byWeather = weather.stream()
                                .collect(Collectors.toMap(
                                                WeatherFocusStatsResponse::getWeatherCondition,
                                                WeatherFocusStatsResponse::getAvgFocusMinutes));
                assertThat(byWeather).containsEntry("맑음", 25.0);
                assertThat(byWeather).containsEntry("비", 45.0);

                MvcResult emotionResult = mockMvc.perform(get("/api/stats/emotions")
                                .header("Authorization", "Bearer " + accessToken))
                                .andExpect(status().isOk())
                                .andReturn();
                List<EmotionStatsResponse> emotions = objectMapper.readValue(
                                emotionResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                new TypeReference<>() {
                                });
                assertThat(emotions).hasSize(1);
                assertThat(emotions.get(0).getDate()).isEqualTo(today);
                assertThat(emotions.get(0).getEmotionScore()).isEqualTo(4);

                MvcResult trendResult = mockMvc.perform(get("/api/stats/trend")
                                .header("Authorization", "Bearer " + accessToken))
                                .andExpect(status().isOk())
                                .andReturn();
                List<TrendStatsResponse> trends = objectMapper.readValue(
                                trendResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                new TypeReference<>() {
                                });
                assertThat(trends).hasSize(30);
                TrendStatsResponse todayTrend = trends.stream()
                                .filter(t -> t.getDate().equals(today))
                                .findFirst()
                                .orElseThrow();
                assertThat(todayTrend.getTotalFocusMinutes()).isEqualTo(70);
        }

        private PomodoroLogResponse createAndCompletePomodoro(
                        long categoryId, int focusMinutes, String weather) throws Exception {
                PomodoroLogCreateRequest req = new PomodoroLogCreateRequest();
                req.setCategoryId(categoryId);
                req.setFocusMinutes(focusMinutes);
                req.setBreakMinutes(5);
                req.setWeatherCondition(weather);
                req.setTemperature(20.0);

                MvcResult createResult = mockMvc.perform(post("/api/pomodoro")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andReturn();
                PomodoroLogResponse response = objectMapper.readValue(
                                createResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                PomodoroLogResponse.class);

                MvcResult completeResult = mockMvc.perform(patch("/api/pomodoro/{id}/complete", response.getId())
                                .header("Authorization", "Bearer " + accessToken))
                                .andExpect(status().isOk())
                                .andReturn();
                return objectMapper.readValue(
                                completeResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                                PomodoroLogResponse.class);
        }
}
