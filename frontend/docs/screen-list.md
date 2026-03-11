# screen-list.md — Florent Flutter 화면 목록

> 와이어프레임(`wireframe.html`) 기준으로 작성.
> 화면 추가/변경 시 이 문서를 먼저 수정하고 와이어프레임과 동기화한다.


---
## 공통 인증 화면 (구매자/판매자 공유)

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| auth-splash | 스플래시 | 로고, 자동 로그인 시도 | 로컬 토큰 확인 → 유효 시 홈으로 |
| auth-login | 카카오 로그인 | Florent 로고, 카카오 로그인 버튼 (노란색) | `POST /auth/kakao` |
| auth-role | 역할 선택 | "구매자로 시작" / "판매자로 시작" 카드 2개 | `POST /auth/role` |
| auth-seller-info | 판매자 사업자 정보 입력 | 가게명, 카카오 주소 검색, 지도 미리보기, 사업자번호(선택) | `POST /auth/seller-info` |

**인증 플로우**
```
앱 시작
  → auth-splash
    → 로컬 토큰 유효: 역할에 따라 buyer/home 또는 seller/home 이동
    → 로컬 토큰 없음/만료: auth-login

auth-login (카카오 로그인 버튼 탭)
  → isNewUser=true: auth-role 이동
  → isNewUser=false: 역할에 따라 홈 이동

auth-role (역할 선택)
  → BUYER 선택: buyer/home 이동
  → SELLER 선택: auth-seller-info 이동

auth-seller-info (사업자 정보 입력 완료)
  → seller/home 이동
```
**토큰 갱신 플로우 (공통)**
```
API 호출 → 401 TOKEN_EXPIRED
  → POST /auth/reissue (자동)
    → 성공: 원래 API 재호출
    → 401 REFRESH_TOKEN_EXPIRED: auth-login으로 이동 (토큰 삭제)
```
---
## 구매자 앱 (18화면)
### 메인

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| b-home | 구매자 홈 | 진행중 요청 카드, 이용 안내, "꽃다발 요청하기" CTA | `GET /buyer/requests` (최근 1건) |

### 요청 생성 플로우 (Step 1~4, 픽업/배송 분기)

| 화면 ID | 화면명 | 주요 컴포넌트 | 비고 |
|---|---|---|---|
| b-r1 | Step1 — 목적·관계·분위기 | 태그 칩 (복수 선택) + 직접 입력 | 목적/관계/분위기 각각 필수 |
| b-r2 | Step2 — 예산 | 2x2 TIER 카드 그리드 | "꽃집마다 가격 다를 수 있어요" 문구 필수 |
| b-r3a | Step3A — 픽업 장소·날짜 | 카카오 주소 검색, 지도 미리보기, DatePicker | 픽업 선택 시 진입 |
| b-r3b | Step3B — 배송 장소 입력 | 주소 검색, 상세주소 입력, 지도 미리보기 | 배송 선택 시 진입 |
| b-r4a | Step4A — 픽업 시간 선택 | 30분 슬롯 그리드 (10:00~20:00), **복수 선택** | PICKUP_30M |
| b-r4b | Step4B — 배송 날짜·시간대 | DatePicker + 시간대 3개 카드, **복수 선택** | DELIVERY_WINDOW |
| b-rdone | 요청 전송 완료 | 요청 요약 카드, "요청 상세 보기" 버튼 | `POST /buyer/requests` 응답 |

**요청 생성 분기 로직**
```
Step2 완료
  → 픽업 선택: Step3A → Step4A → 완료
  → 배송 선택: Step3B → Step4B → 완료
Step3에서 토글로 픽업/배송 전환 가능 (데이터 초기화)
```
### 제안 확인 플로우

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| b-detail | 요청 상세 | 요청 요약, 만료 타이머, 제안 목록 미리보기 ("n명 작성 중" 포함) | `GET /buyer/requests/:id` |
| b-proplist | 제안 목록 | 제안 카드 (가격 비노출, 만료 배지 포함) | `GET /buyer/requests/:id/proposals` |
| b-propdetail | 제안 상세 | 이미지, 큐레이션 내용, 슬롯, 가격(맨 마지막) | `GET /buyer/proposals/:id` |

### 결제·확정 플로우

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| b-pay | Mock 결제 | 결제 요약, 결제 수단 선택 UI (테스트 모드 배너) | `POST /buyer/proposals/:id/select` |
| b-done | 예약 확정 | 확정 hero 화면, 예약 요약 카드 | 응답 데이터 표시 |

### 예약 상세 화면

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| b-reservation-detail | 예약 상세 | 예약 정보 전체, 판매자 정보, 꽃다발 정보, 수령 정보 | `GET /buyer/reservations/:id` |

**b-reservation-detail 표시 항목**
```
[예약 상태 배지] CONFIRMED

[판매자 정보 섹션]
- 가게 이름 (shopName)

[꽃다발 정보 섹션]
- 꽃다발 이름 (concept_title)
- 큐레이션 설명 (description)
- 제안 이미지 (있을 경우만 표시)
- 최종 결제 금액 (강조 표시)

[수령 정보 섹션]
- 수령 방식 배지 (픽업 / 배송)
- 수령 날짜 + 시간 슬롯
- 장소 (픽업: 가게 주소 / 배송: 배송지 주소)

[내 요청 원문 요약 섹션] (접힘/펼침 가능)
- 목적·관계·분위기 태그 칩
- 예산 TIER
```
### 알림 화면

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| b-notifications | 알림 목록 | 알림 카드 리스트, 읽음/안읽음 구분 | `GET /notifications` |

**b-notifications 표시 항목**
```
[알림 카드] (최신순)
- 알림 타입 아이콘
  - PROPOSAL_ARRIVED: 💐 (새 제안)
- 알림 메시지
  - PROPOSAL_ARRIVED: "새로운 제안이 도착했어요! 확인해보세요."
- 수신 시각 ("방금 전" / "n분 전" / "어제" 상대 시간 표시)
- 읽지 않은 알림: 좌측 컬러 인디케이터로 구분

[알림 카드 탭 시 이동 목적지]
- PROPOSAL_ARRIVED → b-propdetail (해당 제안의 제안 상세)
  탭 시 읽음 처리 (인디케이터 제거)

[빈 상태]
- "아직 알림이 없어요" 안내 문구
```
### 탭 화면

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| b-tab-reqs | 내 요청 탭 | 요청 목록, 필터 탭 (전체/진행중/완료) | `GET /buyer/requests` |
| b-tab-reservations | 예약 탭 | 확정된 예약 목록 | `GET /buyer/reservations` |
| b-tab-my | 마이 탭 | 프로필, 설정 메뉴 | `GET /buyer/me` |

**b-tab-reqs 필터 탭별 기대 동작**
```
[전체]
- 모든 요청 카드 표시
- 상태 배지: OPEN(초록, "진행중") / EXPIRED(회색, "만료") / CONFIRMED(sage, "확정")
- 정렬: 최신순

[진행중]
- status = OPEN 인 요청만 표시
- 만료 타이머(ExpiryTimer) 표시
- 제안 수 표시 ("n개의 제안이 도착했어요")
- 빈 상태: "진행 중인 요청이 없어요. 꽃다발을 요청해보세요!" + CTA 버튼

[완료]
- status = CONFIRMED 또는 EXPIRED 인 요청만 표시
- CONFIRMED 카드: "확정" 배지, 탭 시 b-reservation-detail로 이동
- EXPIRED 카드: "만료" 배지, 탭 시 이동 없음
- 빈 상태: "완료된 요청이 없어요"
```
**b-tab-reservations 예약 탭 표시 항목**
```
[예약 카드] (최신 확정순)
- 꽃다발 이름 (concept_title)
- 가게 이름 (shopName)
- 수령 방식 배지 (픽업 / 배송)
- 수령 날짜 + 시간 슬롯
- 최종 결제 금액
- 탭 시 b-reservation-detail로 이동

[빈 상태]
- "아직 확정된 예약이 없어요" 안내 문구
```
---
## 판매자 앱 (14화면)

### 메인

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| s-home | 판매자 홈 | 통계 카드 3개 (새요청/제안대기/이달확정), 최근 요청 목록 | `GET /seller/home` |

### 요청 확인 플로우

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| s-reqlist | 요청 목록 | 요청 카드 (상태 배지: OPEN/EXPIRED/CONFIRMED), 필터 탭 | `GET /seller/requests` |
| s-reqdetail | 요청 상세 — 픽업 | 요청 내용, 경고 배너, "제안서 작성하기" 버튼 | `GET /seller/requests/:id` |
| s-reqdetail-delivery | 요청 상세 — 배송 | 배송지 정보 포함 | `GET /seller/requests/:id` |

**판매자 요청 목록 상태 배지**
- `OPEN` → 정상 노출, 제안 작성 가능
- `EXPIRED` → "만료" 배지, 제안 불가
- `CONFIRMED` → "마감" 배지, 제안 불가

### 제안서 작성 플로우 (픽업/배송 분기)

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| s-p1 | Step1 픽업 — 큐레이션·가격·사진 | 꽃다발 이름, 구성 설명 템플릿, 가격 입력, 이미지 업로드 | `POST /seller/requests/:id/proposals` (DRAFT) |
| s-p2 | Step2 픽업 — 시간 선택 | 30분 슬롯 그리드, **단일 선택**, 구매자 희망 시간 강조 | `PUT /seller/proposals/:id` + 제출 |
| s-p1-delivery | Step1 배송 — 큐레이션·가격·사진 | s-p1과 동일 구조 | 동일 |
| s-p2-delivery | Step2 배송 — 시간대 선택 | 오전/오후/저녁 3개 카드, **단일 선택**, 구매자 미선택 시 비활성(선택 허용) | 동일 |

**제안서 작성 시작 경고 모달 (필수)**
```
제목: 제안서 작성을 시작할까요?
내용: 제안서 작성을 시작한 뒤 정해진 시간 내 제출하지 못하면
      패널티(신뢰도 지수 하락)가 있을 수 있어요.
      신중하게 버튼을 클릭해주세요.
버튼: [취소] [작성 시작]
```
### 완료 화면

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| s-pdone | 제안 전송 완료 | 전송 완료 hero, 제안 요약 | 응답 데이터 |
| s-done | 예약 확정 알림 | 확정 hero (sage 컬러), 예약 요약 | FCM 수신 후 진입 |

### 예약 상세 화면

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| s-reservation-detail | 예약 상세 | 예약 정보 전체, 구매자 정보, 꽃다발 정보, 수령 정보 | `GET /seller/reservations/:id` |

**s-reservation-detail 표시 항목**
```
[예약 상태 배지] CONFIRMED

[구매자 정보]
- 구매자 닉네임 (카카오 닉네임)

[꽃다발 정보]
- 내가 제안한 꽃다발 이름 (concept_title)
- 큐레이션 설명 (description)
- 제안 이미지 (있을 경우)
- 최종 금액

[수령 정보]
- 수령 방식: 픽업 / 배송
- 수령 날짜
- 수령 시간 슬롯
- 장소 (픽업: 가게 주소 / 배송: 배송지 주소)

[요청 원문 요약]
- 목적 태그, 관계 태그, 분위기 태그
- 예산 TIER
```
### 알림 화면

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| s-notifications | 알림 목록 | 알림 카드 리스트, 읽음/안읽음 구분 | `GET /notifications` |

**s-notifications 표시 항목**
```
[알림 카드]
- 알림 타입 아이콘
  - RESERVATION_CONFIRMED: 🌸 (확정)
- 알림 메시지
  - RESERVATION_CONFIRMED: "예약이 확정되었어요! 구매자가 내 제안을 선택했어요."
- 수신 시각 (예: "방금 전", "3분 전", "어제")
- 읽지 않은 알림: 좌측 강조 인디케이터 또는 배경색 구분
- 탭 시: 해당 예약 상세(s-reservation-detail)로 이동 + 읽음 처리

[빈 상태]
- "아직 알림이 없어요" 안내 문구
```
### 탭 화면

| 화면 ID | 화면명 | 주요 컴포넌트 | API |
|---|---|---|---|
| s-tab-req | 요청 탭 | 요청 목록 (s-reqlist와 동일) | 동일 |
| s-tab-hist | 현황 탭 | 월별 통계, 신뢰도 지수 프로그레스 바, 최근 확정 내역 (탭) | `GET /seller/stats` |
| s-tab-my | 마이 탭 | 가게 정보, 신뢰도 점수 배지, 설정 메뉴 | `GET /seller/me` |

**s-tab-hist 최근 확정 내역 카드 표시 항목**
```
[확정 내역 카드] → 탭 시 s-reservation-detail로 이동
- 예약 확정 날짜
- 꽃다발 이름 (concept_title)
- 수령 방식 배지 (픽업 / 배송)
- 최종 금액
- 수령 날짜
```
---
## 라우트 전체 목록

```dart
// 공통 인증
'/splash'
'/login'
'/auth/role'
'/auth/seller-info'

// buyer
'/buyer/home'
'/buyer/request/step1'
'/buyer/request/step2'
'/buyer/request/step3/pickup'
'/buyer/request/step3/delivery'
'/buyer/request/step4/pickup'
'/buyer/request/step4/delivery'
'/buyer/request/done'
'/buyer/requests'
'/buyer/requests/:id'
'/buyer/requests/:id/proposals'
'/buyer/proposals/:id'
'/buyer/proposals/:id/pay'
'/buyer/reservations'
'/buyer/reservations/:id'
'/buyer/notifications'
'/buyer/my'

// seller
'/seller/home'
'/seller/requests'
'/seller/requests/:id'
'/seller/proposals/new/step1'
'/seller/proposals/new/step2/pickup'
'/seller/proposals/new/step2/delivery'
'/seller/proposals/:id/done'
'/seller/reservations/:id'
'/seller/notifications'
'/seller/stats'
'/seller/my'
```
---
## 공통 컴포넌트 목록

| 컴포넌트 | 설명 | 사용처 |
|---|---|---|
| `ExpiryTimer` | 만료 카운트다운 (DM Mono, 빨간색) | 요청 만료, 제안 만료 |
| `StatusBadge` | 상태 뱃지 (DM Mono) | 요청/제안 목록 |
| `BudgetTierCard` | 예산 TIER 카드 | b-r2 |
| `TimeSlotGrid` | 30분 슬롯 그리드 (복수/단일 선택 모드) | b-r4a, s-p2 |
| `DeliveryBandSelector` | 오전/오후/저녁 카드 (복수/단일 선택 모드) | b-r4b, s-p2-delivery |
| `ProposalCard` | 제안 카드 (가격 비노출) | b-proplist |
| `ApiErrorHandler` | 에러 코드별 스낵바/다이얼로그 | 전체 |
| `ConfirmHero` | 예약 확정 hero 화면 | b-done, s-done |