# Etapa de construcción
FROM maven:3.8.6-openjdk-18-slim AS build
WORKDIR /app

# Copiar solo el pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y construir la aplicación
COPY src ./src
RUN mvn clean package -DskipTests

RUN ls -l target/

# Etapa de ejecución
FROM eclipse-temurin:21-alpine
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/target/*.war app.war

# Exponer el puerto
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.war"]