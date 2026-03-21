import '../models/auth_models.dart';

/// 인증 Repository 인터페이스.
abstract class AuthRepository {
  Future<KakaoLoginResult> kakaoLogin(String kakaoAccessToken);
  Future<Map<String, dynamic>> setRole(String role);
  Future<ReissueResult> reissue(String refreshToken);
  Future<void> logout();
  Future<KakaoLoginResult> devLogin(String role);
  Future<SellerInfoResult> registerSellerInfo({
    required String shopName,
    required String shopAddress,
    required double shopLat,
    required double shopLng,
    String? businessNumber,
  });
  Future<void> registerDevice(String fcmToken, String platform);
}
