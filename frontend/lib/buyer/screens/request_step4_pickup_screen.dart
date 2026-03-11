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

class RequestStep4PickupScreen extends ConsumerWidget {
  const RequestStep4PickupScreen({super.key});

  static List<String> get _timeSlots {
    final slots = <String>[];
    for (var h = 10; h <= 20; h++) {
      slots.add('${h.toString().padLeft(2, '0')}:00');
      if (h < 20) {
        slots.add('${h.toString().padLeft(2, '0')}:30');
      }
    }
    return slots;
  }

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
                      '픽업 희망 시간을 선택해주세요',
                      style: AppTypography.body(
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '여러 시간대를 선택할 수 있어요.\n플로리스트가 가능한 시간에 맞춰 제안해드려요.',
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
                          '⏰ 희망 시간',
                          style: AppTypography.body(
                            fontSize: 14,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        const SizedBox(width: 6),
                        Text(
                          '(복수 선택 가능, 10:00 ~ 20:00)',
                          style: AppTypography.body(fontSize: 10, color: ink30),
                        ),
                      ],
                    ),
                    const SizedBox(height: 10),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: _timeSlots.map((time) {
                        final slot =
                            TimeSlot(kind: 'PICKUP_30M', value: time);
                        final isSelected = form.requestedTimeSlots.any(
                          (s) =>
                              s.kind == slot.kind && s.value == slot.value,
                        );
                        return _SlotChip(
                          label: time,
                          isSelected: isSelected,
                          onTap: () => notifier.toggleTimeSlot(slot),
                        );
                      }).toList(),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      '* 복수 선택 가능 · 플로리스트가 이 중 가능한 시간으로 제안해드려요',
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
                        '💡 원하는 시간이 정해지지 않았다면 여러 슬롯을 선택해두세요!',
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

class _SlotChip extends StatelessWidget {
  const _SlotChip({
    required this.label,
    required this.isSelected,
    required this.onTap,
  });

  final String label;
  final bool isSelected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: isSelected ? roseLt : creamColor,
          borderRadius: kBorderRadiusSm,
          border: Border.all(
            color: isSelected ? roseColor : borderColor,
            width: 1.5,
          ),
        ),
        child: Text(
          label,
          style: AppTypography.mono(
            fontSize: 11,
            fontWeight: isSelected ? FontWeight.w500 : FontWeight.w400,
            color: isSelected ? roseColor : ink60,
          ),
        ),
      ),
    );
  }
}
