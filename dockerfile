FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/crypto-recommendation-service-0.0.1-SNAPSHOT.jar crypto-recommendation-service.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/crypto-recommendation-service.jar"]