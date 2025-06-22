# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean install -DskipTests

# Stage 2: Run
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build target/bankingsystem-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]