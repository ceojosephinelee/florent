# .claude/ai-context/api-decisions.md

> API 설계 시 고민했던 결정 이력.
> "왜 이렇게 만들었는가"를 기록한다.
> 구현 코드 설명이 아닌 설계 이유와 트레이드오프를 남긴다.

---

## [AD-001] 제안 목록에서 가격 비노출

- **결정일**: 초기 설계
- **결정 내용**: `GET /api/v1/buyer/requests/{id}/proposals` 응답에 `price` 필드 제외
- **이유**: biz-rules.md §9 — 구매자가 가격이 아닌 큐레이션 품질(concept_title, 꽃 구성)로 먼저 판단하도록 유도. 가격은 상세 진입 후 맨 마지막에만 노출.
- **영향 파일**: `ProposalSummaryResponse.java`

---

## [AD-002] 예약 확정 API 멱등성 키 — 클라이언트 생성

- **결정일**: 초기 설계
- **결정 내용**: `idempotency_key`를 클라이언트가 UUID로 생성하여 `POST /api/v1/buyer/proposals/{id}/select` 바디에 포함
- **이유**: 네트워크 재시도 시 중복 결제 방지. 서버는 PAYMENT.idempotency_key UNIQUE 제약으로 중복 차단. 클라이언트가 생성하면 재시도 시 동일 키 재사용 가능.
- **트레이드오프**: 클라이언트가 키를 생성하므로 악의적으로 다른 키를 매번 생성 가능. MVP에서는 허용.

---

## [AD-003] CONFIRMED 요청에서 제안 목록 조회 허용

- **결정일**: 초기 설계
- **결정 내용**: `request.status = CONFIRMED` 이후에도 `GET /api/v1/buyer/requests/{id}/proposals` 호출 허용 (히스토리 조회)
- **이유**: 구매자가 어떤 제안들이 있었는지 히스토리 확인 필요. EXPIRED 제안도 만료 배지와 함께 노출.

---

> 새 결정이 발생하면 [AD-{N}] 형식으로 추가한다.
