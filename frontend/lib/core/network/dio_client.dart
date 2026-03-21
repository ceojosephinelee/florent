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
      defaultValue: 'http://13.239.23.33:8080/api/v1',
    ),
    connectTimeout: const Duration(seconds: 10),
    receiveTimeout: const Duration(seconds: 10),
    headers: {'Content-Type': 'application/json'},
  ));

  print('[DIO] Dio 인스턴스 생성 — baseUrl: ${dio.options.baseUrl}');

  dio.interceptors.add(InterceptorsWrapper(
    onRequest: (options, handler) async {
      final token = await tokenStorage.getAccessToken();
      if (token != null) {
        options.headers['Authorization'] = 'Bearer $token';
        print('[DIO] → ${options.method} ${options.uri} (token: ${token.substring(0, 20)}...)');
      } else {
        print('[DIO] → ${options.method} ${options.uri} (token: NULL — 헤더 미주입)');
      }
      handler.next(options);
    },
    onResponse: (response, handler) {
      print('[DIO] ← ${response.statusCode} ${response.requestOptions.method} ${response.requestOptions.uri}');
      handler.next(response);
    },
    onError: (error, handler) async {
      print('[DIO] ← ERROR ${error.response?.statusCode} '
          '${error.requestOptions.method} ${error.requestOptions.uri}');
      print('[DIO]   type=${error.type} message=${error.message}');
      print('[DIO]   response.data=${error.response?.data}');

      if (error.response?.statusCode == 401) {
        final data = error.response?.data;
        String? errorCode;
        if (data is Map) {
          final errorMap = data['error'];
          if (errorMap is Map) {
            errorCode = errorMap['code'] as String?;
          }
        }
        print('[DIO]   401 errorCode=$errorCode');

        if (errorCode == 'TOKEN_EXPIRED') {
          final refreshToken = await tokenStorage.getRefreshToken();
          if (refreshToken != null) {
            try {
              print('[DIO]   토큰 재발급 시도...');
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
              if (result.role != null) {
                await tokenStorage.saveRole(result.role!);
              }
              await tokenStorage.saveHasFlowerShop(result.hasFlowerShop);
              print('[DIO]   토큰 재발급 성공 → 원래 요청 재시도');
              final opts = error.requestOptions;
              opts.headers['Authorization'] = 'Bearer ${result.accessToken}';
              final response = await dio.fetch(opts);
              return handler.resolve(response);
            } catch (e) {
              print('[DIO]   토큰 재발급 실패: $e → /login');
              await tokenStorage.clearAll();
              appRouter.go('/login');
              return handler.next(error);
            }
          }
        }

        if (errorCode == 'REFRESH_TOKEN_EXPIRED') {
          print('[DIO]   refreshToken 만료 → /login');
          await tokenStorage.clearAll();
          appRouter.go('/login');
          return handler.next(error);
        }
      }
      handler.next(error);
    },
  ));

  return dio;
});
