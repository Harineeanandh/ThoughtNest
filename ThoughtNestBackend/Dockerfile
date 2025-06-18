# ----------- Build Stage -----------

FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


# ----------- Run Stage -----------

FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built jar
COPY --from=builder /app/target/*.jar app.jar

# COPYING GCS CREDENTIALS FILE TO THE CLASSLOADER PATH (same as src/main/resources becomes at runtime)
COPY src/main/resources/thoughtnest-backend-47b1a528e04b.json /app/thoughtnest-backend-47b1a528e04b.json

# copying into same dir where jar expects classpath resources:
COPY src/main/resources/thoughtnest-backend-47b1a528e04b.json /app/target/classes/thoughtnest-backend-47b1a528e04b.json

EXPOSE 8080

ENTRYPOINT ["java", "-Dserver.port=8080", "-Dserver.address=0.0.0.0", "-jar", "app.jar"]
