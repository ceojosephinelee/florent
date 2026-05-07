import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/auth/auth_provider.dart';
import '../../core/theme/colors.dart';
import '../../core/theme/typography.dart';
import '../providers/seller_providers.dart';

const _sage = Color(0xFF5A7A68);
const _sageLt = Color(0xFFE8F0EC);

void _showSellerWithdrawDialog(BuildContext context, WidgetRef ref) {
  showDialog(
    context: context,
    builder: (ctx) => AlertDialog(
      title: const Text('회원 탈퇴'),
      content: const Text('탈퇴 시 가게 정보와 모든 데이터가 삭제되며 복구할 수 없습니다.\n정말 탈퇴하시겠습니까?'),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(ctx).pop(),
          child: const Text('취소'),
        ),
        TextButton(
          onPressed: () async {
            Navigator.of(ctx).pop();
            try {
              await ref.read(authProvider.notifier).withdraw();
              if (context.mounted) {
                context.go('/login');
              }
            } catch (e) {
              if (context.mounted) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('탈퇴 처리 중 오류가 발생했습니다.')),
                );
              }
            }
          },
          style: TextButton.styleFrom(foregroundColor: Colors.red),
          child: const Text('탈퇴'),
        ),
      ],
    ),
  );
}

class SellerMyTabScreen extends ConsumerWidget {
  const SellerMyTabScreen({super.key});

  static const _menuItems = [
    ('🏪', '가게 정보 관리'),
    ('🔔', '알림 설정'),
    ('❓', '고객센터'),
    ('📄', '이용약관'),
  ];

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncProfile = ref.watch(sellerProfileProvider);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          children: [
            const SizedBox(height: 24),
            asyncProfile.when(
              loading: () => const Center(child: CircularProgressIndicator(strokeWidth: 2)),
              error: (_, __) => Padding(
                padding: const EdgeInsets.symmetric(vertical: 8),
                child: Row(
                  children: [
                    Container(
                      width: 52, height: 52,
                      decoration: const BoxDecoration(color: _sageLt, shape: BoxShape.circle),
                      alignment: Alignment.center,
                      child: const Text('😢', style: TextStyle(fontSize: 22)),
                    ),
                    const SizedBox(width: 14),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text('프로필을 불러올 수 없어요', style: AppTypography.body(fontSize: 13, color: ink60)),
                          const SizedBox(height: 4),
                          GestureDetector(
                            onTap: () => ref.invalidate(sellerProfileProvider),
                            child: Text('다시 시도', style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: _sage)),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              data: (profile) => Row(
                children: [
                  Container(
                    width: 52, height: 52,
                    decoration: const BoxDecoration(color: _sageLt, shape: BoxShape.circle),
                    alignment: Alignment.center,
                    child: const Text('🌸', style: TextStyle(fontSize: 22)),
                  ),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          profile['shopName'] as String? ?? '',
                          style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w700),
                        ),
                        const SizedBox(height: 2),
                        Text(
                          profile['shopAddress'] as String? ?? '',
                          style: AppTypography.body(fontSize: 11, color: ink60),
                        ),
                        const SizedBox(height: 4),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                          decoration: BoxDecoration(color: _sageLt, borderRadius: BorderRadius.circular(4)),
                          child: Text('신뢰도 --점', style: AppTypography.mono(fontSize: 10, fontWeight: FontWeight.w500, color: _sage)),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            Divider(color: borderColor, height: 1),
            const SizedBox(height: 8),
            ..._menuItems.map((item) => Padding(
              padding: const EdgeInsets.symmetric(vertical: 14),
              child: Row(
                children: [
                  Text(item.$1, style: const TextStyle(fontSize: 16)),
                  const SizedBox(width: 12),
                  Expanded(child: Text(item.$2, style: AppTypography.body(fontSize: 14))),
                  Text('›', style: AppTypography.body(fontSize: 18, color: ink30)),
                ],
              ),
            )),
            const SizedBox(height: 24),
            Divider(color: borderColor, height: 1),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              height: 48,
              child: OutlinedButton(
                onPressed: () async {
                  await ref.read(authProvider.notifier).logout();
                  if (context.mounted) {
                    context.go('/login');
                  }
                },
                style: OutlinedButton.styleFrom(
                  foregroundColor: ink60,
                  side: BorderSide(color: borderColor),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                child: Text(
                  '로그아웃',
                  style: AppTypography.body(fontSize: 14, color: ink60),
                ),
              ),
            ),
            const SizedBox(height: 12),
            Center(
              child: GestureDetector(
                onTap: () => _showSellerWithdrawDialog(context, ref),
                child: Text(
                  '회원 탈퇴',
                  style: AppTypography.body(fontSize: 12, color: ink30),
                ),
              ),
            ),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }
}
