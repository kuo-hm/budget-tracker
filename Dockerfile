FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn

# Make mvnw executable
RUN chmod +x mvnw

# Copy the source code
COPY src src

# Copy .env file
COPY .env .env

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the port
EXPOSE 8001

# Set environment variables from .env (for docker-compose)
ENV SPRING_CONFIG_IMPORT=optional:file:.env[.properties]

# Run the application
ENTRYPOINT ["java", "-jar", "target/budget-tracker.jar"]