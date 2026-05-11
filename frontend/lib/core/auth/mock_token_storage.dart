import 'token_storage.dart';

class MockTokenStorage implements TokenStorage {
  final Map<String, String> _store = {};

  @override
  Future<String?> getAccessToken() async => _store['accessToken'];

  @override
  Future<String?> getRefreshToken() async => _store['refreshToken'];

  @override
  Future<String?> getRole() async => _store['role'];

  @override
  Future<bool> getHasFlowerShop() async => _store['hasFlowerShop'] == 'true';

  @override
  Future<void> saveTokens({required String accessToken, required String refreshToken}) async {
    _store['accessToken'] = accessToken;
    _store['refreshToken'] = refreshToken;
  }

  @override
  Future<void> saveRole(String role) async {
    _store['role'] = role;
  }

  @override
  Future<void> saveHasFlowerShop(bool value) async {
    _store['hasFlowerShop'] = value.toString();
  }

  @override
  Future<String?> getSelectedRole() async => _store['selectedRole'];

  @override
  Future<void> saveSelectedRole(String role) async {
    _store['selectedRole'] = role;
  }

  @override
  Future<void> clearSelectedRole() async {
    _store.remove('selectedRole');
  }

  @override
  Future<void> clearAll() async {
    _store.clear();
  }
}
