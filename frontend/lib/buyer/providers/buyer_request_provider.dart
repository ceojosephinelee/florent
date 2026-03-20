import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/data/api/api_buyer_repository.dart';
import '../../core/models/buyer_request.dart';
import '../../core/network/dio_client.dart';

final buyerRepositoryProvider = Provider<ApiBuyerRepository>(
  (ref) => ApiBuyerRepository(ref.watch(dioProvider)),
);

final buyerRequestDetailProvider = FutureProvider.autoDispose
    .family<Map<String, dynamic>, int>((ref, requestId) {
  final repo = ref.watch(buyerRepositoryProvider);
  return repo.getRequestDetail(requestId);
});

// 전체 요청 목록 (필터 탭용)
final allBuyerRequestsProvider =
    FutureProvider.autoDispose<List<BuyerRequestSummary>>((ref) {
  final repo = ref.watch(buyerRepositoryProvider);
  return repo.getRequests();
});

// 홈 화면 진행중 요청 (OPEN만)
final activeRequestsProvider =
    FutureProvider.autoDispose<List<BuyerRequestSummary>>((ref) async {
  final all = await ref.watch(allBuyerRequestsProvider.future);
  return all.where((r) => r.status == 'OPEN').toList();
});

// ── 필터 탭 ──

enum BuyerRequestFilter { all, waiting, hasProposal, confirmed, expired }

final buyerRequestFilterProvider = StateProvider<BuyerRequestFilter>(
  (ref) => BuyerRequestFilter.all,
);

final filteredBuyerRequestsProvider =
    Provider.autoDispose<AsyncValue<List<BuyerRequestSummary>>>((ref) {
  final filter = ref.watch(buyerRequestFilterProvider);
  final asyncReqs = ref.watch(allBuyerRequestsProvider);

  return asyncReqs.whenData((reqs) {
    return switch (filter) {
      BuyerRequestFilter.all => reqs,
      BuyerRequestFilter.waiting =>
        reqs.where((r) => r.status == 'OPEN' && r.submittedProposalCount == 0).toList(),
      BuyerRequestFilter.hasProposal =>
        reqs.where((r) => r.status == 'OPEN' && r.submittedProposalCount >= 1).toList(),
      BuyerRequestFilter.confirmed =>
        reqs.where((r) => r.status == 'CONFIRMED').toList(),
      BuyerRequestFilter.expired =>
        reqs.where((r) => r.status == 'EXPIRED').toList(),
    };
  });
});
