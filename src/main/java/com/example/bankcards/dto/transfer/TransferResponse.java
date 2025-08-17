package com.example.bankcards.dto.transfer;

import com.example.bankcards.entity.Transfer;

import java.math.BigDecimal;
import java.time.Instant;

public class TransferResponse {
    private Long id;
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private Instant createdAt;
    private String status;

    public static TransferResponse from(Transfer t) {
        TransferResponse r = new TransferResponse();
        r.id = t.getId();
        r.fromCardId = t.getFromCard().getId();
        r.toCardId = t.getToCard().getId();
        r.amount = t.getAmount();
        r.createdAt = t.getCreatedAt();
        r.status = t.isSuccess() ? "SUCCESS" : "FAILED";
        return r;
    }

    public Long getId() { return id; }
    public Long getFromCardId() { return fromCardId; }
    public Long getToCardId() { return toCardId; }
    public BigDecimal getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
}