# .claude/ai-context/domain-knowledge.md

> 세션 간 누적되는 도메인 지식.
> 신규 AI 에이전트가 이 파일을 읽으면 프로젝트 컨텍스트를 빠르게 파악할 수 있어야 한다.

---

## 핵심 용어

| 용어 | 정의 | 상태 전이 |
|---|---|---|
| 요청 (CurationRequest) | 구매자가 작성하는 큐레이션 요청서. 48시간 유효. | OPEN → EXPIRED (스케줄러) / OPEN → CONFIRMED (제안 선택) |
| 제안 (Proposal) | 판매자가 요청에 응답하는 제안서. 24시간 유효. | DRAFT → SUBMITTED → SELECTED / NOT_SELECTED / EXPIRED |
| 예약 (Reservation) | 제안 선택 + Mock 결제 완료 시 생성. | CONFIRMED (고정, 취소/변경 없음 — MVP) |
| 결제 (Payment) | Mock 결제. 버튼 클릭 시 즉시 성공. | SUCCEEDED / FAILED |
| 알림 (Notification) | DB에 저장되는 인앱 알림. FCM 발송은 Outbox Worker가 담당. | — |
| Outbox Event | FCM 전송 작업 큐. Notification과 1:N. | PENDING → SENT / FAILED |

---

## 슬롯 규칙 (자주 혼동되는 포인트)

| 역할 | 선택 수 | 선택 범위 |
|---|---|---|
| 구매자 | **복수 선택 가능** | 가능한 시간대를 모두 선택 |
| 판매자 | **단 1개** | 구매자 슬롯 범위와 **무관하게** 자유 선택 |

- 슬롯 종류: `PICKUP_30M` (픽업, 30분 단위), `DELIVERY_WINDOW` (배송, 오전/오후/저녁)
- 슬롯 값 예시: `14:00`, `MORNING`, `AFTERNOON`, `EVENING`

---

## 반경 규칙 (구현 시 주의)

- 기준 좌표: 픽업 장소 또는 배송 주소 좌표 (`place_lat`, `place_lng`)
- 반경: **2km 고정**
- 계산 방식: Bounding Box 1차 SQL 필터 + Haversine 2차 Java 서버 필터
- MVP: 전체 FLOWER_SHOP 메모리 로드 후 Haversine 필터도 허용 (shop 수 소수 가정)

---

## 예산 TIER (UI 참고용, 서버 검증 없음)

| TIER | 레이블 |
|---|---|
| TIER1 | 작게 |
| TIER2 | 보통 |
| TIER3 | 크게 |
| TIER4 | 프리미엄 |

> 판매자 제안 가격이 TIER 범위를 벗어나도 서버에서 에러 없음. 의도된 설계.

---

## 예약 확정 트랜잭션 순서 (단일 트랜잭션 내)

```
1. request.status = CONFIRMED
2. 선택된 proposal.status = SELECTED
3. 나머지 SUBMITTED proposal.status = NOT_SELECTED
4. RESERVATION 생성 (status = CONFIRMED)
5. PAYMENT 생성 (provider = MOCK, status = SUCCEEDED)
6. 선택된 판매자에게 NOTIFICATION(RESERVATION_CONFIRMED) + OUTBOX_EVENT 저장
```

---

## 알림 발송 구조 (Outbox Pattern)

```
Service.method()
  → Notification 생성 + save (DB 트랜잭션과 함께)
  → OutboxEvent 생성 + save (PENDING)
  ↑ 트랜잭션 커밋

OutboxWorker (@Scheduled, 10초 간격)
  → PENDING OutboxEvent 조회
  → UserDevice.fcm_token 조회
  → FCM 발송
  → OutboxEvent.status = SENT / 실패 시 attempt_count++
  → attempt_count > 3 → FAILED
```

---

## MVP 제외 사항 (구현하지 않는다)

- 실 PG 연동 (MockPaymentAdapter 사용)
- Redis (DB 컬럼으로 대체)
- PostGIS (서버 Haversine으로 대체)
- 채팅, 물류 연동
- 판매자 패널티 기능 (문구만 표시)
- 꽃집 프로필 이미지

---

## 구현 중 발견된 엣지케이스 결정 이력

| 날짜 | 모호한 부분 | 결정 | 근거 |
|---|---|---|---|
| — | — | — | — |

> 구현 중 새 결정이 발생하면 이 표에 추가한다.
