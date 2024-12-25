# Stage 1: Build the application
FROM maven:3.8.5-openjdk-21 AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:21
WORKDIR /app
COPY --from=build /app/target/be-android-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
