package com.example.open_fashion.core.navigation

/**
 * Type-safe navigation route identifiers for Jetpack Compose Navigation.
 */
sealed class NavRoute(val route: String) {
    data object Home : NavRoute("home")
    data object Catalog : NavRoute("catalog")
    data object ProductDetail : NavRoute("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    data object Cart : NavRoute("cart")
    data object Wishlist : NavRoute("wishlist")
    data object Checkout : NavRoute("checkout")
    data object OrderHistory : NavRoute("order_history")
    data object OrderTracking : NavRoute("order_tracking/{orderId}") {
        fun createRoute(orderId: String) = "order_tracking/$orderId"
    }
    data object Login : NavRoute("auth_login")
    data object Register : NavRoute("auth_register")
}
