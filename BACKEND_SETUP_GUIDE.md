# OASIS-25 백엔드 프로젝트 세팅 가이드

이 문서는 `OASIS-25 Plan.md`를 기반으로, SWE 1.7 에이전트가 백엔드 프로젝트를 순서대로 구현할 수 있도록 작성된 실행 가이드입니다. 각 Phase는 순서대로 진행하며, Phase 종료 시 Definition of Done(DoD)을 반드시 확인한 뒤 다음 Phase로 넘어갑니다.

---

## 0. Overview

OASIS 25는 **집중 관리 + 카페인/수분 관리 + 감정 회고**를 하나의 플랫폼에서 제공하는 생산성·웰빙 서비스입니다. 백엔드는 프론트엔드(React)와 REST API로 통신하며, 외부 API(Weather, Quote)를 연동하고 MySQL에 데이터를 저장합니다.

### 기술 스택

| 영역 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Build | Gradle |
| Security | Spring Security, JWT |
| Persistence | Spring Data JPA, Hibernate |
| DB | MySQL 8 |
| API Docs | springdoc-openapi (Swagger) |
| Infra (Phase 8) | Docker, Nginx, OCI, GitHub Actions |

### 실행 원칙 (SWE 1.7 지침)
- **Phase 순서를 반드시 지킬 것.** 이전 Phase의 DoD를 만족하지 못하면 다음 Phase로 넘어가지 않는다.
- Controller는 비즈니스 로직 금지, Service는 트랜잭션 관리, Repository는 DB 접근만 수행. DTO 사용 필수, Entity 직접 반환 금지.
- 외부 API 키, 프론트엔드 연동 세부 규격 등 **모호한 부분은 구현 전 사용자에게 질문**한다. 임의로 가정하고 진행하지 않는다.
- 기존 코드 분석 후 구현, 중복 코드 생성 금지, 하드코딩된 API URL 사용 금지.

---

## 1. Phase 1 — Project Setup

### 1.1 프로젝트 생성
- Spring Initializr 기준 설정:
  - Project: Gradle - Groovy
  - Language: Java 21
  - Spring Boot: 3.x (최신 안정 버전)
  - Group: `com.oasis25`
  - Artifact/Name: `oasis25-backend`
  - Packaging: Jar
- 필수 Dependencies:
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Validation
  - MySQL Driver
  - Lombok
  - springdoc-openapi-starter-webmvc-ui (Swagger UI)
  - jjwt (io.jsonwebtoken) — JWT 생성/검증용, `build.gradle`에 직접 추가

### 1.2 `build.gradle` 필수 의존성 (참고 목록)
```
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-validation'
runtimeOnly 'com.mysql:mysql-connector-j'
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.x.x'
implementation 'io.jsonwebtoken:jjwt-api:0.12.x'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.x'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.x'
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.security:spring-security-test'
```
버전은 프로젝트 생성 시점의 Spring Boot BOM과 호환되는 최신 안정 버전을 사용한다.

### 1.3 `application.yml` / 프로필 분리
- `application.yml`: 공통 설정 (프로필 활성화 지정)
- `application-local.yml`: 로컬 개발용 DB 연결 정보 (로컬 MySQL은 사용자가 직접 설치/설정했다고 가정. Docker Compose 사용 안 함)

```yaml
# application.yml
spring:
  profiles:
    active: local
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

```yaml
# application-local.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oasis25?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: <local-db-username>
    password: <local-db-password>
  jpa:
    hibernate:
      ddl-auto: update  # 로컬 개발 초기에만 update, 이후 validate로 전환 검토
```
- 민감정보(비밀번호, JWT secret 등)는 커밋하지 않는다. `application-local.yml`은 `.gitignore`에 추가하거나 placeholder만 커밋하고 실제 값은 환경변수/로컬 파일로 관리한다.

### 1.4 JPA Auditing & BaseEntity
- 메인 애플리케이션 클래스 또는 config 클래스에 `@EnableJpaAuditing` 적용.
- `common.entity.BaseEntity` (또는 `common.util`에 위치) 설계:
  - `createdAt` (`@CreatedDate`)
  - `updatedAt` (`@LastModifiedDate`)
  - `deletedAt` (soft delete용, nullable)
- 모든 Entity는 `BaseEntity`를 상속하여 `created_at`/`updated_at` 컬럼을 기본 포함한다.

### 1.5 Swagger 설정
- springdoc-openapi 의존성만으로 기본 `/swagger-ui.html` 자동 노출.
- 필요 시 `common.config.SwaggerConfig`에서 API 그룹/보안 스킴(Bearer JWT) 설정.

### 1.6 Phase 1 — Definition of Done
- [ ] Gradle 빌드 성공 (`./gradlew build`)
- [ ] 애플리케이션 정상 기동 (로컬 MySQL 연결 성공)
- [ ] Swagger UI 접근 가능
- [ ] `BaseEntity` + JPA Auditing 적용 확인
- [ ] 패키지 구조가 2장의 구조와 일치

---

## 2. 패키지 구조 (`com.oasis25`)

```
com.oasis25
├── auth
│   ├── controller
│   ├── service
│   ├── dto
│   └── entity        # RefreshToken 등 인증 전용 엔티티
├── user
│   ├── controller
│   ├── service
│   ├── repository
│   ├── dto
│   └── entity         # User
├── weather
│   ├── controller
│   ├── service
│   └── dto
├── quote
│   ├── controller
│   ├── service
│   └── dto
├── pomodoro
│   ├── controller
│   ├── service
│   ├── repository
│   ├── dto
│   └── entity         # FocusCategory, FocusSession
├── drink
│   ├── controller
│   ├── service
│   ├── repository
│   ├── dto
│   └── entity         # DrinkLog
├── diary
│   ├── controller
│   ├── service
│   ├── repository
│   ├── dto
│   └── entity         # Diary
├── stats
│   ├── controller
│   ├── service
│   └── dto
└── common
    ├── config          # SecurityConfig, SwaggerConfig, JpaConfig 등
    ├── security         # JwtTokenProvider, JwtAuthenticationFilter, SecurityUtil
    ├── exception        # GlobalExceptionHandler, CustomException, ErrorCode
    ├── util             # BaseEntity 등 공통 유틸
    └── entity           # BaseEntity (util 대신 여기에 둘 수도 있음, 팀 컨벤션에 따라 택1)
```

### 계층 책임 규칙
- **Controller**: 요청/응답 매핑, DTO 검증만. 비즈니스 로직 금지.
- **Service**: 트랜잭션 관리(`@Transactional`), 비즈니스 로직 수행.
- **Repository**: `JpaRepository` 상속, DB 접근만 수행. 복잡한 조회는 `@Query`/`@EntityGraph` 활용.
- **DTO 필수**: Controller ↔ Client 간에는 항상 DTO를 사용하고, Entity를 직접 반환하지 않는다.

---

## 3. DB 스키마 초안

공통 컬럼 (`BaseEntity` 상속): `id (PK, bigint, auto_increment)`, `created_at`, `updated_at`, `deleted_at`

| 테이블 | 주요 컬럼 | 비고 |
|---|---|---|
| `users` | `email`(unique), `password`(BCrypt), `nickname`, `role` | 인증 기본 정보 |
| `refresh_tokens` | `user_id`(FK), `token`, `expires_at` | Refresh Token 저장 방식은 Phase 2에서 확정 |
| `focus_categories` | `user_id`(FK), `name`, `color` | 집중 카테고리 |
| `focus_sessions` | `user_id`(FK), `category_id`(FK), `start_time`, `end_time`, `duration`, `type`(FOCUS/BREAK) | 뽀모도로 세션 기록 |
| `drink_logs` | `user_id`(FK), `type`(WATER/CAFFEINE), `amount`, `logged_at` | 수분/카페인 기록 통합 또는 분리 여부는 구현 전 확인 |
| `diaries` | `user_id`(FK), `content`, `emotion`(enum), `stress_score`, `written_at` | 회고 일기 |

- **N+1 방지**: 연관 엔티티 조회 시 `@EntityGraph` 또는 fetch join 사용. `FetchType.LAZY`를 기본으로 하고 필요한 곳에서만 명시적으로 즉시 로딩한다.
- **Soft Delete**: `deleted_at` 컬럼 활용, `@SQLDelete` + `@Where(clause = "deleted_at is null")` 패턴 적용 검토.
- 위 스키마는 초안이며, 각 Phase 구현 시 세부 컬럼(타입, 제약조건)은 해당 Phase에서 확정한다.

---

## 4. Phase 2 — Authentication

### 4.1 User Entity
- 필드: `id`, `email`, `password`, `nickname`, `role`(예: `ROLE_USER`), `BaseEntity` 상속 필드
- 비밀번호는 `BCryptPasswordEncoder`로 암호화하여 저장.

### 4.2 Spring Security 설정
- `common.config.SecurityConfig`에서 `SecurityFilterChain` 정의:
  - `csrf().disable()` (JWT 기반 stateless API이므로), 세션 정책은 `STATELESS`
  - CORS 설정: 프론트엔드 origin만 허용 (하드코딩 대신 설정값으로 관리)
  - 인증 없이 접근 가능한 경로 (`/api/auth/**`, `/swagger-ui/**` 등) 명시
- `PasswordEncoder` Bean으로 `BCryptPasswordEncoder` 등록

### 4.3 JWT 처리
- `common.security.JwtTokenProvider`: Access Token / Refresh Token 생성 및 검증 담당
  - Access Token 유효기간: 짧게 (예: 30분~1시간, 구현 전 확정)
  - Refresh Token 유효기간: 길게 (예: 7~14일, 구현 전 확정)
- `common.security.JwtAuthenticationFilter`: 매 요청마다 토큰 검증 후 `SecurityContext`에 인증 정보 설정
- Refresh Token 저장 전략: DB(`refresh_tokens` 테이블) 저장 방식 사용, 재발급 시 기존 토큰 무효화(rotate) 여부는 구현 전 확인

### 4.4 인증 API 목록 (초안)
| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/auth/register` | 회원가입 |
| POST | `/api/auth/login` | 로그인 (Access/Refresh Token 발급) |
| POST | `/api/auth/reissue` | Refresh Token으로 Access Token 재발급 |
| POST | `/api/auth/logout` | 로그아웃 (Refresh Token 무효화) |

### 4.5 공통 예외 처리
- `common.exception.GlobalExceptionHandler` (`@RestControllerAdvice`)로 인증 실패, 토큰 만료, 유효성 검증 실패 등을 일관된 에러 응답 포맷으로 처리.
- 에러 응답 포맷(예: `{ code, message, timestamp }`)은 구현 전 확정.

### 4.6 Phase 2 — Definition of Done
- [ ] 회원가입/로그인/토큰 재발급/로그아웃 API 정상 동작 (Swagger 또는 API 테스트로 확인)
- [ ] 비밀번호 BCrypt 암호화 저장 확인
- [ ] 인증 실패 시 일관된 에러 응답 반환
- [ ] Access/Refresh Token 만료 정책 적용 확인
- [ ] Type Error 0, Lint/Build 통과

---

## 5. Phase 3~8 로드맵 요약

### Phase 3 — Dashboard
- Weather API, Quote API 연동 (외부 API 키는 구현 전 사용자에게 확인)
- 현재 시간은 클라이언트 표시가 기본이나, 서버 시간 기준 응답이 필요하면 별도 API 고려
- 외부 API 응답은 캐싱(예: 짧은 TTL) 여부 검토

### Phase 4 — Pomodoro
- `focus_categories` CRUD API
- `focus_sessions` 생성/조회 API (타이머 자체는 프론트 담당, 백엔드는 세션 기록/통계용 저장에 집중)

### Phase 5 — Drink Tracking
- `drink_logs` 생성/조회 API (물/카페인 구분)
- 권장 섭취량 계산 로직: 사용자 정보(체중 등) 기반 계산식은 구현 전 기준 확인

### Phase 6 — Diary
- `diaries` CRUD API
- 감정 선택 enum 정의, 스트레스 점수 계산 로직 위치는 Service 계층에 구현

### Phase 7 — Statistics
- 집중/음료/감정 통계 API (일/주/월 단위 집계)
- 집계 쿼리는 N+1 방지 원칙에 따라 설계 (Projection, `@Query` 집계함수 활용)

### Phase 8 — Deployment
- Docker 이미지화, Nginx 리버스 프록시, OCI Compute/MySQL 배포, GitHub Actions CI/CD
- 상세 배포 가이드는 별도 문서에서 다룬다 (이 문서는 개요만 언급)

---

## 6. 공통 체크리스트 (매 Phase 공통 적용)

**Coding Rules**
- 기존 코드 분석 후 구현, 재사용 가능한 코드 우선 사용
- 동일 기능 중복 구현 금지, 무분별한 전역 상태 생성 금지
- 하드코딩 API URL 금지 (설정값/환경변수로 관리)

**Security Rules**
- Spring Security, JWT, BCrypt, CORS 설정 적용
- SQL Injection(JPA/Parameter Binding 사용으로 방지), XSS, CSRF 방지 고려

**Definition of Done (전체 공통)**
- [ ] Build Success
- [ ] Type Error 0
- [ ] Lint Pass
- [ ] API Test Pass
- [ ] 문서 업데이트 완료 (엔드포인트 변경 시 Swagger/본 문서 반영)

---

## 7. SWE 1.7 실행 지침

1. 이 문서를 **Phase 1부터 순서대로** 진행한다. Phase를 건너뛰지 않는다.
2. 각 Phase 시작 전, 관련 파일을 탐색하고 영향도를 분석한 뒤 구현 전략을 간단히 설명한다.
3. 외부 API 키, 토큰 만료 시간, 에러 응답 포맷 등 **이 문서에 "구현 전 확인/확정"으로 표시된 항목은 반드시 사용자에게 질문**한 뒤 진행한다.
4. Phase 종료 시 해당 Phase의 Definition of Done 체크리스트를 모두 만족했는지 확인한다.
5. 리팩토링이 필요한 경우, UI 동작/API 계약/DB 스키마는 절대 임의로 변경하지 않고, 영향도 분석과 전략을 먼저 제시한 뒤 승인 후 진행한다.
