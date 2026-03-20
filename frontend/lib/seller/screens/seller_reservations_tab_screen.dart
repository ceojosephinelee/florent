import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/seller_providers.dart';

const _sage = Color(0xFF5A7A68);
const _sageLt = Color(0xFFE8F0EC);

class SellerReservationsTabScreen extends ConsumerWidget {
  const SellerReservationsTabScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncReservations = ref.watch(sellerReservationHistoryProvider);

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
                loading: () => const Center(child: CircularProgressIndicator(color: _sage)),
                error: (e, _) => Center(child: Text('오류', style: AppTypography.body(color: ink60))),
                data: (reservations) {
                  if (reservations.isEmpty) {
                    return Center(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Text('📭', style: TextStyle(fontSize: 36)),
                          const SizedBox(height: 12),
                          Text('아직 확정된 예약이 없어요', style: AppTypography.body(fontSize: 14, color: ink60)),
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
                      return GestureDetector(
                        onTap: () => context.push('/seller/reservations/${r.reservationId}'),
                        child: Container(
                          padding: const EdgeInsets.all(14),
                          decoration: BoxDecoration(
                            color: whiteColor,
                            borderRadius: kBorderRadiusMd,
                            border: Border.all(color: borderColor),
                          ),
                          child: Row(
                            children: [
                              const Text('🌸', style: TextStyle(fontSize: 20)),
                              const SizedBox(width: 10),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      r.conceptTitle,
                                      style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w600),
                                      overflow: TextOverflow.ellipsis,
                                    ),
                                    const SizedBox(height: 3),
                                    Text(
                                      '$typeLabel · ${_formatPrice(r.price)}원',
                                      style: AppTypography.body(fontSize: 11, color: ink60),
                                    ),
                                  ],
                                ),
                              ),
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                decoration: BoxDecoration(color: _sageLt, borderRadius: BorderRadius.circular(4)),
                                child: Text('확정', style: AppTypography.mono(fontSize: 10, fontWeight: FontWeight.w500, color: _sage)),
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

  String _formatPrice(int price) {
    return price.toString().replaceAllMapped(
      RegExp(r'(\d)(?=(\d{3})+(?!\d))'),
      (m) => '${m[1]},',
    );
  }
}
