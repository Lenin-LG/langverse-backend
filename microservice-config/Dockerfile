FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia el .jar ya compilado
COPY target/microservice-config-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
