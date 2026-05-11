import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../theme/colors.dart';
import '../../theme/radius.dart';
import '../../theme/typography.dart';
import '../auth_provider.dart';

class EmailLoginScreen extends ConsumerStatefulWidget {
  const EmailLoginScreen({super.key});

  @override
  ConsumerState<EmailLoginScreen> createState() => _EmailLoginScreenState();
}

class _EmailLoginScreenState extends ConsumerState<EmailLoginScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final auth = ref.watch(authProvider);

    ref.listen<AuthState>(authProvider, (prev, next) {
      if (next.isLoading) return;
      switch (next.status) {
        case AuthStatus.buyerAuthenticated:
          context.go('/buyer/home');
        case AuthStatus.needsSellerInfo:
          context.go('/auth/seller-info');
        case AuthStatus.sellerAuthenticated:
          context.go('/seller/home');
        default:
          break;
      }
    });

    return Scaffold(
      backgroundColor: creamColor,
      appBar: AppBar(
        backgroundColor: creamColor,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: inkColor),
          onPressed: () => context.go('/login'),
        ),
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 16),
              Text(
                '이메일 로그인',
                style: AppTypography.serif(fontSize: 28, fontWeight: FontWeight.w600),
              ),
              const SizedBox(height: 32),
              TextField(
                controller: _emailController,
                keyboardType: TextInputType.emailAddress,
                decoration: InputDecoration(
                  labelText: '이메일',
                  labelStyle: AppTypography.body(fontSize: 14, color: ink60),
                  border: OutlineInputBorder(borderRadius: kBorderRadiusMd),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: kBorderRadiusMd,
                    borderSide: BorderSide(color: borderColor),
                  ),
                ),
              ),
              const SizedBox(height: 16),
              TextField(
                controller: _passwordController,
                obscureText: true,
                decoration: InputDecoration(
                  labelText: '비밀번호',
                  labelStyle: AppTypography.body(fontSize: 14, color: ink60),
                  border: OutlineInputBorder(borderRadius: kBorderRadiusMd),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: kBorderRadiusMd,
                    borderSide: BorderSide(color: borderColor),
                  ),
                ),
              ),
              if (auth.error != null) ...[
                const SizedBox(height: 16),
                Text(
                  auth.error!,
                  style: AppTypography.body(fontSize: 12, color: const Color(0xFFC62828)),
                ),
              ],
              const SizedBox(height: 24),
              SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton(
                  onPressed: auth.isLoading
                      ? null
                      : () {
                          final email = _emailController.text.trim();
                          final password = _passwordController.text;
                          if (email.isEmpty || password.isEmpty) return;
                          ref.read(authProvider.notifier).emailLogin(email, password);
                        },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: roseColor,
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(borderRadius: kBorderRadiusMd),
                    elevation: 0,
                  ),
                  child: auth.isLoading
                      ? const SizedBox(
                          width: 20, height: 20,
                          child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                        )
                      : Text('로그인', style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w600, color: Colors.white)),
                ),
              ),
              const SizedBox(height: 16),
              Center(
                child: GestureDetector(
                  onTap: () => context.go('/auth/email-signup'),
                  child: Text(
                    '계정이 없으신가요? 회원가입',
                    style: AppTypography.body(fontSize: 13, color: roseColor),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
