FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=standalone
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]