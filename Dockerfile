# 도커 이미지 지정
FROM eclipse-temurin:17-jdk

# build/libs/ 에 있는 jar 파일을 JAR_FILE 변수에 저장
ARG JAR_FILE=build/libs/*.jar

# JAR_FILE 복사
COPY ${JAR_FILE} app.jar

# Docker 컨테이너가 시작될 때 /app.jar 실행
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","/app.jar"]