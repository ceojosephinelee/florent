import '../../models/auth_models.dart';
import '../auth_repository.dart';

class MockAuthRepository implements AuthRepository {
  @override
  Future<KakaoLoginResult> kakaoLogin(String kakaoAccessToken) async {
    await Future.delayed(const Duration(milliseconds: 500));
    // Mock: 항상 신규 유저로 반환 → 역할 선택 플로우 테스트
    return const KakaoLoginResult(
      accessToken: 'mock_access_token_abc123',
      refreshToken: 'mock_refresh_token_xyz789',
      role: null,
      isNewUser: true,
    );
  }

  @override
  Future<String> setRole(String role) async {
    await Future.delayed(const Duration(milliseconds: 300));
    return role;
  }

  @override
  Future<ReissueResult> reissue(String refreshToken) async {
    await Future.delayed(const Duration(milliseconds: 300));
    return const ReissueResult(
      accessToken: 'mock_new_access_token',
      refreshToken: 'mock_new_refresh_token',
    );
  }

  @override
  Future<void> logout() async {
    await Future.delayed(const Duration(milliseconds: 300));
  }

  @override
  Future<SellerInfoResult> registerSellerInfo({
    required String shopName,
    required String shopAddress,
    required double shopLat,
    required double shopLng,
    String? businessNumber,
  }) async {
    await Future.delayed(const Duration(milliseconds: 500));
    return SellerInfoResult(sellerId: 1, shopName: shopName);
  }
}
