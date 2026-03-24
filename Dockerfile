FROM eclipse-temurin:17-jre-ubi10-minimal
# Set working directory inside the container
WORKDIR /app
# Copy the jar file into the container
COPY target/*.jar petroleum.jar
# Run the jar file
ENTRYPOINT ["java", "-jar", "petroleum.jar"]