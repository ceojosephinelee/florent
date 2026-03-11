import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../theme/colors.dart';
import '../../theme/radius.dart';
import '../../theme/typography.dart';
import '../auth_provider.dart';

const _kakaoYellow = Color(0xFFFEE500);
const _kakaoBrown = Color(0xFF3C1E1E);

class LoginScreen extends ConsumerWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final auth = ref.watch(authProvider);

    ref.listen<AuthState>(authProvider, (prev, next) {
      if (next.isLoading) return;
      switch (next.status) {
        case AuthStatus.needsRole:
          context.go('/auth/role');
        case AuthStatus.buyerAuthenticated:
          context.go('/buyer/home');
        case AuthStatus.sellerAuthenticated:
          context.go('/seller/home');
        default:
          break;
      }
    });

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            children: [
              const Spacer(flex: 3),
              Text(
                'Florent',
                style: AppTypography.serif(
                  fontSize: 48,
                  fontWeight: FontWeight.w600,
                  color: roseColor,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                '나만의 플로리스트 큐레이션',
                style: AppTypography.body(fontSize: 14, color: ink60),
              ),
              const SizedBox(height: 16),
              const Text('🌸', style: TextStyle(fontSize: 48)),
              const Spacer(flex: 4),
              SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton(
                  onPressed: auth.isLoading
                      ? null
                      : () => ref.read(authProvider.notifier).kakaoLogin(),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: _kakaoYellow,
                    disabledBackgroundColor: _kakaoYellow.withValues(alpha: 0.5),
                    foregroundColor: _kakaoBrown,
                    shape: RoundedRectangleBorder(borderRadius: kBorderRadiusMd),
                    elevation: 0,
                  ),
                  child: auth.isLoading
                      ? SizedBox(
                          width: 20,
                          height: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            color: _kakaoBrown,
                          ),
                        )
                      : Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const Text('💬', style: TextStyle(fontSize: 18)),
                            const SizedBox(width: 8),
                            Text(
                              '카카오로 시작하기',
                              style: AppTypography.body(
                                fontSize: 15,
                                fontWeight: FontWeight.w600,
                                color: _kakaoBrown,
                              ),
                            ),
                          ],
                        ),
                ),
              ),
              const SizedBox(height: 16),
              Text(
                '로그인하면 서비스 이용약관에 동의하게 됩니다.',
                style: AppTypography.body(fontSize: 11, color: ink30),
              ),
              const SizedBox(height: 32),
            ],
          ),
        ),
      ),
    );
  }
}
