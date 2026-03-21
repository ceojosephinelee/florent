import 'package:dio/dio.dart';

import '../../models/proposal.dart';

class ApiNotificationRepository {
  final Dio _dio;

  ApiNotificationRepository(this._dio);

  Future<List<NotificationItem>> getNotifications({
    int page = 0,
    int size = 20,
  }) async {
    final response = await _dio.get(
      '/notifications',
      queryParameters: {'page': page, 'size': size},
    );
    print('[NOTIFICATION-RAW] response: ${response.data}');
    final content = (response.data['data']?['notifications'] as List?) ?? [];
    return content
        .map((e) => NotificationItem.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<void> markAsRead(int notificationId) async {
    await _dio.patch('/notifications/$notificationId/read');
  }
}
