# src/test/resources/features/proposal/proposal_inquiry.feature

Feature: 구매자 제안 목록 및 상세 조회
  구매자는 자신의 요청에 도착한 제안 목록을 조회하고, 상세 내용을 확인할 수 있다.
  목록에서는 가격이 노출되지 않으며, 상세에서만 가격을 확인할 수 있다.

  Background:
    Given 구매자 "buyer01" 이 로그인되어 있다
    And   구매자의 OPEN 요청이 존재한다
    And   해당 요청에 SUBMITTED 제안 1건과 EXPIRED 제안 1건이 존재한다

  # ─────────────────────────────────────────────
  # Happy Path
  # ─────────────────────────────────────────────

  Scenario: 제안 목록에서 SUBMITTED 제안이 노출된다
    When  구매자가 요청의 제안 목록을 조회한다
    Then  응답 상태 코드는 200이다
    And   SUBMITTED 제안이 목록에 포함된다
    And   제안 목록 응답에 price 필드가 없다

  Scenario: 제안 목록에서 EXPIRED 제안도 만료 배지와 함께 노출된다
    When  구매자가 요청의 제안 목록을 조회한다
    Then  EXPIRED 제안이 status "EXPIRED" 로 목록에 포함된다

  Scenario: 제안 상세에서 가격이 포함된 모든 정보를 확인한다
    When  구매자가 SUBMITTED 제안의 상세를 조회한다
    Then  응답 상태 코드는 200이다
    And   응답에 conceptTitle, description, availableSlot, price 필드가 모두 포함된다
    And   응답에 shop 정보 (name, phone, addressText) 가 포함된다

  Scenario: DRAFT 제안은 목록에 포함되지 않는다
    Given 해당 요청에 DRAFT 제안 1건이 추가로 존재한다
    When  구매자가 요청의 제안 목록을 조회한다
    Then  응답 상태 코드는 200이다
    And   목록에 DRAFT 상태 제안이 없다

  # ─────────────────────────────────────────────
  # Unhappy Path
  # ─────────────────────────────────────────────

  Scenario: 다른 구매자의 요청에 대한 제안 목록을 조회하면 403이 반환된다
    Given 구매자 "buyer02" 의 요청이 존재한다
    When  구매자 "buyer01" 이 "buyer02" 요청의 제안 목록을 조회한다
    Then  응답 상태 코드는 403이다
    And   에러 코드는 "FORBIDDEN" 이다

  Scenario: 존재하지 않는 제안 상세를 조회하면 404가 반환된다
    When  구매자가 존재하지 않는 proposalId 99999 로 상세를 조회한다
    Then  응답 상태 코드는 404이다
    And   에러 코드는 "PROPOSAL_NOT_FOUND" 이다
