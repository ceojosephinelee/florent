import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/data/mock/mock_seller_data.dart';
import '../../core/models/proposal.dart';
import '../../core/models/seller_models.dart';

// ── 요청 목록 필터 ──

enum SellerRequestFilter { all, pickup, delivery, noProposal }

final sellerRequestFilterProvider = StateProvider<SellerRequestFilter>(
  (ref) => SellerRequestFilter.all,
);

final filteredSellerRequestsProvider =
    Provider<AsyncValue<List<SellerRequestSummary>>>((ref) {
  final filter = ref.watch(sellerRequestFilterProvider);
  final asyncReqs = ref.watch(sellerRequestsProvider);

  return asyncReqs.whenData((reqs) {
    switch (filter) {
      case SellerRequestFilter.all:
        return reqs;
      case SellerRequestFilter.pickup:
        return reqs.where((r) => r.fulfillmentType == 'PICKUP').toList();
      case SellerRequestFilter.delivery:
        return reqs.where((r) => r.fulfillmentType == 'DELIVERY').toList();
      case SellerRequestFilter.noProposal:
        return reqs
            .where((r) =>
                r.status == 'OPEN' &&
                r.myProposalStatus != 'DRAFT' &&
                r.myProposalStatus != 'SUBMITTED')
            .toList();
    }
  });
});

final sellerHomeProvider = FutureProvider<SellerHomeData>((ref) async {
  await Future.delayed(const Duration(milliseconds: 300));
  return mockSellerHome;
});

final sellerRequestsProvider =
    FutureProvider<List<SellerRequestSummary>>((ref) async {
  await Future.delayed(const Duration(milliseconds: 300));
  return mockSellerRequests;
});

final sellerRequestDetailProvider =
    FutureProvider.family<SellerRequestDetail, int>((ref, id) async {
  await Future.delayed(const Duration(milliseconds: 300));
  return mockSellerRequestDetail;
});

// ── 예약 상세 ──

final sellerReservationDetailProvider =
    FutureProvider.family<SellerReservationDetail, int>((ref, id) async {
  await Future.delayed(const Duration(milliseconds: 300));
  final detail = mockSellerReservations[id];
  if (detail == null) throw Exception('Reservation not found: $id');
  return detail;
});

final sellerReservationHistoryProvider =
    FutureProvider<List<SellerReservationSummary>>((ref) async {
  await Future.delayed(const Duration(milliseconds: 300));
  return mockSellerReservationHistory;
});

// ── 판매자 알림 ──

class SellerNotificationsNotifier extends StateNotifier<List<NotificationItem>> {
  SellerNotificationsNotifier() : super(mockSellerNotifications);

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

final sellerNotificationsProvider =
    StateNotifierProvider<SellerNotificationsNotifier, List<NotificationItem>>(
  (ref) => SellerNotificationsNotifier(),
);

final sellerUnreadCountProvider = Provider<int>((ref) {
  final notifications = ref.watch(sellerNotificationsProvider);
  return notifications.where((n) => !n.isRead).length;
});

// ── 제안서 폼 ──

class SellerProposalFormNotifier extends StateNotifier<SellerProposalForm> {
  SellerProposalFormNotifier() : super(const SellerProposalForm());

  void setConceptTitle(String v) => state = state.copyWith(conceptTitle: v);
  void setMainFlowers(String v) => state = state.copyWith(mainFlowers: v);
  void setSubFlowers(String v) => state = state.copyWith(subFlowers: v);
  void setConcept(String v) => state = state.copyWith(concept: v);
  void setWrapping(String v) => state = state.copyWith(wrapping: v);
  void setRecommendation(String v) => state = state.copyWith(recommendation: v);
  void setPrice(int v) => state = state.copyWith(price: v);

  void setSlot(String kind, String value) => state = state.copyWith(
        selectedSlotKind: kind,
        selectedSlotValue: value,
      );

  void reset() => state = const SellerProposalForm();
}

final sellerProposalFormProvider =
    StateNotifierProvider<SellerProposalFormNotifier, SellerProposalForm>(
  (ref) => SellerProposalFormNotifier(),
);
