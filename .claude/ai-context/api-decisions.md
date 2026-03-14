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

## [AD-004] FlowerShop.phone 도메인 필드 추가

- **결정일**: 2026-03-15
- **결정 내용**: `FlowerShop` 도메인 모델에 `shopPhone` 필드 추가. `ProposalDetailResponse.ShopInfo`에 phone 포함.
- **이유**: api-spec.md §3-2 제안 상세 응답의 `shop.phone` 필드 지원. `FlowerShopJpaEntity`에는 이미 `phone` 컬럼이 존재했으나 `toDomain()`에서 누락되어 있었음.
- **영향 파일**: `FlowerShop.java`, `FlowerShopJpaEntity.java`, `ProposalDetail.java`, `ProposalDetailResponse.java`

---

## [AD-005] select/markNotSelected ErrorCode 분리 — PROPOSAL_NOT_SELECTABLE

- **결정일**: 2026-03-15
- **결정 내용**: `Proposal.select()`, `markNotSelected()`에서 `PROPOSAL_NOT_SUBMITTABLE` 대신 `PROPOSAL_NOT_SELECTABLE`(422) 사용.
- **이유**: submit과 select은 의미적으로 다른 동작. 에러 코드를 분리하면 클라이언트가 제출 불가/선택 불가를 구분 가능.
- **영향 파일**: `Proposal.java`, `ErrorCode.java`

---

## [AD-006] JwtAuthenticationFilter @Profile("!local & !test")

- **결정일**: 2026-03-15
- **결정 내용**: JWT 필터를 `@Profile("prod")`에서 `@Profile("!local & !test")`로 변경.
- **이유**: prod, staging 등 local 이외 모든 환경에서 JWT 검증 활성화. test 프로파일에서는 `TestSecurityConfig` + `TestAuthFilter`가 대체하므로 제외. `@WebMvcTest` 컨텍스트에서는 `@MockBean JwtProvider`로 의존성 해결.
- **영향 파일**: `JwtAuthenticationFilter.java`, `BuyerRequestControllerTest.java`, `BuyerProposalControllerTest.java`

---

## [AD-007] JwtAuthenticationFilter shouldNotFilter 화이트리스트 방식

- **결정일**: 2026-03-15
- **결정 내용**: `Set.of` prefix match 방식에서 exact match + prefix match 혼합 방식으로 변경.
- **이유**: `/api/v1/auth/` prefix로 매칭하면 향후 추가되는 인증 필요 API(`/api/v1/auth/me` 등)가 의도치 않게 제외될 수 있음. `/api/v1/auth/kakao`, `/api/v1/auth/reissue`만 명시적으로 제외.
- **영향 파일**: `JwtAuthenticationFilter.java`

---

> 새 결정이 발생하면 [AD-{N}] 형식으로 추가한다.
