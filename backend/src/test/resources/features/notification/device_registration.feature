# language: ko
기능: FCM 디바이스 토큰 등록

  배경:
    Given 구매자 "디바이스테스터"가 가입되어 있다

  시나리오: 새 FCM 토큰을 등록한다
    When 구매자가 FCM 토큰 "fcm-token-abc"을 "IOS"로 등록한다
    Then 응답 상태 코드는 200이다
    And 디바이스 ID가 반환된다

  시나리오: 기존 FCM 토큰이 있으면 업데이트한다
    Given 구매자가 FCM 토큰 "same-token"을 "IOS"로 등록했다
    When 구매자가 FCM 토큰 "same-token"을 "ANDROID"로 등록한다
    Then 응답 상태 코드는 200이다
    And 디바이스 ID가 반환된다