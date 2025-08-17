package com.example.bankcards.controller;

import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc
class CardsControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    CardService cardService;

    @Test
    void request_block_unauthorized_401() throws Exception {
        mvc.perform(post("/api/cards/{id}/request-block", 42L))
           .andExpect(status().isUnauthorized());
    }
}
