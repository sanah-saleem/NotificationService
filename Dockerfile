# 1) Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q clean package -DskipTests

# 2) Run stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# 👇 THIS is the important fix: copy any jar as app.jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
