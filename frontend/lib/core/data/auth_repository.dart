import '../models/auth_models.dart';

/// 인증 Repository 인터페이스.
/// 현재: MockAuthRepository
/// 나중에: ApiAuthRepository (Dio)
abstract class AuthRepository {
  Future<KakaoLoginResult> kakaoLogin(String kakaoAccessToken);
  Future<String> setRole(String role);
  Future<ReissueResult> reissue(String refreshToken);
  Future<void> logout();
  Future<SellerInfoResult> registerSellerInfo({
    required String shopName,
    required String shopAddress,
    required double shopLat,
    required double shopLng,
    String? businessNumber,
  });
}
