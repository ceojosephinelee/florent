import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/data/mock/mock_proposal_data.dart';
import '../../core/models/buyer_reservation.dart';
import '../../core/models/proposal.dart';

final proposalsProvider = FutureProvider<List<ProposalSummary>>((ref) async {
  await Future.delayed(const Duration(milliseconds: 300));
  return mockProposals;
});

final proposalDetailProvider =
    FutureProvider.family<ProposalDetail, int>((ref, id) async {
  await Future.delayed(const Duration(milliseconds: 300));
  return mockProposalDetail;
});

final reservationDetailProvider =
    FutureProvider.family<ReservationDetail, int>((ref, id) async {
  await Future.delayed(const Duration(milliseconds: 300));
  return mockReservation;
});

// ── 구매자 예약 상세 ──

final buyerReservationDetailProvider =
    FutureProvider.family<BuyerReservationDetail, int>((ref, id) async {
  await Future.delayed(const Duration(milliseconds: 300));
  final detail = mockBuyerReservations[id];
  if (detail == null) throw Exception('Reservation not found: $id');
  return detail;
});

// ── 구매자 알림 ──

class BuyerNotificationsNotifier extends StateNotifier<List<NotificationItem>> {
  BuyerNotificationsNotifier() : super(mockNotifications);

  void markAsRead(int notificationId) {
    state = [
      for (final n in state)
        if (n.notificationId == notificationId)
          n.copyWith(isRead: true)
        else
          n,
    ];
  }
}

final buyerNotificationsProvider =
    StateNotifierProvider<BuyerNotificationsNotifier, List<NotificationItem>>(
  (ref) => BuyerNotificationsNotifier(),
);

// 하위 호환용 (알림 탭 등에서 기존 참조)
final notificationsProvider =
    FutureProvider<List<NotificationItem>>((ref) async {
  await Future.delayed(const Duration(milliseconds: 300));
  return mockNotifications;
});

// ── 구매자 예약 목록 ──

final buyerReservationsListProvider =
    FutureProvider<List<BuyerReservationDetail>>((ref) async {
  await Future.delayed(const Duration(milliseconds: 300));
  final list = mockBuyerReservations.values.toList();
  list.sort((a, b) => b.fulfillmentDate.compareTo(a.fulfillmentDate));
  return list;
});
