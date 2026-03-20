import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../buyer/widgets/common/app_nav_bar.dart';
import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/seller_providers.dart';

const _sage = Color(0xFF5A7A68);
const _sageLt = Color(0xFFE8F0EC);

class SellerNotificationsScreen extends ConsumerWidget {
  const SellerNotificationsScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncNotifications = ref.watch(sellerNotificationsProvider);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            const AppNavBar(title: '알림'),
            Expanded(
              child: asyncNotifications.when(
                loading: () => const Center(child: CircularProgressIndicator(color: _sage)),
                error: (e, _) => Center(child: Text('오류', style: AppTypography.body(color: ink60))),
                data: (notifications) => notifications.isEmpty
                  ? Center(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Text('🔔', style: TextStyle(fontSize: 36)),
                          const SizedBox(height: 12),
                          Text('아직 알림이 없어요', style: AppTypography.body(fontSize: 14, color: ink60)),
                        ],
                      ),
                    )
                  : ListView.separated(
                      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                      itemCount: notifications.length,
                      separatorBuilder: (_, _i) => const SizedBox(height: 8),
                      itemBuilder: (_, i) {
                        final n = notifications[i];
                        return GestureDetector(
                          onTap: () {
                            ref.read(sellerNotificationsProvider.notifier).markAsRead(n.notificationId);
                            if (n.referenceType == 'RESERVATION' && n.referenceId != null) {
                              context.push('/seller/reservations/${n.referenceId}');
                            } else if (n.referenceType == 'REQUEST' && n.referenceId != null) {
                              context.push('/seller/requests/${n.referenceId}');
                            }
                          },
                          child: Container(
                            padding: const EdgeInsets.all(14),
                            decoration: BoxDecoration(
                              color: whiteColor,
                              borderRadius: kBorderRadiusMd,
                              border: Border.all(color: n.isRead ? borderColor : _sage, width: n.isRead ? 1 : 1.5),
                            ),
                            child: Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                // 읽지 않은 알림 인디케이터
                                if (!n.isRead)
                                  Container(
                                    width: 4,
                                    height: 4,
                                    margin: const EdgeInsets.only(top: 6, right: 8),
                                    decoration: const BoxDecoration(color: _sage, shape: BoxShape.circle),
                                  )
                                else
                                  const SizedBox(width: 12),

                                // 타입 아이콘
                                Container(
                                  width: 36,
                                  height: 36,
                                  decoration: BoxDecoration(color: _sageLt, borderRadius: kBorderRadiusSm),
                                  alignment: Alignment.center,
                                  child: Text(
                                    _typeEmoji(n.type),
                                    style: const TextStyle(fontSize: 16),
                                  ),
                                ),
                                const SizedBox(width: 10),

                                // 내용
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        n.title,
                                        style: AppTypography.body(
                                          fontSize: 13,
                                          fontWeight: n.isRead ? FontWeight.w400 : FontWeight.w600,
                                        ),
                                      ),
                                      const SizedBox(height: 2),
                                      Text(n.body, style: AppTypography.body(fontSize: 11, color: ink60)),
                                      const SizedBox(height: 4),
                                      Text(
                                        _relativeTime(n.createdAt),
                                        style: AppTypography.body(fontSize: 10, color: ink30),
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          ),
                        );
                      },
                    ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _typeEmoji(String type) => switch (type) {
        'RESERVATION_CONFIRMED' => '🌸',
        'REQUEST_ARRIVED' => '📬',
        'PROPOSAL_ARRIVED' => '📨',
        _ => '🔔',
      };

  String _relativeTime(String isoString) {
    try {
      final dt = DateTime.parse(isoString);
      final diff = DateTime.now().difference(dt);
      if (diff.inMinutes < 1) return '방금 전';
      if (diff.inMinutes < 60) return '${diff.inMinutes}분 전';
      if (diff.inHours < 24) return '${diff.inHours}시간 전';
      if (diff.inDays == 1) return '어제';
      return '${diff.inDays}일 전';
    } catch (_) {
      return isoString;
    }
  }
}
