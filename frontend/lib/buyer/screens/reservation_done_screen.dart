import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/proposal_provider.dart';
import '../widgets/common/bottom_cta_button.dart';

class ReservationDoneScreen extends ConsumerWidget {
  const ReservationDoneScreen({super.key, required this.reservationId});
  final int reservationId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncRes = ref.watch(buyerReservationDetailProvider(reservationId));

    return Scaffold(
      backgroundColor: creamColor,
      body: asyncRes.when(
        loading: () => const Center(child: CircularProgressIndicator(color: roseColor)),
        error: (e, _) => Center(child: Text('오류')),
        data: (res) => SafeArea(
          child: Column(
            children: [
              Expanded(
                child: SingleChildScrollView(
                  padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
                  child: Column(
                    children: [
                      const SizedBox(height: 32),
                      const Text('🎉', style: TextStyle(fontSize: 48)),
                      const SizedBox(height: 14),
                      Text('예약이 확정됐어요!', style: AppTypography.serif(fontSize: 24, fontWeight: FontWeight.w600)),
                      const SizedBox(height: 10),
                      Text(
                        '플로리스트가 소중한 꽃다발을\n정성껏 준비할 거예요 🌷',
                        textAlign: TextAlign.center,
                        style: AppTypography.body(fontSize: 12, color: ink60, height: 1.7),
                      ),
                      const SizedBox(height: 24),
                      Container(
                        width: double.infinity,
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          color: whiteColor,
                          borderRadius: kBorderRadiusLg,
                          border: Border.all(color: borderColor, width: 1.5),
                        ),
                        child: Column(
                          children: [
                            _row('꽃집', '${res.shopEmoji ?? "🌸"} ${res.shopName}'),
                            const SizedBox(height: 8),
                            _row('주소', res.shopAddress),
                            const SizedBox(height: 8),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text('픽업 시간', style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60)),
                                Text(res.slotLabel, style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: roseColor)),
                              ],
                            ),
                            const SizedBox(height: 8),
                            _row('결제 금액', '${NumberFormat('#,###').format(res.price)}원'),
                            const SizedBox(height: 8),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text('상태', style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60)),
                                Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                  decoration: BoxDecoration(color: const Color(0xFFE8F0EC), borderRadius: BorderRadius.circular(4)),
                                  child: Text('✅ 예약 확정', style: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600, color: const Color(0xFF5A7A68))),
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              BottomCtaButton(
                label: '홈으로 돌아가기',
                onPressed: () => context.go('/buyer/home'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _row(String label, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60)),
        Text(value, style: AppTypography.body(fontSize: 12)),
      ],
    );
  }
}
