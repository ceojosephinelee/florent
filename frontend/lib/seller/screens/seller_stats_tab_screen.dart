import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/seller_providers.dart';

const _sage = Color(0xFF5A7A68);

class SellerStatsTabScreen extends ConsumerWidget {
  const SellerStatsTabScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncHistory = ref.watch(sellerReservationHistoryProvider);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          children: [
            const SizedBox(height: 16),
            Text('현황', style: AppTypography.body(fontSize: 17, fontWeight: FontWeight.w700)),
            const SizedBox(height: 16),
            Text('이번 달 통계', style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600, color: ink60)),
            const SizedBox(height: 10),
            Row(
              children: [
                _stat('받은 요청', '12', _sage),
                const SizedBox(width: 8),
                _stat('제안 전송', '8', inkColor),
                const SizedBox(width: 8),
                _stat('예약 확정', '5', inkColor),
              ],
            ),
            const SizedBox(height: 20),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(color: whiteColor, borderRadius: kBorderRadiusMd, border: Border.all(color: borderColor)),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('신뢰도 지수', style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600, color: ink60)),
                  const SizedBox(height: 8),
                  Text('92', style: AppTypography.body(fontSize: 28, fontWeight: FontWeight.w700, color: _sage)),
                  const SizedBox(height: 8),
                  ClipRRect(
                    borderRadius: BorderRadius.circular(4),
                    child: LinearProgressIndicator(value: 0.92, minHeight: 8, backgroundColor: borderColor, valueColor: const AlwaysStoppedAnimation(_sage)),
                  ),
                  const SizedBox(height: 6),
                  Text('미완료 제안이 없을수록 높아져요', style: AppTypography.body(fontSize: 10, color: ink60)),
                ],
              ),
            ),
            const SizedBox(height: 20),
            Text('최근 확정 내역', style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600, color: ink60)),
            const SizedBox(height: 10),
            asyncHistory.when(
              loading: () => const Center(child: CircularProgressIndicator(color: _sage)),
              error: (e, _) => Text('오류', style: AppTypography.body(fontSize: 14, color: ink60)),
              data: (history) {
                if (history.isEmpty) {
                  return Padding(
                    padding: const EdgeInsets.symmetric(vertical: 32),
                    child: Center(
                      child: Column(
                        children: [
                          const Text('📭', style: TextStyle(fontSize: 28)),
                          const SizedBox(height: 8),
                          Text('확정 내역이 없어요', style: AppTypography.body(fontSize: 13, color: ink60)),
                        ],
                      ),
                    ),
                  );
                }
                return Column(
                  children: history.map((r) {
                    final typeLabel = r.fulfillmentType == 'PICKUP' ? '픽업' : '배송';
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 8),
                      child: GestureDetector(
                        onTap: () => context.push('/seller/reservations/${r.reservationId}'),
                        child: Container(
                          padding: const EdgeInsets.all(14),
                          decoration: BoxDecoration(color: whiteColor, borderRadius: kBorderRadiusMd, border: Border.all(color: borderColor)),
                          child: Row(
                            children: [
                              Container(
                                width: 40, height: 40,
                                decoration: BoxDecoration(color: const Color(0xFFF2E8E5), borderRadius: kBorderRadiusSm),
                                alignment: Alignment.center,
                                child: const Text('🌸', style: TextStyle(fontSize: 16)),
                              ),
                              const SizedBox(width: 10),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(r.conceptTitle, style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600)),
                                    Text('${_formatPrice(r.price)}원 · ${r.confirmedAt} · $typeLabel', style: AppTypography.body(fontSize: 10, color: ink60)),
                                  ],
                                ),
                              ),
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                decoration: BoxDecoration(color: const Color(0xFFE8F0EC), borderRadius: BorderRadius.circular(4)),
                                child: Text('완료', style: AppTypography.mono(fontSize: 10, fontWeight: FontWeight.w500, color: _sage)),
                              ),
                            ],
                          ),
                        ),
                      ),
                    );
                  }).toList(),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _stat(String label, String value, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 14),
        decoration: BoxDecoration(color: whiteColor, borderRadius: kBorderRadiusMd, border: Border.all(color: borderColor)),
        child: Column(
          children: [
            Text(value, style: AppTypography.body(fontSize: 22, fontWeight: FontWeight.w700, color: color)),
            const SizedBox(height: 2),
            Text(label, style: AppTypography.body(fontSize: 10, color: ink60)),
          ],
        ),
      ),
    );
  }

  String _formatPrice(int price) {
    final str = price.toString();
    final buffer = StringBuffer();
    for (var i = 0; i < str.length; i++) {
      if (i > 0 && (str.length - i) % 3 == 0) buffer.write(',');
      buffer.write(str[i]);
    }
    return buffer.toString();
  }
}
