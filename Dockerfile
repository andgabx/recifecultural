# Build and run without a local JDK: only Docker is required on the host.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY . .
RUN chmod +x mvnw \
    && ./mvnw -pl apresentacao-backend -am package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/apresentacao-backend/target/recifecultural-apresentacao-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
