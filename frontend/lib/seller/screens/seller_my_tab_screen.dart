import 'package:flutter/material.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/typography.dart';

const _sage = Color(0xFF5A7A68);
const _sageLt = Color(0xFFE8F0EC);

class SellerMyTabScreen extends StatelessWidget {
  const SellerMyTabScreen({super.key});

  static const _menuItems = [
    ('🏪', '가게 정보 관리'),
    ('🕐', '영업시간 설정'),
    ('🔔', '알림 설정'),
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
                  decoration: const BoxDecoration(color: _sageLt, shape: BoxShape.circle),
                  alignment: Alignment.center,
                  child: const Text('🌸', style: TextStyle(fontSize: 22)),
                ),
                const SizedBox(width: 14),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('플라워 그로브', style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w700)),
                      const SizedBox(height: 2),
                      Text('서울 강남구 역삼동', style: AppTypography.body(fontSize: 11, color: ink60)),
                      const SizedBox(height: 4),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                        decoration: BoxDecoration(color: _sageLt, borderRadius: BorderRadius.circular(4)),
                        child: Text('신뢰도 92점', style: AppTypography.mono(fontSize: 10, fontWeight: FontWeight.w500, color: _sage)),
                      ),
                    ],
                  ),
                ),
              ],
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
          ],
        ),
      ),
    );
  }
}
