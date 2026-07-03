package com.bankapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An immutable record of a single deposit or withdrawal, kept so an
 * {@link Account} can print a statement of everything that happened to it.
 */
public final class Transaction {

    public enum Type {
        DEPOSIT,
        WITHDRAWAL
    }

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Type type;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;
    private final LocalDateTime timestamp;

    public Transaction(Type type, BigDecimal amount, BigDecimal balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-10s %10s  (balance after: %s)",
                timestamp.format(TIMESTAMP_FORMAT), type, amount, balanceAfter);
    }
}
