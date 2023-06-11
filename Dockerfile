# Define a imagem base do Docker
FROM openjdk:8-jdk-alpine

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo JAR e o arquivo de configuração para o contêiner
COPY target/ExcelToSQL-jar-with-dependencies.jar /app
COPY config.properties /app

# Define as variáveis de ambiente para o diretório de entrada e saída
ENV INPUT_DIRECTORY=/app/input
ENV OUTPUT_DIRECTORY=/app/output

# Define o comando de execução do JAR
CMD ["java", "-jar", "ExcelToSQL-jar-with-dependencies.jar"]
