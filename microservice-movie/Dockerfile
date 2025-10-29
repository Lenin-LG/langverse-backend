FROM eclipse-temurin:21-jdk
WORKDIR /app

# 🔧 Instalar FFmpeg
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copiar el .jar compilado
COPY target/microservice-movie-0.0.1-SNAPSHOT.jar app.jar
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh
EXPOSE 8092

ENTRYPOINT ["./entrypoint.sh"]
