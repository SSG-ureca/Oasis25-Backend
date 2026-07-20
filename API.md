# OASIS-25 Backend API 문서

- Base URL: `http://localhost:8080`
- 인증이 필요한 API는 `Authorization: Bearer {accessToken}` 헤더를 포함해야 합니다.
- 현재 Controller는 `ResponseEntity<DTO>` 형태로 DTO를 직접 반환합니다.

---

# 1. 인증 API

---

## 회원가입

### POST

```text
/api/auth/register
```

### Request

```json
{
  "email": "test@test.com",
  "password": "password123",
  "nickname": "동현"
}
```

### Response

```json
{
  "id": 1,
  "email": "test@test.com",
  "nickname": "동현",
  "role": "ROLE_USER"
}
```

---

## 로그인

### POST

```text
/api/auth/login
```

### Request

```json
{
  "email": "test@test.com",
  "password": "password123"
}
```

### Response

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## 토큰 재발급

### POST

```text
/api/auth/reissue
```

### Request

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

### Response

```json
{
  "accessToken": "new-jwt-access-token",
  "refreshToken": "new-jwt-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## 로그아웃

### POST

```text
/api/auth/logout
```

### Request

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

### Response

`200 OK`

---

# 2. 일기 API

---

## 일기 생성

### POST

```text
/api/diaries
```

### Request

```json
{
  "diaryDate": "2026-07-20",
  "content": "오늘은 좋은 하루였다.",
  "emotionScore": 4
}
```

### Response

```json
{
  "id": 1,
  "diaryDate": "2026-07-20",
  "content": "오늘은 좋은 하루였다.",
  "aiSummary": null,
  "emotionScore": 4,
  "createdAt": "2026-07-20T10:00:00",
  "updatedAt": "2026-07-20T10:00:00"
}
```

---

## 일기 조회

### GET

```text
/api/diaries?date=2026-07-20
```

### Response

```json
{
  "id": 1,
  "diaryDate": "2026-07-20",
  "content": "오늘은 좋은 하루였다.",
  "aiSummary": null,
  "emotionScore": 4,
  "createdAt": "2026-07-20T10:00:00",
  "updatedAt": "2026-07-20T10:00:00"
}
```

---

## 일기 수정

### PUT

```text
/api/diaries/{id}
```

### Request

```json
{
  "content": "오늘은 정말 좋은 하루였다.",
  "emotionScore": 5
}
```

### Response

```json
{
  "id": 1,
  "diaryDate": "2026-07-20",
  "content": "오늘은 정말 좋은 하루였다.",
  "aiSummary": null,
  "emotionScore": 4,
  "createdAt": "2026-07-20T10:00:00",
  "updatedAt": "2026-07-20T12:00:00"
}
```

---

## 일기 삭제

### DELETE

```text
/api/diaries/{id}
```

### Response

`200 OK`

---

## AI 요약 생성

### POST

```text
/api/diaries/{id}/ai-summary
```

### Response

```json
{
  "id": 1,
  "diaryDate": "2026-07-20",
  "content": "오늘은 좋은 하루였다.",
  "aiSummary": "오늘은 긍정적인 하루였습니다.",
  "emotionScore": 4,
  "createdAt": "2026-07-20T10:00:00",
  "updatedAt": "2026-07-20T12:00:00"
}
```

---

# 3. 피드백 API

---

## 피드백 생성

### POST

```text
/api/feedbacks
```

### Request

```json
{
  "isGood": true,
  "content": "좋은 피드백입니다."
}
```

### Response

```json
{
  "id": 1,
  "isGood": true,
  "content": "좋은 피드백입니다.",
  "createdAt": "2026-07-20T10:00:00"
}
```

---

# 4. 집중 카테고리 API

---

## 집중 카테고리 생성

### POST

```text
/api/pomodoro/categories
```

### Request

```json
{
  "name": "업무",
  "color": "#FF5733"
}
```

### Response

```json
{
  "id": 1,
  "name": "업무",
  "color": "#FF5733",
  "createdAt": "2026-07-20T10:00:00"
}
```

---

## 집중 카테고리 전체 조회

### GET

```text
/api/pomodoro/categories
```

### Response

```json
[
  {
    "id": 1,
    "name": "업무",
    "color": "#FF5733",
    "createdAt": "2026-07-20T10:00:00"
  }
]
```

---

## 집중 카테고리 삭제

### DELETE

```text
/api/pomodoro/categories/{id}
```

### Response

`200 OK`

---

# 5. 뽀모도로 로그 API

---

## 뽀모도로 로그 생성

### POST

```text
/api/pomodoro
```

### Request

```json
{
  "categoryId": 1,
  "focusMinutes": 25,
  "breakMinutes": 5,
  "weatherCondition": "맑음",
  "temperature": 24.5
}
```

### Response

```json
{
  "id": 1,
  "categoryId": 1,
  "categoryName": "업무",
  "focusMinutes": 25,
  "breakMinutes": 5,
  "completed": false,
  "endTime": null,
  "weatherCondition": "맑음",
  "temperature": 24.5,
  "createdAt": "2026-07-20T10:00:00"
}
```

---

## 뽀모도로 로그 완료

### PATCH

```text
/api/pomodoro/{id}/complete
```

### Response

```json
{
  "id": 1,
  "categoryId": 1,
  "categoryName": "업무",
  "focusMinutes": 25,
  "breakMinutes": 5,
  "completed": true,
  "endTime": "2026-07-20T10:25:00",
  "weatherCondition": "맑음",
  "temperature": 24.5,
  "createdAt": "2026-07-20T10:00:00"
}
```

---

## 뽀모도로 로그 날짜별 조회

### GET

```text
/api/pomodoro?date=2026-07-20
```

### Response

```json
[
  {
    "id": 1,
    "categoryId": 1,
    "categoryName": "업무",
    "focusMinutes": 25,
    "breakMinutes": 5,
    "completed": true,
    "endTime": "2026-07-20T10:25:00",
    "weatherCondition": "맑음",
    "temperature": 24.5,
    "createdAt": "2026-07-20T10:00:00"
  }
]
```

---

# 6. 뽀모도로 프리셋 API

---

## 뽀모도로 프리셋 전체 조회

### GET

```text
/api/pomodoro/presets
```

### Response

```json
[
  {
    "id": 1,
    "name": "기본 프리셋",
    "focusMinutes": 25,
    "breakMinutes": 5,
    "isDefault": false,
    "createdAt": "2026-07-20T10:00:00"
  }
]
```

---

## 뽀모도로 프리셋 생성

### POST

```text
/api/pomodoro/presets
```

### Request

```json
{
  "name": "기본 프리셋",
  "focusMinutes": 25,
  "breakMinutes": 5
}
```

### Response

```json
{
  "id": 1,
  "name": "기본 프리셋",
  "focusMinutes": 25,
  "breakMinutes": 5,
  "isDefault": false,
  "createdAt": "2026-07-20T10:00:00"
}
```

---

## 뽀모도로 프리셋 수정

### PATCH

```text
/api/pomodoro/presets/{id}
```

### Request

```json
{
  "name": "업무용 프리셋",
  "focusMinutes": 50,
  "breakMinutes": 10
}
```

### Response

```json
{
  "id": 1,
  "name": "업무용 프리셋",
  "focusMinutes": 50,
  "breakMinutes": 10,
  "isDefault": false,
  "createdAt": "2026-07-20T10:00:00"
}
```

---

## 뽀모도로 프리셋 삭제

### DELETE

```text
/api/pomodoro/presets/{id}
```

### Response

`204 No Content`

---

# 7. 통계 API

---

## 날씨별 집중 통계 조회

### GET

```text
/api/stats/weather
```

### Response

```json
[
  {
    "weatherCondition": "맑음",
    "totalSessions": 10,
    "completedSessions": 8,
    "completionRate": 0.8,
    "avgFocusMinutes": 25.5
  }
]
```

---

# 8. 물/카페인 기록 API

---

## 물/카페인 기록 생성

### POST

```text
/api/water-caffeine
```

### Request

```json
{
  "logType": "WATER",
  "amount": 250
}
```

### Response

```json
{
  "id": 1,
  "logType": "WATER",
  "amount": 250,
  "createdAt": "2026-07-20T10:00:00"
}
```

---

## 물/카페인 기록 날짜별 조회

### GET

```text
/api/water-caffeine?date=2026-07-20
```

### Response

```json
[
  {
    "id": 1,
    "logType": "WATER",
    "amount": 250,
    "createdAt": "2026-07-20T10:00:00"
  }
]
```

---

## 물/카페인 합계 조회

### GET

```text
/api/water-caffeine/summary?date=2026-07-20&type=WATER
```

### Response

```json
500
```

---

## 물/카페인 기록 삭제

### DELETE

```text
/api/water-caffeine/{id}
```

### Response

`200 OK`

---
