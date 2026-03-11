# src/test/resources/features/proposal/proposal_submission.feature

Feature: 판매자 제안서 작성 및 제출
  판매자는 구매자의 요청에 대해 제안서를 작성하고 제출할 수 있다.
  제안서 작성 시작 즉시 DRAFT 상태로 생성되며, 24시간 이내 제출하지 않으면 만료된다.

  Background:
    Given 구매자 "buyer01" 의 OPEN 요청이 존재한다 (기준 좌표 37.498095, 127.027610)
    And   반경 2km 이내에 "장미꽃집" 판매자 "seller01" 이 존재한다
    And   판매자 "seller01" 이 로그인되어 있다

  # ─────────────────────────────────────────────
  # Happy Path — 제안 작성 시작 (DRAFT)
  # ─────────────────────────────────────────────

  Scenario: 판매자가 제안서 작성을 시작하면 DRAFT 상태로 생성된다
    When  판매자가 해당 요청에 대해 제안서 작성을 시작한다
    Then  응답 상태 코드는 201이다
    And   제안 상태는 "DRAFT" 이다
    And   만료 시각은 생성 시각으로부터 24시간 후다

  Scenario: 제안 작성 시작 후 구매자 요청 상세의 draftProposalCount 가 증가한다
    Given 해당 요청의 draftProposalCount 는 0이었다
    When  판매자가 제안서 작성을 시작한다
    Then  구매자가 요청 상세를 조회하면 draftProposalCount 는 1이다

  # ─────────────────────────────────────────────
  # Happy Path — 제안 임시저장 및 제출
  # ─────────────────────────────────────────────

  Scenario: 판매자가 DRAFT 제안을 임시저장한다
    Given 판매자의 DRAFT 제안이 존재한다
    When  판매자가 아래 내용으로 제안을 임시저장한다
      | conceptTitle      | 봄 햇살 같은 화사함                     |
      | description       | 노란 프리지아와 핑크 장미로 봄을 담았어요 |
      | availableSlotKind | PICKUP_30M                             |
      | availableSlotValue| 14:00                                  |
      | price             | 35000                                  |
    Then  응답 상태 코드는 200이다
    And   제안 상태는 여전히 "DRAFT" 이다

  Scenario: 판매자가 DRAFT 제안을 제출하면 SUBMITTED 상태가 된다
    Given 판매자의 DRAFT 제안에 description, availableSlot, price 가 입력되어 있다
    When  판매자가 제안을 제출한다
    Then  응답 상태 코드는 200이다
    And   제안 상태는 "SUBMITTED" 이다
    And   제출 시각(submittedAt) 이 기록된다

  Scenario: 제안 제출 시 구매자에게 PROPOSAL_ARRIVED 알림이 생성된다
    Given 판매자의 DRAFT 제안에 필수 정보가 모두 입력되어 있다
    When  판매자가 제안을 제출한다
    Then  구매자 "buyer01" 에게 PROPOSAL_ARRIVED 알림이 1건 생성된다

  # ─────────────────────────────────────────────
  # Unhappy Path
  # ─────────────────────────────────────────────

  Scenario: 만료된 요청에는 제안 작성을 시작할 수 없다
    Given 구매자의 요청이 EXPIRED 상태다
    When  판매자가 해당 요청에 제안서 작성을 시작한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "BUSINESS_ERROR" 이다

  Scenario: CONFIRMED 된 요청에는 제안 작성을 시작할 수 없다
    Given 구매자의 요청이 CONFIRMED 상태다
    When  판매자가 해당 요청에 제안서 작성을 시작한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "BUSINESS_ERROR" 이다

  Scenario: 같은 요청에 같은 가게가 중복 제안하면 에러가 발생한다
    Given 판매자 "seller01" 이 이미 해당 요청에 제안을 제출했다
    When  판매자 "seller01" 이 같은 요청에 다시 제안 작성을 시작한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "BUSINESS_ERROR" 이다

  Scenario: description 이 없는 DRAFT 제안은 제출할 수 없다
    Given 판매자의 DRAFT 제안에 description 이 없다
    When  판매자가 제안 제출을 시도한다
    Then  응답 상태 코드는 400이다
    And   에러 코드는 "VALIDATION_ERROR" 이다

  Scenario: price 가 0 이하인 제안은 저장/제출할 수 없다
    When  판매자가 price = 0 으로 제안 저장을 시도한다
    Then  응답 상태 코드는 400이다
    And   에러 코드는 "VALIDATION_ERROR" 이다

  Scenario: SUBMITTED 상태 제안은 다시 임시저장할 수 없다
    Given 판매자의 제안이 이미 SUBMITTED 상태다
    When  판매자가 해당 제안을 임시저장하려 한다
    Then  응답 상태 코드는 422이다
    And   에러 코드는 "BUSINESS_ERROR" 이다

  Scenario: 구매자가 판매자 제안 제출 API를 호출하면 403이 반환된다
    Given 구매자 "buyer01" 이 로그인되어 있다
    When  구매자가 제안 제출 API를 호출한다
    Then  응답 상태 코드는 403이다
    And   에러 코드는 "FORBIDDEN" 이다
