package com.bankapp.service;

import com.bankapp.exception.AccountNotFoundException;
import com.bankapp.exception.InsufficientFundsException;
import com.bankapp.model.Account;
import com.bankapp.model.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {

    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankService();
    }

    @Test
    void createAccountAssignsIncrementingAccountNumbers() {
        Account first = bankService.createAccount("Alice", AccountType.SAVINGS);
        Account second = bankService.createAccount("Bob", AccountType.CHECKING);
        assertEquals(first.getAccountNumber() + 1, second.getAccountNumber());
    }

    @Test
    void depositAndWithdrawAffectTheCorrectAccount() throws AccountNotFoundException, InsufficientFundsException {
        Account account = bankService.createAccount("Alice", AccountType.SAVINGS);
        bankService.deposit(account.getAccountNumber(), new BigDecimal("200.00"));
        bankService.withdraw(account.getAccountNumber(), new BigDecimal("75.00"));
        assertEquals(new BigDecimal("125.00"), bankService.getBalance(account.getAccountNumber()));
    }

    @Test
    void operatingOnUnknownAccountThrows() {
        assertThrows(AccountNotFoundException.class, () -> bankService.getBalance(999L));
        assertThrows(AccountNotFoundException.class, () -> bankService.deposit(999L, BigDecimal.TEN));
        assertThrows(AccountNotFoundException.class, () -> bankService.withdraw(999L, BigDecimal.TEN));
    }

    @Test
    void listAccountsReflectsEveryCreatedAccount() {
        bankService.createAccount("Alice", AccountType.SAVINGS);
        bankService.createAccount("Bob", AccountType.CHECKING);
        assertEquals(2, bankService.listAccounts().size());
    }
}
