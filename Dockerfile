# Этап 1 - сборка
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# На этапе сборки копируем только pom
COPY pom.xml .
# Кэширует зависимости, ускоряет повторные сборки
RUN mvn dependency:go-offline
# Копируем код в контейнер и собираем
COPY src ./src
# Скипаем тесты, так как они не выполнятся без загруженной БД
RUN mvn clean package -DskipTests

# Этап 2 - запуск собранного приложения
# Используем официальный образ JRE
FROM eclipse-temurin:17-jre-alpine

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем готовый jar внутрь контейнера, который собрали на 1 этапе
# Используем маску *.jar, если название файла поменяется
COPY --from=build app/target/*.jar app.jar

# Запуск приложения
ENTRYPOINT ["java","-jar","app.jar"]
