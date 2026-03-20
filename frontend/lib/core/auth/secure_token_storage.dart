import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import 'token_storage.dart';

class SecureTokenStorage implements TokenStorage {
  static const _storage = FlutterSecureStorage();
  static const _accessTokenKey = 'access_token';
  static const _refreshTokenKey = 'refresh_token';
  static const _roleKey = 'role';
  static const _hasFlowerShopKey = 'has_flower_shop';

  @override
  Future<String?> getAccessToken() async {
    final value = await _storage.read(key: _accessTokenKey);
    print('[STORAGE] getAccessToken: ${value != null ? "len=${value.length}" : "null"}');
    return value;
  }

  @override
  Future<String?> getRefreshToken() async {
    final value = await _storage.read(key: _refreshTokenKey);
    print('[STORAGE] getRefreshToken: ${value != null ? "len=${value.length}" : "null"}');
    return value;
  }

  @override
  Future<String?> getRole() async {
    final value = await _storage.read(key: _roleKey);
    print('[STORAGE] getRole: $value');
    return value;
  }

  @override
  Future<void> saveTokens({
    required String accessToken,
    required String refreshToken,
  }) async {
    print('[STORAGE] saveTokens — accessToken.len=${accessToken.length}, refreshToken.len=${refreshToken.length}');
    await _storage.write(key: _accessTokenKey, value: accessToken);
    await _storage.write(key: _refreshTokenKey, value: refreshToken);
    // 저장 직후 재읽기 검증
    final readBack = await _storage.read(key: _accessTokenKey);
    print('[STORAGE] saveTokens 검증 — 재읽기 len=${readBack?.length}, 일치=${readBack == accessToken}');
  }

  @override
  Future<bool> getHasFlowerShop() async {
    final value = await _storage.read(key: _hasFlowerShopKey);
    return value == 'true';
  }

  @override
  Future<void> saveRole(String role) =>
      _storage.write(key: _roleKey, value: role);

  @override
  Future<void> saveHasFlowerShop(bool value) =>
      _storage.write(key: _hasFlowerShopKey, value: value.toString());

  @override
  Future<void> clearAll() {
    print('[STORAGE] clearAll 호출됨');
    return _storage.deleteAll();
  }
}
