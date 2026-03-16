import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../auth/auth_provider.dart';
import '../models/auth_models.dart';
import '../router/app_router.dart';

/// Dio 인스턴스 Provider.
/// - Authorization 헤더 자동 주입
/// - 401 TOKEN_EXPIRED → reissue → 원래 요청 재시도
/// - 401 REFRESH_TOKEN_EXPIRED → 토큰 삭제 + /login 리다이렉트
final dioProvider = Provider<Dio>((ref) {
  final tokenStorage = ref.watch(tokenStorageProvider);

  final dio = Dio(BaseOptions(
    baseUrl: const String.fromEnvironment(
      'API_BASE_URL',
      defaultValue: 'http://localhost:8080/api/v1',
    ),
    connectTimeout: const Duration(seconds: 10),
    receiveTimeout: const Duration(seconds: 10),
    headers: {'Content-Type': 'application/json'},
  ));

  dio.interceptors.add(InterceptorsWrapper(
    onRequest: (options, handler) async {
      final token = await tokenStorage.getAccessToken();
      if (token != null) {
        options.headers['Authorization'] = 'Bearer $token';
      }
      handler.next(options);
    },
    onError: (error, handler) async {
      if (error.response?.statusCode == 401) {
        final data = error.response?.data;
        String? errorCode;
        if (data is Map) {
          final errorMap = data['error'];
          if (errorMap is Map) {
            errorCode = errorMap['code'] as String?;
          }
        }

        if (errorCode == 'TOKEN_EXPIRED') {
          final refreshToken = await tokenStorage.getRefreshToken();
          if (refreshToken != null) {
            try {
              // Use a plain Dio to avoid interceptor loop
              final plainDio = Dio(BaseOptions(baseUrl: dio.options.baseUrl));
              final res = await plainDio.post(
                '/auth/reissue',
                data: {'refreshToken': refreshToken},
              );
              final result = ReissueResult.fromJson(
                res.data['data'] as Map<String, dynamic>,
              );
              await tokenStorage.saveTokens(
                accessToken: result.accessToken,
                refreshToken: result.refreshToken,
              );
              final opts = error.requestOptions;
              opts.headers['Authorization'] = 'Bearer ${result.accessToken}';
              final response = await dio.fetch(opts);
              return handler.resolve(response);
            } catch (_) {
              await tokenStorage.clearAll();
              appRouter.go('/login');
              return handler.next(error);
            }
          }
        }

        if (errorCode == 'REFRESH_TOKEN_EXPIRED') {
          await tokenStorage.clearAll();
          appRouter.go('/login');
        }
      }
      handler.next(error);
    },
  ));

  return dio;
});
