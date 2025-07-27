# 1. Gradle로 빌드
FROM gradle:8.7.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# 2. JAR 실행 (경량 이미지 사용)
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# 환경변수 (Render에서 override 가능)
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
