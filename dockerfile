FROM eclipse-temurin:21-jdk
RUN mkdir -p /app/data/prices
ARG JAR_FILE=target/crypto-recommendation-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} /app/app.jar
COPY src/main/resources/data/prices /app/data/prices

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
