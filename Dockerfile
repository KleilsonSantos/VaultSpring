# Etapa 1: Build com Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copia somente os arquivos necessários para baixar dependências
COPY pom.xml .
COPY checkstyle.xml .
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src ./src

# Compila a aplicação
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final com apenas o JAR gerado
FROM eclipse-temurin:17-jre

ARG USER_ID="1001"
ARG GROUP_ID="1001"
RUN groupadd --gid $GROUP_ID appuser && \
    useradd --uid $USER_ID --gid $GROUP_ID -m appuser

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]
