# Use a base image with OpenJDK 17
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from target
COPY target/budget-tracker-0.0.1-SNAPSHOT.jar /app/budget-tracker.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "budget-tracker.jar"]
