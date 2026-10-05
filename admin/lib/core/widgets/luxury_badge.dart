import 'package:flutter/material.dart';
import 'package:open_fashion_admin/core/theme/app_colors.dart';

/// Semantic variants for Luxury Badges in Flutter Admin.
enum LuxuryBadgeVariant {
  gold,
  success,
  warning,
  error,
  neutral,
}

/// Enterprise Luxury Badge & Status Chip Widget.
class LuxuryBadge extends StatelessWidget {
  final String text;
  final LuxuryBadgeVariant variant;
  final double cornerRadius;

  const LuxuryBadge({
    super.key,
    required this.text,
    this.variant = LuxuryBadgeVariant.gold,
    this.cornerRadius = 4.0,
  });

  @override
  Widget build(BuildContext context) {
    Color backgroundColor;
    Color textColor;
    Color borderColor;

    switch (variant) {
      case LuxuryBadgeVariant.gold:
        backgroundColor = AppColors.accentLight.withValues(alpha: 0.35);
        textColor = const Color(0xFFA38356);
        borderColor = AppColors.accent.withValues(alpha: 0.4);
        break;
      case LuxuryBadgeVariant.success:
        backgroundColor = AppColors.success.withValues(alpha: 0.12);
        textColor = AppColors.success;
        borderColor = AppColors.success.withValues(alpha: 0.3);
        break;
      case LuxuryBadgeVariant.warning:
        backgroundColor = AppColors.warning.withValues(alpha: 0.12);
        textColor = AppColors.warning;
        borderColor = AppColors.warning.withValues(alpha: 0.3);
        break;
      case LuxuryBadgeVariant.error:
        backgroundColor = AppColors.error.withValues(alpha: 0.12);
        textColor = AppColors.error;
        borderColor = AppColors.error.withValues(alpha: 0.3);
        break;
      case LuxuryBadgeVariant.neutral:
        backgroundColor = const Color(0xFFF3F4F6);
        textColor = AppColors.textSecondaryLight;
        borderColor = AppColors.borderLight;
        break;
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(cornerRadius),
        border: Border.all(color: borderColor, width: 0.8),
      ),
      child: Text(
        text.toUpperCase(),
        style: TextStyle(
          fontSize: 11,
          fontWeight: FontWeight.bold,
          letterSpacing: 1.2,
          color: textColor,
        ),
      ),
    );
  }
}
