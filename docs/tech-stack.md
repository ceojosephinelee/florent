
# tech-stack.md — Florent 기술 스택 명세

> AI는 이 문서에 명시된 스택과 버전만 사용한다. 임의로 다른 라이브러리를 추가하지 않는다.
> 추가가 필요하다면 반드시 먼저 질문한다.

---

## 1. Language & Framework

| 항목 | 선택 | 비고 |
|---|---|---|
| Language | Java 17 | Record, Sealed Class 적극 활용 |
| Framework | Spring Boot 3.x | 최신 안정 버전 |
| Build Tool | Gradle Kotlin DSL | `build.gradle.kts` |

---

## 2. Frontend

| 항목 | 선택 | 비고 |
|---|---|---|
| Framework | Flutter | 구매자/판매자 앱 공용 코드베이스 |
| 지도 | 카카오맵 Flutter 플러그인 | 지도 렌더링 |
| 위치 | geolocator | 현재 위치 조회 |
| 주소/장소 검색 | 카카오 로컬 API | 주소 검색, 역지오코딩 |

---

## 3. Backend — Core

| 항목 | 선택 | 비고 |
|---|---|---|
| ORM | JPA / Hibernate | Spring Data JPA |
| DB 마이그레이션 | Flyway | `resources/db/migration/V{n}__{desc}.sql` |
| API 문서 | SpringDoc OpenAPI (Swagger UI) | `/swagger-ui.html` |
| HTTP Client | WebClient (Spring WebFlux) | 카카오 로컬 API 연동용 |
| 스케줄러 | Spring Scheduler (`@Scheduled`) | 만료 처리 (1~5분 간격) |
| 유효성 검증 | Spring Validation (`@Valid`) | DTO 검증 |

---

## 4. Auth

| 항목 | 선택 | 비고 |
|---|---|---|
| OAuth | 카카오 OAuth 2.0 | 카카오 Access Token → 자체 JWT 발급 |
| JWT | jjwt (io.jsonwebtoken) | Access Token + Refresh Token |
| Refresh Token 저장 | DB 컬럼 (`USER.refresh_token`) | Redis 없음 (MVP) |
| Security | Spring Security | JWT 필터 체인 |

> **Auth 플로우**: 카카오 OAuth 코드 → 서버에서 카카오 Access Token 교환 →
> 카카오 사용자 정보 조회 → 자체 JWT(Access + Refresh) 발급 → 클라이언트 전달.
> 카카오 토큰은 서버에서만 사용하고 클라이언트에 내려주지 않는다.

---

## 5. Database

| 항목 | 선택 | 비고 |
|---|---|---|
| DB | PostgreSQL | lat/lng: `DECIMAL(9,6)` 컬럼 |
| 로컬 환경 | Docker Compose | `docker-compose.yml` |
| 운영 환경 | AWS RDS (PostgreSQL) | EC2와 동일 VPC |
| 거리 계산 | Bounding Box 1차 필터 + Haversine 2차 필터 | 반경 2km, 서버 계산 |

> **거리 계산 전략**: 지오인덱스(PostGIS) 없이 서버에서 계산.
> MVP에서는 DB에 shop이 소수라고 가정하여 전체 조회 후 Haversine 필터링.
> 성능 이슈 발생 시 PostGIS 확장 또는 Bounding Box SQL 조건으로 개선.

---

## 6. Infra

| 항목 | 선택 | 비고 |
|---|---|---|
| 서버 | AWS EC2 단일 인스턴스 | |
| 컨테이너 | Docker Compose | 로컬/서버 동일 구성 |
| 이미지 스토리지 | AWS S3 + Presigned URL | 제안서 이미지 업로드 |
| 로그 | Logback (JSON 구조화 로그) + CloudWatch | |

---

## 7. Push 알림

| 항목 | 선택 | 비고 |
|---|---|---|
| Push 서비스 | FCM (Firebase Cloud Messaging) | iOS / Android 공용 |
| 전달 패턴 | Outbox Pattern | `OUTBOX_EVENT` 테이블, DB 트랜잭션과 함께 저장 |
| Worker | Spring Scheduler | `@Scheduled(fixedDelay = 10_000)` |
| 재시도 | 최대 3회, exponential backoff | 3회 초과 시 `status = FAILED` |
| 중복 방지 | `dedup_key` (UNIQUE) | 클라이언트도 dedup_key로 중복 푸시 무시 |

---

## 8. 결제

| 항목 | 선택 | 비고 |
|---|---|---|
| MVP | Mock 결제 | 결제 수단 입력 없음, 버튼 클릭 → 즉시 성공 |
| 확장 | 실 PG 연동 가능 구조 | `PaymentPort` 인터페이스로 추상화 |
| 멱등성 | `idempotency_key` (UNIQUE) | 중복 결제 방지 |

---

## 9. 외부 API

| 항목 | 선택 | 비고 |
|---|---|---|
| 카카오 로컬 API | 주소 검색, 장소 검색, 역지오코딩 | WebClient로 호출 |
| 카카오 OAuth API | 사용자 인증 | |

---

## 10. 테스트

| 항목 | 선택 | 비고 |
|---|---|---|
| 단위 테스트 | JUnit 5 | |
| 인수 테스트 | Cucumber + JUnit 5 | BDD 스타일, `.feature` 파일 작성 |
| DB 테스트 | Testcontainers (PostgreSQL) | 실제 DB로 Repository 테스트 |
| 테스트 스타일 | Given-When-Then 주석 필수 | |

---

## 11. 환경 프로파일

| 프로파일 | DB | FCM | PG |
|---|---|---|---|
| `local` | Docker Compose PostgreSQL | Mock (로그 출력) | Mock |
| `prod` | AWS RDS | 실 FCM | Mock → 실 PG 전환 예정 |

> `application-{profile}.yml` 로 분리. 시크릿은 환경 변수로 주입 (`${VAR_NAME}`).

---

## 12. API 버저닝

- 모든 엔드포인트는 `/api/v1/` 접두사를 사용한다.
- 예: `POST /api/v1/buyer/requests`
