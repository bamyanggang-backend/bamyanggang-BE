# Base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the executable jar file into the container
COPY build/libs/bamyanggang-0.0.1-SNAPSHOT.jar app.jar

# Expose ports for Blue-Green deployment (8080 for blue, 8081 for green)
EXPOSE 8080
EXPOSE 8081

# Entry point for the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
