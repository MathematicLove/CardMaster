package com.example.bankcards.dto.card;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CardUpdateRequest {
    @Min(1) @Max(12)
    private int expiryMonth;

    @Min(2025)
    private int expiryYear;

    public int getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(int expiryMonth) { this.expiryMonth = expiryMonth; }
    public int getExpiryYear() { return expiryYear; }
    public void setExpiryYear(int expiryYear) { this.expiryYear = expiryYear; }
}
