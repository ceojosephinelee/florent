import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/proposal_provider.dart';
import '../widgets/common/app_nav_bar.dart';

const _rose = Color(0xFFC8614E);
const _roseLt = Color(0xFFF2E8E5);

class BuyerReservationDetailScreen extends ConsumerWidget {
  const BuyerReservationDetailScreen({super.key, required this.reservationId});
  final int reservationId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncDetail = ref.watch(buyerReservationDetailProvider(reservationId));

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            const AppNavBar(title: '예약 상세'),
            Expanded(
              child: asyncDetail.when(
                loading: () => const Center(child: CircularProgressIndicator(color: _rose)),
                error: (e, _) => Center(child: Text('오류: $e', style: AppTypography.body(fontSize: 14, color: ink60))),
                data: (d) => SingleChildScrollView(
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // 상태 배지
                      Center(
                        child: Container(
                          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                          decoration: BoxDecoration(color: _roseLt, borderRadius: kBorderRadiusSm),
                          child: Text('✅ 예약 확정', style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600, color: _rose)),
                        ),
                      ),
                      const SizedBox(height: 20),

                      // 판매자 정보
                      _SectionCard(
                        title: '판매자 정보',
                        children: [
                          _InfoRow(label: '가게 이름', value: '${d.shopEmoji ?? "🌸"} ${d.shopName}'),
                        ],
                      ),
                      const SizedBox(height: 12),

                      // 꽃다발 정보
                      _SectionCard(
                        title: '꽃다발 정보',
                        children: [
                          _InfoRow(label: '꽃다발 이름', value: d.conceptTitle),
                          const SizedBox(height: 8),
                          Text('큐레이션 설명', style: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600, color: ink60)),
                          const SizedBox(height: 4),
                          Text(d.description, style: AppTypography.body(fontSize: 12, color: inkColor, height: 1.6)),
                          if (d.imageUrls.isNotEmpty) ...[
                            const SizedBox(height: 10),
                            SizedBox(
                              height: 80,
                              child: ListView.separated(
                                scrollDirection: Axis.horizontal,
                                itemCount: d.imageUrls.length,
                                separatorBuilder: (_, __) => const SizedBox(width: 8),
                                itemBuilder: (_, i) => Container(
                                  width: 80,
                                  height: 80,
                                  decoration: BoxDecoration(
                                    color: _roseLt,
                                    borderRadius: kBorderRadiusSm,
                                    border: Border.all(color: borderColor),
                                  ),
                                  alignment: Alignment.center,
                                  child: const Text('🌸', style: TextStyle(fontSize: 24)),
                                ),
                              ),
                            ),
                          ],
                          const SizedBox(height: 10),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text('최종 결제 금액', style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w700, color: ink60)),
                              Text(
                                '${_formatPrice(d.price)}원',
                                style: AppTypography.body(fontSize: 16, fontWeight: FontWeight.w700, color: _rose),
                              ),
                            ],
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),

                      // 수령 정보
                      _SectionCard(
                        title: '수령 정보',
                        children: [
                          Row(
                            children: [
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                decoration: BoxDecoration(
                                  color: d.fulfillmentType == 'PICKUP' ? const Color(0xFFE8F0EC) : _roseLt,
                                  borderRadius: BorderRadius.circular(4),
                                ),
                                child: Text(
                                  d.fulfillmentType == 'PICKUP' ? '픽업' : '배송',
                                  style: AppTypography.mono(
                                    fontSize: 11,
                                    fontWeight: FontWeight.w600,
                                    color: d.fulfillmentType == 'PICKUP' ? const Color(0xFF5A7A68) : _rose,
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 8),
                          _InfoRow(label: '날짜', value: d.fulfillmentDate),
                          const SizedBox(height: 6),
                          _InfoRow(
                            label: '시간',
                            value: _formatSlot(d.fulfillmentSlotKind, d.fulfillmentSlotValue),
                            valueColor: _rose,
                          ),
                          const SizedBox(height: 6),
                          _InfoRow(
                            label: d.fulfillmentType == 'PICKUP' ? '픽업 장소' : '배송지',
                            value: d.placeAddressText,
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),

                      // 내 요청 원문 요약 (접힘/펼침)
                      _ExpandableRequestSummary(
                        purposeTags: d.purposeTags,
                        relationTags: d.relationTags,
                        moodTags: d.moodTags,
                        budgetTier: d.budgetTier,
                      ),
                      const SizedBox(height: 24),
                    ],
                  ),
                ),
              ),
            ),
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

  String _formatSlot(String kind, String value) {
    if (kind == 'DELIVERY_WINDOW') {
      return switch (value) {
        'MORNING' => '오전 (09:00~12:00)',
        'AFTERNOON' => '오후 (12:00~17:00)',
        'EVENING' => '저녁 (17:00~21:00)',
        _ => value,
      };
    }
    return value;
  }
}

// ── 섹션 카드 ──

class _SectionCard extends StatelessWidget {
  const _SectionCard({required this.title, required this.children});
  final String title;
  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: whiteColor,
        borderRadius: kBorderRadiusMd,
        border: Border.all(color: borderColor, width: 1.5),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(title, style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w700)),
          const SizedBox(height: 12),
          ...children,
        ],
      ),
    );
  }
}

// ── 정보 행 ──

class _InfoRow extends StatelessWidget {
  const _InfoRow({required this.label, required this.value, this.valueColor});
  final String label;
  final String value;
  final Color? valueColor;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60)),
        const SizedBox(width: 16),
        Flexible(
          child: Text(
            value,
            style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: valueColor ?? inkColor),
            textAlign: TextAlign.right,
          ),
        ),
      ],
    );
  }
}

// ── 요청 원문 접힘/펼침 ──

class _ExpandableRequestSummary extends StatefulWidget {
  const _ExpandableRequestSummary({
    required this.purposeTags,
    required this.relationTags,
    required this.moodTags,
    this.budgetTier,
  });
  final List<String> purposeTags;
  final List<String> relationTags;
  final List<String> moodTags;
  final String? budgetTier;

  @override
  State<_ExpandableRequestSummary> createState() => _ExpandableRequestSummaryState();
}

class _ExpandableRequestSummaryState extends State<_ExpandableRequestSummary> {
  bool _expanded = false;

  String _budgetLabel(String? tier) => switch (tier) {
        'TIER1' => '작게 (3~5만원)',
        'TIER2' => '보통 (6~8만원)',
        'TIER3' => '크게 (9~13만원)',
        'TIER4' => '프리미엄 (15만원+)',
        _ => '-',
      };

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: whiteColor,
        borderRadius: kBorderRadiusMd,
        border: Border.all(color: borderColor, width: 1.5),
      ),
      child: Column(
        children: [
          GestureDetector(
            onTap: () => setState(() => _expanded = !_expanded),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  Text('내 요청 원문 요약', style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w700)),
                  const Spacer(),
                  Icon(_expanded ? Icons.expand_less : Icons.expand_more, size: 20, color: ink60),
                ],
              ),
            ),
          ),
          AnimatedCrossFade(
            firstChild: const SizedBox.shrink(),
            secondChild: Padding(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Divider(height: 1, color: borderColor),
                  const SizedBox(height: 12),
                  _tagRow('목적', widget.purposeTags),
                  const SizedBox(height: 8),
                  _tagRow('관계', widget.relationTags),
                  const SizedBox(height: 8),
                  _tagRow('분위기', widget.moodTags),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      Text('예산', style: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600, color: ink60)),
                      const SizedBox(width: 12),
                      Text(_budgetLabel(widget.budgetTier), style: AppTypography.body(fontSize: 11)),
                    ],
                  ),
                ],
              ),
            ),
            crossFadeState: _expanded ? CrossFadeState.showSecond : CrossFadeState.showFirst,
            duration: const Duration(milliseconds: 200),
          ),
        ],
      ),
    );
  }

  Widget _tagRow(String label, List<String> tags) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          width: 40,
          child: Text(label, style: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600, color: ink60)),
        ),
        const SizedBox(width: 8),
        Expanded(
          child: Wrap(
            spacing: 6,
            runSpacing: 4,
            children: tags
                .map((t) => Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                      decoration: BoxDecoration(
                        color: creamColor,
                        borderRadius: BorderRadius.circular(4),
                        border: Border.all(color: borderColor),
                      ),
                      child: Text(t, style: AppTypography.body(fontSize: 10)),
                    ))
                .toList(),
          ),
        ),
      ],
    );
  }
}
