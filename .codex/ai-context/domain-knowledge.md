# domain-knowledge.md — Florent 도메인 지식 누적

> AI가 세션마다 새로 파악한 도메인 지식을 여기에 누적한다.
> 세션 종료 시 반드시 업데이트한다.
> 형식: [날짜] 발견한 내용

---

## 핵심 도메인 개념

### 요청 (CurationRequest)
- OPEN → CONFIRMED: 구매자가 제안 선택 시 (단일 트랜잭션)
- OPEN → EXPIRED: 스케줄러가 48h 후 처리
- CONFIRMED 상태에서는 추가 제안 불가

### 제안 (Proposal)
- DRAFT → SUBMITTED: 판매자가 제출 버튼 클릭
- DRAFT/SUBMITTED → EXPIRED: 스케줄러가 24h 후 처리 (SELECTED 제외)
- SUBMITTED → SELECTED: 구매자가 선택 시
- SUBMITTED → NOT_SELECTED: 다른 제안이 선택될 때 일괄 처리
- 한 요청에 같은 가게가 중복 제안 불가

### 예약 확정 트랜잭션 순서 (반드시 준수)
1. request.confirm()
2. proposal.select()
3. 나머지 SUBMITTED proposal.notSelect()
4. Reservation 생성
5. PaymentPort.pay()
6. NOTIFICATION + OUTBOX_EVENT 저장

### 알림 발송 규칙
- REQUEST_ARRIVED: 요청 생성 시 → 반경 2km 내 판매자 전체
- PROPOSAL_ARRIVED: 제안 제출 시 → 해당 구매자만
- RESERVATION_CONFIRMED: 예약 확정 시 → 선택된 판매자만
- 미선택 판매자에게는 알림 없음 (앱 내 배지만)

### 반경 계산
- 기준: 2km 고정
- 방식: Bounding Box SQL 1차 + Haversine 서버 2차
- PostGIS 없음 (MVP)

---

## 세션별 누적 지식

> 아래에 날짜와 함께 추가한다.
> 예: [2025-06-01] JpaEntity toDomain() 변환 시 TimeSlot JSON 파싱 이슈 발견
