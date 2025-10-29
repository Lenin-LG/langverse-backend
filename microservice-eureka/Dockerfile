FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiar el jar generado
COPY target/microservice-eureka-0.0.1-SNAPSHOT.jar app.jar
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh
EXPOSE 8761

ENTRYPOINT ["./entrypoint.sh"]
