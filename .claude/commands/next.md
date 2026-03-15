# /next — 다음 구현 도메인 결정 및 준비

> 현재 PR 머지 완료 후 다음 기능을 시작할 때 실행한다.
> @Ignore feature 목록을 보고 다음 구현 순서를 결정하고 준비한다.

---

## 실행 순서

### Step 1. 현재 상태 파악

```bash
# develop 브랜치로 돌아왔는지 확인
git branch --show-current
git log --oneline -5

# 아직 @Ignore 상태인 feature 확인
grep -r "@Ignore" backend/src/test/resources/features/ --include="*.feature" -l
```

### Step 2. 다음 구현 순서 결정

@Ignore feature와 api-spec.md를 읽고 아래 순서를 기준으로 다음 도메인을 결정해라:

```
권장 구현 순서 (의존성 기준):
1. auth          — 카카오 로그인, JWT 발급 (모든 API의 전제)
2. request       — 구매자 요청 생성 ✅ 완료
3. request-query — 구매자/판매자 요청 조회
4. proposal      — 판매자 제안 작성·제출
5. reservation   — 예약 확정 (결제 포함)
6. notification  — 알림 조회
7. e2e           — 전체 Happy Path
```

### Step 3. 다음 도메인 준비 보고

```
=== 다음 구현 준비 ===

다음 도메인: {도메인명}
해제할 @Ignore feature:
  - {feature 파일명}

참조할 문서:
  - backend/docs/biz-rules.md — {관련 섹션}
  - backend/docs/api-spec.md — {관련 엔드포인트}
  - backend/docs/erd.md — {관련 테이블}

구현 시작 준비:
  [ ] develop 브랜치에 있음
  [ ] 이전 PR 머지 완료
  [ ] known-issues.md OPEN 이슈 확인
======================
```

### Step 4. 구현 시작 프롬프트 출력

다음 도메인의 Layer 1 시작 프롬프트를 출력해줘.
AGENT-PROMPTS.md의 Backend Dev 에이전트 형식을 따른다.

### Step 5. Layer 완료 시 자동 진행 제안

각 Layer 구현이 완료되면, 다음 Layer로의 진행을 자동으로 제안한다:

```
=== Layer 완료 ===
✅ {현재 Layer} 구현 완료
   생성 파일: {파일 목록}

다음 Layer: {다음 Layer 이름}
   예상 파일: {생성할 파일 목록}

다음 Layer로 진행할까요? (Y/N)
================
```

Layer 순서: Domain → Application → Adapter/in → Adapter/out → Test(단위) → Test(Cucumber)
→ 사용자 승인 후 다음 Layer 진행. 거부 시 현재 Layer에서 추가 작업 또는 /ship 실행.