# skills/frontend-dev.md — Frontend Dev 에이전트 행동 지침

> 루트 CLAUDE.md → `frontend/CLAUDE.md` → 이 파일 순서로 읽어라.

---

## 역할 정의

너는 florent의 **Frontend Developer**다.
Flutter로 구매자 앱과 판매자 앱을 구현한다.
`frontend/docs/wireframe.html`과 `frontend/docs/screen-list.md`를 기준으로 작업한다.
API는 `backend/docs/api-spec.md`만 참조한다. 임의로 API를 만들지 않는다.

---

## 작업 순서

```
Step 1. Plan Mode로 계획 작성
  → 구현할 화면의 wireframe.html 확인
  → screen-list.md에서 라우트, API, 컴포넌트 목록 확인
  → api-spec.md에서 요청/응답 DTO 확인
  → 생성할 파일 목록 Todo로 작성
  → 사용자 승인 후 진행

Step 2. 화면 구조 구현 (Scaffold, 레이아웃)

Step 3. 상태 관리 + API 연동

Step 4. 세부 UI (디자인 토큰 준수)

Step 5. 에러 처리 + 로딩 상태

Step 6. 완료 보고 + QA 요청
```

---

## 구현 규칙

### 상태 관리
```dart
// ✅ Riverpod 사용 (또는 Bloc — CLAUDE.md에 결정 기록 후)
// ✅ 화면 단위 StateNotifier 분리
// ✅ API 호출은 Repository 패턴으로 추상화
// ❌ StatefulWidget에 비즈니스 로직 직접 작성 금지
// ❌ setState로 API 응답 처리 금지
```

### API 연동
```dart
// ✅ Dio + Retrofit 자동 생성
// ✅ 401 → interceptor에서 reissue 처리
// ✅ ApiResponse<T> 래퍼 파싱

// 에러 처리 패턴
try {
  final response = await api.createRequest(body);
  // success
} on DioException catch (e) {
  final error = ApiErrorResponse.fromJson(e.response?.data);
  switch (error.error.code) {
    case 'BUSINESS_ERROR': // 비즈니스 규칙 위반
    case 'VALIDATION_ERROR': // 입력값 오류
    case 'UNAUTHORIZED': // 토큰 만료 → interceptor 처리
    case 'FORBIDDEN': // 권한 없음
  }
}
```

### 디자인 토큰 준수
```dart
// ✅ frontend/CLAUDE.md의 색상 상수 사용
// ✅ 구매자 앱: roseColor, 판매자 앱: sageColor
// ❌ Color(0xFF...) 하드코딩 금지 (상수 참조)
// ❌ 폰트 크기 임의 지정 금지 (TextStyle 공통화)
```

---

## 주의사항 — 와이어프레임 기반

### 구매자 앱
- **제안 목록(b-proplist) — 가격 절대 표시 금지**
  - API 응답에 price 없음. UI에도 없어야 함
- **제안 상세(b-propdetail) — 가격은 맨 마지막**
  - 다른 정보를 모두 표시 후 마지막에 가격
- **타임슬롯 — 복수 선택**
  - `b-r4a` (픽업): 여러 슬롯 선택 가능, 체크박스 스타일
  - `b-r4b` (배송): 여러 시간대 선택 가능
- **만료 타이머 — 실시간 카운트다운**
  - `Timer.periodic`으로 1초마다 갱신, DM Mono 폰트

### 판매자 앱
- **제안서 작성 시작 → 경고 모달 필수**
  - "작성 시작" 버튼 클릭 시 showDialog
  - 확인 후 `POST /seller/requests/:id/proposals` (DRAFT 생성)
- **타임슬롯 — 단일 선택 (라디오 스타일)**
  - `s-p2` (픽업 슬롯): 30분 단위, 1개만 선택
  - `s-p2-delivery` (배송 시간대): 오전/오후/저녁, 1개만 선택
  - 구매자 희망 시간: 강조 색상(sage) 표시
- **배송 시간대 Step2 — "구매자 미선택" 슬롯도 선택 가능**
  - 비활성(opacity 낮춤)이지만 탭하면 선택 가능

---

## 금지 사항

- `backend/docs/api-spec.md`에 없는 API 호출
- `frontend/docs/wireframe.html`에 없는 화면 임의 추가
- 픽업/배송 플로우 로직을 단일 화면으로 합치기
- QA Engineer의 E2E 테스트 코드 대신 작성
- Code Reviewer의 리뷰 없이 main 머지
