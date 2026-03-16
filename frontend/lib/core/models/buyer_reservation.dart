/// 구매자 예약 상세 모델
class BuyerReservationDetail {
  final int reservationId;
  final String status;
  final String shopName;
  final String shopAddress;
  final String conceptTitle;
  final String description;
  final int price;
  final String fulfillmentType;
  final String fulfillmentDate;
  final String fulfillmentSlotKind;
  final String fulfillmentSlotValue;
  final String placeAddressText;
  final String? shopEmoji;
  final List<String> imageUrls;
  final List<String> purposeTags;
  final List<String> relationTags;
  final List<String> moodTags;
  final String? budgetTier;

  String get slotLabel => '$fulfillmentDate $fulfillmentSlotValue';

  const BuyerReservationDetail({
    required this.reservationId,
    required this.status,
    required this.shopName,
    required this.shopAddress,
    required this.conceptTitle,
    required this.description,
    required this.price,
    required this.fulfillmentType,
    required this.fulfillmentDate,
    required this.fulfillmentSlotKind,
    required this.fulfillmentSlotValue,
    required this.placeAddressText,
    this.shopEmoji,
    this.imageUrls = const [],
    this.purposeTags = const [],
    this.relationTags = const [],
    this.moodTags = const [],
    this.budgetTier,
  });

  factory BuyerReservationDetail.fromApiJson(Map<String, dynamic> json) {
    final proposal = json['proposal'] as Map<String, dynamic>? ?? {};
    final shop = json['shop'] as Map<String, dynamic>? ?? {};
    final slot = json['fulfillmentSlot'] as Map<String, dynamic>? ?? {};
    final request = json['request'] as Map<String, dynamic>? ?? {};

    return BuyerReservationDetail(
      reservationId: json['reservationId'] as int,
      status: json['status'] as String,
      shopName: shop['name'] as String? ?? '',
      shopAddress: shop['addressText'] as String? ?? '',
      conceptTitle: proposal['conceptTitle'] as String? ?? '',
      description: proposal['description'] as String? ?? '',
      price: proposal['price'] as int? ?? 0,
      fulfillmentType: json['fulfillmentType'] as String,
      fulfillmentDate: json['fulfillmentDate'] as String,
      fulfillmentSlotKind: slot['kind'] as String? ?? '',
      fulfillmentSlotValue: slot['value'] as String? ?? '',
      placeAddressText: json['placeAddressText'] as String? ?? '',
      imageUrls: List<String>.from(proposal['imageUrls'] ?? []),
      purposeTags: List<String>.from(request['purposeTags'] ?? []),
      relationTags: List<String>.from(request['relationTags'] ?? []),
      moodTags: List<String>.from(request['moodTags'] ?? []),
      budgetTier: request['budgetTier'] as String?,
    );
  }
}
