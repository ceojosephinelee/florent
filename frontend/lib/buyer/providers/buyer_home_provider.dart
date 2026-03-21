import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'buyer_request_provider.dart';
import 'proposal_provider.dart';

final buyerProfileProvider = FutureProvider<Map<String, dynamic>>((ref) {
  final repo = ref.watch(buyerRepositoryProvider);
  return repo.getProfile();
});

final buyerNameProvider = Provider<String>((ref) {
  final asyncProfile = ref.watch(buyerProfileProvider);
  return asyncProfile.whenOrNull(data: (p) => p['nickName'] as String?) ?? '';
});

final unreadNotificationCountProvider = Provider.autoDispose<int>((ref) {
  final asyncNotifications = ref.watch(buyerNotificationsProvider);
  return asyncNotifications.whenOrNull(
        data: (items) => items.where((n) => !n.isRead).length,
      ) ??
      0;
});
