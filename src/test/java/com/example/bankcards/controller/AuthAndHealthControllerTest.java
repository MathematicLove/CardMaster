package com.example.bankcards.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false"
})
class AuthAndHealthControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void health_requires_auth_401() throws Exception {
        mvc.perform(get("/api/health"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    void login_without_body_now_returns_500_by_global_handler() throws Exception {
        mvc.perform(post("/api/auth/login"))
           .andExpect(status().isInternalServerError());
    }
}
