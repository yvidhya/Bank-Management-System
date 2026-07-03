package com.bankapp.model;

import com.bankapp.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account(1_000_000_001L, "Jane Doe", AccountType.SAVINGS);
    }

    @Test
    void newAccountStartsWithZeroBalance() {
        assertEquals(new BigDecimal("0.00"), account.getBalance());
    }

    @Test
    void depositIncreasesBalance() {
        account.deposit(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }

    @Test
    void withdrawDecreasesBalance() throws InsufficientFundsException {
        account.deposit(new BigDecimal("100.00"));
        account.withdraw(new BigDecimal("40.00"));
        assertEquals(new BigDecimal("60.00"), account.getBalance());
    }

    @Test
    void withdrawMoreThanBalanceThrows() {
        account.deposit(new BigDecimal("50.00"));
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(new BigDecimal("50.01")));
    }

    @Test
    void depositOfZeroOrNegativeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(new BigDecimal("-5.00")));
    }

    @Test
    void withdrawOfZeroOrNegativeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(new BigDecimal("-5.00")));
    }

    @Test
    void blankAccountHolderNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Account(2L, " ", AccountType.CHECKING));
    }

    @Test
    void historyRecordsEachTransaction() throws InsufficientFundsException {
        account.deposit(new BigDecimal("100.00"));
        account.withdraw(new BigDecimal("30.00"));
        assertEquals(2, account.getHistory().size());
    }
}
