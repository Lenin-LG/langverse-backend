FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY target/microservice-auth-0.0.1-SNAPSHOT.jar app.jar

COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh

EXPOSE 8091

ENTRYPOINT ["./entrypoint.sh"]