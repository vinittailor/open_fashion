import 'package:flutter/material.dart';

/// Centralized Design System Colors for Open Fashion Admin Dashboard.
/// Follows a sleek, modern luxury dark/light palette with champagne and charcoal tones.
abstract class AppColors {
  // Brand / Luxury Accent Colors
  static const Color primary = Color(0xFF111111); // Deep Charcoal / Jet Black
  static const Color primaryLight = Color(0xFF2C2C2C);
  static const Color accent = Color(0xFFC5A880); // Champagne Gold / Sand
  static const Color accentLight = Color(0xFFE8DCC9);

  // Backgrounds & Surface Colors (Light Mode)
  static const Color backgroundLight = Color(0xFFF9F9FB);
  static const Color surfaceLight = Color(0xFFFFFFFF);
  static const Color cardLight = Color(0xFFFFFFFF);
  static const Color borderLight = Color(0xFFE5E7EB);

  // Backgrounds & Surface Colors (Dark Mode)
  static const Color backgroundDark = Color(0xFF0F0F12);
  static const Color surfaceDark = Color(0xFF18181D);
  static const Color cardDark = Color(0xFF1E1E24);
  static const Color borderDark = Color(0xFF2C2C35);

  // Text Colors
  static const Color textPrimaryLight = Color(0xFF111827);
  static const Color textSecondaryLight = Color(0xFF6B7280);
  static const Color textPrimaryDark = Color(0xFFF9FAFB);
  static const Color textSecondaryDark = Color(0xFF9CA3AF);

  // Status & Feedback Colors
  static const Color success = Color(0xFF10B981); // Emerald Green (Delivered / In Stock)
  static const Color warning = Color(0xFFF59E0B); // Amber (Pending / Low Stock)
  static const Color error = Color(0xFFEF4444); // Crimson Red (Cancelled / Out of Stock)
  static const Color info = Color(0xFF3B82F6); // Royal Blue (Shipped / Processing)
}
