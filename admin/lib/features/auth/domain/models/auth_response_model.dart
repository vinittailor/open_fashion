import 'user_model.dart';

/// Immutable domain model representing authenticated session data
class AuthResponseModel {
  final UserModel user;
  final String accessToken;
  final String refreshToken;
  final String tokenType;
  final int expiresIn;
  final int refreshExpiresIn;

  const AuthResponseModel({
    required this.user,
    required this.accessToken,
    required this.refreshToken,
    this.tokenType = 'Bearer',
    this.expiresIn = 900,
    this.refreshExpiresIn = 604800,
  });

  /// Factory constructor to parse backend response envelope data
  factory AuthResponseModel.fromJson(Map<String, dynamic> json) {
    return AuthResponseModel(
      user: UserModel.fromJson(
        (json['user'] as Map<String, dynamic>?) ?? <String, dynamic>{},
      ),
      accessToken: json['accessToken'] as String? ?? '',
      refreshToken: json['refreshToken'] as String? ?? '',
      tokenType: json['tokenType'] as String? ?? 'Bearer',
      expiresIn: (json['expiresIn'] as num?)?.toInt() ?? 900,
      refreshExpiresIn: (json['refreshExpiresIn'] as num?)?.toInt() ?? 604800,
    );
  }

  /// Serializes instance to a JSON-compatible map
  Map<String, dynamic> toJson() {
    return {
      'user': user.toJson(),
      'accessToken': accessToken,
      'refreshToken': refreshToken,
      'tokenType': tokenType,
      'expiresIn': expiresIn,
      'refreshExpiresIn': refreshExpiresIn,
    };
  }
}
