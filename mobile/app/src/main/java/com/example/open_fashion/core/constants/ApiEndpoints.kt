package com.example.open_fashion.core.constants

/**
 * Centralized REST API endpoint paths for the Open Fashion Android Client.
 */
object ApiEndpoints {
    // Base URL: ADB reverse tunnel maps 127.0.0.1:5000 directly to local backend server
    const val BASE_URL = "http://127.0.0.1:5000/api/v1/"

    // --- Authentication Endpoints ---
    const val REGISTER = "auth/register"
    const val LOGIN = "auth/login"
    const val REFRESH = "auth/refresh"
    const val LOGOUT = "auth/logout"
    const val FORGOT_PASSWORD = "auth/forgot-password"
    const val RESET_PASSWORD = "auth/reset-password"
    const val SEND_VERIFICATION = "auth/send-verification"
    const val VERIFY_EMAIL = "auth/verify-email"

    // --- User Profile Endpoints ---
    const val USERS_ME = "users/me"

    // --- Products & Catalog Endpoints ---
    const val PRODUCTS = "products"
    const val CATEGORIES = "categories"

    // --- Wishlist & Cart Endpoints ---
    const val WISHLIST = "wishlist"
    const val CART = "cart"

    // --- Orders & Checkout Endpoints ---
    const val ORDERS = "orders"
}
