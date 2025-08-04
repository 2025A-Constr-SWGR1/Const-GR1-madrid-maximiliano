### Cambios realizados por Kevin – Dockerfile

#### Modificación del Dockerfile

Se reemplazó el Dockerfile original que requería compilar fuera del contenedor por un build multietapa, que permite compilar y ejecutar dentro de Docker...

```dockerfile
# Dockerfile
# Etapa 1: Construcción (Build Stage)
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Runtime Stage)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom", "-jar", "app.jar"]

```
