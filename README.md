# 🏜️ 웰니스 & 생산성 통합 플랫폼, OASIS25

<div align="left">
  <a href="https://app.notion.com/p/Oasis25-39ceadb9c0768030bb14fb2cc416a028"><img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white" alt="Notion" /></a>
  <a href="https://ssg-frontend-eight.vercel.app/"><img src="https://img.shields.io/badge/Vercel-000000?style=for-the-badge&logo=vercel&logoColor=white" alt="Vercel Deploy" /></a>
</div>

> 파편화된 생산성 관리 툴을 한곳에 모으고, 감성적인 디자인과 자체 통계/회고 요소를 결합한
> **뽀모도로 통합 플랫폼 오아시스(Oasis25)** 입니다.
> (프로젝트 진행 기간: 2026.07.16 ~ 2026.07.29)

<br>

## 👥 팀원 소개

|                                                            고유정(팀장)                                                            |                                                               소윤서                                                               |                                                               송동현                                                               |
| :--------------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------------: |
| <img width="364" height="364" alt="image" src="https://github.com/user-attachments/assets/444f70fd-636d-4ade-a7d9-b12cc5422897" /> | <img width="420" height="420" alt="image" src="https://github.com/user-attachments/assets/18d97620-f988-4aaa-9521-9d7acb587021" /> | <img width="420" height="420" alt="image" src="https://github.com/user-attachments/assets/4c7b948c-2831-441b-8729-23236fc997e5" /> |
|                                               [@daenggg](https://github.com/daenggg)                                               |                                               [@HITS-SO](https://github.com/HITS-SO)                                               |                                           [@donghyeon01](https://github.com/donghyeon01)                                           |
|                                                Figma, UX 고도화 및 통계 페이지 구현                                                |                                                   회고 페이지 및 마이페이지 구현                                                   |                                                      홈 페이지 및 백엔드 구현                                                      |

<br>

## 📌 기획 배경 및 해결책

**Pain Point:** 기존의 뽀모도로 타이머 앱은 딱딱한 시간 측정에만 그치며, 하루의 전반적인 건강 상태(수분/카페인 섭취)나 감정 상태(회고)를 종합적으로 파악하기 어려웠습니다.

**Our Solution:**

1.  **통합 대시보드 제공:** 흩어져 있던 타이머, 할 일(ToDo), 건강 트래커를 메인 홈 화면 한곳에서 직관적으로 관리할 수 있도록 설계했습니다.
2.  **감성 경험 (UX):** 클레이모피즘(Claymorphism)과 마이크로 애니메이션, 다크/라이트 모드를 통해 유저가 계속 머물고 싶어 하는 시각적 만족감을 제공합니다.
3.  **데이터 기반의 성장:** 단순 기록을 넘어, 유저의 실제 집중 시간과 날씨/시간대/감정 간의 상관관계를 분석한 통계 및 히트맵을 시각화합니다. 이로 인해 사용자의 꾸준한 이용을 유도합니다.

<br>

## 🌟 핵심 기능 (Core Features)

### 1. 메인 대시보드 (통합 생산성 관리)

> 하나의 화면에서 타이머, 건강 트래커, 날씨, 명언을 모두 관리할 수 있는 클레이모피즘 기반의 올인원 홈 화면입니다.

|                                              ☀️ 라이트 모드 (Light Mode)                                              |                                               🌙 다크 모드 (Dark Mode)                                                |
| :-------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------: |
| <img width="48%" alt="image" src="https://github.com/user-attachments/assets/2a6c489a-8f09-4e87-ac45-f041953d129b" /> | <img width="48%" alt="image" src="https://github.com/user-attachments/assets/c613fed5-44dd-4707-99e5-d7295065bf36" /> |

- **스마트 뽀모도로 타이머:** 작업/휴식 사이클 관리 및 당일 총 집중 시간 자동 동기화

* **수분/카페인 트래커:** 게이지 뱃지를 통한 일일 목표 달성률 시각화
* **외부 API 연동:** 실시간 기상청 단기예보 날씨 및 오늘의 명언 연동
* **클레이모피즘 UI:** 다크/라이트 모드를 완벽 지원하는 둥글고 부드러운 3D 패널 디자인

<br>

### 2. 인터랙티브 통계 대시보드

> 누적된 집중 시간과 감정 기록을 바탕으로 유저의 생산성 트렌드를 시각화합니다.

#### 통계 차트

- **커스텀 SVG 차트:** 외부 무거운 라이브러리 없이 직접 수학적으로 계산하여 렌더링한 부드러운 꺾은선 차트

|                                                        <!-- -->                                                        |                                                        <!-- -->                                                        |
| :--------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------: |
| <img width="100%" alt="image" src="https://github.com/user-attachments/assets/320b96f4-97dc-487b-955e-eb88eb7ae6dc" /> | <img width="100%" alt="image" src="https://github.com/user-attachments/assets/184b6e8c-a0ca-49a0-bbc4-74a31a39e0b3" /> |
| <img width="100%" alt="image" src="https://github.com/user-attachments/assets/4e2eb6ed-5b29-4ee3-beb3-d5eff80d8b9a" /> | <img width="100%" alt="image" src="https://github.com/user-attachments/assets/01c0e6c6-bd98-4d12-9531-54854d167929" /> |

#### 잔디

- **잔디 히트맵:** 깃허브(GitHub) 스타일의 히트맵을 제공하여 꾸준한 사용 및 성장 동기 부여
  [image5]

<br>

### 3. 감성 회고 다이어리

> 하루의 끝에서 나의 감정과 생산성을 돌아보는 다이어리 기능입니다.

<img width="1072" height="762" alt="image" src="https://github.com/user-attachments/assets/8c9164c2-fb0b-42e7-9d5c-f21f3b639b5f" />

- **감정 태그 시스템:** 감정 태그를 선택하여 하루의 기분을 직관적으로 기록
- **인사이트 제공:** 그날의 집중 시간과 감정의 연관성을 스스로 회고할 수 있는 환경 제공

<br>

## 🛠️ 시스템 아키텍처 & 유저 플로우

### [ 시스템 구조 ]

- **Frontend**: React 19, TypeScript, Vite, TailwindCSS, Framer Motion
- **Backend**: Spring Boot 3, Spring Security(JWT), PostgreSQL, Spring Data JPA

<img width="2818" height="2598" alt="image" src="https://github.com/user-attachments/assets/a6a45e72-0855-4dce-9d3f-c6b542c6d291" />

### [ 유저 서비스 Flow ]

방사형(Hub and Spoke) 구조로 설계되어 메인 대시보드를 기점으로 모든 기능이 매끄럽게 연결됩니다.

<img width="2366" height="2150" alt="image" src="https://github.com/user-attachments/assets/59e9d6eb-50c6-4671-b694-fe4469228d99" />

<br>

## 📂 백엔드 폴더 구조

```text
SSG-Backend/
├── src/main/java/com/oasis25
│   ├── auth/            # JWT 및 Spring Security 인증 로직
│   ├── common/          # Exception Handler, 공통 Response 포맷 등
│   ├── diary/           # 회고 다이어리 도메인 (AI 요약 연동)
│   ├── drink/           # 음료 상세 데이터 관리
│   ├── feedback/        # 유저 피드백 서비스
│   ├── pomodoro/        # 뽀모도로 타이머, 카테고리, 프리셋 도메인
│   ├── quote/           # 오늘의 명언 데이터 제공
│   ├── stats/           # 날씨별, 일자별 통계 집계 엔진
│   ├── user/            # 회원 정보 조회 및 수정
│   ├── water/           # 수분 및 카페인 섭취 로그 관리
│   └── weather/         # 외부 날씨 API 연동 컴포넌트
└── src/main/resources
    ├── application.yml         # 공통 인프라/보안/CORS 설정
    └── application-local.yml   # 로컬 DB 및 API 키 로컬 설정
```

---

## 📄 API 명세서 (API Specification)

오아시스 백엔드의 전체 API 명세와 요청/응답 형태는 [**🔗 오아시스 API 명세서 (Notion)**](https://app.notion.com/p/API-3aceadb9c07680ec9490fd35aa16d8c7?source=copy_link)에서 자세히 확인하실 수 있습니다.
