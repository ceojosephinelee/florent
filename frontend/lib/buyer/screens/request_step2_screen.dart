import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/request_form_provider.dart';
import '../widgets/common/app_nav_bar.dart';
import '../widgets/common/bottom_cta_button.dart';
import '../widgets/common/step_progress_bar.dart';

class RequestStep2Screen extends ConsumerWidget {
  const RequestStep2Screen({super.key});

  static const _budgetCards = [
    _BudgetOption(
      tier: 'TIER1',
      emoji: '🌷',
      name: '작은 꽃다발',
      price: '3~5만원',
      desc: '손에 가볍게 들리는 크기',
    ),
    _BudgetOption(
      tier: 'TIER2',
      emoji: '💐',
      name: '기본형',
      price: '6~8만원',
      desc: '두 손으로 받쳐 드는 크기',
    ),
    _BudgetOption(
      tier: 'TIER3',
      emoji: '🌸',
      name: '풍성한 꽃다발',
      price: '9~13만원',
      desc: '가득 안기는 볼륨감',
    ),
    _BudgetOption(
      tier: 'TIER4',
      emoji: '👑',
      name: '프리미엄',
      price: '15만원 이상',
      desc: '시선을 사로잡는 특별한 크기',
    ),
  ];

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final form = ref.watch(requestFormProvider);
    final notifier = ref.read(requestFormProvider.notifier);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            const AppNavBar(title: '꽃다발 요청하기'),
            const StepProgressBar(totalSteps: 4, currentStep: 2),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(18, 8, 18, 16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '예산을 선택해주세요',
                      style: AppTypography.body(
                        fontSize: 13,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '꽃집마다 가격이 다를 수 있어요.\n아래 금액은 참고용입니다.',
                      style: AppTypography.body(
                        fontSize: 11,
                        color: ink60,
                        height: 1.5,
                      ),
                    ),
                    const SizedBox(height: 16),
                    GridView.count(
                      crossAxisCount: 2,
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      mainAxisSpacing: 12,
                      crossAxisSpacing: 12,
                      childAspectRatio: 0.95,
                      children: _budgetCards
                          .map((card) => _BudgetCard(
                                option: card,
                                isSelected: form.budgetTier == card.tier,
                                onTap: () => notifier.setBudgetTier(card.tier),
                              ))
                          .toList(),
                    ),
                    const SizedBox(height: 16),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: creamColor,
                        borderRadius: kBorderRadiusSm,
                        border: Border.all(color: borderColor, width: 1.5),
                      ),
                      child: Text(
                        '⚠️ 실제 가격은 꽃집마다 다를 수 있으며, 위 금액은 참고 범위예요.',
                        style: AppTypography.body(fontSize: 11, color: ink60),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            BottomCtaButton(
              label: '다음 — 수령 방법 선택',
              enabled: form.isStep2Valid,
              onPressed: () => context.push('/buyer/request/step3/pickup'),
            ),
          ],
        ),
      ),
    );
  }
}

class _BudgetOption {
  const _BudgetOption({
    required this.tier,
    required this.emoji,
    required this.name,
    required this.price,
    required this.desc,
  });

  final String tier;
  final String emoji;
  final String name;
  final String price;
  final String desc;
}

class _BudgetCard extends StatelessWidget {
  const _BudgetCard({
    required this.option,
    required this.isSelected,
    required this.onTap,
  });

  final _BudgetOption option;
  final bool isSelected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: isSelected ? roseLt : creamColor,
          borderRadius: kBorderRadiusMd,
          border: Border.all(
            color: isSelected ? roseColor : borderColor,
            width: 1.5,
          ),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(option.emoji, style: const TextStyle(fontSize: 32)),
            const SizedBox(height: 8),
            Text(
              option.name,
              style: AppTypography.body(
                fontSize: 12,
                fontWeight: FontWeight.w700,
                color: isSelected ? roseColor : inkColor,
              ),
            ),
            const SizedBox(height: 2),
            Text(
              option.price,
              style: AppTypography.body(
                fontSize: 11,
                fontWeight: FontWeight.w600,
                color: roseColor,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              option.desc,
              textAlign: TextAlign.center,
              style: AppTypography.body(
                fontSize: 10,
                color: ink60,
                height: 1.4,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
