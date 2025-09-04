# Use an official OpenJDK runtime as a parent image
#FROM openjdk:17-jdk-alpine
FROM maven:3.8.5-openjdk-17 AS Build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build target/RuleEngineBackend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# Command to run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]

