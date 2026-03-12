# src/test/resources/features/health/health_check.feature

Feature: 서버 헬스 체크
  서버가 정상적으로 실행 중인지 확인한다.
  이 시나리오는 Walking Skeleton의 첫 번째 검증 지점이다.

  Scenario: 서버가 정상 실행 중이면 200 응답을 반환한다
    When 클라이언트가 헬스 체크 엔드포인트를 호출한다
    Then 헬스 체크 응답 상태 코드는 200이다
    And  응답 바디에 "UP" 이 포함된다
