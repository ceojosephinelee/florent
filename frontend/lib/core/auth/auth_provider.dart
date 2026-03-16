import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../data/api/api_auth_repository.dart';
import '../data/auth_repository.dart';
import '../network/dio_client.dart';
import 'kakao_login_service.dart';
import 'secure_token_storage.dart';
import 'token_storage.dart';

// ── Providers ──

final tokenStorageProvider = Provider<TokenStorage>(
  (ref) => SecureTokenStorage(),
);

final authRepositoryProvider = Provider<AuthRepository>(
  (ref) => ApiAuthRepository(ref.watch(dioProvider)),
);

final kakaoLoginServiceProvider = Provider<KakaoLoginService>(
  (ref) => KakaoLoginService(),
);

final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  final tokenStorage = ref.watch(tokenStorageProvider);
  final authRepository = ref.watch(authRepositoryProvider);
  final kakaoLoginService = ref.watch(kakaoLoginServiceProvider);
  return AuthNotifier(tokenStorage, authRepository, kakaoLoginService);
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
  final KakaoLoginService _kakaoLoginService;

  AuthNotifier(this._tokenStorage, this._authRepository, this._kakaoLoginService)
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

  /// 카카오 SDK 로그인 → 카카오 Access Token 획득 → 서버 전달.
  Future<void> kakaoLogin() async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final kakaoAccessToken = await _kakaoLoginService.login();
      final result = await _authRepository.kakaoLogin(kakaoAccessToken);
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

  /// 역할 설정 (BUYER / SELLER). 서버에서 새 토큰 반환.
  Future<void> setRole(String role) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final result = await _authRepository.setRole(role);
      await _tokenStorage.saveTokens(
        accessToken: result['accessToken'] as String,
        refreshToken: result['refreshToken'] as String,
      );
      final assignedRole = result['role'] as String? ?? role;
      await _tokenStorage.saveRole(assignedRole);

      if (assignedRole == 'BUYER') {
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
