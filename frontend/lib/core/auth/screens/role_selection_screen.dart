import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../theme/colors.dart';
import '../../theme/radius.dart';
import '../../theme/typography.dart';
import '../auth_provider.dart';

class RoleSelectionScreen extends ConsumerWidget {
  const RoleSelectionScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 60),
              Text(
                'Florent',
                style: AppTypography.serif(
                  fontSize: 36,
                  fontWeight: FontWeight.w600,
                  color: roseColor,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                '어떤 서비스를 이용하시겠어요?',
                style: AppTypography.body(fontSize: 14, color: ink60),
              ),
              const SizedBox(height: 40),
              _RoleCard(
                emoji: '🌸',
                title: '꽃을 주문하고 싶어요',
                description: '원하는 꽃을 요청하고\n근처 플로리스트의 맞춤 제안을 받아보세요.',
                accentColor: roseColor,
                bgColor: roseLt,
                onTap: () async {
                  await ref.read(authProvider.notifier).selectRole('BUYER');
                  if (context.mounted) context.go('/login');
                },
              ),
              const SizedBox(height: 16),
              _RoleCard(
                emoji: '🌿',
                title: '꽃집을 운영하고 있어요',
                description: '주변 고객의 요청을 받고\n나만의 큐레이션 제안서를 보내보세요.',
                accentColor: sageColor,
                bgColor: sageLt,
                onTap: () async {
                  await ref.read(authProvider.notifier).selectRole('SELLER');
                  if (context.mounted) context.go('/login');
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _RoleCard extends StatelessWidget {
  const _RoleCard({
    required this.emoji,
    required this.title,
    required this.description,
    required this.accentColor,
    required this.bgColor,
    required this.onTap,
  });

  final String emoji;
  final String title;
  final String description;
  final Color accentColor;
  final Color bgColor;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      onTap: onTap,
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          color: whiteColor,
          borderRadius: kBorderRadiusLg,
          border: Border.all(color: borderColor, width: 1.5),
        ),
        child: Row(
          children: [
            Container(
              width: 52,
              height: 52,
              decoration: BoxDecoration(color: bgColor, borderRadius: kBorderRadiusMd),
              alignment: Alignment.center,
              child: Text(emoji, style: const TextStyle(fontSize: 24)),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w700, color: accentColor),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    description,
                    style: AppTypography.body(fontSize: 12, color: ink60, height: 1.5),
                  ),
                ],
              ),
            ),
            Icon(Icons.chevron_right, color: ink30, size: 20),
          ],
        ),
      ),
    );
  }
}
