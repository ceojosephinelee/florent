import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';

const _sage = Color(0xFF5A7A68);

class SellerReservationDoneScreen extends StatelessWidget {
  const SellerReservationDoneScreen({super.key, required this.reservationId});
  final int reservationId;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
                child: Column(
                  children: [
                    const SizedBox(height: 32),
                    const Text('🎊', style: TextStyle(fontSize: 48)),
                    const SizedBox(height: 14),
                    Text('예약이 확정됐어요!', style: AppTypography.serif(fontSize: 24, fontWeight: FontWeight.w600, color: _sage)),
                    const SizedBox(height: 10),
                    Text('구매자가 제안을 선택했어요.\n소중한 꽃다발을 준비해주세요 🌷', textAlign: TextAlign.center, style: AppTypography.body(fontSize: 12, color: ink60, height: 1.7)),
                    const SizedBox(height: 24),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(color: whiteColor, borderRadius: kBorderRadiusLg, border: Border.all(color: borderColor, width: 1.5)),
                      child: Column(
                        children: [
                          _row('구매자', '이지수 님'),
                          const SizedBox(height: 8),
                          _row('연락처', '010-****-1234'),
                          const SizedBox(height: 8),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text('픽업 시간', style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60)),
                              Text('3/15 (토) 14:00', style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: _sage)),
                            ],
                          ),
                          const SizedBox(height: 8),
                          _row('픽업 장소', '우리 가게 (내방)'),
                          const SizedBox(height: 8),
                          _row('확정 금액', '68,000원'),
                          const SizedBox(height: 8),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text('상태', style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60)),
                              Container(
                                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                decoration: BoxDecoration(color: const Color(0xFFE8F0EC), borderRadius: BorderRadius.circular(4)),
                                child: Text('✅ 예약 확정', style: AppTypography.body(fontSize: 11, fontWeight: FontWeight.w600, color: _sage)),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
            SafeArea(
              top: false,
              child: Padding(
                padding: const EdgeInsets.fromLTRB(18, 8, 18, 12),
                child: SizedBox(
                  width: double.infinity,
                  height: 48,
                  child: ElevatedButton(
                    onPressed: () => context.go('/seller/home'),
                    style: ElevatedButton.styleFrom(backgroundColor: _sage, foregroundColor: whiteColor, shape: RoundedRectangleBorder(borderRadius: kBorderRadiusLg), elevation: 0),
                    child: Text('홈으로 돌아가기', style: AppTypography.body(fontSize: 14, fontWeight: FontWeight.w600, color: whiteColor)),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _row(String label, String value) => Row(
    mainAxisAlignment: MainAxisAlignment.spaceBetween,
    children: [
      Text(label, style: AppTypography.body(fontSize: 12, fontWeight: FontWeight.w600, color: ink60)),
      Text(value, style: AppTypography.body(fontSize: 12)),
    ],
  );
}
