import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/data/api/api_notification_repository.dart';
import '../../core/data/api/api_seller_repository.dart';
import '../../core/models/proposal.dart';
import '../../core/models/seller_models.dart';
import '../../core/network/dio_client.dart';

// ── Repository Provider ──

final sellerRepositoryProvider = Provider<ApiSellerRepository>(
  (ref) => ApiSellerRepository(ref.watch(dioProvider)),
);

final _sellerNotificationRepoProvider = Provider<ApiNotificationRepository>(
  (ref) => ApiNotificationRepository(ref.watch(dioProvider)),
);

// ── 요청 목록 필터 ──

enum SellerRequestFilter { all, drafting, proposed, confirmed, expired }

final sellerRequestFilterProvider = StateProvider<SellerRequestFilter>(
  (ref) => SellerRequestFilter.all,
);

final filteredSellerRequestsProvider =
    Provider.autoDispose<AsyncValue<List<SellerRequestSummary>>>((ref) {
  final filter = ref.watch(sellerRequestFilterProvider);
  final asyncReqs = ref.watch(sellerRequestsProvider);

  return asyncReqs.whenData((reqs) {
    return switch (filter) {
      SellerRequestFilter.all => reqs,
      SellerRequestFilter.drafting =>
        reqs.where((r) => r.myProposalStatus == 'DRAFT').toList(),
      SellerRequestFilter.proposed =>
        reqs.where((r) => r.myProposalStatus == 'SUBMITTED').toList(),
      SellerRequestFilter.confirmed =>
        reqs.where((r) => r.status == 'CONFIRMED').toList(),
      SellerRequestFilter.expired =>
        reqs.where((r) => r.status == 'EXPIRED').toList(),
    };
  });
});

// ── 홈 ──

final _sellerHomeRawProvider = FutureProvider.autoDispose<Map<String, dynamic>>((ref) {
  final repo = ref.watch(sellerRepositoryProvider);
  return repo.getHome();
});

final sellerHomeProvider = FutureProvider.autoDispose<SellerHomeData>((ref) async {
  final raw = await ref.watch(_sellerHomeRawProvider.future);
  final repo = ref.watch(sellerRepositoryProvider);
  final profile = await repo.getProfile();
  return SellerHomeData(
    openRequestCount: raw['openRequestCount'] as int? ?? 0,
    draftProposalCount: raw['draftProposalCount'] as int? ?? 0,
    confirmedReservationCount: raw['confirmedReservationCount'] as int? ?? 0,
    shopName: profile['shopName'] as String? ?? '',
  );
});

final sellerRecentRequestsProvider =
    FutureProvider.autoDispose<List<SellerRequestSummary>>((ref) async {
  final raw = await ref.watch(_sellerHomeRawProvider.future);
  final list = raw['recentRequests'] as List? ?? [];
  return list
      .map((e) => SellerRequestSummary.fromJson(e as Map<String, dynamic>))
      .toList();
});

// ── 요청 ──

final sellerRequestsProvider =
    FutureProvider.autoDispose<List<SellerRequestSummary>>((ref) {
  final repo = ref.watch(sellerRepositoryProvider);
  return repo.getRequests();
});

final sellerRequestDetailProvider =
    FutureProvider.autoDispose.family<SellerRequestDetail, int>((ref, id) {
  final repo = ref.watch(sellerRepositoryProvider);
  return repo.getRequestDetail(id);
});

// ── 예약 ──

final sellerReservationDetailProvider =
    FutureProvider.autoDispose.family<SellerReservationDetail, int>((ref, id) {
  final repo = ref.watch(sellerRepositoryProvider);
  return repo.getReservationDetail(id);
});

final sellerReservationHistoryProvider =
    FutureProvider.autoDispose<List<SellerReservationSummary>>((ref) {
  final repo = ref.watch(sellerRepositoryProvider);
  return repo.getReservations();
});

// ── 프로필 ──

final sellerProfileProvider = FutureProvider.autoDispose<Map<String, dynamic>>((ref) {
  final repo = ref.watch(sellerRepositoryProvider);
  return repo.getProfile();
});

// ── 판매자 알림 ──

class SellerNotificationsNotifier extends StateNotifier<AsyncValue<List<NotificationItem>>> {
  final ApiNotificationRepository _repo;

  SellerNotificationsNotifier(this._repo) : super(const AsyncValue.loading()) {
    _load();
  }

  Future<void> _load() async {
    print('[NOTIFICATION-SELLER] _load() 호출됨');
    try {
      final items = await _repo.getNotifications();
      print('[NOTIFICATION-SELLER] 수신: ${items.length}건');
      state = AsyncValue.data(items);
    } catch (e, st) {
      print('[NOTIFICATION-SELLER] 에러: $e');
      state = AsyncValue.error(e, st);
    }
  }

  Future<void> markAsRead(int notificationId) async {
    await _repo.markAsRead(notificationId);
    state.whenData((items) {
      state = AsyncValue.data([
        for (final n in items)
          if (n.notificationId == notificationId)
            n.copyWith(isRead: true)
          else
            n,
      ]);
    });
  }

  Future<void> refresh() => _load();
}

final sellerNotificationsProvider =
    StateNotifierProvider.autoDispose<SellerNotificationsNotifier, AsyncValue<List<NotificationItem>>>(
  (ref) => SellerNotificationsNotifier(ref.watch(_sellerNotificationRepoProvider)),
);

final sellerUnreadCountProvider = Provider.autoDispose<int>((ref) {
  final asyncNotifications = ref.watch(sellerNotificationsProvider);
  return asyncNotifications.whenOrNull(
        data: (items) => items.where((n) => !n.isRead).length,
      ) ??
      0;
});

// ── 제안서 폼 ──

class SellerProposalFormNotifier extends StateNotifier<SellerProposalForm> {
  SellerProposalFormNotifier() : super(const SellerProposalForm());

  void setRequestId(int v) => state = state.copyWith(requestId: v);
  void setProposalId(int v) => state = state.copyWith(proposalId: v);
  void setConceptTitle(String v) => state = state.copyWith(conceptTitle: v);
  void setMainFlowers(String v) => state = state.copyWith(mainFlowers: v);
  void setSubFlowers(String v) => state = state.copyWith(subFlowers: v);
  void setConcept(String v) => state = state.copyWith(concept: v);
  void setWrapping(String v) => state = state.copyWith(wrapping: v);
  void setRecommendation(String v) => state = state.copyWith(recommendation: v);
  void setPrice(int v) => state = state.copyWith(price: v);

  void setFulfillmentType(String v) => state = state.copyWith(fulfillmentType: v);
  void setExpiresAt(String v) => state = state.copyWith(expiresAt: v);
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
