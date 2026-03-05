# Usamos Maven para compilar la aplicación (esta sigue igual)
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline  # Descarga dependencias
COPY src ./src
RUN mvn clean package -DskipTests

# Cambiamos esta parte - imagen oficial de Eclipse Temurin (recomendada por Spring)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]