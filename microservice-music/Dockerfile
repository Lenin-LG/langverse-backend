FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY target/microservice-music-0.0.1-SNAPSHOT.jar app.jar
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh
EXPOSE 8093


ENTRYPOINT ["./entrypoint.sh"]
