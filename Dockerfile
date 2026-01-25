# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src

# Skip tests during Docker build - tests are already validated in CI/CD pipeline
# This speeds up Docker image builds as tests run in GitHub Actions before deployment
RUN mvn clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
