# ----------- Build Stage -----------

# Use the Maven base image with Eclipse Temurin JDK 17 for building the project
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy all project files into the container
COPY . .

# Build the application and skip tests to speed up the process
RUN mvn clean package -DskipTests


# ----------- Run Stage -----------

# Use a lightweight JDK 17 base image to run the built application
FROM eclipse-temurin:17-jdk

# Set the working directory for the runtime container
WORKDIR /app

# Copy the built JAR file from the builder stage into this runtime container
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080 to allow communication with the container
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-Dserver.port=8080", "-Dserver.address=0.0.0.0", "-jar", "app.jar"]

