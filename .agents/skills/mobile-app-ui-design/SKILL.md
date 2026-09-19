---
name: mobile-app-ui-design
description: Design high-quality mobile app UI/UX screens, flows, and components for Jetpack Compose and Flutter. Use this skill whenever designing mobile screens, creating app mockups, building UI components, color systems, typography tokens, responsive layouts, or interactive animations.
---

# Mobile App UI/UX Design Skill

This skill guides the creation of professional, polished mobile app interfaces following proven design principles from top-tier apps (Airbnb, Apple, Spotify, Revolut, Phantom).

## Core Philosophy
Great mobile UI isn't about flashiness — it's about intentionality. Every pixel, every spacing value, every color choice should serve the user.

1. **Reduce Friction**: Make primary actions effortless.
2. **Visual Hierarchy**: Guide the user's eye to the most important element first.
3. **Tactile Delight**: Subtle animations and haptic feedback make the interface feel alive.

---

## 🎨 1. The 60/30/10 Color Rule for Luxury E-Commerce
- **60% Neutral Base**: Soft Alabaster (`#FAF9F6`) in Light Mode / Deep OLED Black (`#0F0F12`) in Dark Mode.
- **30% Structural Contrast**: Crisp Charcoal/White for typography, cards, borders (`#111111` / `#FFFFFF`).
- **10% Brand Accent**: Champagne Gold / Warm Sand (`#C5A880`) reserved strictly for CTAs, selected badges, and key icons.

---

## 📏 2. Spacing & The 8-Point Grid System
All margins, paddings, and component heights must be divisible by **4 or 8**:
- `4.dp` / `4.0` — Micro-gaps (badge padding, icon-text gap)
- `8.dp` / `8.0` — Small spacing (card internal padding, chips)
- `12.dp` / `12.0` — Medium-compact spacing (list item vertical gap)
- `16.dp` / `16.0` — Standard screen margin & padding
- `24.dp` / `24.0` — Section separation gap
- `32.dp` / `32.0` — Major visual section breaks
- `48.dp` / `56.dp` — Standard touch target heights for buttons and app bars (Accessibility compliant)

---

## 📱 3. Jetpack Compose & Flutter Luxury E-Commerce Patterns
- **Product Cards**: 3:4 or 4:5 vertical portrait aspect ratio with rounded corners (`16.dp`).
- **Thumb Zone**: Sticky "Add to Cart" and "Checkout" buttons in bottom floating bar.
- **Micro-Animations**: Press-down scale animation (`0.97f`) on touch, smooth alpha crossfades on image loading.
- **Typography**: Editorial header scale (`Outfit` / `Inter`), high-contrast price numbers with monospace tabular alignment.
