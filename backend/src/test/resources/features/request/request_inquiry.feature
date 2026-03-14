# src/test/resources/features/request/request_inquiry.feature

Feature: 구매자 요청 조회
  구매자는 자신이 생성한 요청 목록과 상세 내용을 조회할 수 있다.
  요청 목록에는 "제안 작성 중인 판매자 수"와 "도착한 제안 수"가 표시된다.

  Background:
    Given 구매자 "buyer01" 이 로그인되어 있다
    And   구매자의 OPEN 요청 1건이 존재한다
    And   해당 요청에 DRAFT 제안 2건, SUBMITTED 제안 1건이 존재한다

  # ─────────────────────────────────────────────
  # Happy Path
  # ─────────────────────────────────────────────

  Scenario: 요청 목록에서 제안 카운트를 확인한다
    When  구매자가 요청 목록을 조회한다
    Then  응답 상태 코드는 200이다
    And   첫 번째 요청의 draftProposalCount 는 2다
    And   첫 번째 요청의 submittedProposalCount 는 1이다

  Scenario: 요청 상세를 조회한다
    When  구매자가 요청 상세를 조회한다
    Then  응답 상태 코드는 200이다
    And   요청 상태는 "OPEN" 이다
    And   요청의 만료 시각이 포함된다
    And   타임슬롯 정보가 포함된다

  # ─────────────────────────────────────────────
  # Unhappy Path
  # ─────────────────────────────────────────────

  Scenario: 다른 구매자의 요청 상세를 조회하면 403이 반환된다
    Given 구매자 "buyer02" 의 요청이 존재한다
    When  구매자 "buyer01" 이 "buyer02" 의 요청 상세를 조회한다
    Then  응답 상태 코드는 403이다
    And   에러 코드는 "FORBIDDEN" 이다

  Scenario: 존재하지 않는 요청 상세를 조회하면 404가 반환된다
    When  구매자가 존재하지 않는 requestId 99999 로 조회한다
    Then  응답 상태 코드는 404이다
    And   에러 코드는 "REQUEST_NOT_FOUND" 이다
