import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/proposal_provider.dart';
import '../widgets/common/app_nav_bar.dart';
import '../widgets/common/bottom_cta_button.dart';

class ProposalDetailScreen extends ConsumerWidget {
  const ProposalDetailScreen({super.key, required this.proposalId});

  final int proposalId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncDetail = ref.watch(proposalDetailProvider(proposalId));

    return Scaffold(
      backgroundColor: creamColor,
      body: asyncDetail.when(
        loading: () => const Center(child: CircularProgressIndicator(color: roseColor)),
        error: (e, _) => Center(child: Text('오류', style: AppTypography.body(color: ink60))),
        data: (detail) => SafeArea(
          child: Column(
            children: [
              const AppNavBar(title: '제안 상세'),
              Expanded(
                child: SingleChildScrollView(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        width: double.infinity,
                        height: 210,
                        color: roseLt,
                        alignment: Alignment.center,
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(detail.shopEmoji ?? '🌸', style: const TextStyle(fontSize: 48)),
                            const SizedBox(height: 8),
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                              decoration: BoxDecoration(color: whiteColor.withValues(alpha: 0.8), borderRadius: BorderRadius.circular(4)),
                              child: Text('참고용 이미지', style: AppTypography.body(fontSize: 9, color: ink60)),
                            ),
                          ],
                        ),
                      ),
                      Padding(
                        padding: const EdgeInsets.fromLTRB(18, 16, 18, 24),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(detail.conceptTitle, style: AppTypography.body(fontSize: 15, fontWeight: FontWeight.w700)),
                            const SizedBox(height: 6),
                            Text(
                              '${detail.shopEmoji} ${detail.shopName} · ${detail.shopAddress} ${detail.shopDistance}',
                              style: AppTypography.body(fontSize: 11, color: ink60),
                            ),
                            const SizedBox(height: 12),
                            Text(detail.description, style: AppTypography.body(fontSize: 12, color: ink60, height: 1.6)),
                            const SizedBox(height: 16),
                            Container(
                              width: double.infinity,
                              padding: const EdgeInsets.all(14),
                              decoration: BoxDecoration(
                                color: creamColor,
                                borderRadius: kBorderRadiusMd,
                                border: Border.all(color: borderColor),
                              ),
                              child: Column(
                                children: [
                                  _row('픽업 시간', detail.slotLabel ?? '-'),
                                  const SizedBox(height: 6),
                                  _row('픽업 장소', detail.shopAddress),
                                  const SizedBox(height: 6),
                                  Row(
                                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                    children: [
                                      Text('제안 가격', style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60)),
                                      Text(
                                        '${NumberFormat('#,###').format(detail.price)}원',
                                        style: AppTypography.body(fontSize: 16, fontWeight: FontWeight.w700, color: roseColor),
                                      ),
                                    ],
                                  ),
                                ],
                              ),
                            ),
                            const SizedBox(height: 12),
                            Center(
                              child: Text(
                                '⏱ 이 제안은 ${Duration(milliseconds: DateTime.parse(detail.expiresAt).difference(DateTime.now()).inMilliseconds).inHours}시간 후 만료돼요',
                                style: AppTypography.body(fontSize: 11, color: ink60),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              BottomCtaButton(
                label: '이 제안 선택하기 →',
                onPressed: () => context.push('/buyer/proposals/$proposalId/pay'),
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
