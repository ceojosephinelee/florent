# Florent Backend Bootstrap

Spring Boot 3 + Java 17 기반의 최소 백엔드 골격입니다.

## 1. 사전 요구사항

- Java 17
- Docker / Docker Compose

## 2. 로컬 실행

1. PostgreSQL 실행

```bash
docker compose up -d
```

2. 애플리케이션 실행

```bash
./gradlew bootRun
```

3. HealthCheck 확인

```bash
curl http://localhost:8080/api/v1/health
```

예상 응답

```json
{"status":"UP","service":"florent-backend"}
```

## 3. 테스트 실행

```bash
./gradlew test
```

최초 실행 시 Gradle Wrapper가 Gradle 배포본을 자동 다운로드한다.

- Cucumber feature: `src/test/resources/features/health_check.feature`
- TestAdapter 구조를 통해 Step Definition이 HTTP 세부 구현에 직접 결합되지 않도록 구성

## 4. 주요 구조

- Health API: `GET /api/v1/health`
- DB 연결: PostgreSQL + Flyway (`V1__baseline.sql`)
- 인수 테스트: Cucumber + JUnit 5 + Testcontainers(PostgreSQL)
- 테스트 어댑터: `HealthCheckTestAdapter` + `HttpHealthCheckTestAdapter`
