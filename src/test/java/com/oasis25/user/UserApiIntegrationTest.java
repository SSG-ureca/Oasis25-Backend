package com.oasis25.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oasis25.auth.dto.LoginRequest;
import com.oasis25.auth.dto.RegisterRequest;
import com.oasis25.user.dto.MyProfileResponse;
import com.oasis25.user.dto.PasswordChangeRequest;
import com.oasis25.user.dto.ProfileUpdateRequest;
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
class UserApiIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        private String accessToken;

        @BeforeEach
        void setUp() throws Exception {
                RegisterRequest register = new RegisterRequest();
                register.setEmail("user@example.com");
                register.setPassword("password123");
                register.setNickname("User");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(register)))
                                .andExpect(status().isOk());

                accessToken = login("user@example.com", "password123");
        }

        private String login(String email, String password) throws Exception {
                LoginRequest login = new LoginRequest();
                login.setEmail(email);
                login.setPassword(password);

                MvcResult result = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login)))
                                .andExpect(status().isOk())
                                .andReturn();
                return objectMapper.readTree(result.getResponse().getContentAsString())
                                .get("accessToken").asText();
        }

        @Test
        void profileAndPasswordFlow() throws Exception {
                MvcResult getResult = mockMvc.perform(get("/api/users/me")
                                .header("Authorization", "Bearer " + accessToken))
                                .andExpect(status().isOk())
                                .andReturn();
                MyProfileResponse profile = objectMapper.readValue(
                                getResult.getResponse().getContentAsString(), MyProfileResponse.class);
                assertThat(profile.getEmail()).isEqualTo("user@example.com");
                assertThat(profile.getNickname()).isEqualTo("User");
                assertThat(profile.getProfileImage()).isNull();

                ProfileUpdateRequest update = new ProfileUpdateRequest();
                update.setNickname("NewNick");
                MvcResult updateResult = mockMvc.perform(patch("/api/users/me")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(update)))
                                .andExpect(status().isOk())
                                .andReturn();
                MyProfileResponse updated = objectMapper.readValue(
                                updateResult.getResponse().getContentAsString(), MyProfileResponse.class);
                assertThat(updated.getNickname()).isEqualTo("NewNick");

                PasswordChangeRequest change = new PasswordChangeRequest();
                change.setCurrentPassword("password123");
                change.setNewPassword("newpassword123");
                mockMvc.perform(patch("/api/users/me/password")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(change)))
                                .andExpect(status().isOk());

                String newToken = login("user@example.com", "newpassword123");
                assertThat(newToken).isNotBlank();
        }

        @Test
        void passwordChangeWithWrongCurrentPasswordReturnsUnauthorized() throws Exception {
                PasswordChangeRequest change = new PasswordChangeRequest();
                change.setCurrentPassword("wrong");
                change.setNewPassword("newpassword123");
                mockMvc.perform(patch("/api/users/me/password")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(change)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
        }

        @Test
        void invalidPasswordChangeRequestReturnsBadRequest() throws Exception {
                PasswordChangeRequest change = new PasswordChangeRequest();
                change.setCurrentPassword("password123");
                change.setNewPassword("short");
                mockMvc.perform(patch("/api/users/me/password")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(change)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void profileEndpointsRequireAuthentication() throws Exception {
                mockMvc.perform(get("/api/users/me"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
                mockMvc.perform(patch("/api/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new ProfileUpdateRequest())))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
                mockMvc.perform(patch("/api/users/me/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new PasswordChangeRequest())))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
        }
}
