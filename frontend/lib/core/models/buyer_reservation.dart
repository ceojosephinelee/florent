/// 구매자 예약 상세 모델 (mock용 — 추후 freezed 전환 가능)
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
}
