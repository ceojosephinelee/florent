import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/proposal_provider.dart';

class BuyerReservationsTabScreen extends ConsumerWidget {
  const BuyerReservationsTabScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncReservations = ref.watch(buyerReservationsListProvider);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 12),
              child: Text('예약', style: AppTypography.body(fontSize: 17, fontWeight: FontWeight.w700)),
            ),
            Expanded(
              child: asyncReservations.when(
                loading: () => const Center(child: CircularProgressIndicator(color: roseColor)),
                error: (e, _) => Center(child: Text('오류', style: AppTypography.body(color: ink60))),
                data: (reservations) {
                  if (reservations.isEmpty) {
                    return Center(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Text('📅', style: TextStyle(fontSize: 36)),
                          const SizedBox(height: 12),
                          Text(
                            '아직 확정된 예약이 없어요',
                            style: AppTypography.body(fontSize: 14, color: ink60),
                          ),
                        ],
                      ),
                    );
                  }
                  return ListView.separated(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    itemCount: reservations.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 10),
                    itemBuilder: (_, i) {
                      final r = reservations[i];
                      final typeLabel = r.fulfillmentType == 'PICKUP' ? '픽업' : '배송';
                      final slotLabel = _formatSlot(r.fulfillmentSlotKind, r.fulfillmentSlotValue);

                      return GestureDetector(
                        onTap: () => context.push('/buyer/reservations/${r.reservationId}'),
                        child: Container(
                          padding: const EdgeInsets.all(14),
                          decoration: BoxDecoration(
                            color: whiteColor,
                            borderRadius: kBorderRadiusMd,
                            border: Border.all(color: borderColor),
                          ),
                          child: Row(
                            children: [
                              Text(r.shopEmoji ?? '💐', style: const TextStyle(fontSize: 22)),
                              const SizedBox(width: 10),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Row(
                                      children: [
                                        Expanded(
                                          child: Text(
                                            r.conceptTitle,
                                            style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600),
                                            maxLines: 1,
                                            overflow: TextOverflow.ellipsis,
                                          ),
                                        ),
                                        _FulfillmentBadge(label: typeLabel),
                                      ],
                                    ),
                                    const SizedBox(height: 4),
                                    Text(
                                      r.shopName,
                                      style: AppTypography.body(fontSize: 11, color: ink60),
                                    ),
                                    const SizedBox(height: 4),
                                    Row(
                                      children: [
                                        Expanded(
                                          child: Text(
                                            '${r.fulfillmentDate} · $slotLabel',
                                            style: AppTypography.body(fontSize: 11, color: ink60),
                                          ),
                                        ),
                                        Text(
                                          _formatPrice(r.price),
                                          style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600),
                                        ),
                                      ],
                                    ),
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

  String _formatSlot(String kind, String value) {
    if (kind == 'PICKUP_30M') return '$value 픽업';
    return switch (value) {
      'MORNING' => '오전',
      'AFTERNOON' => '오후',
      'EVENING' => '저녁',
      _ => value,
    };
  }

  String _formatPrice(int price) {
    final str = price.toString();
    final buffer = StringBuffer();
    for (var i = 0; i < str.length; i++) {
      if (i > 0 && (str.length - i) % 3 == 0) buffer.write(',');
      buffer.write(str[i]);
    }
    return '${buffer}원';
  }
}

class _FulfillmentBadge extends StatelessWidget {
  const _FulfillmentBadge({required this.label});
  final String label;

  @override
  Widget build(BuildContext context) {
    final isPickup = label == '픽업';
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(
        color: isPickup ? const Color(0xFFE8F0EC) : roseLt,
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(
        label,
        style: AppTypography.mono(
          fontSize: 10,
          fontWeight: FontWeight.w500,
          color: isPickup ? sageColor : roseColor,
        ),
      ),
    );
  }
}