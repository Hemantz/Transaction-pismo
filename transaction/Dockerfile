# Use official OpenJDK image
FROM openjdk:21-jdk

# Set working directory
WORKDIR /app

# Copy JAR from build output to container
COPY target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
