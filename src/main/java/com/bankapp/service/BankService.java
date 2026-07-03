package com.bankapp.service;

import com.bankapp.exception.AccountNotFoundException;
import com.bankapp.exception.InsufficientFundsException;
import com.bankapp.model.Account;
import com.bankapp.model.AccountType;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service layer that owns every {@link Account} the application knows about.
 *
 * Separating this from the UI means the exact same class could be reused by
 * a REST controller, a CLI, or a test suite -- the Swing frame is just one
 * possible client of this service.
 */
public class BankService {

    /** Account numbers start here purely so they look realistic in a demo. */
    private static final long FIRST_ACCOUNT_NUMBER = 1_000_000_001L;

    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicLong nextAccountNumber = new AtomicLong(FIRST_ACCOUNT_NUMBER);

    public Account createAccount(String holderName, AccountType type) {
        long accountNumber = nextAccountNumber.getAndIncrement();
        Account account = new Account(accountNumber, holderName, type);
        accounts.put(accountNumber, account);
        return account;
    }

    public Account getAccount(long accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    public void deposit(long accountNumber, BigDecimal amount) throws AccountNotFoundException {
        getAccount(accountNumber).deposit(amount);
    }

    public void withdraw(long accountNumber, BigDecimal amount)
            throws AccountNotFoundException, InsufficientFundsException {
        getAccount(accountNumber).withdraw(amount);
    }

    public BigDecimal getBalance(long accountNumber) throws AccountNotFoundException {
        return getAccount(accountNumber).getBalance();
    }

    /** Read-only view of every account currently held by the bank. */
    public Collection<Account> listAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
    }

    public boolean hasAnyAccounts() {
        return !accounts.isEmpty();
    }
}
