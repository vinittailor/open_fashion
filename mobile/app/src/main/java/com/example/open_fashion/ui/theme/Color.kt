package com.example.open_fashion.ui.theme

import androidx.compose.ui.graphics.Color

// ====================================================================
// 60/30/10 Luxury Color System (mobile-app-ui-design standard)
// ====================================================================

// --- 10% Brand & Luxury Accent (CTAs, Selected states, Badges) ---
val AccentGold = Color(0xFFC5A880)          // Primary Champagne Sand / Luxury Gold
val AccentGoldLight = Color(0xFFEADBCE)     // Soft Gold Tint for Badges/Pills (5-10% opacity feel)
val AccentGoldDark = Color(0xFFA6875C)      // Pressed/Hover Gold State

// --- 30% Primary & Structural Contrasts (Headers, Primary Buttons) ---
val PrimaryCharcoal = Color(0xFF111111)     // High-End Jet Black
val PrimaryCharcoalLight = Color(0xFF26262B)// Secondary Elevated Element

// --- 60% Surfaces & Backgrounds (Light Mode) ---
val BackgroundLight = Color(0xFFFAF9F6)     // Soft Alabaster / Off-White (editorial feel)
val SurfaceLight = Color(0xFFFFFFFF)        // Pure White Product Cards
val SurfaceVariantLight = Color(0xFFF3F2EE) // Chip / Input Field Background
val BorderLight = Color(0xFFE7E5E0)         // Subtle 1px Hairline Border

val TextPrimaryLight = Color(0xFF141416)    // 100% Primary Headings
val TextSecondaryLight = Color(0xFF777E90)  // 60-70% Subtitles / Category metadata
val TextTertiaryLight = Color(0xFFB1B5C3)   // 40% Placeholder text

// --- 60% Surfaces & Backgrounds (Dark Mode) ---
val BackgroundDark = Color(0xFF0F0F12)      // Deep OLED Black
val SurfaceDark = Color(0xFF18181D)         // Elevated Dark Card Surface
val SurfaceVariantDark = Color(0xFF23232A)  // Dark Input / Chip Background
val BorderDark = Color(0xFF2C2C35)          // Dark Subtle Border

val TextPrimaryDark = Color(0xFFFCFCFD)     // Crisp White Text
val TextSecondaryDark = Color(0xFF9CA3AF)   // Muted Dark Mode Text
val TextTertiaryDark = Color(0xFF6B7280)    // Muted Placeholder

// --- Semantic Status Feedback ---
val StatusSuccess = Color(0xFF10B981)       // Emerald Green (Delivered / In Stock)
val StatusWarning = Color(0xFFF59E0B)       // Warm Amber (Low Stock / Processing)
val StatusError = Color(0xFFEF4444)         // Crimson Red (Out of Stock / Error)
val StatusInfo = Color(0xFF3B82F6)          // Electric Blue (Order Shipped)