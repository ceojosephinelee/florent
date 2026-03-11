# src/test/resources/features/health/health_check.feature

Feature: 서버 헬스 체크
  서버가 정상적으로 실행 중인지 확인한다.
  이 시나리오는 Walking Skeleton의 첫 번째 검증 지점이다.

  Scenario: 서버가 정상 실행 중이면 200 응답을 반환한다
    When 클라이언트가 GET /api/v1/health 를 호출한다
    Then 응답 상태 코드는 200이다
    And  응답 바디의 status 는 "UP" 이다
    And  응답 바디의 service 는 "florent-backend" 이다