import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/buyer_request_provider.dart';
import '../widgets/common/expiry_timer.dart';

class BuyerRequestsTabScreen extends ConsumerWidget {
  const BuyerRequestsTabScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final selectedFilter = ref.watch(buyerRequestFilterProvider);
    final asyncRequests = ref.watch(filteredBuyerRequestsProvider);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 12),
              child: Text('내 요청', style: AppTypography.body(fontSize: 17, fontWeight: FontWeight.w700)),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Row(
                children: [
                  _FilterTag(
                    label: '전체',
                    isSelected: selectedFilter == BuyerRequestFilter.all,
                    onTap: () => ref.read(buyerRequestFilterProvider.notifier).state = BuyerRequestFilter.all,
                  ),
                  const SizedBox(width: 8),
                  _FilterTag(
                    label: '진행 중',
                    isSelected: selectedFilter == BuyerRequestFilter.active,
                    onTap: () => ref.read(buyerRequestFilterProvider.notifier).state = BuyerRequestFilter.active,
                  ),
                  const SizedBox(width: 8),
                  _FilterTag(
                    label: '완료',
                    isSelected: selectedFilter == BuyerRequestFilter.completed,
                    onTap: () => ref.read(buyerRequestFilterProvider.notifier).state = BuyerRequestFilter.completed,
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            Expanded(
              child: asyncRequests.when(
                loading: () => const Center(child: CircularProgressIndicator(color: roseColor)),
                error: (e, _) => Center(child: Text('오류', style: AppTypography.body(color: ink60))),
                data: (requests) {
                  if (requests.isEmpty) {
                    return _EmptyState(filter: selectedFilter);
                  }
                  return ListView.separated(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    itemCount: requests.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 10),
                    itemBuilder: (_, i) {
                      final r = requests[i];
                      final tag = r.purposeTags.isNotEmpty
                          ? r.purposeTags.first.replaceAll(RegExp(r'[^\w가-힣]'), '').trim()
                          : '꽃다발';
                      final typeLabel = r.fulfillmentType == 'PICKUP' ? '픽업' : '배송';
                      final budgetLabel = _budgetLabel(r.budgetTier);

                      return GestureDetector(
                        onTap: () => _onCardTap(context, r.status, r.requestId),
                        child: Container(
                          padding: const EdgeInsets.all(14),
                          decoration: BoxDecoration(
                            color: whiteColor,
                            borderRadius: kBorderRadiusMd,
                            border: Border.all(color: borderColor),
                          ),
                          child: Row(
                            children: [
                              const Text('🎂', style: TextStyle(fontSize: 22)),
                              const SizedBox(width: 10),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Row(
                                      children: [
                                        Expanded(
                                          child: Text('$tag 꽃다발', style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600)),
                                        ),
                                        _StatusBadge(status: r.status),
                                      ],
                                    ),
                                    const SizedBox(height: 4),
                                    Text('$budgetLabel · $typeLabel · ${r.fulfillmentDate}', style: AppTypography.body(fontSize: 11, color: ink60)),
                                    // 진행중 탭: 제안 수 + 타이머
                                    if (r.status == 'OPEN') ...[
                                      const SizedBox(height: 6),
                                      Row(
                                        children: [
                                          if (r.submittedProposalCount > 0) ...[
                                            Container(
                                              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                              decoration: BoxDecoration(color: roseLt, borderRadius: BorderRadius.circular(4)),
                                              child: Text(
                                                '${r.submittedProposalCount}개의 제안이 도착했어요',
                                                style: AppTypography.body(fontSize: 10, color: roseColor, fontWeight: FontWeight.w500),
                                              ),
                                            ),
                                            const Spacer(),
                                          ] else
                                            const Spacer(),
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
                      );
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

  void _onCardTap(BuildContext context, String status, int requestId) {
    switch (status) {
      case 'OPEN':
        context.push('/buyer/requests/$requestId');
      case 'CONFIRMED':
        // CONFIRMED 요청의 reservationId = requestId + 3000 (mock 매핑)
        context.push('/buyer/reservations/${requestId + 3000}');
      case 'EXPIRED':
        // 만료 카드는 이동 없음
        break;
    }
  }

  String _budgetLabel(String tier) => switch (tier) {
        'TIER1' => '작은 꽃다발',
        'TIER2' => '기본형',
        'TIER3' => '풍성한 꽃다발',
        'TIER4' => '프리미엄',
        _ => tier,
      };
}

// ── 상태 배지 ──

class _StatusBadge extends StatelessWidget {
  const _StatusBadge({required this.status});
  final String status;

  @override
  Widget build(BuildContext context) {
    final (label, bgColor, textColor) = switch (status) {
      'OPEN' => ('진행중', const Color(0xFFE8F0EC), const Color(0xFF5A7A68)),
      'CONFIRMED' => ('확정', const Color(0xFFE8F0EC), const Color(0xFF5A7A68)),
      'EXPIRED' => ('만료', const Color(0xFFF0EDE9), ink60),
      _ => (status, const Color(0xFFF0EDE9), ink60),
    };
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(color: bgColor, borderRadius: BorderRadius.circular(4)),
      child: Text(label, style: AppTypography.mono(fontSize: 10, fontWeight: FontWeight.w500, color: textColor)),
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
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 7),
        decoration: BoxDecoration(
          color: isSelected ? roseLt : creamColor,
          borderRadius: kBorderRadiusSm,
          border: Border.all(color: isSelected ? roseColor : borderColor, width: 1.5),
        ),
        child: Text(
          label,
          style: AppTypography.body(
            fontSize: 12,
            fontWeight: isSelected ? FontWeight.w600 : FontWeight.w400,
            color: isSelected ? roseColor : ink60,
          ),
        ),
      ),
    );
  }
}

// ── 빈 상태 ──

class _EmptyState extends ConsumerWidget {
  const _EmptyState({required this.filter});
  final BuyerRequestFilter filter;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final (emoji, message, showCta) = switch (filter) {
      BuyerRequestFilter.active => ('🌷', '진행 중인 요청이 없어요.\n꽃다발을 요청해보세요!', true),
      BuyerRequestFilter.completed => ('📭', '완료된 요청이 없어요', false),
      BuyerRequestFilter.all => ('🌷', '아직 요청이 없어요.\n꽃다발을 요청해보세요!', true),
    };

    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(emoji, style: const TextStyle(fontSize: 36)),
          const SizedBox(height: 12),
          Text(
            message,
            textAlign: TextAlign.center,
            style: AppTypography.body(fontSize: 14, color: ink60, height: 1.6),
          ),
          if (showCta) ...[
            const SizedBox(height: 16),
            GestureDetector(
              onTap: () => context.push('/buyer/request/step1'),
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                decoration: BoxDecoration(color: roseColor, borderRadius: kBorderRadiusSm),
                child: Text('요청하기', style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600, color: whiteColor)),
              ),
            ),
          ],
        ],
      ),
    );
  }
}
