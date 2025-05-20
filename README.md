# backend

## Swagger UI 접속 방법
* http://localhost:8080/swagger-ui/index.html 로 접속

## 약봉투 OCR API Test 방법
* http://localhost:8080/upload.html
* static에 upload.html 파일 생성 후 notion에서 코드 붙여넣기

## 개요


EveryCare는 **약봉투 OCR**, **의약품 정보 조회**, **병원 정보 필터링** 기능을 통합 제공하는 헬스케어 플랫폼입니다.  
사용자가 스마트폰으로 약봉투 이미지를 업로드하면, OCR로 텍스트를 추출하고, 추출된 약품명을 기반으로 의약품 정보 및 인근 병원 정보를 자동 추천합니다.

<aside>

**개발 환경 세팅 핵심 목표**

- **모듈화 & 확장성**  
  각 기능(이미지 OCR, 의약품 조회, 병원 검색, UI)이 독립 서비스로 구현되어 향후 추가 기능이나 트래픽 증가에도 유연 대응  
- **컨테이너 기반 통합 개발 환경**  
  Flask-OCR 서버, Spring Boot 백엔드, React 프론트엔드를 Docker Compose로 통합 실행하여 일관된 개발/테스트 환경 확보  
- **도메인 중심 구조**  
  OCR, Drug, Hospital, UI 도메인별 책임을 분리하여 유지보수성과 가독성 극대화  
</aside>

## 시스템 구성 및 레포지토리 분리 전략


EveryCare 서비스는 아래 **세 개의 레포지토리**로 구성됩니다:

| 레포지토리       | 역할                                                         |
| --------------- | ------------------------------------------------------------ |
| **flask-server** | EasyOCR 기반 OCR 처리 및 의약품/병원 필터링 로직을 제공하는 마이크로서비스 |
| **backend**      | Spring Boot REST API 서버   |
|                  | - OCR 서버 연동  |
||- 의약품·병원 조회 API 제공  |
||- Swagger UI 문서화                                          |
| **frontend**     | React + Vite + TypeScript UI  |
||- 약봉투 촬영/업로드 화면  |
||- OCR 결과 및 정보 조회 화면  |
||- 사용자 인증/관리 화면                                 |

각 레포지토리는 완전한 독립 빌드·배포가 가능하며, Dockerfile을 통해 컨테이너화되어 있습니다.

## 환경 변수 관리

- **공통 원칙**  
  1. 민감 정보(.env)는 Git에 커밋 금지  
  2. 서비스별 `.env` 파일 분리 관리  
  3. Docker Compose에서 `env_file`로 로드  

| 항목                | 변수 이름 예시                 | 설명                                |
| ------------------- | ----------------------------- | ----------------------------------- |
| **DB 연결 정보**     | MYSQL_HOST, MYSQL_PORT, MYSQL_USER, MYSQL_PASSWORD, MYSQL_DATABASE | Backend가 MySQL 접속 시 사용         |
| **OCR 서버 URL**     | OCR_SERVER_URL                | Backend → Flask-OCR API 호출 URL    |
| **Spring Profile**  | SPRING_PROFILES_ACTIVE        | `dev` / `prod` 구분                 |
| **Frontend API URL** | VITE_API_BASE_URL             | React 앱에서 호출할 Backend 기본 URL |
| **OCR 설정**         | EASYOCR_LANGUAGES, MODEL_PATH  | Flask-OCR 서버 모델 경로/언어 설정   |

```yaml
# backend/.env
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_USER=root
MYSQL_PASSWORD=password
MYSQL_DATABASE=everycare
OCR_SERVER_URL=http://flask-server:5000
SPRING_PROFILES_ACTIVE=dev

# flask-server/.env
EASYOCR_LANGUAGES=ko,en
MODEL_PATH=/app/.EasyOCR/model
```

## 의존성 관리


| 레포지토리            | 주요 프레임워크/라이브러리                                   | 비고                               |
| ---------------- | ------------------------------------------------ | -------------------------------- |
| **flask-server** | Flask, EasyOCR, PyTorch, Requests                | Python 패키지 `requirements.txt` 관리 |
| **backend**      | Spring Boot, Spring Web, Spring Data JPA, Lombok | Gradle(`build.gradle`) 관리        |

## Infra & Docker 환경 구성


* **서비스별 Dockerfile**

  * `flask-server/Dockerfile`
  * `backend/Dockerfile`
  * `frontend/Dockerfile`

* **통합 실행**
  프로젝트 루트에 있는 `docker-compose.yml`로 전체 스택( MySQL + Flask-OCR + Backend + Frontend ) 동시 실행 가능

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: everycare-mysql
    env_file: ./backend/.env
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - '3306:3306'

  flask-server:
    build: ./flask-server
    container_name: everycare-ocr
    env_file: ./flask-server/.env
    ports:
      - '5000:5000'
    depends_on:
      - mysql

  backend:
    build: ./backend
    container_name: everycare-backend
    env_file: ./backend/.env
    ports:
      - '8080:8080'
    depends_on:
      - mysql
      - flask-server

  frontend:
    build: ./frontend
    container_name: everycare-frontend
    env_file: ./frontend/.env
    ports:
      - '3000:3000'
    depends_on:
      - backend

volumes:
  mysql_data:
```

## 패키지 구조

### flask-server

```
flask-server/
├── .EasyOCR/           # OCR 모델 파일
├── fonts/              # 테스트용 글꼴
├── app.py              # Flask 애플리케이션 진입점
├── drug_gpt.py         # GPT 기반 의약품 정보 로직
├── drug_search_api.py  # 외부 의약품 API 연동 모듈
├── filter_drug.py      # 의약품명 필터링 유틸
├── filter_hospital.py  # 병원 정보 필터링 유틸
├── perform_ocr.py      # OCR 수행 유틸
├── ocr_test.py         # 로컬 OCR 테스트 스크립트
├── requirements.txt    # Python 라이브러리 목록
└── Dockerfile
```

### backend

```
backend/
├── src/
│   └── main/
│       └── java/com/everycare/backend
│           ├── config/       # Swagger, CORS 등 설정
│           ├── controller/   # REST 컨트롤러
│           ├── service/      # 비즈니스 로직
│           ├── repository/   # JPA 리포지토리
│           ├── dto/          # 요청/응답 객체
│           └── entity/       # JPA 엔티티
├── build.gradle
├── Dockerfile
├── docker-compose.yml
└── HELP.md
```
