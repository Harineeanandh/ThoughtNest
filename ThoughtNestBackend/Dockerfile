# ----------- Build Stage -----------
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .

COPY src/main/resources/thoughtnest-backend-47b1a528e04b.json src/main/resources/

RUN mvn clean package -DskipTests


# ----------- Run Stage -----------
FROM eclipse-temurin:17-jdk
WORKDIR /app


COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Dserver.port=8080", "-Dserver.address=0.0.0.0", "-jar", "app.jar"]
