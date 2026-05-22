# Estágio 1: Compilação (Build)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia os arquivos de configuração do Maven e o código fonte
COPY pom.xml .
COPY src ./src

# Compila o projeto gerando o arquivo .jar (ignorando os testes para agilizar)
RUN mvn clean package -DskipTests

# Estágio 2: Execução (Runtime)
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia apenas o arquivo .jar compilado do estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Define variáveis de ambiente com valores padrão (Podem ser alteradas ao rodar o contêiner)
ENV PORT=8080
ENV DB_URL=jdbc:postgresql://localhost:5432/minimundo
ENV DB_USER=admin
ENV DB_PASS=admin

# Expõe a porta que a aplicação vai rodar
EXPOSE ${PORT}

# Comando para iniciar a aplicação injetando as variáveis do ambiente
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT} --spring.datasource.url=${DB_URL} --spring.datasource.username=${DB_USER} --spring.datasource.password=${DB_PASS}"]