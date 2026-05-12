<div align="center">

# 🌸 꽃 큐레이션 플랫폼 Florent

**나만의 플로리스트**

요청서 1장으로 주변 꽃집의 제안을 받고, 마음에 드는 제안을 골라 예약을 확정합니다.

[![Backend](https://img.shields.io/badge/Backend-Spring_Boot_3-6DB33F?style=flat-square&logo=springboot&logoColor=white)](#기술-스택)
[![Frontend](https://img.shields.io/badge/Frontend-Flutter-02569B?style=flat-square&logo=flutter&logoColor=white)](#기술-스택)
[![Cloud](https://img.shields.io/badge/Cloud-AWS-FF9900?style=flat-square&logo=amazonaws&logoColor=white)](#인프라)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-8B5CF6?style=flat-square)](#아키텍처)


</div>

---

## 📖 프로젝트 소개

> **"상황에 맞는 꽃다발을 설명하기 어렵고, 여러 꽃집을 비교하기는 더 번거롭다."**

기존 꽃 시장은 어떤 꽃집이 어떤 가격에 어떤 스타일을 만드는지, 직접 발품을 팔기 전에는 알 수 없습니다. 
**Florent**는 이 정보 비대칭을 해결하기 위해 구매자를 중심으로 거래 구조를 바꾸어, **요청서 → 제안 → 선택 → 예약 확정**의 역경매 구조로 다시 설계했습니다.

- 👤 **개발 / 운영**: 1인 (기획·디자인·풀스택·인프라·운영)
- 🛠️ **개발 방식**: Claude Code 기반 AI 오케스트레이션 워크플로우
- 📅 **상태**: MVP — iOS Happy Path E2E 검증 완료, 베타 운영 중

### 핵심 사용자 흐름

```
[구매자]                              [판매자]
  │                                      │
  ① 요청서 작성 (목적·관계·분위기·예산·픽업/배송)
  │     ↓ 반경 2km 내 꽃집에 자동 전달
  │                                      ② 요청 수신 → 제안서 작성 (24h 유효)
  │ ③ 제안 비교 ←──────────────────────────┘
  │ ④ 제안 선택 + 연락(추후 결제기능 구현)
  │ ⑤ 예약 확정 ──────────────────────→ 선택/미선택 알림
  ↓                                      ↓
  픽업 또는 배송 약속 시간                  꽃다발 준비
```

---

## 🎬 데모

<div align="center">

### 📱 앱 스크린샷

<table>
  <tr>
    <td align="center"><b>요청서 작성</b></td>
    <td align="center"><b>제안 비교</b></td>
    <td align="center"><b>예약 확정</b></td>
    <td align="center"><b>판매자 요청함</b></td>
  </tr>
  <tr>
    <td><img src="docs/images/screen-request.png" width="200" alt="요청서 작성" /></td>
    <td><img src="docs/images/screen-proposals.png" width="200" alt="제안 비교" /></td>
    <td><img src="docs/images/screen-reservation.png" width="200" alt="예약 확정" /></td>
    <td><img src="docs/images/screen-seller.png" width="200" alt="판매자 요청함" /></td>
  </tr>
</table>

> 📌 *이미지는 `docs/images/` 폴더에 추가하면 자동으로 표시됩니다.*

### 🎥 데모 영상

[![Florent Demo Video](docs/images/demo-thumbnail.png)](https://youtu.be/PLACEHOLDER)

> 📌 *YouTube 데모 링크를 위 URL에 교체하세요.*

</div>

---

## 🏛️ 아키텍처

Florent는 **Hexagonal Architecture (Ports & Adapters)** 기반으로 설계되었습니다. 단순한 패턴 채택이 아니라, **"외부 의존성(결제·인증·알림·푸시)을 비즈니스 컨텍스트에 따라 자유롭게 교체할 수 있어야 한다"** 는 비즈니스 요구사항에서 출발한 의사결정입니다.

```
┌──────────────────────────────────────────────────────────────┐
│                     Inbound Adapters                         │
│       BuyerRequestController · SellerProposalController      │
│       NotificationController · OutboxWorker · ExpiryScheduler│
└─────────────────────┬────────────────────────────────────────┘
                      │  Inbound Ports (UseCase 인터페이스)
┌─────────────────────▼────────────────────────────────────────┐
│                    Application Layer                         │
│   BuyerRequestService · ReservationService · ProposalService │
└─────────────────────┬────────────────────────────────────────┘
                      │
┌─────────────────────▼────────────────────────────────────────┐
│                       Domain Layer                           │
│   CurationRequest · Proposal · Reservation · Notification    │
│   (프레임워크 어노테이션 없음, 순수 Java)                       │
└─────────────────────┬────────────────────────────────────────┘
                      │  Outbound Ports (Repository · Port 인터페이스)
┌─────────────────────▼────────────────────────────────────────┐
│                    Outbound Adapters                         │
│   JPA Repository · Kakao OAuth · MockPaymentAdapter          │
│   FcmPushAdapter · S3PresignedUrlAdapter                     │
└──────────────────────────────────────────────────────────────┘
```

### 핵심 도메인

| 도메인 | 역할 |
|--------|------|
| **CurationRequest** | 구매자의 요청서 (48시간 유효, 목적·관계·분위기·예산 4단계) |
| **Proposal** | 판매자의 제안서 (24시간 유효, 가격·시간 제시) |
| **Reservation** | 제안 선택 + Mock 결제 후 확정되는 예약 |
| **Notification** | 인앱 알림함 (요청 도착·제안 도착·예약 확정) |

---

## 🛠️ 기술 스택

### Backend
- **Language / Framework**: Java 17, Spring Boot 3
- **Architecture**: Hexagonal (Ports & Adapters), DDD
- **Database**: PostgreSQL
- **Auth**: Kakao OAuth 2.0, JWT
- **Payment**: Mock Payment Adapter (PG 연동 추상화)
- **Notification**: 인앱 알림함 (DB) + Firebase Cloud Messaging
- **Storage**: AWS S3 (Presigned URL)
- **Testing**: Cucumber (BDD), Testcontainers, JUnit 5

### Frontend
- **Framework**: Flutter (iOS / Android)
- **State Management**: Riverpod
- **Routing**: GoRouter
- **Auth**: Kakao Flutter SDK
- **Code Generation**: freezed, json_serializable

### Infrastructure
- **Compute**: AWS EC2 (t3.medium)
- **Database**: AWS RDS (PostgreSQL)
- **Storage**: AWS S3
- **DNS / Domain**: Route53, florent.co.kr
- **Web Server**: Nginx + SSL (Let's Encrypt / Certbot)

### AI Orchestration
- **Claude Code** with custom workflow
  - `CLAUDE.md` — 프로젝트 컨텍스트 및 제약 조건
  - 6개 슬래시 커맨드 (`/start`, `/ship`, `/review`, `/sync-docs`, `/next`, `/debt`)
  - 4개 전문 리뷰어 스킬 (DDD · 성능 · 보안 · 테스트)

---

## 🤖 AI Orchestration 개발 프로세스

Florent는 단순히 AI에게 코드를 시킨 프로젝트가 아닙니다. **"개발자가 AI를 어떻게 통제하고 관리하는가"** 를 시스템으로 만든 프로젝트입니다.

### CLAUDE.md 제약 시스템

프로젝트의 의사결정과 제약 조건을 코드와 함께 버전 관리합니다.

```markdown
# 프로젝트 헌법 (예시)
- 모든 외부 의존성은 Port-Adapter 형태로 분리한다.
- 도메인 객체는 프레임워크 어노테이션을 받지 않는다.
- Application Service는 Repository 인터페이스에만 의존한다.
- 모든 알림은 인앱 알림함을 거친 후 푸시로 발송된다.
```

→ AI에게 매번 설명할 필요 없이, **프로젝트의 의사결정이 코드와 함께 버전 관리**됩니다.

### 슬래시 커맨드 워크플로우

| 커맨드 | 역할 |
|--------|------|
| `/start` | 작업 시작 — 컨텍스트 로딩, 브랜치 생성 |
| `/ship` | PR 생성 — 자동 리뷰 후 머지 준비 |
| `/review` | 4개 전문 리뷰어로 코드 리뷰 |
| `/sync-docs` | 코드 변경에 맞춰 문서(`biz-rules.md`, `api-spec.md` 등) 동기화 |
| `/next` | 다음 우선순위 작업 추천 |
| `/debt` | 기술 부채 추적 및 우선순위화 |

### 4개의 전문 리뷰어

병렬 실행되는 4개의 sub-agent가 각자의 관점으로 코드를 검토합니다.

- 🏗️ **DDD Reviewer** — 도메인 경계, 애그리거트 설계, 유비쿼터스 언어
- ⚡ **Performance Reviewer** — N+1, 쿼리 최적화, 캐싱
- 🔒 **Security Reviewer** — 인증·인가, 입력 검증, 시크릿 관리
- 🧪 **Testing Reviewer** — 테스트 커버리지, 엣지 케이스

### 테스트 전략

Hexagonal 구조 덕분에 테스트가 다층으로 분리됩니다.

```
src/test/java/com/florent/
├── domain/         # 순수 도메인 단위 테스트 (의존 없음, 가장 빠름)
├── application/    # Fake Port 주입, DB/FCM 없이 테스트
├── adapter/in/     # @WebMvcTest + UseCase Mock
├── adapter/out/    # Testcontainers (실제 PostgreSQL)
└── fake/           # FakeRepository, FakePort 구현체
```

`src/test/resources/features/`에 Cucumber BDD 시나리오로 핵심 플로우(요청 생성, 제안 제출, 예약 확정)를 명세합니다.

---

## 📂 프로젝트 구조

```
florent/
├── backend/                          # Spring Boot 3 (Hexagonal)
│   ├── src/main/java/com/florent/
│   │   ├── domain/                   # 순수 도메인 (프레임워크 의존성 없음)
│   │   │   ├── request/              # CurationRequest
│   │   │   ├── proposal/             # Proposal
│   │   │   ├── reservation/          # Reservation
│   │   │   └── notification/         # Notification
│   │   ├── application/              # UseCase, Service
│   │   │   ├── buyer/
│   │   │   ├── seller/
│   │   │   └── reservation/
│   │   ├── adapter/
│   │   │   ├── in/web/               # REST Controllers
│   │   │   └── out/                  # JPA · Kakao · Payment · FCM · S3
│   │   └── config/
│   ├── src/test/                     # 다층 테스트 (도메인 / 애플리케이션 / 어댑터)
│   └── build.gradle
│
├── frontend/                         # Flutter (iOS / Android)
│   ├── lib/
│   │   ├── feature/
│   │   │   ├── buyer/
│   │   │   └── seller/
│   │   ├── core/
│   │   └── main.dart
│   └── pubspec.yaml
│
├── docs/                             # 아키텍처 문서, ERD, API 스펙
│   ├── adr/                          # Architecture Decision Records
│   ├── biz-rules.md
│   ├── erd.md
│   ├── api-spec.md
│   ├── architecture.md
│   ├── conventions.md
│   └── images/
│
├── .claude/                          # AI 워크플로우
│   ├── commands/                     # 슬래시 커맨드 정의
│   └── skills/                       # 리뷰어 스킬
│
├── docker-compose.yml
└── CLAUDE.md                         # 프로젝트 헌법
```

---

## 👤 만든 사람

**이지현 (Jihyun Lee)** — *혼자서 팀을 만드는 개발자*

홍익대학교 컴퓨터공학과 (2026년 2월 졸업). 
AI-Native Product Engineer로서 기획·디자인·풀스택·인프라·운영을 1인 사이클로 돌립니다. Florent는 그 사이클 자체를 시연하는 프로젝트입니다.

- ✍️ [Tistory Blog](https://ceojosephine.tistory.com)
- 💼 [LinkedIn — @ceojosephine](https://linkedin.com/in/ceojosephine)

---



