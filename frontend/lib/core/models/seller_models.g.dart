// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'seller_models.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$SellerHomeDataImpl _$$SellerHomeDataImplFromJson(Map<String, dynamic> json) =>
    _$SellerHomeDataImpl(
      openRequestCount: (json['openRequestCount'] as num).toInt(),
      draftProposalCount: (json['draftProposalCount'] as num).toInt(),
      confirmedReservationCount: (json['confirmedReservationCount'] as num)
          .toInt(),
      shopName: json['shopName'] as String,
    );

Map<String, dynamic> _$$SellerHomeDataImplToJson(
  _$SellerHomeDataImpl instance,
) => <String, dynamic>{
  'openRequestCount': instance.openRequestCount,
  'draftProposalCount': instance.draftProposalCount,
  'confirmedReservationCount': instance.confirmedReservationCount,
  'shopName': instance.shopName,
};

_$SellerRequestSummaryImpl _$$SellerRequestSummaryImplFromJson(
  Map<String, dynamic> json,
) => _$SellerRequestSummaryImpl(
  requestId: (json['requestId'] as num).toInt(),
  status: json['status'] as String,
  budgetTier: json['budgetTier'] as String,
  fulfillmentType: json['fulfillmentType'] as String,
  fulfillmentDate: json['fulfillmentDate'] as String,
  expiresAt: json['expiresAt'] as String,
  purposeTags:
      (json['purposeTags'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList() ??
      const [],
  moodTags:
      (json['moodTags'] as List<dynamic>?)?.map((e) => e as String).toList() ??
      const [],
  myProposalStatus: json['myProposalStatus'] as String?,
  distance: json['distance'] as String?,
  slotLabel: json['slotLabel'] as String?,
);

Map<String, dynamic> _$$SellerRequestSummaryImplToJson(
  _$SellerRequestSummaryImpl instance,
) => <String, dynamic>{
  'requestId': instance.requestId,
  'status': instance.status,
  'budgetTier': instance.budgetTier,
  'fulfillmentType': instance.fulfillmentType,
  'fulfillmentDate': instance.fulfillmentDate,
  'expiresAt': instance.expiresAt,
  'purposeTags': instance.purposeTags,
  'moodTags': instance.moodTags,
  'myProposalStatus': instance.myProposalStatus,
  'distance': instance.distance,
  'slotLabel': instance.slotLabel,
};

_$SellerRequestDetailImpl _$$SellerRequestDetailImplFromJson(
  Map<String, dynamic> json,
) => _$SellerRequestDetailImpl(
  requestId: (json['requestId'] as num).toInt(),
  status: json['status'] as String,
  budgetTier: json['budgetTier'] as String,
  fulfillmentType: json['fulfillmentType'] as String,
  fulfillmentDate: json['fulfillmentDate'] as String,
  expiresAt: json['expiresAt'] as String,
  placeAddressText: json['placeAddressText'] as String,
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
  moodTags:
      (json['moodTags'] as List<dynamic>?)?.map((e) => e as String).toList() ??
      const [],
  requestedTimeSlots:
      (json['requestedTimeSlots'] as List<dynamic>?)
          ?.map((e) => Map<String, String>.from(e as Map))
          .toList() ??
      const [],
  myProposalId: json['myProposalId'] as String?,
  distance: json['distance'] as String?,
);

Map<String, dynamic> _$$SellerRequestDetailImplToJson(
  _$SellerRequestDetailImpl instance,
) => <String, dynamic>{
  'requestId': instance.requestId,
  'status': instance.status,
  'budgetTier': instance.budgetTier,
  'fulfillmentType': instance.fulfillmentType,
  'fulfillmentDate': instance.fulfillmentDate,
  'expiresAt': instance.expiresAt,
  'placeAddressText': instance.placeAddressText,
  'purposeTags': instance.purposeTags,
  'relationTags': instance.relationTags,
  'moodTags': instance.moodTags,
  'requestedTimeSlots': instance.requestedTimeSlots,
  'myProposalId': instance.myProposalId,
  'distance': instance.distance,
};

_$SellerReservationDetailImpl _$$SellerReservationDetailImplFromJson(
  Map<String, dynamic> json,
) => _$SellerReservationDetailImpl(
  reservationId: (json['reservationId'] as num).toInt(),
  status: json['status'] as String,
  buyerNickName: json['buyerNickName'] as String,
  conceptTitle: json['conceptTitle'] as String,
  description: json['description'] as String,
  price: (json['price'] as num).toInt(),
  fulfillmentType: json['fulfillmentType'] as String,
  fulfillmentDate: json['fulfillmentDate'] as String,
  fulfillmentSlotKind: json['fulfillmentSlotKind'] as String,
  fulfillmentSlotValue: json['fulfillmentSlotValue'] as String,
  placeAddressText: json['placeAddressText'] as String,
  confirmedAt: json['confirmedAt'] as String,
  imageUrls:
      (json['imageUrls'] as List<dynamic>?)?.map((e) => e as String).toList() ??
      const [],
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
  moodTags:
      (json['moodTags'] as List<dynamic>?)?.map((e) => e as String).toList() ??
      const [],
  budgetTier: json['budgetTier'] as String?,
);

Map<String, dynamic> _$$SellerReservationDetailImplToJson(
  _$SellerReservationDetailImpl instance,
) => <String, dynamic>{
  'reservationId': instance.reservationId,
  'status': instance.status,
  'buyerNickName': instance.buyerNickName,
  'conceptTitle': instance.conceptTitle,
  'description': instance.description,
  'price': instance.price,
  'fulfillmentType': instance.fulfillmentType,
  'fulfillmentDate': instance.fulfillmentDate,
  'fulfillmentSlotKind': instance.fulfillmentSlotKind,
  'fulfillmentSlotValue': instance.fulfillmentSlotValue,
  'placeAddressText': instance.placeAddressText,
  'confirmedAt': instance.confirmedAt,
  'imageUrls': instance.imageUrls,
  'purposeTags': instance.purposeTags,
  'relationTags': instance.relationTags,
  'moodTags': instance.moodTags,
  'budgetTier': instance.budgetTier,
};

_$SellerReservationSummaryImpl _$$SellerReservationSummaryImplFromJson(
  Map<String, dynamic> json,
) => _$SellerReservationSummaryImpl(
  reservationId: (json['reservationId'] as num).toInt(),
  conceptTitle: json['conceptTitle'] as String,
  price: (json['price'] as num).toInt(),
  confirmedAt: json['confirmedAt'] as String,
  fulfillmentType: json['fulfillmentType'] as String,
);

Map<String, dynamic> _$$SellerReservationSummaryImplToJson(
  _$SellerReservationSummaryImpl instance,
) => <String, dynamic>{
  'reservationId': instance.reservationId,
  'conceptTitle': instance.conceptTitle,
  'price': instance.price,
  'confirmedAt': instance.confirmedAt,
  'fulfillmentType': instance.fulfillmentType,
};
