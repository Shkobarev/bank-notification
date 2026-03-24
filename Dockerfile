FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY bank-repository/pom.xml bank-repository/
COPY bank-service/pom.xml bank-service/
COPY bank-api/pom.xml bank-api/

COPY bank-repository/src bank-repository/src/
COPY bank-service/src bank-service/src/
COPY bank-api/src bank-api/src/

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/bank-api/target/bank-api-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]