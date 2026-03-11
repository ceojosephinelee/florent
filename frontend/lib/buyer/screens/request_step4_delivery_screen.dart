import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/models/time_slot.dart';
import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/request_form_provider.dart';
import '../widgets/common/app_nav_bar.dart';
import '../widgets/common/bottom_cta_button.dart';
import '../widgets/common/step_progress_bar.dart';

class RequestStep4DeliveryScreen extends ConsumerWidget {
  const RequestStep4DeliveryScreen({super.key});

  static const _bands = [
    _BandOption(
      emoji: '🌤',
      label: '오전',
      time: '09:00 ~ 12:00',
      value: 'MORNING',
    ),
    _BandOption(
      emoji: '☀️',
      label: '오후',
      time: '12:00 ~ 18:00',
      value: 'AFTERNOON',
    ),
    _BandOption(
      emoji: '🌙',
      label: '저녁',
      time: '18:00 ~ 21:00',
      value: 'EVENING',
    ),
  ];

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final form = ref.watch(requestFormProvider);
    final notifier = ref.read(requestFormProvider.notifier);

    String dateDisplay = '';
    if (form.fulfillmentDate != null) {
      final date = DateTime.parse(form.fulfillmentDate!);
      final weekday = ['월', '화', '수', '목', '금', '토', '일'];
      dateDisplay =
          '${date.year}년 ${date.month}월 ${date.day}일 (${weekday[date.weekday - 1]})';
    }

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            const AppNavBar(title: '꽃다발 요청하기'),
            const StepProgressBar(totalSteps: 4, currentStep: 4),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(18, 8, 18, 16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '배송 희망 시간대를 선택해주세요',
                      style: AppTypography.body(
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '원하는 배송 시간대를 알려주세요.',
                      style: AppTypography.body(
                        fontSize: 11,
                        color: ink60,
                        height: 1.5,
                      ),
                    ),
                    const SizedBox(height: 14),
                    if (dateDisplay.isNotEmpty)
                      Container(
                        width: double.infinity,
                        padding: const EdgeInsets.symmetric(
                          horizontal: 13,
                          vertical: 10,
                        ),
                        margin: const EdgeInsets.only(bottom: 14),
                        decoration: BoxDecoration(
                          color: creamColor,
                          borderRadius: kBorderRadiusSm,
                          border: Border.all(color: borderColor),
                        ),
                        child: Row(
                          children: [
                            const Text('📅', style: TextStyle(fontSize: 13)),
                            const SizedBox(width: 8),
                            Text(
                              '선택한 날짜',
                              style: AppTypography.body(
                                fontSize: 12,
                                color: ink60,
                              ),
                            ),
                            const Spacer(),
                            Text(
                              dateDisplay,
                              style: AppTypography.body(
                                fontSize: 12,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ],
                        ),
                      ),
                    Row(
                      children: [
                        Text(
                          '⏰ 배송 희망 시간대',
                          style: AppTypography.body(
                            fontSize: 14,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        const SizedBox(width: 6),
                        Text(
                          '(복수 선택 가능)',
                          style: AppTypography.body(fontSize: 10, color: ink30),
                        ),
                      ],
                    ),
                    const SizedBox(height: 10),
                    Row(
                      children: _bands.map((band) {
                        final slot = TimeSlot(
                          kind: 'DELIVERY_WINDOW',
                          value: band.value,
                        );
                        final isSelected = form.requestedTimeSlots.any(
                          (s) =>
                              s.kind == slot.kind && s.value == slot.value,
                        );
                        return Expanded(
                          child: Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 4),
                            child: _BandCard(
                              band: band,
                              isSelected: isSelected,
                              onTap: () => notifier.toggleTimeSlot(slot),
                            ),
                          ),
                        );
                      }).toList(),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      '* 복수 선택 가능 · 플로리스트가 가능한 시간대로 제안해드려요',
                      style: AppTypography.body(
                        fontSize: 10,
                        color: ink30,
                        height: 1.5,
                      ),
                    ),
                    const SizedBox(height: 12),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: creamColor,
                        borderRadius: kBorderRadiusSm,
                        border: Border.all(color: borderColor, width: 1.5),
                      ),
                      child: Text(
                        '💡 시간대가 유연하다면 여러 옵션을 선택해두세요. 제안이 더 빨리 올 수 있어요!',
                        style: AppTypography.body(fontSize: 11, color: ink60),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            BottomCtaButton(
              label: '요청서 제출하기 🌷',
              enabled: form.isStep4Valid,
              onPressed: () => context.push('/buyer/request/done'),
            ),
          ],
        ),
      ),
    );
  }
}

class _BandOption {
  const _BandOption({
    required this.emoji,
    required this.label,
    required this.time,
    required this.value,
  });

  final String emoji;
  final String label;
  final String time;
  final String value;
}

class _BandCard extends StatelessWidget {
  const _BandCard({
    required this.band,
    required this.isSelected,
    required this.onTap,
  });

  final _BandOption band;
  final bool isSelected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 12),
        decoration: BoxDecoration(
          color: isSelected ? roseLt : creamColor,
          borderRadius: kBorderRadiusSm,
          border: Border.all(
            color: isSelected ? roseColor : borderColor,
            width: 1.5,
          ),
        ),
        child: Column(
          children: [
            Text(band.emoji, style: const TextStyle(fontSize: 15)),
            const SizedBox(height: 3),
            Text(
              band.label,
              style: AppTypography.body(
                fontSize: 12,
                fontWeight: FontWeight.w600,
                color: isSelected ? roseColor : inkColor,
              ),
            ),
            const SizedBox(height: 3),
            Text(
              band.time,
              style: AppTypography.body(
                fontSize: 10,
                color: isSelected ? roseColor : ink30,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
