package com.example.bankcards.dto.card;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.util.MaskingUtil;

import java.math.BigDecimal;
import java.time.Instant;

public class CardResponse {
    private Long id;
    private Long ownerId;
    private String ownerName;
    private String maskedNumber;
    private int expiryMonth;
    private int expiryYear;
    private CardStatus status;
    private BigDecimal balance;
    private Instant createdAt;
    private Instant updatedAt;

    public static CardResponse from(Card c) {
        CardResponse r = new CardResponse();
        r.id = c.getId();
        r.ownerId = c.getOwner().getId();
        r.ownerName = c.getOwner().getFullName();
        r.maskedNumber = MaskingUtil.maskLast4(c.getLast4());
        r.expiryMonth = c.getExpiryMonth();
        r.expiryYear = c.getExpiryYear();
        r.status = c.getEffectiveStatus();
        r.balance = c.getBalance();
        r.createdAt = c.getCreatedAt();
        r.updatedAt = c.getUpdatedAt();
        return r;
    }

    public Long getId() { return id; }
    public Long getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public String getMaskedNumber() { return maskedNumber; }
    public int getExpiryMonth() { return expiryMonth; }
    public int getExpiryYear() { return expiryYear; }
    public CardStatus getStatus() { return status; }
    public BigDecimal getBalance() { return balance; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
