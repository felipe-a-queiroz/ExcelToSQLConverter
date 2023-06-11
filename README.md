# Excel to SQL Converter

O Excel to SQL Converter é uma aplicação Java que varre um diretório por arquivos XLSX, converte os dados desses arquivos em scripts SQL de inserção e os salva em outro diretório.

## Funcionalidades

- Varre um diretório por arquivos XLSX.
- Para cada arquivo XLSX encontrado, converte os dados em scripts SQL de inserção.
- Salva os scripts SQL em outro diretório.
- Gera instruções DELETE antes de cada instrução INSERT para a linha que está formatada com a cor verde.

## Requisitos

- Java Development Kit (JDK) 8 ou superior.
- Maven 3.x ou superior.

## Configuração

1. Clone o repositório para a sua máquina local.

2. Abra o arquivo `config.properties` no diretório raiz do projeto.

3. No arquivo `config.properties`, configure os diretórios de entrada e saída. Exemplo:

   ```properties
   inputDirectory=/caminho/do/diretorio/input
   outputDirectory=/caminho/do/diretorio/output

4. Salve e feche o arquivo config.properties.

## Como executar

1. Abra um terminal e navegue até o diretório raiz do projeto.

2. Execute o seguinte comando Maven para compilar o projeto:

  ```bash
  mvn clean package
  ```
3. Após a compilação bem-sucedida, execute o seguinte comando para executar a aplicação:

```bash
  java -jar target/ExcelToSQL.jar
  ```
4. A aplicação irá varrer o diretório de entrada, converter os arquivos XLSX em scripts SQL e salvá-los no diretório de saída.

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir problemas (issues) ou enviar pull requests com melhorias, correções ou novas funcionalidades.

## Licença

Este projeto é licenciado sob a MIT License.
