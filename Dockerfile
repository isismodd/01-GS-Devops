# Estágio de build
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /build

# Copiar arquivos do projeto
COPY pom.xml .
COPY src ./src

# Baixar dependências e fazer build
RUN mvn clean package -DskipTests

# Estágio final
FROM eclipse-temurin:17-jre-alpine

# Criar usuário não privilegiado (REQUISITO)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Definir diretório de trabalho (REQUISITO)
WORKDIR /app

# Copiar o JAR gerado
COPY --from=build /build/target/*.jar app.jar

# Variável de ambiente (REQUISITO)
ENV DB_HOST=db-561497
ENV DB_PORT=1521
ENV DB_SERVICE=XE

# Expor porta (REQUISITO)
EXPOSE 8080

# Mudar para usuário não privilegiado (REQUISITO)
USER appuser

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
