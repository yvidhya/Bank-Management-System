package com.bankapp.exception;

/**
 * Thrown when a withdrawal is attempted for more than the current balance.
 * A checked exception is used deliberately: callers (UI, service layer) are
 * forced to handle the failure instead of it surfacing as an unchecked
 * runtime crash.
 */
public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
