import 'package:dio/dio.dart';

import '../../models/buyer_request.dart';
import '../../models/buyer_reservation.dart';
import '../../models/proposal.dart';

class ApiBuyerRepository {
  final Dio _dio;

  ApiBuyerRepository(this._dio);

  // ── 요청 ──

  Future<List<BuyerRequestSummary>> getRequests({
    int page = 0,
    int size = 20,
  }) async {
    final response = await _dio.get(
      '/buyer/requests',
      queryParameters: {'page': page, 'size': size},
    );
    final content = response.data['data']['content'] as List;
    return content
        .map((e) => BuyerRequestSummary.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<Map<String, dynamic>> createRequest(Map<String, dynamic> data) async {
    final response = await _dio.post('/buyer/requests', data: data);
    return response.data['data'] as Map<String, dynamic>;
  }

  // ── 제안 ──

  Future<List<ProposalSummary>> getProposals(int requestId) async {
    final response =
        await _dio.get('/buyer/requests/$requestId/proposals');
    final list = response.data['data'] as List;
    return list
        .map((e) => ProposalSummary.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<ProposalDetail> getProposalDetail(int proposalId) async {
    final response = await _dio.get('/buyer/proposals/$proposalId');
    final data = response.data['data'] as Map<String, dynamic>;
    final shop = data['shop'] as Map<String, dynamic>? ?? {};
    return ProposalDetail(
      proposalId: data['proposalId'] as int,
      requestId: data['requestId'] as int,
      status: data['status'] as String,
      shopName: shop['name'] as String? ?? '',
      shopAddress: shop['addressText'] as String? ?? '',
      conceptTitle: data['conceptTitle'] as String? ?? '',
      description: data['description'] as String? ?? '',
      price: data['price'] as int? ?? 0,
      expiresAt: data['expiresAt'] as String? ?? '',
      mainFlowers: List<String>.from(data['mainFlowers'] ?? []),
      imageUrls: List<String>.from(data['imageUrls'] ?? []),
    );
  }

  Future<Map<String, dynamic>> selectProposal(
    int proposalId,
    String idempotencyKey,
  ) async {
    final response = await _dio.post(
      '/buyer/proposals/$proposalId/select',
      data: {'idempotencyKey': idempotencyKey},
    );
    return response.data['data'] as Map<String, dynamic>;
  }

  // ── 예약 ──

  Future<List<BuyerReservationDetail>> getReservations() async {
    final response = await _dio.get('/buyer/reservations');
    final list = response.data['data'] as List;
    return list.map((e) {
      final item = e as Map<String, dynamic>;
      final slot = item['fulfillmentSlot'] as Map<String, dynamic>? ?? {};
      return BuyerReservationDetail(
        reservationId: item['reservationId'] as int,
        status: item['status'] as String,
        shopName: item['shopName'] as String? ?? '',
        shopAddress: '',
        conceptTitle: item['conceptTitle'] as String? ?? '',
        description: '',
        price: item['price'] as int? ?? 0,
        fulfillmentType: item['fulfillmentType'] as String,
        fulfillmentDate: item['fulfillmentDate'] as String,
        fulfillmentSlotKind: slot['kind'] as String? ?? '',
        fulfillmentSlotValue: slot['value'] as String? ?? '',
        placeAddressText: '',
      );
    }).toList();
  }

  Future<BuyerReservationDetail> getReservationDetail(
    int reservationId,
  ) async {
    final response =
        await _dio.get('/buyer/reservations/$reservationId');
    final data = response.data['data'] as Map<String, dynamic>;
    return BuyerReservationDetail.fromApiJson(data);
  }

  // ── 프로필 ──

  Future<Map<String, dynamic>> getProfile() async {
    final response = await _dio.get('/buyer/me');
    return response.data['data'] as Map<String, dynamic>;
  }
}
