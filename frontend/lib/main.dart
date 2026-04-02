import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:kakao_flutter_sdk_user/kakao_flutter_sdk_user.dart';

import 'core/router/app_router.dart';
import 'core/theme/app_theme.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  KakaoSdk.init(
    nativeAppKey: const String.fromEnvironment('KAKAO_NATIVE_KEY'),
  );

  FirebaseMessaging.onMessage.listen((RemoteMessage message) {
    print('[FCM] 포그라운드 메시지 수신: ${message.notification?.title} / ${message.notification?.body}');
  });

  runApp(const ProviderScope(child: FlorentApp()));
}

class FlorentApp extends StatelessWidget {
  const FlorentApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'Florent',
      theme: buyerTheme(),
      routerConfig: appRouter,
      debugShowCheckedModeBanner: false,
    );
  }
}
