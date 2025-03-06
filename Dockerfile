# Stage 1: Build the application using Java 21
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven configuration and source files
COPY pom.xml .
COPY src src

# Copy Maven wrapper and configuration
COPY mvnw .
COPY .mvn .mvn

# Set execution permissions for Maven wrapper
RUN chmod +x ./mvnw

# Build the application without running tests
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final Docker image with the built application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set the entry point for the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose the application port
EXPOSE 8990