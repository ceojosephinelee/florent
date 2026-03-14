# src/test/resources/features/proposal/seller_proposal_management.feature

Feature: 판매자 제안 관리
  판매자는 본인이 작성한 제안 목록을 조회할 수 있고, 타인의 제안에는 접근할 수 없다.

  Background:
    Given 구매자 "buyer_mgmt01" 이 로그인되어 있다
    And   구매자의 OPEN 요청이 존재한다

  # ─────────────────────────────────────────────
  # Happy Path
  # ─────────────────────────────────────────────

  Scenario: 판매자가 본인 제안 목록을 페이지네이션으로 조회한다
    Given 판매자 "꽃집B" 가 꽃집과 함께 등록되어 있다
    And   판매자가 해당 요청에 제안 초안을 생성했다
    When  판매자가 본인 제안 목록을 조회한다
    Then  응답 상태 코드는 200이다
    And   제안 목록에 1건이 포함된다
    And   목록 응답에 페이지네이션 필드가 포함된다

  # ─────────────────────────────────────────────
  # Unhappy Path
  # ─────────────────────────────────────────────

  Scenario: 다른 판매자의 제안을 임시저장하면 403이 반환된다
    Given 판매자 "꽃집C" 가 꽃집과 함께 등록되어 있다
    And   판매자가 해당 요청에 제안 초안을 생성했다
    And   다른 판매자 "꽃집D" 가 꽃집과 함께 등록되어 있다
    When  다른 판매자가 해당 제안을 임시저장한다
    Then  응답 상태 코드는 403이다
    And   에러 코드는 "FORBIDDEN" 이다

  Scenario: SUBMITTED 제안 임시저장 시 422 PROPOSAL_NOT_EDITABLE
    Given 판매자 "꽃집E" 가 꽃집과 함께 등록되어 있다
    And   판매자가 해당 요청에 제안 초안을 생성했다
    And   판매자가 제안을 임시저장했다
    And   판매자가 제안을 제출했다
    When  판매자가_SUBMITTED_제안을_임시저장한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "PROPOSAL_NOT_EDITABLE" 이다
