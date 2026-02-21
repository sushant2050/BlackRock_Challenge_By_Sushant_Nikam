# Build command: docker build -t blk-hacking-ind-sushant-nikam .
FROM eclipse-temurin:21-jdk-jammy


WORKDIR /app


COPY target/*.jar app.jar


EXPOSE 5477

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=5477"]
