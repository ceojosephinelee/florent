# src/test/resources/features/reservation/reservation_confirmation.feature

Feature: 구매자 제안 선택 및 예약 확정
  구매자는 SUBMITTED 상태의 제안을 선택하여 예약을 확정하고 Mock 결제를 완료할 수 있다.

  Background:
    Given 구매자 "buyer_rsv01" 이 로그인되어 있다
    And   구매자의 OPEN 요청이 존재한다
    And   판매자 "꽃집R" 가 꽃집과 함께 등록되어 있다
    And   판매자가 해당 요청에 제안 초안을 생성했다
    And   판매자가 제안을 임시저장했다
    And   판매자가 제안을 제출했다

  # ─────────────────────────────────────────────
  # Happy Path
  # ─────────────────────────────────────────────

  Scenario: 구매자가 SUBMITTED 제안을 선택하면 예약이 확정된다
    When  구매자가 해당 제안을 선택한다
    Then  응답 상태 코드는 201이다
    And   예약 상태는 "CONFIRMED" 이다
    And   결제 상태는 "SUCCEEDED" 이다
    And   판매자에게 RESERVATION_CONFIRMED 알림이 생성된다

  Scenario: 구매자가 예약 목록을 조회한다
    Given 구매자가 해당 제안을 선택했다
    When  구매자가 예약 목록을 조회한다
    Then  응답 상태 코드는 200이다
    And   예약 목록에 1건이 포함된다

  Scenario: 구매자가 예약 상세를 조회한다
    Given 구매자가 해당 제안을 선택했다
    When  구매자가 예약 상세를 조회한다
    Then  응답 상태 코드는 200이다
    And   예약 상세의 상태는 "CONFIRMED" 이다

  # ─────────────────────────────────────────────
  # Unhappy Path
  # ─────────────────────────────────────────────

  Scenario: 같은 idempotencyKey로 중복 결제하면 422가 반환된다
    Given 구매자가 해당 제안을 선택했다
    When  구매자가 같은_idempotencyKey로_다시_선택한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "DUPLICATE_PAYMENT" 이다

  Scenario: 이미 확정된 요청의 다른 제안을 선택하면 422가 반환된다
    Given 다른 판매자 "꽃집S" 가 꽃집과 함께 등록되어 있다
    And   다른 판매자가 해당 요청에 제안을 제출했다
    And   구매자가 해당 제안을 선택했다
    When  구매자가 다른_판매자의_제안을_선택한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "REQUEST_ALREADY_CONFIRMED" 이다
