import 'package:flutter/material.dart';
import 'package:open_fashion_admin/core/theme/app_colors.dart';

/// Enterprise Luxury Text Field Widget for Flutter Admin.
class LuxuryTextField extends StatefulWidget {
  final TextEditingController? controller;
  final String label;
  final String? placeholder;
  final String? errorMessage;
  final String? Function(String?)? validator;
  final ValueChanged<String>? onChanged;
  final IconData? prefixIcon;
  final bool isPassword;
  final bool enabled;
  final TextInputType keyboardType;
  final int maxLines;
  final double cornerRadius;

  const LuxuryTextField({
    super.key,
    required this.label,
    this.controller,
    this.placeholder,
    this.errorMessage,
    this.validator,
    this.onChanged,
    this.prefixIcon,
    this.isPassword = false,
    this.enabled = true,
    this.keyboardType = TextInputType.text,
    this.maxLines = 1,
    this.cornerRadius = 2.0,
  });

  @override
  State<LuxuryTextField> createState() => _LuxuryTextFieldState();
}

class _LuxuryTextFieldState extends State<LuxuryTextField> {
  late bool _obscureText;

  @override
  void initState() {
    super.initState();
    _obscureText = widget.isPassword;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        TextFormField(
          controller: widget.controller,
          enabled: widget.enabled,
          obscureText: _obscureText,
          keyboardType: widget.keyboardType,
          maxLines: widget.maxLines,
          onChanged: widget.onChanged,
          validator: widget.validator,
          style: const TextStyle(
            fontSize: 14,
            fontWeight: FontWeight.normal,
            color: AppColors.textPrimaryLight,
          ),
          decoration: InputDecoration(
            labelText: widget.label,
            hintText: widget.placeholder,
            errorText: widget.errorMessage,
            labelStyle: const TextStyle(
              fontSize: 13,
              color: AppColors.textSecondaryLight,
              letterSpacing: 0.5,
            ),
            hintStyle: TextStyle(
              fontSize: 13,
              color: AppColors.textSecondaryLight.withValues(alpha: 0.5),
            ),
            prefixIcon: widget.prefixIcon != null
                ? Icon(widget.prefixIcon, size: 18, color: AppColors.accent)
                : null,
            suffixIcon: widget.isPassword
                ? IconButton(
                    icon: Icon(
                      _obscureText ? Icons.visibility_off : Icons.visibility,
                      size: 18,
                      color: AppColors.textSecondaryLight,
                    ),
                    onPressed: () {
                      setState(() {
                        _obscureText = !_obscureText;
                      });
                    },
                  )
                : null,
            filled: true,
            fillColor: Colors.white,
            contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(widget.cornerRadius),
              borderSide: const BorderSide(color: AppColors.borderLight),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(widget.cornerRadius),
              borderSide: const BorderSide(color: AppColors.accent, width: 1.5),
            ),
            errorBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(widget.cornerRadius),
              borderSide: const BorderSide(color: AppColors.error),
            ),
            focusedErrorBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(widget.cornerRadius),
              borderSide: const BorderSide(color: AppColors.error, width: 1.5),
            ),
          ),
        ),
      ],
    );
  }
}
