import 'package:flutter/material.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/typography.dart';

class BuyerMyTabScreen extends StatelessWidget {
  const BuyerMyTabScreen({super.key});

  static const _menuItems = [
    ('📋', '내 요청 내역'),
    ('🔔', '알림 설정'),
    ('📍', '주소 관리'),
    ('❓', '고객센터'),
    ('📄', '이용약관'),
  ];

  @override
  Widget build(BuildContext context) {
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
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('이지수', style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w700)),
                    const SizedBox(height: 2),
                    Text('ji.soo@email.com', style: AppTypography.body(fontSize: 11, color: ink60)),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 24),
            Divider(color: borderColor, height: 1),
            const SizedBox(height: 8),
            ..._menuItems.map((item) => _MenuItem(emoji: item.$1, label: item.$2)),
          ],
        ),
      ),
    );
  }
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
