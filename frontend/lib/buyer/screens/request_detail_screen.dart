import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/proposal_provider.dart';
import '../widgets/common/app_nav_bar.dart';
import '../widgets/common/expiry_timer.dart';

class RequestDetailScreen extends ConsumerWidget {
  const RequestDetailScreen({super.key, required this.requestId});

  final int requestId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncProposals = ref.watch(proposalsProvider(requestId));
    final expiresAt = DateTime.now().add(const Duration(hours: 22, minutes: 14));

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            AppNavBar(title: '요청 상세'),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(18, 0, 18, 24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Text('⏱ 요청 만료까지 ', style: AppTypography.body(fontSize: 12, color: ink60)),
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
                          _infoLine('목적', '🎂 생일 · 부모님'),
                          _infoLine('분위기', '로맨틱 · 자연스러운'),
                          _infoLine('예산', '기본형 (6~8만원)'),
                          _infoLine('수령', '픽업 · 3/15 14:00'),
                        ],
                      ),
                    ),
                    const SizedBox(height: 16),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: roseLt,
                        borderRadius: kBorderRadiusSm,
                      ),
                      child: Text(
                        '1명의 플로리스트가 제안서를 작성 중이에요',
                        style: AppTypography.body(fontSize: 12, color: roseColor),
                        textAlign: TextAlign.center,
                      ),
                    ),
                    const SizedBox(height: 20),
                    Text('도착한 제안 (2)', style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w700)),
                    const SizedBox(height: 12),
                    asyncProposals.when(
                      loading: () => const Center(child: CircularProgressIndicator(color: roseColor)),
                      error: (e, _) => Text('오류', style: AppTypography.body(color: ink60)),
                      data: (proposals) => Column(
                        children: proposals.map((p) => _ProposalListItem(
                          proposal: p,
                          onTap: () => context.push('/buyer/proposals/${p.proposalId}'),
                        )).toList(),
                      ),
                    ),
                  ],
                ),
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
            child: Text(label, style: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600, color: ink60)),
          ),
          Expanded(child: Text(value, style: AppTypography.body(fontSize: 11))),
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
                Text(proposal.shopEmoji ?? '🌸', style: const TextStyle(fontSize: 18)),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    proposal.conceptTitle,
                    style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600),
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(color: roseLt, borderRadius: BorderRadius.circular(4)),
                  child: Text(
                    '⏱ ${Duration(milliseconds: DateTime.parse(proposal.expiresAt).difference(DateTime.now()).inMilliseconds).inHours}h',
                    style: AppTypography.mono(fontSize: 10, color: roseColor),
                  ),
                ),
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
