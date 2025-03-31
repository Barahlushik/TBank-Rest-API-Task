FROM maven:3.8.6-eclipse-temurin-17 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/src/main/resources/ /app/resources/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]