import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/proposal_provider.dart';
import '../widgets/common/bottom_cta_button.dart';

class ReservationDoneScreen extends ConsumerWidget {
  const ReservationDoneScreen({super.key, required this.reservationId});
  final int reservationId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncRes = ref.watch(buyerReservationDetailProvider(reservationId));

    return Scaffold(
      backgroundColor: creamColor,
      body: asyncRes.when(
        loading: () => const Center(child: CircularProgressIndicator(color: roseColor)),
        error: (e, _) => Center(child: Text('오류')),
        data: (res) {
          final isPending = res.status == 'PENDING_CONTACT';
          final isConfirmed = res.status == 'CONFIRMED';

          return SafeArea(
            child: Column(
              children: [
                Expanded(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
                    child: Column(
                      children: [
                        const SizedBox(height: 32),
                        Text(isPending ? '💐' : '🎉', style: const TextStyle(fontSize: 48)),
                        const SizedBox(height: 14),
                        Text(
                          isPending ? '제안을 선택했어요!' : '예약이 확정됐어요!',
                          style: AppTypography.serif(fontSize: 24, fontWeight: FontWeight.w600),
                        ),
                        const SizedBox(height: 10),
                        Text(
                          isPending
                              ? '꽃집에서 연락이 올 거예요.\n잠시만 기다려주세요 🌷'
                              : '플로리스트가 소중한 꽃을\n정성껏 준비할 거예요 🌷',
                          textAlign: TextAlign.center,
                          style: AppTypography.body(fontSize: 12, color: ink60, height: 1.7),
                        ),
                        const SizedBox(height: 24),

                        // 상태 배지
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                          decoration: BoxDecoration(
                            color: isPending ? const Color(0xFFFFF3E0) : const Color(0xFFE8F0EC),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            isPending ? '꽃집 연락 대기 중' : '예약 확정',
                            style: AppTypography.body(
                              fontSize: 13,
                              fontWeight: FontWeight.w600,
                              color: isPending ? const Color(0xFFE65100) : const Color(0xFF5A7A68),
                            ),
                          ),
                        ),
                        const SizedBox(height: 20),

                        // 예약 정보 카드
                        Container(
                          width: double.infinity,
                          padding: const EdgeInsets.all(16),
                          decoration: BoxDecoration(
                            color: whiteColor,
                            borderRadius: kBorderRadiusLg,
                            border: Border.all(color: borderColor, width: 1.5),
                          ),
                          child: Column(
                            children: [
                              _row('꽃집', '${res.shopEmoji ?? "🌸"} ${res.shopName}'),
                              const SizedBox(height: 8),
                              _row('주소', res.shopAddress),
                              const SizedBox(height: 8),
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Text('픽업 시간', style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60)),
                                  Text(res.slotLabel, style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: roseColor)),
                                ],
                              ),
                              const SizedBox(height: 8),
                              _row('금액', '${NumberFormat('#,###').format(res.price)}원'),
                            ],
                          ),
                        ),

                        // 전화 버튼 (shopPhone이 있을 때)
                        if (res.shopPhone != null && res.shopPhone!.isNotEmpty) ...[
                          const SizedBox(height: 16),
                          SizedBox(
                            width: double.infinity,
                            height: 48,
                            child: OutlinedButton.icon(
                              onPressed: () async {
                                final uri = Uri.parse('tel:${res.shopPhone}');
                                if (!await launchUrl(uri)) {
                                  if (!context.mounted) return;
                                  ScaffoldMessenger.of(context).showSnackBar(
                                    const SnackBar(content: Text('전화 앱을 열 수 없어요')),
                                  );
                                }
                              },
                              icon: const Text('📞', style: TextStyle(fontSize: 16)),
                              label: Text(
                                '꽃집에 전화하기',
                                style: AppTypography.body(fontSize: 14, fontWeight: FontWeight.w600, color: roseColor),
                              ),
                              style: OutlinedButton.styleFrom(
                                side: const BorderSide(color: roseColor, width: 1.5),
                                shape: RoundedRectangleBorder(borderRadius: kBorderRadiusMd),
                              ),
                            ),
                          ),
                        ],
                      ],
                    ),
                  ),
                ),
                BottomCtaButton(
                  label: '홈으로 돌아가기',
                  onPressed: () => context.go('/buyer/home'),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _row(String label, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60)),
        Flexible(child: Text(value, style: AppTypography.body(fontSize: 12), textAlign: TextAlign.right)),
      ],
    );
  }
}
