import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../theme/colors.dart';
import '../../theme/radius.dart';
import '../../theme/typography.dart';
import '../auth_provider.dart';

class EmailSignupScreen extends ConsumerStatefulWidget {
  const EmailSignupScreen({super.key});

  @override
  ConsumerState<EmailSignupScreen> createState() => _EmailSignupScreenState();
}

class _EmailSignupScreenState extends ConsumerState<EmailSignupScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();
  final _nicknameController = TextEditingController();
  String? _localError;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    _nicknameController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final auth = ref.watch(authProvider);

    ref.listen<AuthState>(authProvider, (prev, next) {
      if (next.isLoading) return;
      if (next.status == AuthStatus.needsRole) {
        context.go('/auth/role');
      }
    });

    return Scaffold(
      backgroundColor: creamColor,
      appBar: AppBar(
        backgroundColor: creamColor,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: inkColor),
          onPressed: () => context.go('/auth/email-login'),
        ),
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 16),
              Text(
                '회원가입',
                style: AppTypography.serif(fontSize: 28, fontWeight: FontWeight.w600),
              ),
              const SizedBox(height: 32),
              TextField(
                controller: _nicknameController,
                decoration: InputDecoration(
                  labelText: '닉네임',
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
                  labelText: '비밀번호 (8자 이상)',
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
                controller: _confirmPasswordController,
                obscureText: true,
                decoration: InputDecoration(
                  labelText: '비밀번호 확인',
                  labelStyle: AppTypography.body(fontSize: 14, color: ink60),
                  border: OutlineInputBorder(borderRadius: kBorderRadiusMd),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: kBorderRadiusMd,
                    borderSide: BorderSide(color: borderColor),
                  ),
                ),
              ),
              if (_localError != null || auth.error != null) ...[
                const SizedBox(height: 16),
                Text(
                  _localError ?? auth.error!,
                  style: AppTypography.body(fontSize: 12, color: const Color(0xFFC62828)),
                ),
              ],
              const SizedBox(height: 24),
              SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton(
                  onPressed: auth.isLoading ? null : _submit,
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
                      : Text('가입하기', style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w600, color: Colors.white)),
                ),
              ),
              const SizedBox(height: 32),
            ],
          ),
        ),
      ),
    );
  }

  void _submit() {
    setState(() => _localError = null);

    final nickname = _nicknameController.text.trim();
    final email = _emailController.text.trim();
    final password = _passwordController.text;
    final confirm = _confirmPasswordController.text;

    if (nickname.isEmpty || email.isEmpty || password.isEmpty) {
      setState(() => _localError = '모든 항목을 입력해주세요.');
      return;
    }
    if (password.length < 8) {
      setState(() => _localError = '비밀번호는 8자 이상이어야 합니다.');
      return;
    }
    if (password != confirm) {
      setState(() => _localError = '비밀번호가 일치하지 않습니다.');
      return;
    }

    ref.read(authProvider.notifier).emailSignup(email, password, nickname);
  }
}
