import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import 'package:uuid/uuid.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/buyer_request_provider.dart';
import '../providers/proposal_provider.dart';
import '../widgets/common/app_nav_bar.dart';
import '../widgets/common/bottom_cta_button.dart';

class PaymentScreen extends ConsumerStatefulWidget {
  const PaymentScreen({super.key, required this.proposalId});

  final int proposalId;

  @override
  ConsumerState<PaymentScreen> createState() => _PaymentScreenState();
}

class _PaymentScreenState extends ConsumerState<PaymentScreen> {
  bool _isSubmitting = false;

  Future<void> _handlePayment() async {
    if (_isSubmitting) return;
    setState(() => _isSubmitting = true);

    try {
      final repo = ref.read(buyerRepositoryProvider);
      final idempotencyKey = const Uuid().v4();
      final result = await repo.selectProposal(widget.proposalId, idempotencyKey);
      final reservationId = result['reservationId'] as int;

      if (!mounted) return;

      // 예약 목록 캐시 갱신
      ref.invalidate(buyerReservationsListProvider);

      context.go('/buyer/reservations/$reservationId/done');
    } catch (e) {
      if (!mounted) return;
      setState(() => _isSubmitting = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('결제에 실패했어요. 다시 시도해주세요.'),
          backgroundColor: roseColor,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final asyncDetail = ref.watch(proposalDetailProvider(widget.proposalId));

    return Scaffold(
      backgroundColor: creamColor,
      body: asyncDetail.when(
        loading: () => const Center(child: CircularProgressIndicator(color: roseColor)),
        error: (e, _) => Center(child: Text('오류')),
        data: (detail) {
          final priceFormatted = NumberFormat('#,###').format(detail.price);
          return SafeArea(
            child: Column(
              children: [
                const AppNavBar(title: '결제'),
                Expanded(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.fromLTRB(18, 8, 18, 16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Container(
                          width: double.infinity,
                          padding: const EdgeInsets.all(16),
                          decoration: BoxDecoration(
                            color: creamColor,
                            borderRadius: kBorderRadiusMd,
                            border: Border.all(color: borderColor),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                children: [
                                  Text(detail.shopEmoji ?? '🌸', style: const TextStyle(fontSize: 28)),
                                  const SizedBox(width: 10),
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text(detail.conceptTitle, style: AppTypography.body(fontSize: 13, fontWeight: FontWeight.w700)),
                                        const SizedBox(height: 2),
                                        Text('${detail.shopName} · 픽업 ${detail.slotLabel}', style: AppTypography.body(fontSize: 11, color: ink60)),
                                      ],
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 14),
                              Divider(color: borderColor, height: 1),
                              const SizedBox(height: 10),
                              _priceRow('꽃다발 가격', '$priceFormatted원', false),
                              const SizedBox(height: 8),
                              _priceRow('최종 결제', '$priceFormatted원', true),
                            ],
                          ),
                        ),
                        const SizedBox(height: 20),
                        Text('결제 방법', style: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w700, color: ink60)),
                        const SizedBox(height: 10),
                        GridView.count(
                          crossAxisCount: 2,
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          mainAxisSpacing: 8,
                          crossAxisSpacing: 8,
                          childAspectRatio: 2.8,
                          children: [
                            _PaymentMethod(label: '💳 신용카드', isSelected: true),
                            _PaymentMethod(label: '📱 카카오페이', isSelected: false),
                            _PaymentMethod(label: '🍎 애플페이', isSelected: false),
                            _PaymentMethod(label: '🏦 계좌이체', isSelected: false),
                          ],
                        ),
                        const SizedBox(height: 16),
                        Container(
                          width: double.infinity,
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: const Color(0xFFFEF0EE),
                            borderRadius: kBorderRadiusSm,
                            border: Border.all(color: const Color(0xFFF0B8B0)),
                          ),
                          child: Text(
                            '🧪 테스트 모드 — 실제 결제가 이루어지지 않아요',
                            style: AppTypography.body(fontSize: 11, color: const Color(0xFF8A3020)),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                BottomCtaButton(
                  label: _isSubmitting ? '처리 중...' : '$priceFormatted원 결제하기',
                  onPressed: _handlePayment,
                  enabled: !_isSubmitting,
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _priceRow(String label, String value, bool isTotal) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: AppTypography.body(fontSize: isTotal ? 13 : 12, fontWeight: isTotal ? FontWeight.w700 : FontWeight.w400, color: ink60)),
        Text(value, style: AppTypography.body(fontSize: isTotal ? 17 : 12, fontWeight: isTotal ? FontWeight.w700 : FontWeight.w400, color: isTotal ? roseColor : inkColor)),
      ],
    );
  }
}

class _PaymentMethod extends StatelessWidget {
  const _PaymentMethod({required this.label, required this.isSelected});
  final String label;
  final bool isSelected;

  @override
  Widget build(BuildContext context) {
    return Container(
      alignment: Alignment.center,
      decoration: BoxDecoration(
        color: isSelected ? roseLt : creamColor,
        borderRadius: kBorderRadiusSm,
        border: Border.all(color: isSelected ? roseColor : borderColor, width: 1.5),
      ),
      child: Text(label, style: AppTypography.body(fontSize: 12, fontWeight: isSelected ? FontWeight.w600 : FontWeight.w400, color: isSelected ? roseColor : inkColor)),
    );
  }
}
