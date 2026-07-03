package com.bankapp.model;

import com.bankapp.exception.InsufficientFundsException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a single bank account belonging to one account holder.
 *
 * Balances are stored as {@link BigDecimal} (not double/float) to avoid the
 * floating point rounding errors that are unsafe for monetary values.
 * Every deposit/withdrawal is also recorded in an internal transaction
 * history so the account can produce a statement on demand.
 */
public final class Account {

    private final long accountNumber;
    private final String accountHolder;
    private final AccountType type;
    private final LocalDateTime createdAt;
    private BigDecimal balance;
    private final List<Transaction> history = new ArrayList<>();

    public Account(long accountNumber, String accountHolder, AccountType type) {
        if (accountHolder == null || accountHolder.isBlank()) {
            throw new IllegalArgumentException("Account holder name cannot be blank");
        }
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder.trim();
        this.type = type;
        this.balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.createdAt = LocalDateTime.now();
    }

    public synchronized void deposit(BigDecimal amount) {
        validatePositive(amount);
        balance = balance.add(amount).setScale(2, RoundingMode.HALF_UP);
        history.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance));
    }

    public synchronized void withdraw(BigDecimal amount) throws InsufficientFundsException {
        validatePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds: balance is " + balance + " but withdrawal of " + amount + " was requested");
        }
        balance = balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
        history.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, balance));
    }

    private void validatePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public AccountType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public synchronized List<Transaction> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber=" + accountNumber +
                ", accountHolder='" + accountHolder + '\'' +
                ", type=" + type +
                ", balance=" + balance +
                '}';
    }
}
