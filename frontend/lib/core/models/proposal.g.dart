// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$ProposalSummaryImpl _$$ProposalSummaryImplFromJson(
  Map<String, dynamic> json,
) => _$ProposalSummaryImpl(
  proposalId: (json['proposalId'] as num).toInt(),
  shopName: json['shopName'] as String,
  conceptTitle: json['conceptTitle'] as String,
  status: json['status'] as String,
  expiresAt: json['expiresAt'] as String,
  description: json['description'] as String?,
  shopDistance: json['shopDistance'] as String?,
  slotLabel: json['slotLabel'] as String?,
  mainFlowers: json['mainFlowers'] as String?,
  shopEmoji: json['shopEmoji'] as String?,
);

Map<String, dynamic> _$$ProposalSummaryImplToJson(
  _$ProposalSummaryImpl instance,
) => <String, dynamic>{
  'proposalId': instance.proposalId,
  'shopName': instance.shopName,
  'conceptTitle': instance.conceptTitle,
  'status': instance.status,
  'expiresAt': instance.expiresAt,
  'description': instance.description,
  'shopDistance': instance.shopDistance,
  'slotLabel': instance.slotLabel,
  'mainFlowers': instance.mainFlowers,
  'shopEmoji': instance.shopEmoji,
};

_$ProposalDetailImpl _$$ProposalDetailImplFromJson(Map<String, dynamic> json) =>
    _$ProposalDetailImpl(
      proposalId: (json['proposalId'] as num).toInt(),
      requestId: (json['requestId'] as num).toInt(),
      status: json['status'] as String,
      shopName: json['shopName'] as String,
      shopAddress: json['shopAddress'] as String,
      conceptTitle: json['conceptTitle'] as String,
      description: json['description'] as String,
      price: (json['price'] as num).toInt(),
      expiresAt: json['expiresAt'] as String,
      shopDistance: json['shopDistance'] as String?,
      slotLabel: json['slotLabel'] as String?,
      shopEmoji: json['shopEmoji'] as String?,
      mainFlowers:
          (json['mainFlowers'] as List<dynamic>?)
              ?.map((e) => e as String)
              .toList() ??
          const [],
      imageUrls:
          (json['imageUrls'] as List<dynamic>?)
              ?.map((e) => e as String)
              .toList() ??
          const [],
    );

Map<String, dynamic> _$$ProposalDetailImplToJson(
  _$ProposalDetailImpl instance,
) => <String, dynamic>{
  'proposalId': instance.proposalId,
  'requestId': instance.requestId,
  'status': instance.status,
  'shopName': instance.shopName,
  'shopAddress': instance.shopAddress,
  'conceptTitle': instance.conceptTitle,
  'description': instance.description,
  'price': instance.price,
  'expiresAt': instance.expiresAt,
  'shopDistance': instance.shopDistance,
  'slotLabel': instance.slotLabel,
  'shopEmoji': instance.shopEmoji,
  'mainFlowers': instance.mainFlowers,
  'imageUrls': instance.imageUrls,
};

_$ReservationDetailImpl _$$ReservationDetailImplFromJson(
  Map<String, dynamic> json,
) => _$ReservationDetailImpl(
  reservationId: (json['reservationId'] as num).toInt(),
  status: json['status'] as String,
  shopName: json['shopName'] as String,
  shopAddress: json['shopAddress'] as String,
  conceptTitle: json['conceptTitle'] as String,
  price: (json['price'] as num).toInt(),
  fulfillmentType: json['fulfillmentType'] as String,
  fulfillmentDate: json['fulfillmentDate'] as String,
  slotLabel: json['slotLabel'] as String,
  shopEmoji: json['shopEmoji'] as String?,
);

Map<String, dynamic> _$$ReservationDetailImplToJson(
  _$ReservationDetailImpl instance,
) => <String, dynamic>{
  'reservationId': instance.reservationId,
  'status': instance.status,
  'shopName': instance.shopName,
  'shopAddress': instance.shopAddress,
  'conceptTitle': instance.conceptTitle,
  'price': instance.price,
  'fulfillmentType': instance.fulfillmentType,
  'fulfillmentDate': instance.fulfillmentDate,
  'slotLabel': instance.slotLabel,
  'shopEmoji': instance.shopEmoji,
};

_$NotificationItemImpl _$$NotificationItemImplFromJson(
  Map<String, dynamic> json,
) => _$NotificationItemImpl(
  notificationId: (json['notificationId'] as num).toInt(),
  type: json['type'] as String,
  title: json['title'] as String,
  body: json['body'] as String,
  createdAt: json['createdAt'] as String,
  isRead: json['isRead'] as bool,
  referenceType: json['referenceType'] as String?,
  referenceId: (json['referenceId'] as num?)?.toInt(),
  proposalId: (json['proposalId'] as num?)?.toInt(),
);

Map<String, dynamic> _$$NotificationItemImplToJson(
  _$NotificationItemImpl instance,
) => <String, dynamic>{
  'notificationId': instance.notificationId,
  'type': instance.type,
  'title': instance.title,
  'body': instance.body,
  'createdAt': instance.createdAt,
  'isRead': instance.isRead,
  'referenceType': instance.referenceType,
  'referenceId': instance.referenceId,
  'proposalId': instance.proposalId,
};
