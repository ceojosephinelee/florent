import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:kakao_flutter_sdk_user/kakao_flutter_sdk_user.dart';

import 'buyer/providers/buyer_home_provider.dart';
import 'buyer/providers/buyer_request_provider.dart';
import 'buyer/providers/proposal_provider.dart';
import 'core/router/app_router.dart';
import 'core/theme/app_theme.dart';
import 'seller/providers/seller_providers.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  KakaoSdk.init(
    nativeAppKey: const String.fromEnvironment('KAKAO_NATIVE_KEY'),
  );

  runApp(const ProviderScope(child: FlorentApp()));
}

class FlorentApp extends ConsumerStatefulWidget {
  const FlorentApp({super.key});

  @override
  ConsumerState<FlorentApp> createState() => _FlorentAppState();
}

class _FlorentAppState extends ConsumerState<FlorentApp> {
  @override
  void initState() {
    super.initState();
    _setupFcmListeners();
  }

  void _setupFcmListeners() {
    // 포그라운드 메시지 수신 → provider invalidate
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      print('[FCM] 포그라운드 메시지 수신: ${message.notification?.title} / ${message.notification?.body}');
      _refreshProviders();
    });

    // 백그라운드에서 알림 탭 → 앱 복귀
    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      print('[FCM] 백그라운드 알림 탭: ${message.notification?.title}');
      _refreshProviders();
      _navigateByMessage(message);
    });

    // terminated 상태에서 알림 탭으로 앱 시작
    FirebaseMessaging.instance.getInitialMessage().then((message) {
      if (message != null) {
        print('[FCM] 초기 메시지(terminated → open): ${message.notification?.title}');
        _refreshProviders();
        _navigateByMessage(message);
      }
    });
  }

  void _refreshProviders() {
    // 판매자
    ref.invalidate(sellerNotificationsProvider);
    ref.invalidate(sellerHomeProvider);
    ref.invalidate(sellerRecentRequestsProvider);
    ref.invalidate(sellerRequestsProvider);

    // 구매자
    ref.invalidate(buyerNotificationsProvider);
    ref.invalidate(buyerReservationsListProvider);
    ref.invalidate(allBuyerRequestsProvider);
    ref.invalidate(activeRequestsProvider);
    ref.invalidate(buyerProfileProvider);
  }

  void _navigateByMessage(RemoteMessage message) {
    final data = message.data;
    final type = data['type'];
    final referenceId = data['referenceId'];
    if (type == null || referenceId == null) return;

    Future.delayed(const Duration(milliseconds: 300), () {
      if (!mounted) return;
      switch (type) {
        case 'REQUEST_ARRIVED':
          appRouter.push('/seller/requests/$referenceId');
        case 'PROPOSAL_ARRIVED':
          appRouter.push('/buyer/proposals/$referenceId');
        case 'RESERVATION_CONFIRMED':
          appRouter.push('/buyer/reservations/$referenceId');
      }
    });
  }

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
