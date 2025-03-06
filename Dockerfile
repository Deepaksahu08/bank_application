# Stage 1: Build the application
FROM openjdk:19-jdk AS build
WORKDIR /app

# Copy only necessary files first to leverage caching
COPY pom.xml mvnw .mvn/ ./
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline --no-transfer-progress

# Now copy the rest of the source code
COPY src/ src/
RUN ./mvnw clean package -DskipTests --no-transfer-progress

# Stage 2: Create the final image
FROM openjdk:19-jdk-slim
WORKDIR /app
VOLUME /tmp

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Expose the application port
EXPOSE 8990