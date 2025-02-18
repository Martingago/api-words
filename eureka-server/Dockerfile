# Usar una imagen base de Java (OpenJDK)
FROM eclipse-temurin:21-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR generado por Maven/Gradle
COPY target/eureka-server-0.0.1-SNAPSHOT.jar eureka-server.jar

# Exponer el puerto en el que corre Eureka
EXPOSE 8761

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]