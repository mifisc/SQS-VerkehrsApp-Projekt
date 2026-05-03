FROM maven:3.9.14-eclipse-temurin-25 AS builder
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN chmod +x mvnw
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]