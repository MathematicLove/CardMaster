package com.example.bankcards.dto.card;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CardCreateRequest {
    @NotNull(message = "ID владельца обязателен")
    private Long ownerId;

    @NotBlank(message = "Номер карты обязателен")
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать 16 цифр")
    private String cardNumber;

    @Min(value = 1) @Max(value = 12)
    private int expiryMonth;

    @Min(value = 2024, message = "Год должен быть >= 2025")
    private int expiryYear;

    private CardStatus status = CardStatus.ACTIVE;

    @NotNull(message = "Начальный баланс обязателен")
    @DecimalMin(value = "0.00", inclusive = true, message = "Баланс не может быть отрицательным если только вы не псих")
    private BigDecimal balance;

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public int getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(int expiryMonth) { this.expiryMonth = expiryMonth; }
    public int getExpiryYear() { return expiryYear; }
    public void setExpiryYear(int expiryYear) { this.expiryYear = expiryYear; }
    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
