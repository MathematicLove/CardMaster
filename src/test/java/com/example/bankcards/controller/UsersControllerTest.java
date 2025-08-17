package com.example.bankcards.controller;

import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.bankcards.SecurityTestUtils.asUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @Test
    void list_users_unauthorized_401() throws Exception {
        mvc.perform(get("/api/users"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    void list_users_as_user_forbidden_403() throws Exception {
        mvc.perform(get("/api/users").with(asUser(10L)))
           .andExpect(status().isForbidden());
    }
}
