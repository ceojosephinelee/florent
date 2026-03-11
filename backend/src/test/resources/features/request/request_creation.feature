# src/test/resources/features/request/request_creation.feature

Feature: 구매자 꽃 큐레이션 요청 생성
  구매자는 꽃 큐레이션 요청서를 작성하여 반경 2km 내 꽃집들에게 제안을 요청할 수 있다.
  요청 생성 후 48시간 동안 유효하며, 이후 자동 만료된다.

  Background:
    Given 구매자 "buyer01" 이 로그인되어 있다
    And   반경 2km 이내에 꽃집 "장미꽃집" 이 위치해 있다 (위도 37.4985, 경도 127.0279)
    And   반경 2km 밖에 꽃집 "먼꽃집" 이 위치해 있다 (위도 37.5500, 경도 127.0800)

  # ─────────────────────────────────────────────
  # Happy Path
  # ─────────────────────────────────────────────

  Scenario: 픽업 방식으로 요청을 생성하면 OPEN 상태로 생성된다
    When  구매자가 아래 내용으로 꽃 요청을 생성한다
      | purposeTags       | 생일                          |
      | relationTags      | 친구                          |
      | moodTags          | 밝음, 화사함                   |
      | budgetTier        | TIER2                         |
      | fulfillmentType   | PICKUP                        |
      | fulfillmentDate   | 오늘로부터 3일 후               |
      | timeSlots         | PICKUP_30M:14:00              |
      | placeAddressText  | 서울시 강남구 테헤란로 1         |
      | placeLat          | 37.498095                     |
      | placeLng          | 127.027610                    |
    Then  응답 상태 코드는 201이다
    And   요청 상태는 "OPEN" 이다
    And   만료 시각은 생성 시각으로부터 48시간 후다

  Scenario: 요청 생성 시 반경 2km 내 꽃집 판매자에게 알림이 전송된다
    When  구매자가 픽업 방식 요청을 생성한다 (기준 좌표 37.498095, 127.027610)
    Then  "장미꽃집" 판매자에게 REQUEST_ARRIVED 알림이 1건 생성된다
    And   "먼꽃집" 판매자에게는 알림이 생성되지 않는다

  Scenario: 배송 방식으로 요청을 생성한다
    When  구매자가 아래 내용으로 꽃 요청을 생성한다
      | fulfillmentType   | DELIVERY                      |
      | timeSlots         | DELIVERY_WINDOW:MORNING       |
      | placeAddressText  | 서울시 강남구 역삼동 1번지       |
      | placeLat          | 37.498095                     |
      | placeLng          | 127.027610                    |
    Then  응답 상태 코드는 201이다
    And   요청 상태는 "OPEN" 이다

  Scenario: 복수 타임슬롯을 선택하여 요청을 생성한다
    When  구매자가 타임슬롯 [PICKUP_30M:14:00, PICKUP_30M:15:00] 으로 요청을 생성한다
    Then  응답 상태 코드는 201이다
    And   저장된 요청의 타임슬롯 수는 2개다

  # ─────────────────────────────────────────────
  # Unhappy Path
  # ─────────────────────────────────────────────

  Scenario: 인증 없이 요청 생성 시 401이 반환된다
    When  인증 헤더 없이 요청 생성 API를 호출한다
    Then  응답 상태 코드는 401이다
    And   에러 코드는 "UNAUTHORIZED" 이다

  Scenario: 필수 필드(description)가 누락되면 400이 반환된다
    When  구매자가 budgetTier 없이 요청 생성을 시도한다
    Then  응답 상태 코드는 400이다
    And   에러 코드는 "VALIDATION_ERROR" 이다

  Scenario: 판매자 계정으로 요청 생성 시 403이 반환된다
    Given 판매자 "seller01" 이 로그인되어 있다
    When  판매자가 요청 생성 API를 호출한다
    Then  응답 상태 코드는 403이다
    And   에러 코드는 "FORBIDDEN" 이다
