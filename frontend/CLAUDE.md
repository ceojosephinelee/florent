# frontend/CLAUDE.md — florent 프론트엔드 (Flutter)

> 루트 CLAUDE.md를 먼저 읽고 이 파일을 읽어라.
> API 명세 원본은 `backend/docs/api-spec.md`다. 이 파일을 직접 수정하지 않는다.

---

## 0. 너는 누구인가

너는 Flutter/Dart에 정통한 시니어 모바일 엔지니어다.
클린 아키텍처, 상태관리, UX 일관성을 최우선으로 한다.
와이어프레임(`frontend/docs/wireframe.html`)을 기준으로 화면을 구현한다.

**절대 규칙**
- `backend/docs/api-spec.md`에 없는 API를 임의로 호출하지 않는다.
- 와이어프레임에 없는 UI 요소를 임의로 추가하지 않는다.
- 불확실하면 추측 금지. 반드시 질문한다.

---

## 1. 기술 스택

| 항목 | 선택 |
|---|---|
| Framework | Flutter (iOS + Android 공용) |
| 상태관리 | Riverpod (또는 Bloc — 결정 후 기록) |
| 네트워킹 | Dio + Retrofit |
| 지도 | 카카오맵 Flutter 플러그인 |
| 위치 | geolocator |
| 주소/장소 | 카카오 로컬 API |
| FCM | firebase_messaging |
| 이미지 업로드 | S3 Presigned URL 직접 PUT |
| 로컬 저장 | flutter_secure_storage (토큰) |

---

## 2. 앱 구조

두 개의 앱을 **단일 코드베이스**에서 관리한다.

```
frontend/
├── lib/
│   ├── main.dart
│   ├── core/           # 공통 (네트워크, 토큰, 에러 처리)
│   ├── buyer/          # 구매자 앱 전용
│   │   ├── screens/
│   │   ├── widgets/
│   │   └── providers/
│   └── seller/         # 판매자 앱 전용
│       ├── screens/
│       ├── widgets/
│       └── providers/
└── docs/
    ├── screen-list.md  ← 전체 화면 목록 + 라우트
    └── wireframe.html  ← 와이어프레임 원본
```

---

## 3. 디자인 토큰 (와이어프레임 기준)

```dart
// 색상
const roseColor     = Color(0xFFC8614E);  // 구매자 앱 Primary
const roseLt        = Color(0xFFF2E8E5);
const sageColor     = Color(0xFF5A7A68);  // 판매자 앱 Primary
const sageLt        = Color(0xFFE8F0EC);
const inkColor      = Color(0xFF1C1917);
const ink60         = Color(0xFF6B6560);
const ink30         = Color(0xFFB8B4AF);
const borderColor   = Color(0xFFE4E0DA);
const creamColor    = Color(0xFFFAF7F4);

// 타이포그래피
// 영문 세리프: Cormorant Garamond (로고, 확정 화면 타이틀)
// 한국어 본문: Noto Sans KR
// 모노: DM Mono (타이머, 뱃지)

// 반경
const borderRadiusSm = 8.0;
const borderRadiusMd = 14.0;
const borderRadiusLg = 20.0;
const borderRadiusXl = 28.0;
```

---

## 4. 화면 목록 (와이어프레임 기준)

### 구매자 앱 (18화면)

| 화면 ID | 화면명 | 라우트 |
|---|---|---|
| b-home | 구매자 홈 | `/buyer/home` |
| b-r1 | 요청생성 Step1 — 목적·관계·분위기 | `/buyer/request/step1` |
| b-r2 | 요청생성 Step2 — 예산 | `/buyer/request/step2` |
| b-r3a | 요청생성 Step3A — 픽업 장소·날짜 | `/buyer/request/step3/pickup` |
| b-r3b | 요청생성 Step3B — 배송 장소 입력 | `/buyer/request/step3/delivery` |
| b-r4a | 요청생성 Step4A — 픽업 시간 선택 | `/buyer/request/step4/pickup` |
| b-r4b | 요청생성 Step4B — 배송 날짜·시간대 | `/buyer/request/step4/delivery` |
| b-rdone | 요청 전송 완료 | `/buyer/request/done` |
| b-detail | 요청 상세 (제안 대기) | `/buyer/requests/:id` |
| b-proplist | 제안 목록 | `/buyer/requests/:id/proposals` |
| b-propdetail | 제안 상세 | `/buyer/proposals/:id` |
| b-pay | Mock 결제 | `/buyer/proposals/:id/pay` |
| b-done | 예약 확정 | `/buyer/reservations/:id` |
| b-tab-reqs | 내 요청 탭 | `/buyer/requests` |
| b-tab-notif | 알림 탭 | `/buyer/notifications` |
| b-tab-my | 마이 탭 | `/buyer/my` |

### 판매자 앱 (14화면)

| 화면 ID | 화면명 | 라우트 |
|---|---|---|
| s-home | 판매자 홈 대시보드 | `/seller/home` |
| s-reqlist | 요청 목록 | `/seller/requests` |
| s-reqdetail | 요청 상세 — 픽업 요청 | `/seller/requests/:id` |
| s-reqdetail-delivery | 요청 상세 — 배송 요청 | `/seller/requests/:id` (type=delivery) |
| s-p1 | 제안서 작성 Step1 — 픽업 | `/seller/proposals/new/step1` |
| s-p2 | 제안서 작성 Step2 — 픽업 시간 | `/seller/proposals/new/step2/pickup` |
| s-p1-delivery | 제안서 작성 Step1 — 배송 | `/seller/proposals/new/step1` (delivery) |
| s-p2-delivery | 제안서 작성 Step2 — 배송 시간대 | `/seller/proposals/new/step2/delivery` |
| s-pdone | 제안 전송 완료 | `/seller/proposals/:id/done` |
| s-done | 예약 확정 알림 | `/seller/reservations/:id` |
| s-tab-req | 요청 탭 | `/seller/requests` |
| s-tab-hist | 현황 탭 | `/seller/stats` |
| s-tab-my | 마이 탭 | `/seller/my` |

---

## 5. 핵심 UX 규칙 (와이어프레임 기반)

### 공통
- 구매자 앱 Primary = `roseColor`, 판매자 앱 Primary = `sageColor`
- 하단 네비게이션 4개 탭 (구매자: 홈/내요청/알림/마이, 판매자: 홈/요청/현황/마이)
- 상태 배지: 폰트 DM Mono, 소문자 금지

### 구매자 앱 특이사항
- 요청 생성 플로우: 픽업/배송 분기 (Step3 에서 토글)
- 제안 목록: **가격 절대 비노출** (API에도 없음)
- 제안 상세: 가격은 **맨 마지막** 항목
- 예산 선택: TIER 카드 4개 (2x2 그리드), "꽃집마다 가격이 다를 수 있어요" 문구 필수
- 제안 만료 타이머: DM Mono, 빨간색, 실시간 카운트다운

### 판매자 앱 특이사항
- 제안서 작성 시작 버튼 클릭 전 반드시 경고 모달 표시:
  "제안서 작성을 시작한 뒤 정해진 시간 내 제출하지 못하면 패널티(신뢰도 지수 하락)가 있을 수 있어요."
- 픽업 슬롯: 30분 단위 (10:00 ~ 20:00), **단 1개만** 선택 가능
- 배송 슬롯: 오전/오후/저녁 3가지, **단 1개만** 선택 가능
- 구매자 희망 시간 강조 표시 (Step2 화면)
- 현황 탭: 신뢰도 지수 프로그레스 바 포함

### 금지 UI 패턴
- 픽업/배송 슬롯에서 복수 선택 UI (판매자는 단일 선택)
- 제안 목록 화면에서 가격 표시
- "반경 n km 내 전송됨" 텍스트 표시 (와이어프레임에 없음)
- 결제 수단 입력 UI (Mock 결제 — 버튼 클릭만)

---

## 6. API 연동 규칙

- 토큰: `flutter_secure_storage` 저장, Dio interceptor로 자동 주입
- 에러 처리: `ApiResponse.success = false` → `error.code`로 분기 처리
- 401 자동 재발급: Dio interceptor → `/api/v1/auth/reissue` 호출 → 재시도
- 이미지 업로드: `POST /api/v1/images/presigned-url` → S3 직접 PUT → URL 저장

---

## 7. 작업 시작 전 체크리스트

- [ ] `backend/docs/api-spec.md`의 해당 엔드포인트 확인
- [ ] `frontend/docs/screen-list.md`의 라우트/화면 ID 확인
- [ ] `frontend/docs/wireframe.html`의 UI 레이아웃 확인
- [ ] 디자인 토큰 준수 (color, typography, radius)
- [ ] 픽업/배송 플로우 분기 처리
- [ ] 구매자/판매자 슬롯 규칙 구분 (구매자 복수, 판매자 단일)
- [ ] Plan Mode로 계획 먼저, 승인 후 구현
