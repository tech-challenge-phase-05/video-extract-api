# 🔹 Fase 1: Build da aplicação
FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

# Copia o projeto inteiro (pom + src)
COPY . .

# Empacota a aplicação sem rodar testes
RUN mvn clean package -DskipTests

# 🔹 Fase 2: Runtime com JDK leve
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Diretório de certificados (opcional)
RUN mkdir -p /etc/certs

# Copia o JAR gerado na fase anterior (nome correto com base no pom.xml)
COPY --from=build /app/target/videoextractorapi-0.0.1-SNAPSHOT.jar video-extractor-api.jar

# Expõe a porta padrão
EXPOSE 8080

# Executa a aplicação
ENTRYPOINT ["java", "-Djavax.net.ssl.trustStore=/opt/java/openjdk/lib/security/cacerts", "-Djavax.net.ssl.trustStorePassword=changeit", "-jar", "video-extractor-api.jar"]
