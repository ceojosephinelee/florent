import 'package:flutter/material.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/radius.dart';
import '../../../core/theme/typography.dart';

class HowItWorksSection extends StatelessWidget {
  const HowItWorksSection({super.key});

  static const _steps = [
    _Step(emoji: '📝', title: '요청하기', desc: '원하는 꽃의 용도,\n분위기를 알려주세요'),
    _Step(emoji: '💐', title: '제안 받기', desc: '주변 꽃집에서\n맞춤 제안이 와요'),
    _Step(emoji: '✨', title: '선택하기', desc: '마음에 드는 제안을\n골라 예약하세요'),
  ];

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '이용 안내',
            style: AppTypography.body(fontSize: 18, fontWeight: FontWeight.w700),
          ),
          const SizedBox(height: 16),
          Row(
            children: _steps
                .map((step) => Expanded(
                      child: _StepCard(step: step),
                    ))
                .toList(),
          ),
        ],
      ),
    );
  }
}

class _Step {
  const _Step({required this.emoji, required this.title, required this.desc});
  final String emoji;
  final String title;
  final String desc;
}

class _StepCard extends StatelessWidget {
  const _StepCard({required this.step});

  final _Step step;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 4),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: whiteColor,
        borderRadius: kBorderRadiusMd,
        border: Border.all(color: borderColor),
      ),
      child: Column(
        children: [
          Text(step.emoji, style: const TextStyle(fontSize: 28)),
          const SizedBox(height: 8),
          Text(
            step.title,
            style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600),
          ),
          const SizedBox(height: 4),
          Text(
            step.desc,
            textAlign: TextAlign.center,
            style: AppTypography.body(fontSize: 11, color: ink60, height: 1.4),
          ),
        ],
      ),
    );
  }
}
