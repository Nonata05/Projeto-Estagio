# Estágio 1: Build do JAR com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia os arquivos do Maven e dependências primeiro (otimiza o cache de build)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e gera o build do JAR ignorando os testes unitários
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Rodar a aplicação Java
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o arquivo .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Porta padrão geralmente usada pelo Spring Boot (8080)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
