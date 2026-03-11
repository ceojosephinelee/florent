import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'proposal_provider.dart';

final buyerNameProvider = Provider<String>((_) => '지민');

final unreadNotificationCountProvider = Provider<int>((ref) {
  final notifications = ref.watch(buyerNotificationsProvider);
  return notifications.where((n) => !n.isRead).length;
});
