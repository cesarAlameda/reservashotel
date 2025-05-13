# Stage 1: Build con Maven + Eclipse Temurin JDK 21
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiamos pom y src
COPY pom.xml .
COPY src ./src

# Compilamos sin tests, forzando UTF-8 para evitar MalformedInputException
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# Stage 2: Runtime con Eclipse Temurin JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el JAR generado
COPY --from=builder /app/target/reservashotel-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9525
ENTRYPOINT ["java","-jar","app.jar"]
