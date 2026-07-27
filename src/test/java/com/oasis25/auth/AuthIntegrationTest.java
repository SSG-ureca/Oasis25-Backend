package com.oasis25.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oasis25.auth.dto.LoginRequest;
import com.oasis25.auth.dto.LogoutRequest;
import com.oasis25.auth.dto.RegisterRequest;
import com.oasis25.auth.dto.ReissueRequest;
import jakarta.servlet.http.Cookie;
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
class AuthIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void registerLoginReissueLogoutFlow() throws Exception {
                RegisterRequest register = new RegisterRequest();
                register.setEmail("test@example.com");
                register.setPassword("password123");
                register.setNickname("TestUser");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(register)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("test@example.com"))
                                .andExpect(jsonPath("$.role").value("ROLE_USER"));

                LoginRequest login = new LoginRequest();
                login.setEmail("test@example.com");
                login.setPassword("password123");

                MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login)))
                                .andExpect(status().isOk())
                                .andExpect(header().exists("Set-Cookie"))
                                .andExpect(jsonPath("$.expiresIn").exists())
                                .andReturn();

                Cookie[] loginCookies = loginResult.getResponse().getCookies();
                Cookie refreshTokenCookie = null;
                for (Cookie c : loginCookies) {
                        if ("refreshToken".equals(c.getName())) {
                                refreshTokenCookie = c;
                                break;
                        }
                }
                assertThat(refreshTokenCookie).isNotNull();

                MvcResult reissueResult = mockMvc.perform(post("/api/auth/reissue")
                                .cookie(refreshTokenCookie))
                                .andExpect(status().isOk())
                                .andExpect(header().exists("Set-Cookie"))
                                .andExpect(jsonPath("$.expiresIn").exists())
                                .andReturn();

                Cookie[] reissueCookies = reissueResult.getResponse().getCookies();
                Cookie reissueRefreshCookie = null;
                for (Cookie c : reissueCookies) {
                        if ("refreshToken".equals(c.getName())) {
                                reissueRefreshCookie = c;
                                break;
                        }
                }
                assertThat(reissueRefreshCookie).isNotNull();
                assertThat(reissueRefreshCookie.getValue()).isNotEqualTo(refreshTokenCookie.getValue());

                MvcResult logoutResult = mockMvc.perform(post("/api/auth/logout")
                                .cookie(reissueRefreshCookie))
                                .andExpect(status().isOk())
                                .andReturn();

                for (Cookie c : logoutResult.getResponse().getCookies()) {
                        if ("accessToken".equals(c.getName()) || "refreshToken".equals(c.getName())) {
                                assertThat(c.getMaxAge()).isEqualTo(0);
                        }
                }
        }

        @Test
        void duplicateEmailReturnsConflict() throws Exception {
                RegisterRequest request = new RegisterRequest();
                request.setEmail("dup@example.com");
                request.setPassword("password123");
                request.setNickname("Dup");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict());
        }

        @Test
        void invalidLoginReturnsUnauthorized() throws Exception {
                LoginRequest login = new LoginRequest();
                login.setEmail("none@example.com");
                login.setPassword("wrong");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login)))
                                .andExpect(status().isUnauthorized());
        }
}
