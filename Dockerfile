# 1. Java 21 + Debian 기반 이미지 사용
FROM eclipse-temurin:21-jdk

# 2. ffmpeg 설치
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean

# 3. 빌드한 JAR 파일을 컨테이너에 복사
ARG JAR_FILE=build/libs/see-write-say_app-0.0.1-SNAPSHOT.jar
COPY build/libs/see-write-say_app-0.0.1-SNAPSHOT.jar app.jar

# 4. 포트 오픈
EXPOSE 8080

# 5. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]