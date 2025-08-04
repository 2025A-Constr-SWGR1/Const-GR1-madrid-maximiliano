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

# Permite pasar argumentos al contenedor como parámetros a Java
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/urandom -jar app.jar $0 $@"]

