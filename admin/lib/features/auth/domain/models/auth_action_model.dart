/// Strongly-typed response model for authentication action flows (Forgot Password, Email Verification, OTPs)
class AuthActionModel {
  final String message;
  final String? devToken;
  final String? devOtp;

  const AuthActionModel({
    required this.message,
    this.devToken,
    this.devOtp,
  });

  factory AuthActionModel.fromJson(Map<String, dynamic> json) {
    final data = (json['data'] is Map<String, dynamic>)
        ? json['data'] as Map<String, dynamic>
        : <String, dynamic>{};

    return AuthActionModel(
      message: json['message'] as String? ?? 'Action processed successfully.',
      devToken: data['devToken'] as String?,
      devOtp: data['devOtp'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'message': message,
      if (devToken != null) 'devToken': devToken,
      if (devOtp != null) 'devOtp': devOtp,
    };
  }
}
