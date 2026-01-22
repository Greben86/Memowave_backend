# Родительский образ контейнера с gradle внутри
FROM gradle:jdk21-alpine AS build
LABEL authors="viktor"
COPY ../ /app
WORKDIR /app
RUN gradle clean build --no-daemon

# Родительский образ контейнера с java внутри
FROM eclipse-temurin:21-jre-alpine-3.22 AS runtime
LABEL authors="viktor"
RUN mkdir /app
WORKDIR /app
# Копирование jar-файла в контейнер
COPY --from=build /app/module-user-management/build/libs/*-0.0.1-SNAPSHOT.jar /app.jar
RUN apk add --no-cache curl
# Открытие порта 8081
EXPOSE 8081
USER 1000:1000
# Команда, которая будет запущена при старте контейнера java -jar ./app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]