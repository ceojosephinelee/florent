import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/auth/auth_provider.dart';
import '../../core/theme/colors.dart';
import '../../core/theme/typography.dart';
import '../providers/buyer_home_provider.dart';

class BuyerMyTabScreen extends ConsumerWidget {
  const BuyerMyTabScreen({super.key});

  static const _menuItems = [
    ('🔔', '알림 설정'),
    ('❓', '고객센터'),
    ('📄', '이용약관'),
  ];

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncProfile = ref.watch(buyerProfileProvider);
    final nickName = asyncProfile.whenOrNull(
          data: (p) => p['nickName'] as String?,
        ) ?? '';
    final email = asyncProfile.whenOrNull(
          data: (p) => p['email'] as String?,
        ) ?? '';

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          children: [
            const SizedBox(height: 24),
            Row(
              children: [
                Container(
                  width: 52, height: 52,
                  decoration: BoxDecoration(color: roseLt, shape: BoxShape.circle),
                  alignment: Alignment.center,
                  child: const Text('😊', style: TextStyle(fontSize: 22)),
                ),
                const SizedBox(width: 14),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        nickName.isNotEmpty ? nickName : '-',
                        style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w700),
                        overflow: TextOverflow.ellipsis,
                        maxLines: 1,
                      ),
                      const SizedBox(height: 2),
                      Text(
                        email.isNotEmpty ? email : '-',
                        style: AppTypography.body(fontSize: 11, color: ink60),
                        overflow: TextOverflow.ellipsis,
                        maxLines: 1,
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),
            Divider(color: borderColor, height: 1),
            const SizedBox(height: 8),
            ..._menuItems.map((item) => _MenuItem(emoji: item.$1, label: item.$2)),
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
                onTap: () => _showWithdrawDialog(context, ref),
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

void _showWithdrawDialog(BuildContext context, WidgetRef ref) {
  showDialog(
    context: context,
    builder: (ctx) => AlertDialog(
      title: const Text('회원 탈퇴'),
      content: const Text('탈퇴 시 모든 데이터가 삭제되며 복구할 수 없습니다.\n정말 탈퇴하시겠습니까?'),
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

class _MenuItem extends StatelessWidget {
  const _MenuItem({required this.emoji, required this.label});
  final String emoji;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 14),
      child: Row(
        children: [
          Text(emoji, style: const TextStyle(fontSize: 16)),
          const SizedBox(width: 12),
          Expanded(child: Text(label, style: AppTypography.body(fontSize: 14))),
          Text('›', style: AppTypography.body(fontSize: 18, color: ink30)),
        ],
      ),
    );
  }
}
