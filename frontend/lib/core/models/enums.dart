enum RequestStatus {
  open('OPEN', '진행중'),
  expired('EXPIRED', '만료'),
  confirmed('CONFIRMED', '확정');

  const RequestStatus(this.value, this.label);
  final String value;
  final String label;
}

enum BudgetTier {
  tier1('TIER1', '작게', '30,000원 ~ 50,000원'),
  tier2('TIER2', '보통', '60,000원 ~ 80,000원'),
  tier3('TIER3', '크게', '90,000원 ~ 130,000원'),
  tier4('TIER4', '프리미엄', '150,000원 이상');

  const BudgetTier(this.value, this.label, this.priceRange);
  final String value;
  final String label;
  final String priceRange;
}

enum FulfillmentType {
  pickup('PICKUP', '픽업'),
  delivery('DELIVERY', '배송');

  const FulfillmentType(this.value, this.label);
  final String value;
  final String label;
}
