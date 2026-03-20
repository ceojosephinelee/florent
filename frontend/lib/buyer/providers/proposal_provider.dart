import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/data/api/api_notification_repository.dart';
import '../../core/models/buyer_reservation.dart';
import '../../core/models/proposal.dart';
import '../../core/network/dio_client.dart';
import 'buyer_request_provider.dart';

// ── 제안 ──

final proposalsProvider = FutureProvider.autoDispose
    .family<List<ProposalSummary>, int>((ref, requestId) {
  final repo = ref.watch(buyerRepositoryProvider);
  return repo.getProposals(requestId);
});

final proposalDetailProvider =
    FutureProvider.autoDispose.family<ProposalDetail, int>((ref, id) {
  final repo = ref.watch(buyerRepositoryProvider);
  return repo.getProposalDetail(id);
});

// ── 구매자 예약 상세 ──

final buyerReservationDetailProvider = FutureProvider.autoDispose
    .family<BuyerReservationDetail, int>((ref, id) {
  final repo = ref.watch(buyerRepositoryProvider);
  return repo.getReservationDetail(id);
});

// ── 구매자 알림 ──

final _notificationRepoProvider = Provider<ApiNotificationRepository>(
  (ref) => ApiNotificationRepository(ref.watch(dioProvider)),
);

class BuyerNotificationsNotifier extends StateNotifier<AsyncValue<List<NotificationItem>>> {
  final ApiNotificationRepository _repo;

  BuyerNotificationsNotifier(this._repo) : super(const AsyncValue.loading()) {
    _load();
  }

  Future<void> _load() async {
    print('[NOTIFICATION-BUYER] _load() 호출됨');
    try {
      final items = await _repo.getNotifications();
      print('[NOTIFICATION-BUYER] 수신: ${items.length}건');
      state = AsyncValue.data(items);
    } catch (e, st) {
      print('[NOTIFICATION-BUYER] 에러: $e');
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

final buyerNotificationsProvider =
    StateNotifierProvider.autoDispose<BuyerNotificationsNotifier, AsyncValue<List<NotificationItem>>>(
  (ref) => BuyerNotificationsNotifier(ref.watch(_notificationRepoProvider)),
);

// ── 구매자 예약 목록 ──

final buyerReservationsListProvider =
    FutureProvider.autoDispose<List<BuyerReservationDetail>>((ref) async {
  final repo = ref.watch(buyerRepositoryProvider);
  final all = await repo.getReservations();
  return all.where((r) => r.status == 'CONFIRMED').toList();
});
