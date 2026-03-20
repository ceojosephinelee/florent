import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../buyer/widgets/common/expiry_timer.dart';
import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/seller_providers.dart';

const _sage = Color(0xFF5A7A68);
const _sageLt = Color(0xFFE8F0EC);

class SellerRequestsTabScreen extends ConsumerWidget {
  const SellerRequestsTabScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final filter = ref.watch(sellerRequestFilterProvider);
    final asyncFiltered = ref.watch(filteredSellerRequestsProvider);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 헤더
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 12),
              child: Row(
                children: [
                  Text('요청', style: AppTypography.body(fontSize: 17, fontWeight: FontWeight.w700)),
                  const Spacer(),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                    decoration: BoxDecoration(color: _sageLt, borderRadius: BorderRadius.circular(4)),
                    child: Text('반경 2km', style: AppTypography.mono(fontSize: 10, color: _sage)),
                  ),
                ],
              ),
            ),

            // 필터 탭
            SizedBox(
              height: 36,
              child: ListView(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(horizontal: 20),
                children: [
                  for (final (f, label) in [
                    (SellerRequestFilter.all, '전체'),
                    (SellerRequestFilter.drafting, '작성 중'),
                    (SellerRequestFilter.proposed, '제안 중'),
                    (SellerRequestFilter.confirmed, '확정'),
                    (SellerRequestFilter.expired, '만료'),
                  ]) ...[
                    _FilterTag(
                      label: label,
                      isSelected: filter == f,
                      onTap: () => ref.read(sellerRequestFilterProvider.notifier).state = f,
                    ),
                    const SizedBox(width: 8),
                  ],
                ],
              ),
            ),
            const SizedBox(height: 12),

            // 목록
            Expanded(
              child: asyncFiltered.when(
                loading: () => const Center(child: CircularProgressIndicator(color: _sage)),
                error: (e, _) => Center(child: Text('오류', style: AppTypography.body(fontSize: 14, color: ink60))),
                data: (reqs) {
                  if (reqs.isEmpty) {
                    return Center(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Text('📭', style: TextStyle(fontSize: 36)),
                          const SizedBox(height: 12),
                          Text(
                            '해당하는 요청이 없어요',
                            style: AppTypography.body(fontSize: 14, color: ink60),
                          ),
                        ],
                      ),
                    );
                  }

                  return ListView.separated(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    itemCount: reqs.length,
                    separatorBuilder: (_, _i) => const SizedBox(height: 10),
                    itemBuilder: (_, i) {
                      final r = reqs[i];
                      return _RequestCard(r: r);
                    },
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ── 요청 카드 ──

class _RequestCard extends StatelessWidget {
  const _RequestCard({required this.r});
  final dynamic r;

  @override
  Widget build(BuildContext context) {
    final tag = r.purposeTags.isNotEmpty ? r.purposeTags.first : '';
    final budget = switch (r.budgetTier as String) {
      'TIER1' => '작은 꽃다발',
      'TIER2' => '기본형',
      'TIER3' => '풍성한 꽃다발',
      _ => '프리미엄',
    };
    final type = r.fulfillmentType == 'PICKUP' ? '픽업' : '배송';

    // 상태에 따른 배지
    final (String badgeText, Color badgeColor, Color badgeBg) = switch (r.status as String) {
      'EXPIRED' => ('만료', ink60, const Color(0xFFF0EDEA)),
      'CONFIRMED' => ('마감', ink60, const Color(0xFFF0EDEA)),
      _ => r.myProposalStatus == 'DRAFT'
          ? ('작성중', const Color(0xFF8A6B20), const Color(0xFFFEF7EC))
          : r.myProposalStatus == 'SUBMITTED'
              ? ('제출완료', _sage, _sageLt)
              : ('미제안', _sage, _sageLt),
    };

    final isExpiredOrConfirmed = r.status == 'EXPIRED' || r.status == 'CONFIRMED';

    return GestureDetector(
      onTap: () => context.push('/seller/requests/${r.requestId}'),
      child: Opacity(
        opacity: isExpiredOrConfirmed ? 0.55 : 1.0,
        child: Container(
          padding: const EdgeInsets.all(14),
          decoration: BoxDecoration(
            color: whiteColor,
            borderRadius: kBorderRadiusMd,
            border: Border.all(color: borderColor),
          ),
          child: Row(
            children: [
              const Text('🎂', style: TextStyle(fontSize: 20)),
              const SizedBox(width: 10),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: Text(
                            '$tag · $budget',
                            style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600),
                          ),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                          decoration: BoxDecoration(color: badgeBg, borderRadius: BorderRadius.circular(4)),
                          child: Text(
                            badgeText,
                            style: AppTypography.mono(fontSize: 10, fontWeight: FontWeight.w500, color: badgeColor),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 3),
                    Text(
                      '${r.moodTags.join(" · ")} · $type · ${r.distance}',
                      style: AppTypography.body(fontSize: 11, color: ink60),
                    ),
                    if (!isExpiredOrConfirmed) ...[
                      const SizedBox(height: 4),
                      Row(
                        children: [
                          const Text('⏱ ', style: TextStyle(fontSize: 10)),
                          ExpiryTimer(expiresAt: DateTime.parse(r.expiresAt)),
                        ],
                      ),
                    ],
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

// ── 필터 태그 ──

class _FilterTag extends StatelessWidget {
  const _FilterTag({required this.label, required this.isSelected, required this.onTap});
  final String label;
  final bool isSelected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 7),
        decoration: BoxDecoration(
          color: isSelected ? _sageLt : creamColor,
          borderRadius: kBorderRadiusSm,
          border: Border.all(color: isSelected ? _sage : borderColor, width: 1.5),
        ),
        child: Text(
          label,
          style: AppTypography.body(
            fontSize: 12,
            fontWeight: isSelected ? FontWeight.w600 : FontWeight.w400,
            color: isSelected ? _sage : ink60,
          ),
        ),
      ),
    );
  }
}
