package com.bankapp.model;

/**
 * The kind of account a customer holds. Kept as an enum so the UI can offer
 * a fixed, validated set of choices instead of free-text input.
 */
public enum AccountType {
    SAVINGS,
    CHECKING
}
