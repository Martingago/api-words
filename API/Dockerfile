# Etapa de construcción
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiar solo el pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y construir la aplicación
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM gcr.io/distroless/java21
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/target/*.war app.war

# Exponer el puerto
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.war"]