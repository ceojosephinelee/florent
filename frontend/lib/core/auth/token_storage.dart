/// 토큰 저장소 인터페이스.
/// 현재: MockTokenStorage (인메모리)
/// 나중에: SecureTokenStorage (flutter_secure_storage)
abstract class TokenStorage {
  Future<String?> getAccessToken();
  Future<String?> getRefreshToken();
  Future<String?> getRole();
  Future<bool> getHasFlowerShop();
  Future<void> saveTokens({required String accessToken, required String refreshToken});
  Future<void> saveRole(String role);
  Future<void> saveHasFlowerShop(bool value);
  Future<void> clearAll();
}
