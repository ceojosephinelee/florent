import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../../core/models/proposal.dart';
import '../providers/proposal_provider.dart';
import '../widgets/common/app_nav_bar.dart';

class BuyerNotificationsTabScreen extends ConsumerWidget {
  const BuyerNotificationsTabScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncNotifications = ref.watch(buyerNotificationsProvider);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const AppNavBar(title: '알림'),
            Expanded(
              child: asyncNotifications.when(
                loading: () => const Center(child: CircularProgressIndicator(color: roseColor)),
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
                      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 4),
                      itemCount: notifications.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 8),
                      itemBuilder: (_, i) {
                        final n = notifications[i];
                        final iconEmoji = n.type == 'PROPOSAL_ARRIVED' ? '💐' : '✅';
                        final iconBg = n.isRead ? const Color(0xFFF0EDE9) : roseLt;

                        return GestureDetector(
                          onTap: () {
                            ref.read(buyerNotificationsProvider.notifier).markAsRead(n.notificationId);
                            _navigateByType(context, n);
                          },
                          child: Container(
                            padding: const EdgeInsets.all(14),
                            decoration: BoxDecoration(
                              color: whiteColor,
                              borderRadius: kBorderRadiusMd,
                              border: Border.all(
                                color: n.isRead ? borderColor : roseColor,
                                width: n.isRead ? 1 : 1.5,
                              ),
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
                                    decoration: const BoxDecoration(color: roseColor, shape: BoxShape.circle),
                                  )
                                else
                                  const SizedBox(width: 12),

                                // 타입 아이콘
                                Container(
                                  width: 36,
                                  height: 36,
                                  decoration: BoxDecoration(color: iconBg, borderRadius: kBorderRadiusSm),
                                  alignment: Alignment.center,
                                  child: Text(iconEmoji, style: const TextStyle(fontSize: 16)),
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

  void _navigateByType(BuildContext context, NotificationItem n) {
    switch (n.type) {
      case 'PROPOSAL_ARRIVED':
        if (n.proposalId != null) {
          context.push('/buyer/proposals/${n.proposalId}');
        }
      case 'RESERVATION_CONFIRMED':
        if (n.referenceId != null) {
          context.push('/buyer/reservations/${n.referenceId}');
        }
      default:
        break;
    }
  }

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
