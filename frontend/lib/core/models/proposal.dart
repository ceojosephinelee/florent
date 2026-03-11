import 'package:freezed_annotation/freezed_annotation.dart';

part 'proposal.freezed.dart';
part 'proposal.g.dart';

@freezed
class ProposalSummary with _$ProposalSummary {
  const factory ProposalSummary({
    required int proposalId,
    required String shopName,
    required String conceptTitle,
    required String status,
    required String expiresAt,
    String? description,
    String? shopDistance,
    String? slotLabel,
    String? mainFlowers,
    String? shopEmoji,
  }) = _ProposalSummary;

  factory ProposalSummary.fromJson(Map<String, dynamic> json) =>
      _$ProposalSummaryFromJson(json);
}

@freezed
class ProposalDetail with _$ProposalDetail {
  const factory ProposalDetail({
    required int proposalId,
    required int requestId,
    required String status,
    required String shopName,
    required String shopAddress,
    required String conceptTitle,
    required String description,
    required int price,
    required String expiresAt,
    String? shopDistance,
    String? slotLabel,
    String? shopEmoji,
    @Default([]) List<String> mainFlowers,
    @Default([]) List<String> imageUrls,
  }) = _ProposalDetail;

  factory ProposalDetail.fromJson(Map<String, dynamic> json) =>
      _$ProposalDetailFromJson(json);
}

@freezed
class ReservationDetail with _$ReservationDetail {
  const factory ReservationDetail({
    required int reservationId,
    required String status,
    required String shopName,
    required String shopAddress,
    required String conceptTitle,
    required int price,
    required String fulfillmentType,
    required String fulfillmentDate,
    required String slotLabel,
    String? shopEmoji,
  }) = _ReservationDetail;

  factory ReservationDetail.fromJson(Map<String, dynamic> json) =>
      _$ReservationDetailFromJson(json);
}

@freezed
class NotificationItem with _$NotificationItem {
  const factory NotificationItem({
    required int notificationId,
    required String type,
    required String title,
    required String body,
    required String createdAt,
    required bool isRead,
    String? referenceType,
    int? referenceId,
    int? proposalId,
  }) = _NotificationItem;

  factory NotificationItem.fromJson(Map<String, dynamic> json) =>
      _$NotificationItemFromJson(json);
}
