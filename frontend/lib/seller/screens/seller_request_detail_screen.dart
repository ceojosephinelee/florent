import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../buyer/widgets/common/app_nav_bar.dart';
import '../../buyer/widgets/common/expiry_timer.dart';
import '../../core/models/enums.dart';
import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/seller_providers.dart';

const _sage = Color(0xFF5A7A68);

class SellerRequestDetailScreen extends ConsumerWidget {
  const SellerRequestDetailScreen({super.key, required this.requestId});
  final int requestId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncDetail = ref.watch(sellerRequestDetailProvider(requestId));

    return Scaffold(
      backgroundColor: creamColor,
      body: asyncDetail.when(
        loading: () => const Center(child: CircularProgressIndicator(color: _sage)),
        error: (e, _) => Center(child: Text('오류')),
        data: (detail) {
          final expiresAt = DateTime.parse(detail.expiresAt);
          final budget = BudgetTier.values.where((t) => t.value == detail.budgetTier).firstOrNull;
          final isPickup = detail.fulfillmentType == 'PICKUP';
          final slotsLabel = detail.requestedTimeSlots.map((s) => s['value'] ?? '').join(', ');

          return SafeArea(
            child: Column(
              children: [
                const AppNavBar(title: '요청서 상세'),
                Expanded(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.fromLTRB(18, 0, 18, 24),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                              decoration: BoxDecoration(color: const Color(0xFFE8F0EC), borderRadius: BorderRadius.circular(4)),
                              child: Text('미제안', style: AppTypography.mono(fontSize: 10, fontWeight: FontWeight.w500, color: _sage)),
                            ),
                            const SizedBox(width: 8),
                            Text('⏱ 요청 만료까지 ', style: AppTypography.body(fontSize: 11, color: ink60)),
                            ExpiryTimer(expiresAt: expiresAt),
                          ],
                        ),
                        const SizedBox(height: 16),
                        Container(
                          width: double.infinity,
                          padding: const EdgeInsets.all(16),
                          decoration: BoxDecoration(color: whiteColor, borderRadius: kBorderRadiusLg, border: Border.all(color: borderColor)),
                          child: Column(
                            children: [
                              _row('목적', detail.purposeTags.isNotEmpty ? detail.purposeTags.join(' · ') : '-'),
                              _row('관계', detail.relationTags.isNotEmpty ? detail.relationTags.join(' · ') : '-'),
                              _row('분위기', detail.moodTags.isNotEmpty ? detail.moodTags.join(' · ') : '-'),
                              _row('예산', budget != null ? '${budget.label} (${budget.priceRange} 참고)' : '-'),
                              _row('수령', '${isPickup ? "픽업" : "배송"} · ${detail.fulfillmentDate} $slotsLabel'),
                              _row(isPickup ? '픽업 장소' : '배송지', '${detail.placeAddressText} · ${detail.distance ?? ""}'),
                            ],
                          ),
                        ),
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
                            '⚠️ 제안서 작성을 시작하면 24시간 내에 완료해주세요. 미완료 시 신뢰도 지수가 하락할 수 있어요.',
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
                        onPressed: () => context.push('/seller/proposals/new/step1?requestId=$requestId'),
                        style: ElevatedButton.styleFrom(backgroundColor: _sage, foregroundColor: whiteColor, shape: RoundedRectangleBorder(borderRadius: kBorderRadiusMd), elevation: 0),
                        child: Text('제안서 작성하기 ✍️', style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w600, color: whiteColor)),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _row(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(width: 65, child: Text(label, style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60))),
          Expanded(child: Text(value, style: AppTypography.body(fontSize: 12))),
        ],
      ),
    );
  }
}
