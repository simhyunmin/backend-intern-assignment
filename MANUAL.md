## 기술 스택
- **Language**: Kotlin 1.9.x
- **Framework**: Spring Boot 3.x
- **Build**: Gradle (Kotlin DSL)
- **Database**: H2 (In-memory, Local용), PostgreSQL (Prod용)
- **Environment**: `dotenv-kotlin`을 이용한 환경변수 관리

---

## 빠른 실행 (Quick Start)
별도의 설정 없이 바로 실행하여 로컬 환경(H2 + Mock AI)에서 테스트할 수 있습니다.

### 필수 조건
- JDK 17+
- Git

### 1. 실행 (Local Profile)
H2 데이터베이스와 Mock AI 클라이언트가 활성화됩니다. (`.env` 파일 불필요)
```bash
git clone https://github.com/simhyunmin/backend-intern-assignment.git
```
```bash
cd backend-intern-assignment
```
```bash
./gradlew bootRun
```

## 챗봇 API 시연 가이드

### 1. 시연을 위한 준비 (Getting Started)

시연은 API 명세를 시각적으로 확인하고 직접 호출할 수 있는 **Swagger UI** 환경에서 진행됩니다.

#### **Swagger UI 접속**

*   웹 브라우저를 통해 아래 주소로 접속합니다.
*   **URL:** `http://localhost:8080/swagger-ui/index.html`

#### **시연용 계정 정보**

시연을 위해 두 가지 역할의 계정이 준비되어 있습니다.

*   **일반 사용자 계정:** 직접 회원가입하여 생성합니다. (아래 시나리오 3-1 참고)
*   **관리자 계정:**
    *   **Email:** `admin@example.com`
    *   **Password:** `admin1234`
    *   **역할:** 일반 사용자의 기능은 물론, 서비스 전체의 활동 기록을 조회하고 데이터를 분석하는 등, 운영 및 관리를 위한 강력한 기능을 사용할 수 있습니다.

### 2. 핵심 기능 시연 시나리오 (Walkthrough)

아래 시나리오에 따라 기능을 순서대로 체험해보시는 것을 권장합니다.

#### **Step 1. 일반 사용자 회원가입**

가장 먼저, 일반 사용자로 서비스를 이용하기 위해 새 계정을 생성합니다.

1.  Swagger UI에서 `User` > `POST /members/signup` API를 엽니다.
2.  `Request body`에 아래와 같이 이름, 이메일, 비밀번호를 입력하고 `Execute` 버튼을 클릭합니다.
    ```json
    {
      "email": "test-user@example.com",
      "password": "password1234",
      "name": "김시연"
    }
    ```

#### **Step 2. 로그인 및 인증 토큰 획득**

API를 사용하기 위해 필요한 인증 토큰(JWT)을 발급받습니다.

1.  `User` > `POST /members/login` API를 엽니다.
2.  방금 가입한 계정 정보로 `Request body`를 작성하고 `Execute`합니다.
3.  응답(Response)으로 받은 `accessToken` 값을 복사합니다. 이 토큰이 사용자를 인증하는 열쇠입니다.
4.  화면 우측 상단의 `Authorize` 버튼을 클릭하고, `Value` 필드에 **`Bearer [복사한 토큰]`** 형식으로 붙여넣은 후 `Authorize` 버튼을 누릅니다. (예: `Bearer eyJhbGciOi...`)

#### **Step 3. AI 챗봇과 대화하기**

이제 인증이 필요한 챗봇 API를 사용할 수 있습니다.

1.  `Chat` > `POST /chats` API를 엽니다.
2.  `Request body`에 질문을 입력하고 `Execute`합니다.
    ```json
    {
      "question": "Sionic AI의 RAG 기술에 대해 알려주세요."
    }
    ```
3.  정상적으로 AI의 답변이 반환되는 것을 확인합니다.

#### **Step 4. 자동 문맥 관리 기능 확인**

저희 챗봇 API는 사용자의 편의를 위해 **대화의 문맥을 자동으로 관리**하는 기능을 갖추고 있습니다.

*   **기능 설명:** 사용자가 짧은 시간 내에 나누는 대화는 하나의 주제(스레드)로 자동 그룹화됩니다. 하지만 **마지막 대화로부터 30분이 지난 후** 새로운 질문을 하면, AI는 이를 새로운 주제로 인지하고 대화를 시작합니다. 이는 사용자가 여러 주제를 넘나들며 대화하더라도 문맥이 섞이지 않도록 하여 답변의 정확성을 높이는 중요한 기능입니다.

*   **시연 방법:** `Step 3`을 여러 번 반복하여 대화를 나누면, `GET /chats` API를 통해 모든 대화가 하나의 스레드에 묶여있는 것을 확인할 수 있습니다.

#### **Step 5. 관리자 기능 시연**

이제 관리자 계정으로 로그인하여 서비스 전체를 조망하는 강력한 기능을 체험할 수 있습니다.

1.  화면 우측 상단 `Authorize` 버튼을 눌러 기존 인증 정보를 `Logout` 합니다.
2.  `admin@example.com` 계정으로 `Step 2`와 동일하게 로그인하여 새로운 `accessToken`을 발급받고, 다시 `Authorize`를 수행합니다.
3.  `Admin` > `GET /admin/reports/activity` API를 `Execute`하여, 최근 24시간 동안의 가입자 수와 대화 생성 수를 확인합니다.
4.  `Admin` > `GET /admin/reports/chats/csv` API를 `Execute`하고 `Download file`을 클릭하여, 모든 사용자의 대화 기록이 담긴 CSV 보고서를 다운로드합니다.

### 3. 주의사항 (Precaution)

*   **데이터 초기화:** 본 시연 환경은 빠른 테스트와 반복적인 시연을 위해, 서버가 재시작될 때마다 모든 대화 및 사용자 정보가 초기화되는 인메모리 데이터베이스(H2)를 사용하고 있습니다.
*   **시뮬레이션 응답:** 현재 챗봇의 답변은 실제 AI 모델이 아닌, 미리 정의된 응답을 반환하는 시뮬레이션 모드(Mock)로 동작합니다. 이는 외부 AI 서비스의 API Key 보안 및 비용 문제를 고려한 조치입니다.

