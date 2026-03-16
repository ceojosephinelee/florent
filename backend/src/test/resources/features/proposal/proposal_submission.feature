# src/test/resources/features/proposal/proposal_submission.feature

Feature: 판매자 제안 작성 및 제출
  판매자는 OPEN 상태 요청에 대해 제안 초안을 생성하고, 임시저장한 뒤, 제출할 수 있다.

  Background:
    Given 구매자 "buyer_sub01" 이 로그인되어 있다
    And   구매자의 OPEN 요청이 존재한다
    And   판매자 "꽃집A" 가 꽃집과 함께 등록되어 있다

  # ─────────────────────────────────────────────
  # Happy Path
  # ─────────────────────────────────────────────

  Scenario: 판매자가 OPEN 요청에 제안 초안(DRAFT)을 생성한다
    When  판매자가 해당 요청에 제안 초안을 생성한다
    Then  응답 상태 코드는 201이다
    And   제안 상태는 "DRAFT" 이다
    And   제안 만료 시각이 존재한다

  Scenario: 판매자가 DRAFT 제안을 임시저장한다
    Given 판매자가 해당 요청에 제안 초안을 생성했다
    When  판매자가 제안을 임시저장한다
    Then  응답 상태 코드는 200이다
    And   제안 상태는 "DRAFT" 이다

  Scenario: 판매자가 임시저장된 제안을 제출한다
    Given 판매자가 해당 요청에 제안 초안을 생성했다
    And   판매자가 제안을 임시저장했다
    When  판매자가 제안을 제출한다
    Then  응답 상태 코드는 200이다
    And   제안 상태는 "SUBMITTED" 이다
    And   제안 제출 시각이 존재한다
    And   구매자에게 PROPOSAL_ARRIVED 알림이 생성된다

  # ─────────────────────────────────────────────
  # Unhappy Path
  # ─────────────────────────────────────────────

  Scenario: 같은 요청에 중복으로 제안을 생성하면 422가 반환된다
    Given 판매자가 해당 요청에 제안 초안을 생성했다
    When  판매자가 해당 요청에 제안 초안을 생성한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "PROPOSAL_ALREADY_EXISTS" 이다

  Scenario: OPEN이 아닌 요청에 제안을 생성하면 422가 반환된다
    Given EXPIRED 상태의 요청이 존재한다
    When  판매자가 만료된 요청에 제안 초안을 생성한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "REQUEST_NOT_OPEN" 이다

  Scenario: 필수 필드 없이 제출하면 400 VALIDATION_ERROR
    Given 판매자가 해당 요청에 제안 초안을 생성했다
    When  판매자가 필수_필드_없이_제안을_제출한다
    Then  응답 상태 코드는 400이다
    And   에러 코드는 "VALIDATION_ERROR" 이다
