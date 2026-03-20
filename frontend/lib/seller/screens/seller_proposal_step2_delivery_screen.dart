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

class SellerProposalStep2DeliveryScreen extends ConsumerStatefulWidget {
  const SellerProposalStep2DeliveryScreen({super.key});

  @override
  ConsumerState<SellerProposalStep2DeliveryScreen> createState() =>
      _SellerProposalStep2DeliveryScreenState();
}

class _SellerProposalStep2DeliveryScreenState
    extends ConsumerState<SellerProposalStep2DeliveryScreen> {
  bool _isSubmitting = false;

  static const _bands = [
    _BandOption(emoji: '🌤', label: '오전', time: '09:00 ~ 12:00', value: 'MORNING'),
    _BandOption(emoji: '☀️', label: '오후', time: '12:00 ~ 18:00', value: 'AFTERNOON'),
    _BandOption(emoji: '🌙', label: '저녁', time: '18:00 ~ 21:00', value: 'EVENING'),
  ];

  Future<void> _handleSubmit() async {
    if (_isSubmitting) return;
    setState(() => _isSubmitting = true);

    try {
      final form = ref.read(sellerProposalFormProvider);
      final repo = ref.read(sellerRepositoryProvider);
      final proposalId = form.proposalId!;

      await repo.updateProposal(proposalId, {
        'conceptTitle': form.conceptTitle,
        'description': [
          if (form.mainFlowers.isNotEmpty) '메인 꽃: ${form.mainFlowers}',
          if (form.subFlowers.isNotEmpty) '서브 꽃·그린: ${form.subFlowers}',
          if (form.concept.isNotEmpty) '컨셉·색감: ${form.concept}',
          if (form.wrapping.isNotEmpty) '포장·마무리: ${form.wrapping}',
          if (form.recommendation.isNotEmpty) '추천 이유: ${form.recommendation}',
        ].join('\n'),
        'availableSlot': {
          'kind': form.selectedSlotKind,
          'value': form.selectedSlotValue,
        },
        'price': form.price,
      });

      await repo.submitProposal(proposalId);

      if (!mounted) return;

      ref.invalidate(sellerRequestsProvider);
      ref.invalidate(sellerRecentRequestsProvider);
      ref.invalidate(sellerHomeProvider);

      context.push('/seller/proposals/done');
    } catch (e) {
      if (!mounted) return;
      setState(() => _isSubmitting = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('제안서 전송에 실패했어요. 다시 시도해주세요.'),
          backgroundColor: roseColor,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final form = ref.watch(sellerProposalFormProvider);
    final notifier = ref.read(sellerProposalFormProvider.notifier);

    final requestId = form.requestId;
    final asyncDetail = requestId != null
        ? ref.watch(sellerRequestDetailProvider(requestId))
        : null;

    final buyerSlots = <String>[];
    if (asyncDetail != null) {
      asyncDetail.whenData((detail) {
        for (final slot in detail.requestedTimeSlots) {
          if (slot['kind'] == 'DELIVERY_WINDOW') {
            final v = slot['value'];
            if (v != null) buyerSlots.add(v);
          }
        }
      });
    }

    final buyerHint = buyerSlots.isNotEmpty
        ? '📌 구매자가 희망하는 배송 시간대는 ${buyerSlots.map(_bandLabel).join(', ')}이에요.\n가능하다면 해당 시간대를 우선 선택해주세요.'
        : '📌 구매자의 희망 배송 시간대 정보를 불러오는 중이에요.';

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
                      decoration: BoxDecoration(
                        color: _sageLt,
                        borderRadius: kBorderRadiusSm,
                        border: Border.all(color: const Color(0xFFB8CFC4)),
                      ),
                      child: Text(
                        buyerHint,
                        style: AppTypography.body(fontSize: 11, color: _sage, height: 1.5),
                      ),
                    ),
                    const SizedBox(height: 16),
                    Text('🚚 배송 가능한 시간대 선택 *',
                        style: AppTypography.body(fontSize: 14, fontWeight: FontWeight.w600)),
                    const SizedBox(height: 4),
                    Text('단 하나의 시간대만 선택해 확정 약속으로 제안돼요.',
                        style: AppTypography.body(fontSize: 11, color: ink60)),
                    const SizedBox(height: 10),
                    Row(
                      children: _bands.map((band) {
                        final isHighlight = buyerSlots.contains(band.value);
                        final isSelected = form.selectedSlotValue == band.value;
                        return Expanded(
                          child: Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 4),
                            child: GestureDetector(
                              onTap: () => notifier.setSlot('DELIVERY_WINDOW', band.value),
                              child: Container(
                                padding: const EdgeInsets.symmetric(vertical: 14),
                                decoration: BoxDecoration(
                                  color: isSelected ? _sageLt : creamColor,
                                  borderRadius: kBorderRadiusSm,
                                  border: Border.all(
                                    color: isSelected
                                        ? _sage
                                        : isHighlight
                                            ? const Color(0xFFB8CFC4)
                                            : borderColor,
                                    width: 1.5,
                                  ),
                                ),
                                child: Column(
                                  children: [
                                    Text(band.emoji, style: const TextStyle(fontSize: 16)),
                                    const SizedBox(height: 4),
                                    Text(band.label,
                                        style: AppTypography.body(
                                          fontSize: 12,
                                          fontWeight: FontWeight.w600,
                                          color: isSelected ? _sage : isHighlight ? _sage : inkColor,
                                        )),
                                    const SizedBox(height: 2),
                                    Text(band.time,
                                        style: AppTypography.body(
                                          fontSize: 10,
                                          color: isSelected ? _sage : ink30,
                                        )),
                                  ],
                                ),
                              ),
                            ),
                          ),
                        );
                      }).toList(),
                    ),
                    const SizedBox(height: 6),
                    Text('* 선택한 시간대는 예약 확정 시 약속으로 확정됩니다',
                        style: AppTypography.body(fontSize: 10, color: ink30)),
                    const SizedBox(height: 16),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: const Color(0xFFFEF7EC),
                        borderRadius: kBorderRadiusSm,
                        border: Border.all(color: const Color(0xFFE8D5B0)),
                      ),
                      child: Text(
                        '⚠️ 제안서는 24시간 내 전송해주세요. 미전송 시 신뢰도 지수가 하락해요.',
                        style: AppTypography.body(fontSize: 11, color: const Color(0xFF8A6B20)),
                      ),
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
                    onPressed: form.isStep2Valid && !_isSubmitting ? _handleSubmit : null,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: _sage,
                      disabledBackgroundColor: ink30,
                      foregroundColor: whiteColor,
                      shape: RoundedRectangleBorder(borderRadius: kBorderRadiusMd),
                      elevation: 0,
                    ),
                    child: Text(
                      _isSubmitting ? '전송 중...' : '제안서 전송하기 📨',
                      style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w600, color: whiteColor),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _bandLabel(String value) {
    return switch (value) {
      'MORNING' => '오전',
      'AFTERNOON' => '오후',
      'EVENING' => '저녁',
      _ => value,
    };
  }

  Widget _stepBar() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
      child: Row(
        children: [
          Container(
              width: 28, height: 28,
              decoration: const BoxDecoration(shape: BoxShape.circle, color: _sage),
              alignment: Alignment.center,
              child: const Icon(Icons.check, size: 14, color: whiteColor)),
          Expanded(child: Container(height: 2, color: _sage)),
          Container(
              width: 28, height: 28,
              decoration: const BoxDecoration(shape: BoxShape.circle, color: inkColor),
              alignment: Alignment.center,
              child: Text('2', style: AppTypography.mono(fontSize: 11, color: whiteColor))),
        ],
      ),
    );
  }
}

class _BandOption {
  const _BandOption({required this.emoji, required this.label, required this.time, required this.value});
  final String emoji;
  final String label;
  final String time;
  final String value;
}
