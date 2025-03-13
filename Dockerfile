FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

COPY .env .env

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]

EXPOSE 8001