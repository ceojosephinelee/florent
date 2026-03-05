# known-issues.md — 알려진 문제 및 해결책

> 개발 중 발견된 문제, 임시 해결책, 추후 개선 필요 사항을 기록한다.
> 세션 시작 전 반드시 읽어라. 이미 해결된 문제를 다시 만들지 않도록.

---

## 현재 알려진 제약사항 (설계 결정)

### Mock PG
- **현황**: PaymentPort → MockPaymentAdapter 사용. 실 PG 없음.
- **동작**: "결제하기" 버튼 클릭 → 즉시 SUCCEEDED
- **주의**: PaymentPort 인터페이스로만 참조. MockPaymentAdapter 직접 주입 금지.
- **전환 시**: TossPaymentAdapter 구현 후 Spring Bean 교체만 하면 됨

### Refresh Token 저장
- **현황**: Redis 없음. USER 테이블 컬럼(refresh_token, refresh_token_expires_at)으로 관리
- **주의**: 로그아웃 시 refresh_token = null 처리 필수

### 거리 계산
- **현황**: PostGIS 없음. 전체 shop 조회 후 HaversineUtil 서버 계산
- **주의**: shop 수가 많아지면 성능 이슈 가능. MVP에서는 허용.
- **개선 시**: PostGIS 도입 또는 Bounding Box SQL 필터 강화

### 이미지
- **현황**: 제안서 이미지만 S3. 꽃집 프로필 이미지 MVP 제외.
- **플로우**: Presigned URL → S3 직접 PUT → URL을 제안 임시저장 API에 포함

### started_at 부재
- **현황**: PROPOSAL에 started_at 없음. created_at으로 대체.
- **패널티 기능**: UI 경고 문구만. 실제 패널티 로직 MVP 제외.

---

## 세션별 발견 문제 누적

> 아래에 날짜와 함께 추가한다.
> 예: [2025-06-01] N+1 이슈 — ProposalRepositoryImpl.findSubmittedByRequestId()
>     → @EntityGraph로 해결. JpaRepository 커스텀 쿼리 참고.
