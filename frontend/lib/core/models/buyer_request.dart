import 'package:freezed_annotation/freezed_annotation.dart';

part 'buyer_request.freezed.dart';
part 'buyer_request.g.dart';

@freezed
class BuyerRequestSummary with _$BuyerRequestSummary {
  const factory BuyerRequestSummary({
    required int requestId,
    required String status,
    required String budgetTier,
    required String fulfillmentType,
    required String fulfillmentDate,
    required String expiresAt,
    required int draftProposalCount,
    required int submittedProposalCount,
    @Default([]) List<String> purposeTags,
  }) = _BuyerRequestSummary;

  factory BuyerRequestSummary.fromJson(Map<String, dynamic> json) =>
      _$BuyerRequestSummaryFromJson(json);
}
