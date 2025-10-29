#!/bin/sh

# Esperar a config-server
echo " Esperando a config-server en http://config-server:8888/actuator/health ..."
until curl -s http://config-server:8888/actuator/health | grep '"status":"UP"' > /dev/null
do
  echo " Config server aún no responde. Reintentando..."
  sleep 10
done
echo " Config server está arriba."

# Esperar a Eureka
echo "Esperando a Eureka en http://msvc-eureka:8761/actuator/health ..."
until curl -s http://msvc-eureka:8761/actuator/health | grep '"status":"UP"' > /dev/null
do
  echo " Eureka aún no responde. Reintentando..."
  sleep 10
done
echo "Eureka está arriba."

# Iniciar el microservicio
echo "Iniciando aplicación..."
exec java -jar app.jar
