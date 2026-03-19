import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../theme/colors.dart';
import '../../theme/typography.dart';
import '../auth_provider.dart';

class SplashScreen extends ConsumerStatefulWidget {
  const SplashScreen({super.key});

  @override
  ConsumerState<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends ConsumerState<SplashScreen> {
  @override
  void initState() {
    super.initState();
    print('[SPLASH] initState 호출됨');
    _checkAuth();
  }

  Future<void> _checkAuth() async {
    print('[SPLASH] _checkAuth 시작 — 1.5초 대기');
    await Future.delayed(const Duration(milliseconds: 1500));
    if (!mounted) {
      print('[SPLASH] mounted=false → checkAuthStatus 건너뜀');
      return;
    }

    print('[SPLASH] checkAuthStatus 호출 직전');
    await ref.read(authProvider.notifier).checkAuthStatus();
    print('[SPLASH] checkAuthStatus 완료 — status: ${ref.read(authProvider).status}');
  }

  @override
  Widget build(BuildContext context) {
    ref.listen<AuthState>(authProvider, (prev, next) {
      if (next.isLoading) return;
      switch (next.status) {
        case AuthStatus.unauthenticated:
          context.go('/login');
        case AuthStatus.needsRole:
          context.go('/auth/role');
        case AuthStatus.needsSellerInfo:
          context.go('/auth/seller-info');
        case AuthStatus.buyerAuthenticated:
          context.go('/buyer/home');
        case AuthStatus.sellerAuthenticated:
          context.go('/seller/home');
        case AuthStatus.unknown:
          break;
      }
    });

    return Scaffold(
      backgroundColor: creamColor,
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Florent',
              style: AppTypography.serif(
                fontSize: 42,
                fontWeight: FontWeight.w600,
                color: roseColor,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              '나만의 플로리스트',
              style: AppTypography.body(fontSize: 14, color: ink60),
            ),
          ],
        ),
      ),
    );
  }
}
