# Build stage
FROM gradle:9.4.1-jdk21 AS build

WORKDIR /app

# Copy gradle wrapper files
COPY gradlew settings.gradle build.gradle gradle/ ./

# Download dependencies (cached layer)
RUN gradle build -x test --no-daemon || true

# Copy source code
COPY src ./src

# Build the application
RUN gradle build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8067

ENTRYPOINT ["java", "-jar", "app.jar"]
