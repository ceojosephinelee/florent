# src/test/resources/features/reservation/reservation_confirmation.feature

Feature: 제안 선택 및 예약 확정 (핵심 Happy Path)
  구매자는 도착한 제안 중 하나를 선택하여 Mock 결제를 완료하고 예약을 확정한다.
  예약 확정은 단일 트랜잭션 내에서 요청 상태 변경, 제안 상태 변경, 예약 생성, 결제 생성, 알림 저장이 모두 처리된다.

  Background:
    Given 구매자 "buyer01" 이 로그인되어 있다
    And   구매자의 OPEN 요청이 존재한다
    And   판매자 "seller01" 의 SUBMITTED 제안 (가격 35000) 이 존재한다
    And   판매자 "seller02" 의 SUBMITTED 제안 (가격 42000) 이 존재한다

  # ─────────────────────────────────────────────
  # Happy Path
  # ─────────────────────────────────────────────

  Scenario: 구매자가 제안을 선택하면 예약이 확정된다
    When  구매자가 "seller01" 의 제안을 선택한다 (idempotencyKey: "uuid-001")
    Then  응답 상태 코드는 201이다
    And   응답에 reservationId 가 포함된다
    And   예약 상태는 "CONFIRMED" 이다
    And   결제 상태는 "SUCCEEDED" 이다
    And   결제 금액은 35000이다

  Scenario: 제안 선택 후 요청 상태가 CONFIRMED 로 변경된다
    When  구매자가 "seller01" 의 제안을 선택한다
    Then  요청 상태가 "CONFIRMED" 로 변경된다

  Scenario: 제안 선택 후 선택된 제안은 SELECTED, 나머지는 NOT_SELECTED 로 변경된다
    When  구매자가 "seller01" 의 제안을 선택한다
    Then  "seller01" 의 제안 상태는 "SELECTED" 이다
    And   "seller02" 의 제안 상태는 "NOT_SELECTED" 이다

  Scenario: 예약 확정 후 선택된 판매자에게 RESERVATION_CONFIRMED 알림이 생성된다
    When  구매자가 "seller01" 의 제안을 선택한다
    Then  "seller01" 판매자에게 RESERVATION_CONFIRMED 알림이 1건 생성된다
    And   "seller02" 판매자에게는 알림이 생성되지 않는다

  Scenario: 구매자가 예약 목록을 조회하면 확정된 예약을 확인할 수 있다
    Given 구매자가 "seller01" 의 제안을 선택하여 예약을 확정했다
    When  구매자가 예약 목록을 조회한다
    Then  응답 상태 코드는 200이다
    And   예약 1건이 목록에 포함된다
    And   예약 상태는 "CONFIRMED" 이다
    And   꽃집 이름은 "장미꽃집" 이다

  Scenario: 동일한 idempotencyKey 로 중복 결제를 시도하면 멱등하게 처리된다
    Given 구매자가 "seller01" 의 제안을 idempotencyKey "uuid-001" 로 선택하여 예약을 확정했다
    When  구매자가 동일한 idempotencyKey "uuid-001" 로 다시 선택 API를 호출한다
    Then  새로운 PAYMENT 가 생성되지 않는다
    And   기존 예약 정보를 반환한다

  # ─────────────────────────────────────────────
  # Unhappy Path
  # ─────────────────────────────────────────────

  Scenario: 이미 CONFIRMED 된 요청의 제안을 선택하면 422가 반환된다
    Given 구매자의 요청이 이미 CONFIRMED 상태다
    When  구매자가 "seller02" 의 제안을 선택한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "BUSINESS_ERROR" 이다

  Scenario: 만료된 제안을 선택하면 422가 반환된다
    Given "seller01" 의 제안이 EXPIRED 상태다
    When  구매자가 해당 제안을 선택한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "BUSINESS_ERROR" 이다

  Scenario: 만료된 요청의 제안을 선택하면 422가 반환된다
    Given 구매자의 요청이 EXPIRED 상태다
    When  구매자가 "seller01" 의 제안을 선택한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "BUSINESS_ERROR" 이다

  Scenario: 다른 구매자의 요청에 대한 제안을 선택하면 403이 반환된다
    Given 구매자 "buyer02" 의 요청에 대한 제안이 존재한다
    When  구매자 "buyer01" 이 해당 제안을 선택한다
    Then  응답 상태 코드는 403이다
    And   에러 코드는 "FORBIDDEN" 이다

  Scenario: idempotencyKey 없이 제안 선택 시 400이 반환된다
    When  구매자가 idempotencyKey 없이 제안 선택 API를 호출한다
    Then  응답 상태 코드는 400이다
    And   에러 코드는 "VALIDATION_ERROR" 이다
