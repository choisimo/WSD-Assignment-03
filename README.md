# WSD Assignment 03

## 프로젝트 개요

**WSD Assignment 03**은 다양한 기능을 제공하는 RESTful API 서비스로, 사용자 인증, 댓글 관리, 북마크, 검색 기록 등과 같은 다양한 서비스를 지원합니다. Spring Boot와 Swagger를 사용해 개발되었으며, 데이터베이스로 MariaDB와 Redis를 사용합니다.

---

## API 명세

### 1. 사용자 인증

#### 회원가입
- **Endpoint**: `/auth/register`
- **Method**: POST
- **설명**: 사용자를 등록합니다.
- **Request Body**:
  ```json
  {
    "username": "example",
    "email": "example@example.com",
    "password": "password123"
  }
  ```
- **Response**: 회원가입 성공 또는 실패 상태.

#### 로그인
- **Endpoint**: `/auth/login`
- **Method**: POST
- **설명**: ID와 비밀번호를 통해 로그인을 요청합니다.

#### 프로필 업데이트
- **Endpoint**: `/auth/profile`
- **Method**: PUT
- **설명**: 사용자의 프로필 정보를 업데이트합니다.
- **Request Body**:
  ```json
  {
    "username": "new_name",
    "email": "new_email@example.com"
  }
  ```

#### 회원 탈퇴
- **Endpoint**: `/auth/withdraw`
- **Method**: DELETE
- **설명**: 회원 탈퇴를 요청합니다.

---

### 2. 댓글 관리

#### 댓글 작성
- **Endpoint**: `/api/protected/comments`
- **Method**: POST
- **설명**: 새로운 댓글을 작성합니다.
- **Request Body**:
  ```json
  {
    "postId": 1,
    "content": "This is a comment."
  }
  ```

#### 댓글 조회
- **Endpoint**: `/api/protected/comments/{id}`
- **Method**: GET
- **설명**: 특정 게시글의 댓글 목록을 조회합니다.
- **Query Parameters**:
  - `page` (int, required): 페이지 번호
  - `size` (int, required): 페이지 크기
  - `sort` (String, required): 정렬 방식 (`asc` 또는 `desc`).

#### 특정 댓글 조회
- **Endpoint**: `/api/protected/comments/{id}/{cid}`
- **Method**: GET
- **설명**: 특정 댓글을 조회합니다.

#### 댓글 삭제
- **Endpoint**: `/api/protected/comments/{id}`
- **Method**: DELETE
- **설명**: 댓글을 삭제합니다.

---

### 3. 알림 관리

#### 알림 생성
- **Endpoint**: `/api/protected/notification`
- **Method**: POST
- **설명**: 새 알림을 생성합니다.

#### 알림 조회
- **Endpoint**: `/api/protected/notification`
- **Method**: GET
- **설명**: 사용자의 모든 알림을 조회합니다.

#### 특정 알림 조회
- **Endpoint**: `/api/protected/notification/{id}`
- **Method**: GET
- **설명**: 특정 알림을 조회합니다.

#### 알림 삭제
- **Endpoint**: `/api/protected/notification/{id}`
- **Method**: DELETE
- **설명**: 특정 알림을 삭제합니다.

---

### 4. 검색 기록 관리

#### 검색 기록 조회
- **Endpoint**: `/api/protected/search-history`
- **Method**: GET
- **설명**: 사용자의 모든 검색 기록을 조회합니다.

#### 검색 기록 삭제
- **Endpoint**: `/api/protected/search-history`
- **Method**: DELETE
- **설명**: 사용자의 모든 검색 기록을 삭제합니다.

---

## 기술 스택

- **Backend**: Spring Boot 3.x
- **Database**: MariaDB, Redis
- **Authentication**: Spring Security with JWT
- **Documentation**: Swagger UI
- **Build Tool**: Gradle

## 프로젝트 구조

```
.
├── .gradle/                  # Gradle 빌드 도구 설정 및 캐시 파일
├── .idea/                   # IntelliJ IDEA 설정 파일
├── build/                   # Gradle 빌드 산출물
│   ├── classes/             # 컴파일된 클래스 파일
│   ├── resources/           # 리소스 파일
│   ├── reports/             # 테스트 리포트
│   └── tmp/                 # 임시 파일
├── gradle/                  # Gradle 래퍼 설정 파일
├── src/
│   ├── main/                # 메인 소스 코드
│   │   ├── java/com/nodove/WSD_Assignment_03/
│   │   │   ├── configuration/  # 설정 관련 코드
│   │   │   │   ├── QueryDsl/
│   │   │   │   ├── redis/
│   │   │   │   ├── security/
│   │   │   │   ├── smtp/
│   │   │   │   ├── swagger/
│   │   │   │   ├── token/
│   │   │   │   │   ├── components/
│   │   │   │   │   └── principalDetails/
│   │   │   │   └── utility/
│   │   │   │       └── password/
│   │   │   ├── constants/         # 상수 정의
│   │   │   ├── controller/        # API 컨트롤러
│   │   │   ├── domain/            # 엔티티 클래스
│   │   │   │   └── SaramIn/
│   │   │   ├── dto/               # DTO 클래스
│   │   │   │   ├── ApiResponse/
│   │   │   │   ├── Crawler/
│   │   │   │   │   ├── BookMark/
│   │   │   │   │   └── Comment/
│   │   │   │   └── users/
│   │   │   ├── filter/            # 필터 클래스
│   │   │   ├── repository/        # 레포지토리 인터페이스
│   │   │   │   └── CrawlerRepository/
│   │   │   │       ├── Bookmark/
│   │   │   │       └── JobPosting/
│   │   │   └── service/           # 서비스 레이어
│   │   └── resources/             # 리소스 파일 (application.yml 등)
│   └── test/                      # 테스트 코드
└── build.gradle                   # Gradle 빌드 설정 파일
```

---

## 실행 방법

### 1. 프로젝트 클론

```bash
git clone https://github.com/nodove/wsd-assignment-03.git
cd wsd-assignment-03
```

### 2. 환경 변수 설정

`src/main/resources/application.yml` 파일을 열고 다음 값을 설정합니다:

```yaml
spring:
  datasource:
    url: jdbc:mysql://<DB_HOST>:<DB_PORT>/<DB_NAME>
    username: <DB_USERNAME>
    password: <DB_PASSWORD>
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 4. Swagger 문서 접근

- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- [http://jcloud.nodove.com:10026/swagger-ui/index.html](http://jcloud.nodove.com:10026/swagger-ui/index.html)

---


