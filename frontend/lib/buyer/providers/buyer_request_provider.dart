import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/data/buyer_request_repository.dart';
import '../../core/data/mock/mock_buyer_request_repository.dart';
import '../../core/models/buyer_request.dart';

final buyerRequestRepositoryProvider = Provider<BuyerRequestRepository>(
  (ref) => MockBuyerRequestRepository(),
);

final activeRequestsProvider =
    FutureProvider<List<BuyerRequestSummary>>((ref) {
  final repo = ref.watch(buyerRequestRepositoryProvider);
  return repo.getActiveRequests();
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
