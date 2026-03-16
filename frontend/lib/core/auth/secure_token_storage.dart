import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import 'token_storage.dart';

class SecureTokenStorage implements TokenStorage {
  static const _storage = FlutterSecureStorage();
  static const _accessTokenKey = 'access_token';
  static const _refreshTokenKey = 'refresh_token';
  static const _roleKey = 'role';

  @override
  Future<String?> getAccessToken() => _storage.read(key: _accessTokenKey);

  @override
  Future<String?> getRefreshToken() => _storage.read(key: _refreshTokenKey);

  @override
  Future<String?> getRole() => _storage.read(key: _roleKey);

  @override
  Future<void> saveTokens({
    required String accessToken,
    required String refreshToken,
  }) async {
    await _storage.write(key: _accessTokenKey, value: accessToken);
    await _storage.write(key: _refreshTokenKey, value: refreshToken);
  }

  @override
  Future<void> saveRole(String role) =>
      _storage.write(key: _roleKey, value: role);

  @override
  Future<void> clearAll() => _storage.deleteAll();
}
