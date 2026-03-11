import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../buyer/widgets/common/app_nav_bar.dart';
import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/seller_providers.dart';

const _sage = Color(0xFF5A7A68);
const _sageLt = Color(0xFFE8F0EC);

class SellerProposalStep2PickupScreen extends ConsumerWidget {
  const SellerProposalStep2PickupScreen({super.key});

  static List<String> get _timeSlots {
    final slots = <String>[];
    for (var h = 10; h <= 20; h++) {
      slots.add('${h.toString().padLeft(2, '0')}:00');
      if (h < 20) slots.add('${h.toString().padLeft(2, '0')}:30');
    }
    return slots;
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final form = ref.watch(sellerProposalFormProvider);
    final notifier = ref.read(sellerProposalFormProvider.notifier);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            const AppNavBar(title: '제안서 작성'),
            _stepBar(),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(18, 8, 18, 16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(color: _sageLt, borderRadius: kBorderRadiusSm, border: Border.all(color: const Color(0xFFB8CFC4))),
                      child: Text('📌 구매자가 희망하는 픽업 시간은 14:00, 14:30이에요.\n가능하다면 해당 시간을 우선 선택해주세요.', style: AppTypography.body(fontSize: 11, color: _sage, height: 1.5)),
                    ),
                    const SizedBox(height: 16),
                    Text('⏰ 픽업 가능한 시간 선택 *', style: AppTypography.body(fontSize: 14, fontWeight: FontWeight.w600)),
                    const SizedBox(height: 4),
                    Text('단 하나의 시간만 선택해 확정 약속으로 제안돼요.', style: AppTypography.body(fontSize: 11, color: ink60)),
                    const SizedBox(height: 10),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: _timeSlots.map((time) {
                        final isHighlight = time == '14:00' || time == '14:30';
                        final isSelected = form.selectedSlotValue == time;
                        return GestureDetector(
                          onTap: () => notifier.setSlot('PICKUP_30M', time),
                          child: Container(
                            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                            decoration: BoxDecoration(
                              color: isSelected ? _sageLt : creamColor,
                              borderRadius: kBorderRadiusSm,
                              border: Border.all(color: isSelected ? _sage : isHighlight ? const Color(0xFFB8CFC4) : borderColor, width: 1.5),
                            ),
                            child: Text(time, style: AppTypography.mono(fontSize: 11, fontWeight: isSelected ? FontWeight.w500 : FontWeight.w400, color: isSelected ? _sage : isHighlight ? _sage : ink60)),
                          ),
                        );
                      }).toList(),
                    ),
                    const SizedBox(height: 6),
                    Text('* 선택한 시간은 예약 확정 시 약속으로 확정됩니다', style: AppTypography.body(fontSize: 10, color: ink30)),
                    const SizedBox(height: 16),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(color: const Color(0xFFFEF7EC), borderRadius: kBorderRadiusSm, border: Border.all(color: const Color(0xFFE8D5B0))),
                      child: Text('⚠️ 제안서는 24시간 내 전송해주세요. 미전송 시 신뢰도 지수가 하락해요.', style: AppTypography.body(fontSize: 11, color: const Color(0xFF8A6B20))),
                    ),
                  ],
                ),
              ),
            ),
            SafeArea(
              top: false,
              child: Padding(
                padding: const EdgeInsets.fromLTRB(18, 8, 18, 12),
                child: SizedBox(
                  width: double.infinity,
                  height: 52,
                  child: ElevatedButton(
                    onPressed: form.isStep2Valid ? () => context.push('/seller/proposals/done') : null,
                    style: ElevatedButton.styleFrom(backgroundColor: _sage, disabledBackgroundColor: ink30, foregroundColor: whiteColor, shape: RoundedRectangleBorder(borderRadius: kBorderRadiusMd), elevation: 0),
                    child: Text('제안서 전송하기 📨', style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w600, color: whiteColor)),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _stepBar() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
      child: Row(
        children: [
          Container(width: 28, height: 28, decoration: const BoxDecoration(shape: BoxShape.circle, color: _sage), alignment: Alignment.center, child: const Icon(Icons.check, size: 14, color: whiteColor)),
          Expanded(child: Container(height: 2, color: _sage)),
          Container(width: 28, height: 28, decoration: const BoxDecoration(shape: BoxShape.circle, color: inkColor), alignment: Alignment.center, child: Text('2', style: AppTypography.mono(fontSize: 11, color: whiteColor))),
        ],
      ),
    );
  }
}
