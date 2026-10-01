import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/storage/token_storage.dart';
import '../../data/auth_repository.dart';
import '../../domain/models/user_model.dart';

/// Authentication state lifecycle status
enum AuthStatus {
  initial,
  authenticating,
  authenticated,
  unauthenticated,
  error,
}

/// Immutable state container for Admin Authentication
class AuthState {
  final AuthStatus status;
  final UserModel? user;
  final String? errorMessage;

  const AuthState({
    this.status = AuthStatus.unauthenticated,
    this.user,
    this.errorMessage,
  });

  bool get isAuthenticated => status == AuthStatus.authenticated && user != null;
  bool get isLoading => status == AuthStatus.authenticating;

  AuthState copyWith({
    AuthStatus? status,
    UserModel? user,
    String? errorMessage,
  }) {
    return AuthState(
      status: status ?? this.status,
      user: user ?? this.user,
      errorMessage: errorMessage,
    );
  }
}

/// Global StateNotifierProvider for Admin Authentication
final authControllerProvider =
    StateNotifierProvider<AuthController, AuthState>((ref) {
  final authRepository = ref.watch(authRepositoryProvider);
  final tokenStorage = ref.watch(tokenStorageProvider);
  return AuthController(authRepository, tokenStorage);
});

/// Riverpod Controller managing Login, Session Restoration, and Logout
class AuthController extends StateNotifier<AuthState> {
  final AuthRepository _authRepository;
  final TokenStorage _tokenStorage;

  AuthController(this._authRepository, this._tokenStorage)
      : super(const AuthState()) {
    checkSession();
  }

  /// Checks for an existing session on app startup and attempts token refresh
  Future<void> checkSession() async {
    try {
      final refreshToken = await _tokenStorage.getRefreshToken();
      if (refreshToken == null || refreshToken.isEmpty) {
        state = state.copyWith(status: AuthStatus.unauthenticated);
        return;
      }

      state = state.copyWith(status: AuthStatus.authenticating);
      final response = await _authRepository.refreshToken(refreshToken: refreshToken);

      await _tokenStorage.saveTokens(
        accessToken: response.accessToken,
        refreshToken: response.refreshToken,
      );

      state = state.copyWith(
        status: AuthStatus.authenticated,
        user: response.user,
        errorMessage: null,
      );
    } catch (_) {
      await _tokenStorage.clearTokens();
      state = state.copyWith(
        status: AuthStatus.unauthenticated,
        user: null,
      );
    }
  }

  /// Authenticates with email and password
  Future<bool> login({
    required String email,
    required String password,
  }) async {
    state = state.copyWith(status: AuthStatus.authenticating, errorMessage: null);

    try {
      final response = await _authRepository.login(
        email: email,
        password: password,
      );

      await _tokenStorage.saveTokens(
        accessToken: response.accessToken,
        refreshToken: response.refreshToken,
      );

      state = state.copyWith(
        status: AuthStatus.authenticated,
        user: response.user,
        errorMessage: null,
      );
      return true;
    } catch (e) {
      final message = e.toString().replaceFirst('Exception: ', '');
      state = state.copyWith(
        status: AuthStatus.error,
        errorMessage: message,
      );
      return false;
    }
  }

  /// Logs the user out and clears all session tokens
  Future<void> logout() async {
    final userId = state.user?.id;
    await _authRepository.logout(userId: userId);
    await _tokenStorage.clearTokens();

    state = const AuthState(
      status: AuthStatus.unauthenticated,
      user: null,
    );
  }
}
