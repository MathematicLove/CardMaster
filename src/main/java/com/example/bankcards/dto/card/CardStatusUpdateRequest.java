package com.example.bankcards.dto.card;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.NotNull;

public class CardStatusUpdateRequest {
    @NotNull
    private CardStatus status;

    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }
}
