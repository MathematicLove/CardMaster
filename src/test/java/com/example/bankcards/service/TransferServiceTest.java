package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock CardRepository cardRepository;
    @InjectMocks TransferService transferService;

    Card from, to;

    @BeforeEach
    void setUp() {
        User owner = new User();
        ReflectionTestUtils.setField(owner, "id", 7L);

        from = new Card();
        to   = new Card();

        ReflectionTestUtils.setField(from, "id", 1L);
        ReflectionTestUtils.setField(to,   "id", 2L);
        ReflectionTestUtils.setField(from, "owner", owner);
        ReflectionTestUtils.setField(to,   "owner", owner);
        ReflectionTestUtils.setField(from, "balance", new BigDecimal("50.00"));
        ReflectionTestUtils.setField(to,   "balance", new BigDecimal("10.00"));
    }
}
