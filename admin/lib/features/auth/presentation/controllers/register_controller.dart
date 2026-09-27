import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/auth_repository.dart';
import '../../domain/models/user_model.dart';

/// Riverpod StateNotifier for Registration / Invitation Form Flow
final registerControllerProvider =
    StateNotifierProvider.autoDispose<RegisterController, AsyncValue<UserModel?>>((ref) {
  final authRepository = ref.watch(authRepositoryProvider);
  return RegisterController(authRepository);
});

class RegisterController extends StateNotifier<AsyncValue<UserModel?>> {
  final AuthRepository _authRepository;

  RegisterController(this._authRepository) : super(const AsyncData(null));

  /// Submits the registration form payload to the backend
  Future<bool> registerUser({
    required String name,
    required String email,
    required String password,
    String? phoneNumber,
  }) async {
    state = const AsyncLoading();

    try {
      final user = await _authRepository.register(
        name: name,
        email: email,
        password: password,
        phoneNumber: phoneNumber,
      );

      state = AsyncData(user);
      return true;
    } catch (e, stack) {
      state = AsyncError(e, stack);
      return false;
    }
  }

  /// Resets state back to initial
  void reset() {
    state = const AsyncData(null);
  }
}
