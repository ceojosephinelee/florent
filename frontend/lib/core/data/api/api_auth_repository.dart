import 'package:dio/dio.dart';

import '../../models/auth_models.dart';
import '../auth_repository.dart';

class ApiAuthRepository implements AuthRepository {
  final Dio _dio;

  ApiAuthRepository(this._dio);

  @override
  Future<KakaoLoginResult> kakaoLogin(String kakaoAccessToken) async {
    final response = await _dio.post(
      '/auth/kakao',
      data: {'kakaoAccessToken': kakaoAccessToken},
    );
    final data = response.data['data'] as Map<String, dynamic>;
    print('[API] kakaoLogin — accessToken.len=${(data['accessToken'] as String).length}');
    return KakaoLoginResult.fromJson(data);
  }

  @override
  Future<Map<String, dynamic>> setRole(String role) async {
    final response = await _dio.post('/auth/role', data: {'role': role});
    return response.data['data'] as Map<String, dynamic>;
  }

  @override
  Future<ReissueResult> reissue(String refreshToken) async {
    // 인터셉터 무한 루프 방지: 별도 Dio 인스턴스 사용
    final plainDio = Dio(BaseOptions(
      baseUrl: _dio.options.baseUrl,
      headers: {'Content-Type': 'application/json'},
    ));
    final response = await plainDio.post(
      '/auth/reissue',
      data: {'refreshToken': refreshToken},
    );
    return ReissueResult.fromJson(
      response.data['data'] as Map<String, dynamic>,
    );
  }

  @override
  Future<KakaoLoginResult> devLogin(String role) async {
    final response = await _dio.post('/auth/dev-login', data: {'role': role});
    final data = response.data['data'] as Map<String, dynamic>;
    return KakaoLoginResult.fromJson(data);
  }

  @override
  Future<void> logout() async {
    await _dio.post('/auth/logout');
  }

  @override
  Future<void> registerDevice(String fcmToken, String platform) async {
    await _dio.post('/devices', data: {
      'fcmToken': fcmToken,
      'platform': platform,
    });
    print('[API] registerDevice — platform=$platform');
  }

  @override
  Future<SellerInfoResult> registerSellerInfo({
    required String shopName,
    required String shopAddress,
    required double shopLat,
    required double shopLng,
    String? businessNumber,
  }) async {
    final response = await _dio.post('/auth/seller-info', data: {
      'shopName': shopName,
      'shopAddress': shopAddress,
      'shopLat': shopLat,
      'shopLng': shopLng,
      if (businessNumber != null) 'businessNumber': businessNumber,
    });
    return SellerInfoResult.fromJson(
      response.data['data'] as Map<String, dynamic>,
    );
  }
}
