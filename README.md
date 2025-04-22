# Video Extract API

Este repositório contém a implementação da API para o projeto Video Extractor, desenvolvida em Kotlin.

# No diagrama de arquitetura abaixo, essa api implementa a funcionalidade da figura com o nome "Rest API", sendo responsável por gerar as urls pré assinadas para upload do video, como também responsável por receber a notificação dos arquivos processados e disponibilizar o link dos mesmos para download

![image](https://github.com/user-attachments/assets/21f8b7c6-55e6-4f68-bc8a-a1cc52e47ac3)


## Pré-requisitos

- Java Development Kit (JDK) instalado.
- Maven instalado ou utilizar o Maven Wrapper fornecido no projeto.

## Como Usar

1. Clone este repositório:

   ```bash
   git clone https://github.com/tech-challenge-phase-05/video-extract-api.git
   ```

2. Navegue até o diretório do projeto:

   ```bash
   cd video-extract-api
   ```

3. Compile e execute a aplicação utilizando o Maven Wrapper:

   ```bash
   ./mvnw clean install
   java -jar target/video-extract-api.jar
   ```

   *Observação: No Windows, utilize `mvnw.cmd` em vez de `./mvnw`.*
