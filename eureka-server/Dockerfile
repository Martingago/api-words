# Usar una imagen base de Java con Maven (OpenJDK + Maven)
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el código fuente del proyecto (incluyendo pom.xml)
COPY pom.xml .
COPY src ./src

# Generar el archivo .jar usando Maven
RUN mvn clean package -DskipTests

# Usar una imagen base de Java (OpenJDK) para la etapa de ejecución
FROM eclipse-temurin:21-jre-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR generado en la etapa anterior
COPY --from=build /app/target/eureka-server-0.0.1-SNAPSHOT.jar eureka-server.jar

# Exponer el puerto en el que corre Eureka
EXPOSE 8761

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]