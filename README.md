# Тестовое задание
## Технологии
- Java 17
- Spring Boot 3.5.6
- Postgresql 17
- Maven
- Docker

## Docker
В корне с проектом:
- `docker-compose up -d` - запуск БД и приложения
- `docker-compose up -d --build` - перезапуск с повторной сборкой
- `docker-compose stop` - остановка контейнеров без удаления
- `docker-compose down` - остановка контейнеров с удалением
- `docker-compose down -v` - полное очищение ресурсов(включая тома)