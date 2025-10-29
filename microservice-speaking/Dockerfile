FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY target/microservice-speaking-0.0.1-SNAPSHOT.jar app.jar
# Copiar el script de arranque
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh
EXPOSE 8094


ENTRYPOINT ["./entrypoint.sh"]
