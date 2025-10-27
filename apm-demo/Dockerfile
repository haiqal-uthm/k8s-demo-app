# Start from OpenJDK 21 (you can change if needed)
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file from build context
COPY target/apm-demo-1.0-SNAPSHOT.jar app.jar

# Expose app port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
