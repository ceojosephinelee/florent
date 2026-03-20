// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'buyer_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$BuyerRequestSummaryImpl _$$BuyerRequestSummaryImplFromJson(
  Map<String, dynamic> json,
) => _$BuyerRequestSummaryImpl(
  requestId: (json['requestId'] as num).toInt(),
  status: json['status'] as String,
  budgetTier: json['budgetTier'] as String,
  fulfillmentType: json['fulfillmentType'] as String,
  fulfillmentDate: json['fulfillmentDate'] as String,
  expiresAt: json['expiresAt'] as String,
  draftProposalCount: (json['draftProposalCount'] as num).toInt(),
  submittedProposalCount: (json['submittedProposalCount'] as num).toInt(),
  purposeTags:
      (json['purposeTags'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList() ??
      const [],
  relationTags:
      (json['relationTags'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList() ??
      const [],
);

Map<String, dynamic> _$$BuyerRequestSummaryImplToJson(
  _$BuyerRequestSummaryImpl instance,
) => <String, dynamic>{
  'requestId': instance.requestId,
  'status': instance.status,
  'budgetTier': instance.budgetTier,
  'fulfillmentType': instance.fulfillmentType,
  'fulfillmentDate': instance.fulfillmentDate,
  'expiresAt': instance.expiresAt,
  'draftProposalCount': instance.draftProposalCount,
  'submittedProposalCount': instance.submittedProposalCount,
  'purposeTags': instance.purposeTags,
  'relationTags': instance.relationTags,
};
