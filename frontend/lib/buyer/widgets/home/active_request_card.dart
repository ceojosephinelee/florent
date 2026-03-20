import 'package:flutter/material.dart';

import '../../../core/models/buyer_request.dart';
import '../../../core/models/enums.dart';
import '../../../core/theme/colors.dart';
import '../../../core/theme/radius.dart';
import '../../../core/theme/typography.dart';
import '../common/expiry_timer.dart';

class ActiveRequestCard extends StatelessWidget {
  const ActiveRequestCard({
    super.key,
    required this.request,
    this.onTap,
  });

  final BuyerRequestSummary request;
  final VoidCallback? onTap;

  String get _displayName {
    final parts = <String>[
      if (request.purposeTags.isNotEmpty) request.purposeTags.first,
      if (request.relationTags.isNotEmpty) request.relationTags.first,
    ];
    return parts.isNotEmpty ? parts.join(' · ') : '꽃다발 요청';
  }

  String get _fulfillmentLabel {
    return request.fulfillmentType == FulfillmentType.pickup.value
        ? FulfillmentType.pickup.label
        : FulfillmentType.delivery.label;
  }

  String get _budgetLabel {
    return BudgetTier.values
        .firstWhere(
          (t) => t.value == request.budgetTier,
          orElse: () => BudgetTier.tier2,
        )
        .label;
  }

  @override
  Widget build(BuildContext context) {
    final expiresAt = DateTime.parse(request.expiresAt);

    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(16),
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
                Text('🌸', style: TextStyle(fontSize: 20)),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    _displayName,
                    style: AppTypography.body(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                _ProposalBadge(count: request.submittedProposalCount),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                _MetaChip(label: _fulfillmentLabel),
                const SizedBox(width: 6),
                _MetaChip(label: _budgetLabel),
                const SizedBox(width: 6),
                _MetaChip(label: request.fulfillmentDate),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Icon(Icons.timer_outlined, size: 16, color: ink60),
                const SizedBox(width: 4),
                Text('남은 시간 ', style: AppTypography.body(fontSize: 12, color: ink60)),
                ExpiryTimer(expiresAt: expiresAt),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _ProposalBadge extends StatelessWidget {
  const _ProposalBadge({required this.count});

  final int count;

  @override
  Widget build(BuildContext context) {
    if (count == 0) return const SizedBox.shrink();
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: roseLt,
        borderRadius: BorderRadius.circular(borderRadiusSm),
      ),
      child: Text(
        '제안 $count',
        style: AppTypography.mono(
          fontSize: 12,
          fontWeight: FontWeight.w500,
          color: roseColor,
        ),
      ),
    );
  }
}

class _MetaChip extends StatelessWidget {
  const _MetaChip({required this.label});

  final String label;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: creamColor,
        borderRadius: BorderRadius.circular(borderRadiusSm),
      ),
      child: Text(
        label,
        style: AppTypography.body(fontSize: 12, color: ink60),
      ),
    );
  }
}
