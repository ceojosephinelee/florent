import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/data/api/api_buyer_repository.dart';
import '../../core/models/buyer_request.dart';
import '../../core/network/dio_client.dart';

final buyerRepositoryProvider = Provider<ApiBuyerRepository>(
  (ref) => ApiBuyerRepository(ref.watch(dioProvider)),
);

final buyerRequestDetailProvider =
    FutureProvider.family<Map<String, dynamic>, int>((ref, requestId) {
  final repo = ref.watch(buyerRepositoryProvider);
  return repo.getRequestDetail(requestId);
});

final activeRequestsProvider =
    FutureProvider<List<BuyerRequestSummary>>((ref) {
  final repo = ref.watch(buyerRepositoryProvider);
  return repo.getRequests();
});

// ── 필터 탭 ──

enum BuyerRequestFilter { all, active, completed }

final buyerRequestFilterProvider = StateProvider<BuyerRequestFilter>(
  (ref) => BuyerRequestFilter.all,
);

final filteredBuyerRequestsProvider =
    Provider<AsyncValue<List<BuyerRequestSummary>>>((ref) {
  final filter = ref.watch(buyerRequestFilterProvider);
  final asyncReqs = ref.watch(activeRequestsProvider);

  return asyncReqs.whenData((reqs) {
    switch (filter) {
      case BuyerRequestFilter.all:
        return reqs;
      case BuyerRequestFilter.active:
        return reqs.where((r) => r.status == 'OPEN').toList();
      case BuyerRequestFilter.completed:
        return reqs
            .where((r) => r.status == 'CONFIRMED' || r.status == 'EXPIRED')
            .toList();
    }
  });
});
