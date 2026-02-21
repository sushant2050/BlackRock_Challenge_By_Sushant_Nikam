# Build command: docker build -t blk-hacking-ind-sushant-nikam .
FROM eclipse-temurin:17-jdk-focal


WORKDIR /app


COPY target/*.jar app.jar


EXPOSE 5477

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=5477"]