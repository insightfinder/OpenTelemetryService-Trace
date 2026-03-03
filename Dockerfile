# Build stage
FROM maven:3-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/traceserver-*.jar /app/traceserver.jar

# Copy application configuration files
COPY src/main/resources/*.yml /app/

# Expose the default gRPC port (adjust if needed)
EXPOSE 50051

# Run the application
ENTRYPOINT ["java", "-jar", "/app/traceserver.jar"]
