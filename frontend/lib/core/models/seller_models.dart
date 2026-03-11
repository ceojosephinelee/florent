import 'package:freezed_annotation/freezed_annotation.dart';

part 'seller_models.freezed.dart';
part 'seller_models.g.dart';

@freezed
class SellerHomeData with _$SellerHomeData {
  const factory SellerHomeData({
    required int openRequestCount,
    required int draftProposalCount,
    required int confirmedReservationCount,
    required String shopName,
  }) = _SellerHomeData;

  factory SellerHomeData.fromJson(Map<String, dynamic> json) =>
      _$SellerHomeDataFromJson(json);
}

@freezed
class SellerRequestSummary with _$SellerRequestSummary {
  const factory SellerRequestSummary({
    required int requestId,
    required String status,
    required String budgetTier,
    required String fulfillmentType,
    required String fulfillmentDate,
    required String expiresAt,
    @Default([]) List<String> purposeTags,
    @Default([]) List<String> moodTags,
    String? myProposalStatus,
    String? distance,
    String? slotLabel,
  }) = _SellerRequestSummary;

  factory SellerRequestSummary.fromJson(Map<String, dynamic> json) =>
      _$SellerRequestSummaryFromJson(json);
}

@freezed
class SellerRequestDetail with _$SellerRequestDetail {
  const factory SellerRequestDetail({
    required int requestId,
    required String status,
    required String budgetTier,
    required String fulfillmentType,
    required String fulfillmentDate,
    required String expiresAt,
    required String placeAddressText,
    @Default([]) List<String> purposeTags,
    @Default([]) List<String> relationTags,
    @Default([]) List<String> moodTags,
    @Default([]) List<Map<String, String>> requestedTimeSlots,
    String? myProposalId,
    String? distance,
  }) = _SellerRequestDetail;

  factory SellerRequestDetail.fromJson(Map<String, dynamic> json) =>
      _$SellerRequestDetailFromJson(json);
}

@freezed
class SellerReservationDetail with _$SellerReservationDetail {
  const factory SellerReservationDetail({
    required int reservationId,
    required String status,
    required String buyerNickName,
    required String conceptTitle,
    required String description,
    required int price,
    required String fulfillmentType,
    required String fulfillmentDate,
    required String fulfillmentSlotKind,
    required String fulfillmentSlotValue,
    required String placeAddressText,
    required String confirmedAt,
    @Default([]) List<String> imageUrls,
    @Default([]) List<String> purposeTags,
    @Default([]) List<String> relationTags,
    @Default([]) List<String> moodTags,
    String? budgetTier,
  }) = _SellerReservationDetail;

  factory SellerReservationDetail.fromJson(Map<String, dynamic> json) =>
      _$SellerReservationDetailFromJson(json);
}

@freezed
class SellerReservationSummary with _$SellerReservationSummary {
  const factory SellerReservationSummary({
    required int reservationId,
    required String conceptTitle,
    required int price,
    required String confirmedAt,
    required String fulfillmentType,
  }) = _SellerReservationSummary;

  factory SellerReservationSummary.fromJson(Map<String, dynamic> json) =>
      _$SellerReservationSummaryFromJson(json);
}

@freezed
class SellerProposalForm with _$SellerProposalForm {
  const SellerProposalForm._();

  const factory SellerProposalForm({
    @Default('') String conceptTitle,
    @Default('') String mainFlowers,
    @Default('') String subFlowers,
    @Default('') String concept,
    @Default('') String wrapping,
    @Default('') String recommendation,
    @Default(0) int price,
    String? selectedSlotKind,
    String? selectedSlotValue,
  }) = _SellerProposalForm;

  bool get isStep1Valid =>
      conceptTitle.isNotEmpty && price > 0;

  bool get isStep2Valid =>
      selectedSlotValue != null;
}
