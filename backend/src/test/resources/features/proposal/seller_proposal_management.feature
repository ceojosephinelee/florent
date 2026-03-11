# src/test/resources/features/proposal/seller_proposal_management.feature

Feature: 판매자 요청함 및 제안 관리
  판매자는 반경 2km 이내 구매자의 요청을 확인하고, 제안서를 관리할 수 있다.
  EXPIRED 요청에는 "만료" 배지가, CONFIRMED 요청에는 "마감" 배지가 표시된다.

  Background:
    Given 판매자 "seller01" (꽃집 좌표 37.498095, 127.027610) 이 로그인되어 있다
    And   반경 2km 이내에 OPEN 요청 2건, EXPIRED 요청 1건, CONFIRMED 요청 1건이 존재한다

  # ─────────────────────────────────────────────
  # Happy Path
  # ─────────────────────────────────────────────

  Scenario: 판매자가 요청함 목록을 조회하면 모든 상태의 요청이 노출된다
    When  판매자가 요청함 목록을 조회한다
    Then  응답 상태 코드는 200이다
    And   OPEN 요청 2건이 포함된다
    And   EXPIRED 요청은 "EXPIRED" 상태로 포함된다
    And   CONFIRMED 요청은 "CONFIRMED" 상태로 포함된다

  Scenario: 판매자가 자신의 제안 목록을 조회한다
    Given 판매자가 OPEN 요청에 SUBMITTED 제안 1건을 제출했다
    When  판매자가 자신의 제안 목록을 조회한다
    Then  응답 상태 코드는 200이다
    And   SUBMITTED 제안 1건이 포함된다

  Scenario: 예약 확정 후 판매자가 자신의 예약을 조회한다
    Given 구매자가 판매자 "seller01" 의 제안을 선택하여 예약을 확정했다
    When  판매자가 예약 목록을 조회한다
    Then  응답 상태 코드는 200이다
    And   예약 1건이 포함된다
    And   예약 상태는 "CONFIRMED" 이다

  # ─────────────────────────────────────────────
  # Unhappy Path
  # ─────────────────────────────────────────────

  Scenario: EXPIRED 요청에는 제안 작성을 시작할 수 없다
    Given EXPIRED 요청이 존재한다
    When  판매자가 해당 요청에 제안서 작성을 시작한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "BUSINESS_ERROR" 이다

  Scenario: CONFIRMED 요청에는 제안 작성을 시작할 수 없다
    Given CONFIRMED 요청이 존재한다
    When  판매자가 해당 요청에 제안서 작성을 시작한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "BUSINESS_ERROR" 이다
