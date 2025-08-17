package com.example.bankcards.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transfers")
public class Transfer extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Card fromCard;

    @ManyToOne(optional = false)
    private Card toCard;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private boolean success;

    public Long getId() { return id; }
    public Card getFromCard() { return fromCard; }
    public void setFromCard(Card fromCard) { this.fromCard = fromCard; }
    public Card getToCard() { return toCard; }
    public void setToCard(Card toCard) { this.toCard = toCard; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
