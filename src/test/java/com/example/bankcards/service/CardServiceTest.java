package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
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
class CardServiceTest {

    @Mock CardRepository cardRepository;
    @InjectMocks CardService cardService;

    Card cardOwnedBy7;
    Card cardOwnedBy8;

    @BeforeEach
    void init() {
        User u7 = new User();
        ReflectionTestUtils.setField(u7, "id", 7L);

        User u8 = new User();
        ReflectionTestUtils.setField(u8, "id", 8L);

        cardOwnedBy7 = new Card();
        ReflectionTestUtils.setField(cardOwnedBy7, "id", 100L);
        ReflectionTestUtils.setField(cardOwnedBy7, "owner", u7);
        ReflectionTestUtils.setField(cardOwnedBy7, "status", CardStatus.ACTIVE);
        ReflectionTestUtils.setField(cardOwnedBy7, "balance", new BigDecimal("100.00"));

        cardOwnedBy8 = new Card();
        ReflectionTestUtils.setField(cardOwnedBy8, "id", 200L);
        ReflectionTestUtils.setField(cardOwnedBy8, "owner", u8);
        ReflectionTestUtils.setField(cardOwnedBy8, "status", CardStatus.ACTIVE);
        ReflectionTestUtils.setField(cardOwnedBy8, "balance", new BigDecimal("50.00"));
    }

    @Test
    void requestBlock_by_owner_ok() {
        given(cardRepository.findById(100L)).willReturn(Optional.of(cardOwnedBy7));
        cardService.requestBlock(100L);
    }

    @Test
    void requestBlock_by_other_user_forbidden() {
        given(cardRepository.findById(200L)).willReturn(Optional.of(cardOwnedBy8));
        assertThatThrownBy(() -> cardService.requestBlock(200L))
                .isInstanceOf(ForbiddenException.class);
    }
}
