package com.example.open_fashion.ui.theme

import androidx.compose.ui.graphics.Color

// ====================================================================
// 60/30/10 Luxury Color System (ui-craft & tasteful-ui standard)
// 60% Alabaster / OLED Canvas | 30% Charcoal Structure | 10% Champagne Gold
// ====================================================================

// --- 10% Brand & Luxury Accent (CTAs, Selected states, Badges) ---
val AccentGold = Color(0xFFC5A880)          // Primary Champagne Sand / Luxury Gold
val AccentGoldLight = Color(0xFFF3EAE0)     // Soft Gold Tint for Badges/Pills (10% tint)
val AccentGoldDark = Color(0xFFA38356)      // Deep Burnished Gold (Pressed / Hover)
val AccentGoldSubtle = Color(0xFFFAF5EE)    // Ultra-light gold wash for card highlights

// --- 30% Primary & Structural Contrasts (Headers, Primary Buttons) ---
val PrimaryCharcoal = Color(0xFF121214)     // High-End Obsidian / Jet Black
val PrimaryCharcoalLight = Color(0xFF222228)// Secondary Elevated Element
val PrimaryCharcoalMuted = Color(0xFF33333D)// Muted Charcoal

// --- 60% Surfaces & Backgrounds (Light Mode) ---
val BackgroundLight = Color(0xFFFAF9F6)     // Soft Alabaster / Off-White (editorial feel)
val SurfaceLight = Color(0xFFFFFFFF)        // Pure White Product Cards
val SurfaceVariantLight = Color(0xFFF4F3EF) // Chip / Input Field Background
val BorderLight = Color(0xFFE8E6E1)         // Subtle 1px Hairline Border

val TextPrimaryLight = Color(0xFF141416)    // 100% Primary Headings
val TextSecondaryLight = Color(0xFF6E717C)  // Subtitles / Category metadata (WCAG AAA)
val TextTertiaryLight = Color(0xFFA6ABB8)   // Placeholder / Hint text

// --- 60% Surfaces & Backgrounds (Dark Mode) ---
val BackgroundDark = Color(0xFF0C0C0E)      // Deep OLED Obsidian Black
val SurfaceDark = Color(0xFF16161B)         // Elevated Dark Card Surface
val SurfaceVariantDark = Color(0xFF202027)  // Dark Input / Chip Background
val BorderDark = Color(0xFF282832)          // Dark Subtle Hairline Border

val TextPrimaryDark = Color(0xFFFCFCFD)     // Crisp White Text
val TextSecondaryDark = Color(0xFF9EABB8)   // Muted Dark Mode Text
val TextTertiaryDark = Color(0xFF636A78)    // Muted Placeholder

// --- Semantic Status Feedback ---
val StatusSuccess = Color(0xFF10B981)       // Emerald Green (Delivered / In Stock)
val StatusWarning = Color(0xFFF59E0B)       // Warm Amber (Low Stock / Processing)
val StatusError = Color(0xFFEF4444)         // Crimson Red (Out of Stock / Error)
val StatusInfo = Color(0xFF3B82F6)          // Electric Blue (Order Shipped)