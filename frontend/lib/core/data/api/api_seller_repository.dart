import 'dart:io';

import 'package:dio/dio.dart';

import '../../models/seller_models.dart';

class ApiSellerRepository {
  final Dio _dio;

  ApiSellerRepository(this._dio);

  // ── 홈 ──

  Future<Map<String, dynamic>> getHome() async {
    final response = await _dio.get('/seller/home');
    return response.data['data'] as Map<String, dynamic>;
  }

  // ── 요청 ──

  Future<List<SellerRequestSummary>> getRequests({
    int page = 0,
    int size = 20,
  }) async {
    final response = await _dio.get(
      '/seller/requests',
      queryParameters: {'page': page, 'size': size},
    );
    final content = response.data['data']['content'] as List;
    return content
        .map((e) => SellerRequestSummary.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<SellerRequestDetail> getRequestDetail(int requestId) async {
    final response = await _dio.get('/seller/requests/$requestId');
    return SellerRequestDetail.fromJson(
      response.data['data'] as Map<String, dynamic>,
    );
  }

  // ── 제안 ──

  Future<Map<String, dynamic>> createDraftProposal(int requestId) async {
    final response =
        await _dio.post('/seller/requests/$requestId/proposals');
    return response.data['data'] as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> updateProposal(
    int proposalId,
    Map<String, dynamic> data,
  ) async {
    final response =
        await _dio.patch('/seller/proposals/$proposalId', data: data);
    return response.data['data'] as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> submitProposal(int proposalId) async {
    final response =
        await _dio.post('/seller/proposals/$proposalId/submit');
    return response.data['data'] as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> getProposalDetail(int proposalId) async {
    final response = await _dio.get('/seller/proposals/$proposalId');
    return response.data['data'] as Map<String, dynamic>;
  }

  // ── 예약 ──

  Future<List<SellerReservationSummary>> getReservations() async {
    final response = await _dio.get('/seller/reservations');
    final list = response.data['data'] as List;
    return list
        .map((e) => SellerReservationSummary.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<SellerReservationDetail> getReservationDetail(
    int reservationId,
  ) async {
    final response =
        await _dio.get('/seller/reservations/$reservationId');
    final data = response.data['data'] as Map<String, dynamic>;
    final proposal = data['proposal'] as Map<String, dynamic>? ?? {};
    final slot = data['fulfillmentSlot'] as Map<String, dynamic>? ?? {};
    final request = data['request'] as Map<String, dynamic>? ?? {};
    return SellerReservationDetail(
      reservationId: data['reservationId'] as int,
      status: data['status'] as String,
      buyerNickName: data['buyerNickName'] as String? ?? '',
      conceptTitle: proposal['conceptTitle'] as String? ?? '',
      description: proposal['description'] as String? ?? '',
      price: proposal['price'] as int? ?? 0,
      fulfillmentType: data['fulfillmentType'] as String,
      fulfillmentDate: data['fulfillmentDate'] as String,
      fulfillmentSlotKind: slot['kind'] as String? ?? '',
      fulfillmentSlotValue: slot['value'] as String? ?? '',
      placeAddressText: data['placeAddressText'] as String? ?? '',
      confirmedAt: data['confirmedAt'] as String? ?? '',
      imageUrls: List<String>.from(proposal['imageUrls'] ?? []),
      purposeTags: List<String>.from(request['purposeTags'] ?? []),
      relationTags: List<String>.from(request['relationTags'] ?? []),
      moodTags: List<String>.from(request['moodTags'] ?? []),
      budgetTier: request['budgetTier'] as String?,
    );
  }

  Future<Map<String, dynamic>> confirmReservation(int reservationId) async {
    final response = await _dio.post('/seller/reservations/$reservationId/confirm');
    return response.data['data'] as Map<String, dynamic>;
  }

  // ── 이미지 업로드 ──

  /// 1) 서버에서 presigned URL 발급 → 2) S3에 직접 업로드 → 3) imageUrl 반환
  Future<String> uploadImage(File imageFile) async {
    final fileName = imageFile.path.split('/').last;
    final contentType = _guessContentType(fileName);

    // Step 1: presigned URL 발급
    final response = await _dio.post('/images/presigned-url', data: {
      'fileName': fileName,
      'contentType': contentType,
      'target': 'PROPOSAL',
    });
    final presignedUrl = response.data['data']['presignedUrl'] as String;
    final imageUrl = response.data['data']['imageUrl'] as String;

    // Step 2: S3에 직접 PUT 업로드 (인증 헤더 없는 별도 Dio 인스턴스 사용)
    final bytes = await imageFile.readAsBytes();
    print('[IMAGE] 업로드 시작: fileName=$fileName, contentType=$contentType, size=${bytes.length}');
    print('[IMAGE] presignedUrl=${presignedUrl.substring(0, 80)}...');

    final s3Dio = Dio();
    try {
      await s3Dio.put(
        presignedUrl,
        data: Stream.fromIterable([bytes]),
        options: Options(
          headers: {
            'Content-Type': contentType,
            'Content-Length': bytes.length,
          },
          sendTimeout: const Duration(seconds: 30),
          receiveTimeout: const Duration(seconds: 30),
        ),
      );
    } on DioException catch (e) {
      print('[IMAGE] S3 업로드 실패: status=${e.response?.statusCode}, body=${e.response?.data}');
      throw Exception('S3 업로드 실패: ${e.response?.statusCode} ${e.response?.data}');
    }

    // Step 3: 업로드 완료된 이미지 URL 반환
    return imageUrl;
  }

  String _guessContentType(String fileName) {
    final ext = fileName.split('.').last.toLowerCase();
    return switch (ext) {
      'png' => 'image/png',
      'gif' => 'image/gif',
      'webp' => 'image/webp',
      'heic' || 'heif' => 'image/heic',
      _ => 'image/jpeg',
    };
  }

  // ── 프로필 ──

  Future<Map<String, dynamic>> getProfile() async {
    final response = await _dio.get('/seller/me');
    return response.data['data'] as Map<String, dynamic>;
  }

  // ── 통계 ──

  Future<Map<String, dynamic>> getStats() async {
    final response = await _dio.get('/seller/stats');
    return response.data['data'] as Map<String, dynamic>;
  }

  // ── 꽃집 ──

  Future<Map<String, dynamic>> getShop() async {
    final response = await _dio.get('/seller/shop');
    return response.data['data'] as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> updateShop(Map<String, dynamic> data) async {
    final response = await _dio.patch('/seller/shop', data: data);
    return response.data['data'] as Map<String, dynamic>;
  }
}
