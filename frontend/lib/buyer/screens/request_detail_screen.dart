import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/models/enums.dart';
import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/buyer_request_provider.dart';
import '../providers/proposal_provider.dart';
import '../widgets/common/app_nav_bar.dart';
import '../widgets/common/expiry_timer.dart';

class RequestDetailScreen extends ConsumerWidget {
  const RequestDetailScreen({super.key, required this.requestId});

  final int requestId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncDetail = ref.watch(buyerRequestDetailProvider(requestId));
    final asyncProposals = ref.watch(proposalsProvider(requestId));

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            const AppNavBar(title: '요청 상세'),
            Expanded(
              child: asyncDetail.when(
                loading: () => const Center(
                    child: CircularProgressIndicator(color: roseColor)),
                error: (e, _) => Center(
                    child: Text('요청 정보를 불러올 수 없어요',
                        style: AppTypography.body(color: ink60))),
                data: (detail) {
                  final expiresAt =
                      DateTime.parse(detail['expiresAt'] as String);
                  final purposeTags =
                      List<String>.from(detail['purposeTags'] ?? []);
                  final relationTags =
                      List<String>.from(detail['relationTags'] ?? []);
                  final moodTags =
                      List<String>.from(detail['moodTags'] ?? []);
                  final budget = BudgetTier.values
                      .where((t) => t.value == detail['budgetTier'])
                      .firstOrNull;
                  final fulfillmentType =
                      detail['fulfillmentType'] as String? ?? '';
                  final fulfillmentDate =
                      detail['fulfillmentDate'] as String? ?? '';
                  final slots = detail['requestedTimeSlots'] as List? ?? [];
                  final slotLabel = slots.isNotEmpty
                      ? (slots.first as Map)['value'] as String? ?? ''
                      : '';
                  final draftCount =
                      detail['draftProposalCount'] as int? ?? 0;
                  final submittedCount =
                      detail['submittedProposalCount'] as int? ?? 0;

                  final typeLabel =
                      fulfillmentType == 'PICKUP' ? '픽업' : '배송';

                  return SingleChildScrollView(
                    padding: const EdgeInsets.fromLTRB(18, 0, 18, 24),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            Text('⏱ 요청 만료까지 ',
                                style: AppTypography.body(
                                    fontSize: 12, color: ink60)),
                            ExpiryTimer(expiresAt: expiresAt),
                          ],
                        ),
                        const SizedBox(height: 12),
                        Container(
                          width: double.infinity,
                          padding: const EdgeInsets.all(14),
                          decoration: BoxDecoration(
                            color: creamColor,
                            borderRadius: kBorderRadiusMd,
                            border: Border.all(color: borderColor),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              if (purposeTags.isNotEmpty)
                                _infoLine('목적',
                                    purposeTags.join(' · ')),
                              if (relationTags.isNotEmpty)
                                _infoLine('관계',
                                    relationTags.join(' · ')),
                              if (moodTags.isNotEmpty)
                                _infoLine('분위기',
                                    moodTags.join(' · ')),
                              if (budget != null)
                                _infoLine('예산',
                                    '${budget.label} (${budget.priceRange})'),
                              _infoLine('수령',
                                  '$typeLabel · $fulfillmentDate ${slotLabel.isNotEmpty ? slotLabel : ''}'),
                            ],
                          ),
                        ),
                        const SizedBox(height: 16),
                        if (draftCount > 0)
                          Container(
                            width: double.infinity,
                            padding: const EdgeInsets.all(12),
                            decoration: BoxDecoration(
                              color: roseLt,
                              borderRadius: kBorderRadiusSm,
                            ),
                            child: Text(
                              '$draftCount명의 플로리스트가 제안서를 작성 중이에요',
                              style: AppTypography.body(
                                  fontSize: 12, color: roseColor),
                              textAlign: TextAlign.center,
                            ),
                          ),
                        const SizedBox(height: 20),
                        asyncProposals.when(
                          loading: () => const Center(
                              child: CircularProgressIndicator(
                                  color: roseColor)),
                          error: (e, _) => Text('제안 목록을 불러올 수 없어요',
                              style: AppTypography.body(color: ink60)),
                          data: (proposals) => Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                '도착한 제안 ($submittedCount)',
                                style: AppTypography.body(
                                    fontSize: 15,
                                    fontWeight: FontWeight.w700),
                              ),
                              const SizedBox(height: 12),
                              if (proposals.isEmpty)
                                Container(
                                  width: double.infinity,
                                  padding: const EdgeInsets.all(24),
                                  alignment: Alignment.center,
                                  child: Text(
                                    '아직 도착한 제안이 없어요',
                                    style: AppTypography.body(
                                        fontSize: 13, color: ink60),
                                  ),
                                )
                              else
                                ...proposals.map((p) => _ProposalListItem(
                                      proposal: p,
                                      onTap: () => context.push(
                                          '/buyer/proposals/${p.proposalId}'),
                                    )),
                            ],
                          ),
                        ),
                      ],
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _infoLine(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 3),
      child: Row(
        children: [
          SizedBox(
            width: 50,
            child: Text(label,
                style: AppTypography.body(
                    fontSize: 11,
                    fontWeight: FontWeight.w600,
                    color: ink60)),
          ),
          Expanded(
              child: Text(value, style: AppTypography.body(fontSize: 11))),
        ],
      ),
    );
  }
}

class _ProposalListItem extends StatelessWidget {
  const _ProposalListItem({required this.proposal, this.onTap});

  final dynamic proposal;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: whiteColor,
          borderRadius: kBorderRadiusMd,
          border: Border.all(color: borderColor),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text(proposal.shopEmoji ?? '🌸',
                    style: const TextStyle(fontSize: 18)),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    proposal.conceptTitle,
                    style: AppTypography.body(
                        fontSize: 13, fontWeight: FontWeight.w600),
                  ),
                ),
                Builder(builder: (_) {
                  try {
                    final expires = DateTime.parse(proposal.expiresAt);
                    final hours =
                        expires.difference(DateTime.now()).inHours;
                    return Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                          color: roseLt,
                          borderRadius: BorderRadius.circular(4)),
                      child: Text(
                        '⏱ ${hours}h',
                        style: AppTypography.mono(
                            fontSize: 10, color: roseColor),
                      ),
                    );
                  } catch (_) {
                    return const SizedBox.shrink();
                  }
                }),
              ],
            ),
            const SizedBox(height: 6),
            Text(
              '${proposal.shopName} · 📍 ${proposal.shopDistance ?? ""}',
              style: AppTypography.body(fontSize: 11, color: ink60),
            ),
          ],
        ),
      ),
    );
  }
}
