package com.example.open_fashion.core.constants

/**
 * Centralized Regular Expressions & Validation rules for the Open Fashion Android client.
 * Compiled once in memory upon class loading and shared across all ViewModels and UseCases.
 */
object ValidationPatterns {

    /**
     * Standard RFC 5322 email regex pattern.
     */
    val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    /**
     * Enterprise password complexity regex:
     * - Minimum 8 characters
     * - At least one lowercase letter (?=.*[a-z])
     * - At least one uppercase letter (?=.*[A-Z])
     * - At least one numeric digit (?=.*\d)
     * - At least one special symbol (?=.*[@$!%*?&#^()_+\-=[\]{}|;:,.<>])
     */
    val PASSWORD_REGEX = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=\\[\\]{}|;:,.<>]).{8,}\$")

    /**
     * Minimum length required for password string.
     */
    const val MIN_PASSWORD_LENGTH = 8

    /**
     * Validates whether an email string adheres to valid RFC email syntax.
     */
    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && EMAIL_REGEX.matches(email.trim())

    /**
     * Validates whether a password satisfies length and complexity rules.
     */
    fun isValidPassword(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH && PASSWORD_REGEX.matches(password)
}
