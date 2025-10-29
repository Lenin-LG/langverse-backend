
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY target/microservice-report-0.0.1-SNAPSHOT.jar app.jar
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh

# Opcional: solo si quieres asegurar herramientas de build
RUN apt-get update && apt-get install -y build-essential

EXPOSE 8094

ENTRYPOINT ["./entrypoint.sh"]
