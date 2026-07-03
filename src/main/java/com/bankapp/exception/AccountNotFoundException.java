package com.bankapp.exception;

/**
 * Thrown when a caller references an account number that does not exist
 * in the bank's records.
 */
public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(long accountNumber) {
        super("No account found with account number: " + accountNumber);
    }
}
