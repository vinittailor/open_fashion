import 'package:flutter/material.dart';
import 'package:open_fashion_admin/core/theme/app_colors.dart';

/// Visual variants for Open Fashion Admin Luxury Buttons.
enum LuxuryButtonVariant {
  primary,     // Champagne Gold background + Charcoal text
  secondary,   // Obsidian Charcoal background + White text
  outline,     // Hairline border + Transparent background
  destructive, // Crimson Red border / text
  ghost,       // Borderless text & icon
}

/// Enterprise Luxury Button Widget for Flutter Admin.
class LuxuryButton extends StatelessWidget {
  final String text;
  final VoidCallback? onPressed;
  final LuxuryButtonVariant variant;
  final bool isLoading;
  final IconData? icon;
  final double height;
  final double cornerRadius;
  final double? width;

  const LuxuryButton({
    super.key,
    required this.text,
    this.onPressed,
    this.variant = LuxuryButtonVariant.primary,
    this.isLoading = false,
    this.icon,
    this.height = 48.0,
    this.cornerRadius = 2.0,
    this.width,
  });

  @override
  Widget build(BuildContext context) {
    Color backgroundColor;
    Color foregroundColor;
    BorderSide? borderSide;

    switch (variant) {
      case LuxuryButtonVariant.primary:
        backgroundColor = AppColors.accent;
        foregroundColor = AppColors.primary;
        borderSide = null;
        break;
      case LuxuryButtonVariant.secondary:
        backgroundColor = AppColors.primary;
        foregroundColor = Colors.white;
        borderSide = null;
        break;
      case LuxuryButtonVariant.outline:
        backgroundColor = Colors.transparent;
        foregroundColor = AppColors.textPrimaryLight;
        borderSide = const BorderSide(color: AppColors.borderLight);
        break;
      case LuxuryButtonVariant.destructive:
        backgroundColor = Colors.transparent;
        foregroundColor = AppColors.error;
        borderSide = BorderSide(color: AppColors.error.withValues(alpha: 0.4));
        break;
      case LuxuryButtonVariant.ghost:
        backgroundColor = Colors.transparent;
        foregroundColor = AppColors.accent;
        borderSide = null;
        break;
    }

    return SizedBox(
      width: width ?? double.infinity,
      height: height,
      child: ElevatedButton(
        onPressed: isLoading ? null : onPressed,
        style: ElevatedButton.styleFrom(
          backgroundColor: backgroundColor,
          foregroundColor: foregroundColor,
          disabledBackgroundColor: backgroundColor.withValues(alpha: 0.4),
          disabledForegroundColor: foregroundColor.withValues(alpha: 0.4),
          elevation: 0,
          shape: RoundedCornerShape(cornerRadius),
          side: borderSide,
          padding: const EdgeInsets.symmetric(horizontal: 20),
        ),
        child: isLoading
            ? SizedBox(
                width: 20,
                height: 20,
                child: CircularProgressIndicator(
                  strokeWidth: 2,
                  valueColor: AlwaysStoppedAnimation<Color>(foregroundColor),
                ),
              )
            : Row(
                mainAxisSize: MainAxisSize.min,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  if (icon != null) ...[
                    Icon(icon, size: 16, color: foregroundColor),
                    const SizedBox(width: 8),
                  ],
                  Text(
                    text.toUpperCase(),
                    style: TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 2.0,
                      color: foregroundColor,
                    ),
                  ),
                ],
              ),
      ),
    );
  }
}

/// Helper for standardizing rectangular borders across Flutter admin widgets.
class RoundedCornerShape extends RoundedRectangleBorder {
  RoundedCornerShape(double radius)
      : super(borderRadius: BorderRadius.circular(radius));
}
