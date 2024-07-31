FROM ubuntu:latest
LABEL authors="dokyounglee"

ENTRYPOINT ["top", "-b"]

FROM openjdk:17 AS builder

# 작업 디렉토리 설정
WORKDIR /backend

VOLUME /tmp

# dos2unix 설치
RUN microdnf install -y dos2unix

# Gradle wrapper 및 프로젝트 의존성 복사
COPY gradlew .
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/
COPY gradle/wrapper/gradle-wrapper.properties gradle/wrapper/
COPY gradle .gradle/
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# gradlew 파일의 줄 끝 문자를 Unix 스타일로 변환
RUN dos2unix gradlew

RUN chmod +x ./gradlew
# gradlew 실행권한 부여
RUN microdnf install findutils

RUN ./gradlew bootJar
# 애플리케이션 빌드
#RUN gradle clean build --no-daemon
# 애플리케이션 실행을 위한 JAR 파일 복사

FROM openjdk:17

# 작업 디렉토리를 /src로 변경
WORKDIR /src
COPY --from=builder /backend/build/libs/*.jar app.jar


ARG JAR_FILE
RUN mkdir -p /var/log/spring-boot


# 애플리케이션 실행
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
