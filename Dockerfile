# Usando imagem leve do Java 17
FROM eclipse-temurin:17-jdk

# Define diretório de trabalho
WORKDIR /app

# Copia o JAR gerado pelo Maven
COPY target/*.jar app.jar

# Define porta de escuta (Render usa isso)
ENV PORT=8080
EXPOSE 8080

# Executa a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
