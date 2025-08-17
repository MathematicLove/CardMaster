package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "cards")
public class Card extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String numberEncrypted;

    @Column(nullable = false, length = 4)
    private String last4;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User owner;

    @Column(nullable = false)
    private int expiryMonth;

    @Column(nullable = false)
    private int expiryYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status = CardStatus.ACTIVE;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance;

    public boolean isExpiredNow() {
        YearMonth now = YearMonth.now();
        YearMonth exp = YearMonth.of(expiryYear, expiryMonth);
        return now.isAfter(exp);
    }

    public CardStatus getEffectiveStatus() {
        return isExpiredNow() ? CardStatus.EXPIRED : status;
    }

    public Long getId() { return id; }
    public String getNumberEncrypted() { return numberEncrypted; }
    public void setNumberEncrypted(String numberEncrypted) { this.numberEncrypted = numberEncrypted; }
    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public int getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(int expiryMonth) { this.expiryMonth = expiryMonth; }
    public int getExpiryYear() { return expiryYear; }
    public void setExpiryYear(int expiryYear) { this.expiryYear = expiryYear; }
    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
