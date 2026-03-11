import '../../models/proposal.dart';
import '../../models/seller_models.dart';

final mockSellerHome = SellerHomeData(
  openRequestCount: 3,
  draftProposalCount: 1,
  confirmedReservationCount: 5,
  shopName: '플라워 그로브',
);

final mockSellerRequests = [
  SellerRequestSummary(
    requestId: 1,
    status: 'OPEN',
    budgetTier: 'TIER2',
    fulfillmentType: 'PICKUP',
    fulfillmentDate: '2026-03-15',
    expiresAt: DateTime.now().add(const Duration(hours: 21)).toIso8601String(),
    purposeTags: ['생일'],
    moodTags: ['로맨틱', '자연스러운'],
    distance: '1.2km',
    slotLabel: '14:00',
  ),
  SellerRequestSummary(
    requestId: 2,
    status: 'OPEN',
    budgetTier: 'TIER3',
    fulfillmentType: 'DELIVERY',
    fulfillmentDate: '2026-03-16',
    expiresAt: DateTime.now().add(const Duration(hours: 30)).toIso8601String(),
    purposeTags: ['기념일'],
    moodTags: ['고급스러운', '로맨틱'],
    myProposalStatus: 'DRAFT',
    distance: '2.8km',
    slotLabel: '오전',
  ),
  SellerRequestSummary(
    requestId: 3,
    status: 'OPEN',
    budgetTier: 'TIER1',
    fulfillmentType: 'PICKUP',
    fulfillmentDate: '2026-03-17',
    expiresAt: DateTime.now().add(const Duration(hours: 40)).toIso8601String(),
    purposeTags: ['졸업'],
    moodTags: ['청순한', '귀여운'],
    distance: '0.8km',
  ),
  SellerRequestSummary(
    requestId: 4,
    status: 'EXPIRED',
    budgetTier: 'TIER2',
    fulfillmentType: 'DELIVERY',
    fulfillmentDate: '2026-03-10',
    expiresAt: DateTime.now().subtract(const Duration(hours: 2)).toIso8601String(),
    purposeTags: ['감사'],
    moodTags: ['따뜻한', '자연스러운'],
    distance: '1.5km',
    slotLabel: '오후',
  ),
  SellerRequestSummary(
    requestId: 5,
    status: 'CONFIRMED',
    budgetTier: 'TIER4',
    fulfillmentType: 'PICKUP',
    fulfillmentDate: '2026-03-12',
    expiresAt: DateTime.now().subtract(const Duration(hours: 10)).toIso8601String(),
    purposeTags: ['프로포즈'],
    moodTags: ['고급스러운', '화려한'],
    myProposalStatus: 'SUBMITTED',
    distance: '0.5km',
    slotLabel: '18:00',
  ),
];

// ── 예약 상세 mock ──

final mockSellerReservations = <int, SellerReservationDetail>{
  1001: SellerReservationDetail(
    reservationId: 1001,
    status: 'CONFIRMED',
    buyerNickName: '이지수',
    conceptTitle: '핑크 작약 꽃다발',
    description: '핑크 장미 5송이와 작약 3송이를 중심으로, 유칼립투스와 스타티스를 곁들인 따뜻한 봄 분위기의 꽃다발입니다.',
    price: 68000,
    fulfillmentType: 'PICKUP',
    fulfillmentDate: '2026-03-15',
    fulfillmentSlotKind: 'PICKUP_30M',
    fulfillmentSlotValue: '14:00',
    placeAddressText: '플라워 그로브 (서울 강남구 역삼동 123-45)',
    confirmedAt: '2026-03-10T10:30:00',
    purposeTags: ['생일'],
    relationTags: ['부모님'],
    moodTags: ['로맨틱', '자연스러운'],
    budgetTier: 'TIER2',
  ),
  1002: SellerReservationDetail(
    reservationId: 1002,
    status: 'CONFIRMED',
    buyerNickName: '김민수',
    conceptTitle: '화이트 프리지아 배송 꽃다발',
    description: '화이트 프리지아와 라넌큘러스를 중심으로, 깔끔하고 우아한 느낌의 꽃다발입니다. 배송 시 보냉 패키지 포함.',
    price: 95000,
    fulfillmentType: 'DELIVERY',
    fulfillmentDate: '2026-03-08',
    fulfillmentSlotKind: 'DELIVERY_WINDOW',
    fulfillmentSlotValue: 'AFTERNOON',
    placeAddressText: '서울 서초구 반포동 456-78',
    confirmedAt: '2026-03-06T15:20:00',
    purposeTags: ['기념일'],
    relationTags: ['연인'],
    moodTags: ['고급스러운', '우아한'],
    budgetTier: 'TIER3',
  ),
};

final mockSellerReservationHistory = [
  SellerReservationSummary(reservationId: 1001, conceptTitle: '핑크 작약 꽃다발', price: 68000, confirmedAt: '2026-03-10', fulfillmentType: 'PICKUP'),
  SellerReservationSummary(reservationId: 1002, conceptTitle: '화이트 프리지아 배송 꽃다발', price: 95000, confirmedAt: '2026-03-06', fulfillmentType: 'DELIVERY'),
];

// ── 판매자 알림 mock ──

final mockSellerNotifications = [
  NotificationItem(
    notificationId: 201,
    type: 'RESERVATION_CONFIRMED',
    title: '예약이 확정되었어요!',
    body: '구매자 이지수 님이 내 제안을 선택했어요.',
    createdAt: DateTime.now().subtract(const Duration(minutes: 5)).toIso8601String(),
    isRead: false,
    referenceType: 'RESERVATION',
    referenceId: 1001,
  ),
  NotificationItem(
    notificationId: 202,
    type: 'REQUEST_ARRIVED',
    title: '새로운 요청이 도착했어요',
    body: '생일 · 기본형 · 픽업 요청이 들어왔어요.',
    createdAt: DateTime.now().subtract(const Duration(hours: 2)).toIso8601String(),
    isRead: false,
    referenceType: 'REQUEST',
    referenceId: 1,
  ),
  NotificationItem(
    notificationId: 203,
    type: 'RESERVATION_CONFIRMED',
    title: '예약이 확정되었어요!',
    body: '구매자 김민수 님이 내 제안을 선택했어요.',
    createdAt: DateTime.now().subtract(const Duration(days: 1, hours: 3)).toIso8601String(),
    isRead: true,
    referenceType: 'RESERVATION',
    referenceId: 1002,
  ),
];

final mockSellerRequestDetail = SellerRequestDetail(
  requestId: 1,
  status: 'OPEN',
  budgetTier: 'TIER2',
  fulfillmentType: 'PICKUP',
  fulfillmentDate: '2026-03-15',
  expiresAt: DateTime.now().add(const Duration(hours: 21)).toIso8601String(),
  placeAddressText: '강남구 역삼동 근처',
  purposeTags: ['생일'],
  relationTags: ['부모님'],
  moodTags: ['로맨틱', '자연스러운'],
  requestedTimeSlots: [
    {'kind': 'PICKUP_30M', 'value': '14:00'},
    {'kind': 'PICKUP_30M', 'value': '14:30'},
  ],
  distance: '1.2km',
);
