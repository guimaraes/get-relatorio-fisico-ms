FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests --no-transfer-progress
FROM eclipse-temurin:21-jdk
WORKDIR /app
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseContainerSupport -Xms512m -Xmx1024m"
COPY --from=builder /app/target/report-gateway-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["sh", "-c", "sleep 10 && java $JAVA_OPTS -jar app.jar"]
