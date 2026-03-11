# src/test/resources/features/e2e/full_happy_path.feature

Feature: 전체 서비스 흐름 End-to-End (Happy Path)
  구매자가 요청을 생성하고, 판매자가 제안을 제출하고,
  구매자가 제안을 선택하여 예약을 확정하는 전체 플로우가 에러 없이 작동해야 한다.

  이 시나리오는 핵심 비즈니스 사이클의 Green Light 검증이다.

  Scenario: 꽃 큐레이션 요청부터 예약 확정까지 전체 플로우가 성공한다
    # ── Step 1. 구매자 요청 생성 ──────────────────
    Given 구매자 "buyer01" 이 로그인되어 있다
    And   반경 2km 이내에 "장미꽃집" 판매자 "seller01" 이 존재한다
    When  구매자가 픽업 방식으로 꽃 큐레이션 요청을 생성한다
    Then  요청이 OPEN 상태로 생성된다
    And   "seller01" 에게 REQUEST_ARRIVED 알림이 생성된다

    # ── Step 2. 판매자 제안 작성 및 제출 ───────────
    Given 판매자 "seller01" 이 로그인되어 있다
    When  판매자가 해당 요청에 대해 제안서 작성을 시작한다
    Then  제안이 DRAFT 상태로 생성된다
    When  판매자가 아래 내용으로 제안을 임시저장하고 제출한다
      | conceptTitle       | 봄 햇살 같은 화사함                     |
      | description        | 노란 프리지아와 핑크 장미로 봄을 담았어요 |
      | availableSlotKind  | PICKUP_30M                             |
      | availableSlotValue | 14:00                                  |
      | price              | 35000                                  |
    Then  제안 상태는 "SUBMITTED" 이다
    And   구매자 "buyer01" 에게 PROPOSAL_ARRIVED 알림이 생성된다

    # ── Step 3. 구매자 제안 조회 ──────────────────
    Given 구매자 "buyer01" 이 로그인되어 있다
    When  구매자가 요청의 제안 목록을 조회한다
    Then  SUBMITTED 제안 1건이 목록에 포함된다
    And   목록에 price 필드는 없다
    When  구매자가 해당 제안의 상세를 조회한다
    Then  개념 제목, 설명, 가격이 모두 포함된다

    # ── Step 4. 제안 선택 및 예약 확정 ──────────────
    When  구매자가 해당 제안을 선택한다 (idempotencyKey: "e2e-uuid-001")
    Then  응답 상태 코드는 201이다
    And   예약 상태는 "CONFIRMED" 이다
    And   결제 상태는 "SUCCEEDED" 이다
    And   요청 상태가 "CONFIRMED" 로 변경된다
    And   "seller01" 에게 RESERVATION_CONFIRMED 알림이 생성된다

    # ── Step 5. 구매자 예약 확인 ──────────────────
    When  구매자가 예약 목록을 조회한다
    Then  예약 1건이 조회된다
    And   꽃집 이름은 "장미꽃집" 이다
    And   수령 방식은 "PICKUP" 이다
