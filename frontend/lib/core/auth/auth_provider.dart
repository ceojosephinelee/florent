import 'dart:developer' as dev;
import 'dart:io' show Platform;

import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

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

  /// 스플래시에서 호출. 저장된 토큰 확인 → 만료 검증 → 상태 분기.
  Future<void> checkAuthStatus() async {
    print('[AUTH] checkAuthStatus 호출됨');
    state = state.copyWith(isLoading: true);

    try {
      final accessToken = await _tokenStorage.getAccessToken();
      print('[AUTH] 저장된 accessToken: ${accessToken != null ? "있음(len=${accessToken.length})" : "null"}');

      if (accessToken == null) {
        print('[AUTH] → unauthenticated (토큰 없음)');
        state = state.copyWith(status: AuthStatus.unauthenticated, isLoading: false);
        return;
      }

      // accessToken 만료 여부를 로컬에서 확인
      bool accessExpired;
      try {
        accessExpired = JwtDecoder.isExpired(accessToken);
        print('[AUTH] accessToken 만료 여부: $accessExpired');
      } catch (e) {
        print('[AUTH] accessToken JWT 디코딩 실패: $e → unauthenticated');
        await _tokenStorage.clearAll();
        state = state.copyWith(status: AuthStatus.unauthenticated, isLoading: false);
        return;
      }

      if (accessExpired) {
        print('[AUTH] accessToken 만료 → refreshToken 확인');
        final refreshToken = await _tokenStorage.getRefreshToken();

        bool refreshExpired;
        if (refreshToken == null) {
          refreshExpired = true;
        } else {
          try {
            refreshExpired = JwtDecoder.isExpired(refreshToken);
          } catch (e) {
            print('[AUTH] refreshToken JWT 디코딩 실패: $e');
            refreshExpired = true;
          }
        }

        if (refreshExpired) {
          print('[AUTH] → unauthenticated (refreshToken 없거나 만료)');
          await _tokenStorage.clearAll();
          state = state.copyWith(status: AuthStatus.unauthenticated, isLoading: false);
          return;
        }

        try {
          print('[AUTH] POST /auth/reissue 호출');
          final result = await _authRepository.reissue(refreshToken!);
          await _tokenStorage.saveTokens(
            accessToken: result.accessToken,
            refreshToken: result.refreshToken,
          );
          if (result.role != null) {
            await _tokenStorage.saveRole(result.role!);
          }
          print('[AUTH] 토큰 재발급 성공 — role=${result.role}, hasFlowerShop=${result.hasFlowerShop}');

          final role = result.role ?? await _tokenStorage.getRole();
          if (role == 'BUYER') {
            state = state.copyWith(status: AuthStatus.buyerAuthenticated, isLoading: false);
            _registerFcmToken();
          } else if (role == 'SELLER' && !result.hasFlowerShop) {
            state = state.copyWith(status: AuthStatus.needsSellerInfo, isLoading: false);
          } else if (role == 'SELLER') {
            state = state.copyWith(status: AuthStatus.sellerAuthenticated, isLoading: false);
            _registerFcmToken();
          } else {
            state = state.copyWith(status: AuthStatus.needsRole, isLoading: false);
          }
          return;
        } catch (e) {
          print('[AUTH] 토큰 재발급 실패: $e → unauthenticated');
          await _tokenStorage.clearAll();
          state = state.copyWith(status: AuthStatus.unauthenticated, isLoading: false);
          return;
        }
      }

      final role = await _tokenStorage.getRole();
      final hasShop = await _tokenStorage.getHasFlowerShop();
      print('[AUTH] role=$role, hasFlowerShop=$hasShop → 상태 전환');
      if (role == 'BUYER') {
        state = state.copyWith(status: AuthStatus.buyerAuthenticated, isLoading: false);
        _registerFcmToken();
      } else if (role == 'SELLER' && !hasShop) {
        state = state.copyWith(status: AuthStatus.needsSellerInfo, isLoading: false);
      } else if (role == 'SELLER') {
        state = state.copyWith(status: AuthStatus.sellerAuthenticated, isLoading: false);
        _registerFcmToken();
      } else {
        state = state.copyWith(status: AuthStatus.needsRole, isLoading: false);
      }
    } catch (e, st) {
      print('[AUTH] checkAuthStatus 예외: $e');
      dev.log('[AUTH] checkAuthStatus 예외', error: e, stackTrace: st);
      await _tokenStorage.clearAll();
      state = state.copyWith(status: AuthStatus.unauthenticated, isLoading: false);
    }
  }

  /// 카카오 SDK 로그인 → 카카오 Access Token 획득 → 서버 전달.
  Future<void> kakaoLogin() async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      print('[AUTH] 1) 카카오 SDK 로그인 시작');
      final kakaoAccessToken = await _kakaoLoginService.login();
      print('[AUTH] 2) 카카오 토큰 획득 성공: ${kakaoAccessToken.substring(0, 10)}...');

      print('[AUTH] 3) POST /auth/kakao 호출');
      final result = await _authRepository.kakaoLogin(kakaoAccessToken);
      print('[AUTH] 4) 서버 응답 — isNewUser: ${result.isNewUser}, role: ${result.role}');

      await _tokenStorage.saveTokens(
        accessToken: result.accessToken,
        refreshToken: result.refreshToken,
      );
      if (result.role != null) {
        await _tokenStorage.saveRole(result.role!);
      }
      await _tokenStorage.saveHasFlowerShop(result.hasFlowerShop);
      // 저장 직후 재읽기 검증
      final savedToken = await _tokenStorage.getAccessToken();
      print('[AUTH] 5) 토큰 저장 완료 — 재읽기: ${savedToken != null ? "${savedToken.substring(0, 20)}... (len=${savedToken.length})" : "null ← 저장 실패!"}');
      print('[AUTH] 5) hasFlowerShop=${result.hasFlowerShop}');

      if (result.isNewUser || result.role == null) {
        print('[AUTH] 6) → needsRole');
        state = state.copyWith(status: AuthStatus.needsRole, isLoading: false);
      } else if (result.role == 'BUYER') {
        print('[AUTH] 6) → buyerAuthenticated');
        state = state.copyWith(status: AuthStatus.buyerAuthenticated, isLoading: false);
        _registerFcmToken();
      } else if (result.role == 'SELLER' && !result.hasFlowerShop) {
        print('[AUTH] 6) → needsSellerInfo (가게 미등록)');
        state = state.copyWith(status: AuthStatus.needsSellerInfo, isLoading: false);
      } else {
        print('[AUTH] 6) → sellerAuthenticated');
        state = state.copyWith(status: AuthStatus.sellerAuthenticated, isLoading: false);
        _registerFcmToken();
      }
    } catch (e, st) {
      print('[AUTH] kakaoLogin ERROR: $e\n$st');
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }

  /// 개발용 로그인. 카카오 SDK 없이 BUYER/SELLER로 즉시 로그인.
  Future<void> devLogin(String role) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final result = await _authRepository.devLogin(role);
      await _tokenStorage.saveTokens(
        accessToken: result.accessToken,
        refreshToken: result.refreshToken,
      );
      if (result.role != null) await _tokenStorage.saveRole(result.role!);
      await _tokenStorage.saveHasFlowerShop(result.hasFlowerShop);

      if (result.role == 'BUYER') {
        state = state.copyWith(status: AuthStatus.buyerAuthenticated, isLoading: false);
        _registerFcmToken();
      } else if (result.role == 'SELLER' && !result.hasFlowerShop) {
        state = state.copyWith(status: AuthStatus.needsSellerInfo, isLoading: false);
      } else {
        state = state.copyWith(status: AuthStatus.sellerAuthenticated, isLoading: false);
        _registerFcmToken();
      }
    } catch (e, st) {
      print('[AUTH] devLogin ERROR: $e\n$st');
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }

  /// 역할 설정 (BUYER / SELLER). 서버에서 새 토큰 반환.
  Future<void> setRole(String role) async {
    print('[AUTH] setRole($role) 시작');
    state = state.copyWith(isLoading: true, error: null);
    try {
      print('[AUTH] POST /auth/role 호출');
      final result = await _authRepository.setRole(role);
      print('[AUTH] 서버 응답: $result');
      await _tokenStorage.saveTokens(
        accessToken: result['accessToken'] as String,
        refreshToken: result['refreshToken'] as String,
      );
      final assignedRole = result['role'] as String? ?? role;
      await _tokenStorage.saveRole(assignedRole);

      if (assignedRole == 'BUYER') {
        print('[AUTH] → buyerAuthenticated');
        state = state.copyWith(status: AuthStatus.buyerAuthenticated, isLoading: false);
      } else {
        print('[AUTH] → needsSellerInfo');
        state = state.copyWith(status: AuthStatus.needsSellerInfo, isLoading: false);
      }
    } catch (e, st) {
      print('[AUTH] setRole ERROR: $e\n$st');
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
      await _tokenStorage.saveHasFlowerShop(true);
      state = state.copyWith(status: AuthStatus.sellerAuthenticated, isLoading: false);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }

  /// FCM 토큰을 서버에 등록.
  Future<void> _registerFcmToken() async {
    try {
      final messaging = FirebaseMessaging.instance;
      await messaging.requestPermission();
      final fcmToken = await messaging.getToken();
      if (fcmToken != null) {
        final platform = Platform.isIOS ? 'IOS' : 'ANDROID';
        await _authRepository.registerDevice(fcmToken, platform);
        print('[FCM] 토큰 등록 완료: platform=$platform');
      }
    } catch (e) {
      print('[FCM] 토큰 등록 실패 (무시): $e');
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
