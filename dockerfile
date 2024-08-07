# Usar una imagen base de OpenJDK 17
FROM openjdk:17
LABEL maintainer="V11Playko"

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo Gradle Wrapper y el archivo build.gradle
COPY gradlew .
COPY build.gradle .
COPY gradle gradle

# Copiar el código fuente del proyecto
COPY src src

# Copiar el archivo jar generado en el contenedor
COPY build/libs/microcurriculo-0.0.1-SNAPSHOT.jar /app/microcurriculo.jar

# Exponer el puerto en el que correrá la aplicación
EXPOSE 8080

# Ejecutar la aplicación
CMD ["java", "-jar", "/app/microcurriculo.jar"]
