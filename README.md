# Backend Internship Assignment

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