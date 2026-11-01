import 'package:envied/envied.dart';

part 'env.g.dart';

/// Strongly-typed, compile-time validated environment configuration for Open Fashion Admin.
/// Protected by compile-time byte-mask XOR obfuscation to prevent string extraction.
@Envied(path: '.env')
abstract class Env {
  @EnviedField(varName: 'API_BASE_URL', obfuscate: true)
  static final String apiBaseUrl = _Env.apiBaseUrl;

  @EnviedField(varName: 'ENVIRONMENT', defaultValue: 'development')
  static const String environment = _Env.environment;
}
