import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/seller_providers.dart';

const _sage = Color(0xFF5A7A68);
const _sageLt = Color(0xFFE8F0EC);

class SellerHomeScreen extends ConsumerWidget {
  const SellerHomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncHome = ref.watch(sellerHomeProvider);
    final asyncRecent = ref.watch(sellerRecentRequestsProvider);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: asyncHome.when(
          loading: () => const Center(child: CircularProgressIndicator(color: _sage)),
          error: (e, _) => Center(child: Text('오류', style: AppTypography.body(color: ink60))),
          data: (home) => SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text('Florent', style: AppTypography.serif(fontSize: 20, fontWeight: FontWeight.w600, color: _sage)),
                          Text('플로리스트', style: AppTypography.body(fontSize: 8, color: ink60)),
                        ],
                      ),
                      GestureDetector(
                        onTap: () => context.push('/seller/notifications'),
                        child: Stack(
                          children: [
                            const Icon(Icons.notifications_outlined, size: 28, color: inkColor),
                            if (ref.watch(sellerUnreadCountProvider) > 0)
                              Positioned(
                                right: 0,
                                top: 0,
                                child: Container(
                                  width: 16, height: 16,
                                  decoration: const BoxDecoration(color: roseColor, shape: BoxShape.circle),
                                  alignment: Alignment.center,
                                  child: Text(
                                    '${ref.watch(sellerUnreadCountProvider)}',
                                    style: AppTypography.mono(fontSize: 9, color: whiteColor, fontWeight: FontWeight.w600),
                                  ),
                                ),
                              ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 20),
                  child: Text('안녕하세요, ${home.shopName} 👋', style: AppTypography.body(fontSize: 11, color: ink60)),
                ),
                const SizedBox(height: 12),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 20),
                  child: Row(
                    children: [
                      _StatCard(label: '새 요청', value: '${home.openRequestCount}', color: _sage),
                      const SizedBox(width: 8),
                      _StatCard(label: '제안 대기', value: '${home.draftProposalCount}', color: inkColor),
                      const SizedBox(width: 8),
                      _StatCard(label: '이번달 확정', value: '${home.confirmedReservationCount}', color: inkColor),
                    ],
                  ),
                ),
                const SizedBox(height: 16),
                const Divider(),
                Padding(
                  padding: const EdgeInsets.fromLTRB(20, 16, 20, 12),
                  child: Text('새로 들어온 요청', style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w700)),
                ),
                asyncRecent.when(
                  loading: () => const Center(child: Padding(
                    padding: EdgeInsets.all(16),
                    child: CircularProgressIndicator(strokeWidth: 2, color: _sage),
                  )),
                  error: (_, __) => const SizedBox.shrink(),
                  data: (requests) => Column(
                    children: requests.where((r) => r.myProposalStatus == null).map((r) {
                      final tag = r.purposeTags.isNotEmpty ? r.purposeTags.first : '';
                      final budget = r.budgetTier == 'TIER2' ? '기본형' : r.budgetTier == 'TIER1' ? '작은 꽃다발' : r.budgetTier == 'TIER3' ? '풍성한 꽃다발' : '프리미엄';
                      final type = r.fulfillmentType == 'PICKUP' ? '픽업' : '배송';
                      return GestureDetector(
                        onTap: () => context.push('/seller/requests/${r.requestId}'),
                        child: Container(
                          margin: const EdgeInsets.fromLTRB(20, 0, 20, 10),
                          padding: const EdgeInsets.all(14),
                          decoration: BoxDecoration(color: whiteColor, borderRadius: kBorderRadiusMd, border: Border.all(color: borderColor)),
                          child: Row(
                            children: [
                              const Text('🎂', style: TextStyle(fontSize: 20)),
                              const SizedBox(width: 10),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text('$tag · $budget', style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600)),
                                    const SizedBox(height: 3),
                                    Text('${r.moodTags.join(" · ")} · $type\n${r.fulfillmentDate} · ${r.distance ?? ''}', style: AppTypography.body(fontSize: 11, color: ink60, height: 1.4)),
                                  ],
                                ),
                              ),
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                decoration: BoxDecoration(color: _sageLt, borderRadius: BorderRadius.circular(4)),
                                child: Text('NEW', style: AppTypography.mono(fontSize: 10, fontWeight: FontWeight.w500, color: _sage)),
                              ),
                            ],
                          ),
                        ),
                      );
                    }).toList(),
                  ),
                ),
                const SizedBox(height: 24),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _StatCard extends StatelessWidget {
  const _StatCard({required this.label, required this.value, required this.color});
  final String label;
  final String value;
  final Color color;

  @override
  Widget build(BuildContext context) {
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
}
