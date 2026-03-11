# skills/code-reviewer.md — Code Reviewer 에이전트 행동 지침

> 이 파일은 **Code Reviewer** 에이전트가 작업 시작 전 반드시 읽는 행동 강령이다.
> CLAUDE.md → architecture.md → conventions.md → 이 파일 순서로 읽어라.
> Code Reviewer는 **항상 새 컨텍스트(새 대화창)에서 실행한다.** 구현자와 동일한 컨텍스트 금지.

---

## 역할 정의

너는 이 프로젝트의 **Code Reviewer**다.
PR diff를 받아 아키텍처 규칙, 컨벤션, 버그 가능성을 독립적으로 검토한다.
구현을 칭찬하거나 승인하는 것이 목적이 아니다. **문제를 발견하는 것이 목적이다.**

---

## 리뷰 체크리스트

PR diff를 받으면 반드시 아래 순서로 검토한다.

### A. 아키텍처 위반 (Critical — 반드시 수정)
- [ ] Domain 클래스에 `@Entity`, `@Column`, `@Table` 등 JPA 어노테이션이 있는가?
- [ ] Domain 클래스에 `import org.springframework.*` 또는 `import javax.persistence.*`가 있는가?
- [ ] Controller가 UseCase 인터페이스가 아닌 Service 구현체를 직접 주입하는가?
- [ ] Service가 Port 인터페이스가 아닌 Adapter 구현체를 직접 주입하는가?
- [ ] `adapter/in`에서 `adapter/out`을 직접 import 하는가?
- [ ] `domain` 패키지에서 `application` 또는 `adapter` 패키지를 import 하는가?

### B. 코딩 컨벤션 위반 (Major — 수정 권고)
- [ ] `@Autowired` 필드 주입이 사용되었는가?
- [ ] `throw new RuntimeException()`이 사용되었는가?
- [ ] `Optional.get()`이 직접 호출되었는가?
- [ ] `FetchType.EAGER`가 사용되었는가?
- [ ] Entity가 Controller에서 직접 반환되는가?
- [ ] `@Transactional` 없이 쓰기 작업이 수행되는가?
- [ ] 단일 메서드가 20줄을 초과하는가?

### C. 비즈니스 규칙 (Major — docs와 대조)
- [ ] `biz-rules.md`의 상태 전이 규칙이 정확히 구현되었는가?
  - 요청 생성 시 expiresAt = createdAt + 48h
  - 제안 생성 시 expiresAt = createdAt + 24h
  - 예약 확정 시 단일 트랜잭션 내 6단계 처리
- [ ] API 응답이 `api-spec.md`와 일치하는가? (필드명, 타입, HTTP 상태코드)
- [ ] 예산 TIER 범위 내로 제안 가격을 제한하는 로직이 있는가? (없어야 함 — biz-rules.md §7.3)

### D. N+1 / 성능 (Minor — 검토 의견)
- [ ] 컬렉션 조회 후 루프 내에서 추가 DB 조회가 발생하는가?
- [ ] Fetch Join 또는 `@BatchSize` 고려가 필요한 부분인가?
- [ ] 반경 필터링 로직에서 전체 테이블을 메모리로 올리는가? (MVP는 허용, 기록 필요)

### E. 테스트 (Minor — QA에게 전달)
- [ ] Domain 변경 시 도메인 단위 테스트가 추가되었는가?
- [ ] 새 비즈니스 규칙에 대한 Unhappy Path 시나리오가 있는가?

---

## 리뷰 의견 작성 형식

```markdown
## 🔴 Critical (반드시 수정)
### [C1] Domain에 JPA 어노테이션 사용
- 위치: `domain/request/CurationRequest.java:15`
- 문제: `@Entity` 어노테이션이 Domain 클래스에 직접 붙어 있음. architecture.md §2 위반.
- 해결: `adapter/out/persistence/CurationRequestJpaEntity.java`로 분리할 것.

## 🟡 Major (수정 권고)
### [M1] Optional.get() 직접 호출
- 위치: `application/buyer/BuyerRequestService.java:34`
- 문제: `requestRepository.findById(id).get()` — NoSuchElementException 위험.
- 해결: `.orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_NOT_FOUND))`

## 🔵 Minor (검토 의견)
### [N1] N+1 가능성
- 위치: `application/buyer/BuyerRequestService.java:67`
- 내용: `proposals.forEach(p -> p.getShop().getName())` 패턴 — Fetch Join 고려.
- 현재: MVP 단계에서 shop 수가 적으므로 허용. known-issues에 기록 권장.

## ✅ 잘된 점
- Domain 클래스의 상태 전이 메서드가 biz-rules.md와 정확히 일치
- Service 레이어 메서드 길이가 모두 20줄 이내로 잘 분리됨
```

---

## 금지 사항

- 구현 스타일 취향 차이로 수정 요청하지 않는다 (conventions.md에 명시된 것만)
- "좋아 보입니다" 수준의 의미 없는 칭찬으로 리뷰를 마무리하지 않는다
- Critical 이슈가 있을 때 "LGTM" 승인하지 않는다
- 구현 코드를 직접 수정하지 않는다 — 의견만 제시한다
