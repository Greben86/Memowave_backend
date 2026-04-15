# Родительский образ контейнера с java внутри
FROM eclipse-temurin:21-jre-alpine
LABEL authors="viktor"
RUN apk add --no-cache curl
RUN mkdir /app
WORKDIR /app
# Копирование jar-файла в контейнер
COPY /build/libs/*-0.0.1-SNAPSHOT.jar /app.jar
# Открытие порта 8080
EXPOSE 8080
USER 1000:1000
# Команда, которая будет запущена при старте контейнера java -jar ./app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]