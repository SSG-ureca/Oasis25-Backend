package com.oasis25;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oasis25.auth.dto.LoginRequest;
import com.oasis25.auth.dto.RegisterRequest;
import com.oasis25.diary.dto.DiaryCreateRequest;
import com.oasis25.diary.dto.DiaryResponse;
import com.oasis25.feedback.dto.FeedbackCreateRequest;
import com.oasis25.feedback.dto.FeedbackResponse;
import com.oasis25.pomodoro.dto.FocusCategoryCreateRequest;
import com.oasis25.pomodoro.dto.FocusCategoryResponse;
import com.oasis25.pomodoro.dto.PomodoroLogCreateRequest;
import com.oasis25.pomodoro.dto.PomodoroLogResponse;
import com.oasis25.water.dto.WaterCaffeineLogCreateRequest;
import com.oasis25.water.dto.WaterCaffeineLogResponse;
import com.oasis25.water.entity.WaterCaffeineLogType;
import java.time.LocalDate;
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
class DomainIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setEmail("domain@example.com");
        register.setPassword("password123");
        register.setNickname("Domain");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest();
        login.setEmail("domain@example.com");
        login.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        accessToken = node.get("accessToken").asText();
    }

    @Test
    void waterCaffeineFlow() throws Exception {
        WaterCaffeineLogCreateRequest request = new WaterCaffeineLogCreateRequest();
        request.setLogType(WaterCaffeineLogType.WATER);
        request.setAmount(250);

        MvcResult createResult = mockMvc.perform(post("/api/water-caffeine")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        WaterCaffeineLogResponse response = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), WaterCaffeineLogResponse.class);
        assertThat(response.getLogType()).isEqualTo(WaterCaffeineLogType.WATER);
        assertThat(response.getAmount()).isEqualTo(250);

        MvcResult summaryResult = mockMvc.perform(get("/api/water-caffeine/summary")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("date", LocalDate.now().toString())
                        .param("type", WaterCaffeineLogType.WATER.name()))
                .andExpect(status().isOk())
                .andReturn();

        Integer total = Integer.parseInt(summaryResult.getResponse().getContentAsString());
        assertThat(total).isEqualTo(250);

        mockMvc.perform(delete("/api/water-caffeine/{id}", response.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void pomodoroFlow() throws Exception {
        FocusCategoryCreateRequest categoryRequest = new FocusCategoryCreateRequest();
        categoryRequest.setName("Study");
        categoryRequest.setColor("#FF0000");

        MvcResult categoryResult = mockMvc.perform(post("/api/pomodoro/categories")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())
                .andReturn();

        FocusCategoryResponse category = objectMapper.readValue(
                categoryResult.getResponse().getContentAsString(), FocusCategoryResponse.class);

        PomodoroLogCreateRequest pomodoroRequest = new PomodoroLogCreateRequest();
        pomodoroRequest.setCategoryId(category.getId());
        pomodoroRequest.setFocusMinutes(25);
        pomodoroRequest.setBreakMinutes(5);

        MvcResult pomodoroResult = mockMvc.perform(post("/api/pomodoro")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pomodoroRequest)))
                .andExpect(status().isOk())
                .andReturn();

        PomodoroLogResponse pomodoro = objectMapper.readValue(
                pomodoroResult.getResponse().getContentAsString(), PomodoroLogResponse.class);

        MvcResult completeResult = mockMvc.perform(patch("/api/pomodoro/{id}/complete", pomodoro.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        PomodoroLogResponse completed = objectMapper.readValue(
                completeResult.getResponse().getContentAsString(), PomodoroLogResponse.class);
        assertThat(completed.isCompleted()).isTrue();
        assertThat(completed.getEndTime()).isNotNull();
    }

    @Test
    void diaryFlow() throws Exception {
        DiaryCreateRequest request = new DiaryCreateRequest();
        request.setDiaryDate(LocalDate.now());
        request.setContent("Today was productive and I feel good.");

        MvcResult createResult = mockMvc.perform(post("/api/diaries")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        DiaryResponse diary = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), DiaryResponse.class);

        MvcResult getResult = mockMvc.perform(get("/api/diaries")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("date", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andReturn();

        DiaryResponse found = objectMapper.readValue(
                getResult.getResponse().getContentAsString(), DiaryResponse.class);
        assertThat(found.getId()).isEqualTo(diary.getId());

        MvcResult aiResult = mockMvc.perform(post("/api/diaries/{id}/ai-summary", diary.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        DiaryResponse summarized = objectMapper.readValue(
                aiResult.getResponse().getContentAsString(), DiaryResponse.class);
        assertThat(summarized.getAiSummary()).isNotNull();

        mockMvc.perform(delete("/api/diaries/{id}", diary.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void feedbackFlow() throws Exception {
        FeedbackCreateRequest request = new FeedbackCreateRequest();
        request.setIsGood(true);
        request.setContent("Great app!");

        MvcResult result = mockMvc.perform(post("/api/feedbacks")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        FeedbackResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), FeedbackResponse.class);
        assertThat(response.isGood()).isTrue();
        assertThat(response.getContent()).isEqualTo("Great app!");
    }
}
