import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/seller_providers.dart';

const _sage = Color(0xFF5A7A68);

class SellerProposalDoneScreen extends ConsumerWidget {
  const SellerProposalDoneScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final form = ref.watch(sellerProposalFormProvider);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
                child: Column(
                  children: [
                    const SizedBox(height: 32),
                    Container(
                      width: 72, height: 72,
                      decoration: BoxDecoration(color: _sage, borderRadius: kBorderRadiusLg),
                      alignment: Alignment.center,
                      child: const Text('📨', style: TextStyle(fontSize: 32)),
                    ),
                    const SizedBox(height: 14),
                    Text('제안서를 전송했어요!', style: AppTypography.serif(fontSize: 24, fontWeight: FontWeight.w600)),
                    const SizedBox(height: 10),
                    Text('구매자가 확인하면 알림을 드릴게요.\n선택되면 예약이 즉시 확정됩니다 🌷', textAlign: TextAlign.center, style: AppTypography.body(fontSize: 12, color: ink60, height: 1.7)),
                    const SizedBox(height: 24),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(color: whiteColor, borderRadius: kBorderRadiusLg, border: Border.all(color: borderColor, width: 1.5)),
                      child: Column(
                        children: [
                          _row('요청', '생일 · 기본형'),
                          const SizedBox(height: 6),
                          _row('제안 제목', form.conceptTitle.isNotEmpty ? form.conceptTitle : '-'),
                          const SizedBox(height: 6),
                          _row('제안 가격', form.price > 0 ? '${form.price}원' : '-'),
                          const SizedBox(height: 6),
                          _row('픽업 시간', form.selectedSlotValue != null ? '3/15 ${form.selectedSlotValue}' : '-'),
                          const SizedBox(height: 6),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text('제안 만료', style: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600, color: ink60)),
                              Text('⏱ 23시간 45분', style: AppTypography.mono(fontSize: 11, color: roseColor)),
                            ],
                          ),
                        ],
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
                  height: 48,
                  child: ElevatedButton(
                    onPressed: () {
                      ref.read(sellerProposalFormProvider.notifier).reset();
                      context.go('/seller/home');
                    },
                    style: ElevatedButton.styleFrom(backgroundColor: _sage, foregroundColor: whiteColor, shape: RoundedRectangleBorder(borderRadius: kBorderRadiusLg), elevation: 0),
                    child: Text('요청 목록으로', style: AppTypography.body(fontSize: 14, fontWeight: FontWeight.w600, color: whiteColor)),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _row(String label, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600, color: ink60)),
        Flexible(child: Text(value, style: AppTypography.body(fontSize: 11), textAlign: TextAlign.right)),
      ],
    );
  }
}
