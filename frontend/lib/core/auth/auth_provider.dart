import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../data/auth_repository.dart';
import '../data/mock/mock_auth_repository.dart';
import 'mock_token_storage.dart';
import 'token_storage.dart';

// ── Providers ──

final tokenStorageProvider = Provider<TokenStorage>(
  (ref) => MockTokenStorage(),
);

final authRepositoryProvider = Provider<AuthRepository>(
  (ref) => MockAuthRepository(),
);

final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  final tokenStorage = ref.watch(tokenStorageProvider);
  final authRepository = ref.watch(authRepositoryProvider);
  return AuthNotifier(tokenStorage, authRepository);
});

// ── State ──

enum AuthStatus {
  unknown,
  unauthenticated,
  needsRole,
  needsSellerInfo,
  buyerAuthenticated,
  sellerAuthenticated,
}

class AuthState {
  final AuthStatus status;
  final bool isLoading;
  final String? error;

  const AuthState({
    this.status = AuthStatus.unknown,
    this.isLoading = false,
    this.error,
  });

  AuthState copyWith({AuthStatus? status, bool? isLoading, String? error}) =>
      AuthState(
        status: status ?? this.status,
        isLoading: isLoading ?? this.isLoading,
        error: error,
      );
}

// ── Notifier ──

class AuthNotifier extends StateNotifier<AuthState> {
  final TokenStorage _tokenStorage;
  final AuthRepository _authRepository;

  AuthNotifier(this._tokenStorage, this._authRepository)
      : super(const AuthState());

  /// 스플래시에서 호출. 저장된 토큰 확인 → 상태 분기.
  Future<void> checkAuthStatus() async {
    state = state.copyWith(isLoading: true);

    final accessToken = await _tokenStorage.getAccessToken();
    if (accessToken == null) {
      state = state.copyWith(status: AuthStatus.unauthenticated, isLoading: false);
      return;
    }

    final role = await _tokenStorage.getRole();
    if (role == 'BUYER') {
      state = state.copyWith(status: AuthStatus.buyerAuthenticated, isLoading: false);
    } else if (role == 'SELLER') {
      state = state.copyWith(status: AuthStatus.sellerAuthenticated, isLoading: false);
    } else {
      state = state.copyWith(status: AuthStatus.needsRole, isLoading: false);
    }
  }

  /// 카카오 로그인. Mock에서는 항상 신규 유저 반환.
  Future<void> kakaoLogin() async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final result = await _authRepository.kakaoLogin('mock_kakao_token');
      await _tokenStorage.saveTokens(
        accessToken: result.accessToken,
        refreshToken: result.refreshToken,
      );
      if (result.role != null) {
        await _tokenStorage.saveRole(result.role!);
      }

      if (result.isNewUser || result.role == null) {
        state = state.copyWith(status: AuthStatus.needsRole, isLoading: false);
      } else if (result.role == 'BUYER') {
        state = state.copyWith(status: AuthStatus.buyerAuthenticated, isLoading: false);
      } else {
        state = state.copyWith(status: AuthStatus.sellerAuthenticated, isLoading: false);
      }
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }

  /// 역할 설정 (BUYER / SELLER).
  Future<void> setRole(String role) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      await _authRepository.setRole(role);
      await _tokenStorage.saveRole(role);

      if (role == 'BUYER') {
        state = state.copyWith(status: AuthStatus.buyerAuthenticated, isLoading: false);
      } else {
        state = state.copyWith(status: AuthStatus.needsSellerInfo, isLoading: false);
      }
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }

  /// 판매자 사업자 정보 등록.
  Future<void> registerSellerInfo({
    required String shopName,
    required String shopAddress,
    required double shopLat,
    required double shopLng,
    String? businessNumber,
  }) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      await _authRepository.registerSellerInfo(
        shopName: shopName,
        shopAddress: shopAddress,
        shopLat: shopLat,
        shopLng: shopLng,
        businessNumber: businessNumber,
      );
      state = state.copyWith(status: AuthStatus.sellerAuthenticated, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }

  /// 로그아웃. 토큰 삭제 + /login 이동.
  Future<void> logout() async {
    try {
      await _authRepository.logout();
    } catch (_) {
      // 로그아웃 API 실패해도 로컬 토큰은 삭제
    }
    await _tokenStorage.clearAll();
    state = const AuthState(status: AuthStatus.unauthenticated);
  }
}
