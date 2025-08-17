# Система управления банковскими картами

Spring Boot-приложение: CRUD по картам, фильтрация/пагинация, переводы между своими картами, JWT-аутентификация, роли ADMIN/USER, миграции Liquibase, документация OpenAPI.

Дополнительно реализовано:
- Валидация номера карты по алгоритму Луна (Luhn).
- Номер карты хранится зашифрованным; наружу отдается только маска `**** **** **** 1234` (последние 4 цифры).


--------------------------------------------------------------------------------

## Архитектура и стек

- Java 21, Spring Boot 3.3.x
- Spring Security + JWT (ролевая модель ADMIN/USER)
- Spring Data JPA
- PostgreSQL (dev/prod), H2 (test)
- Liquibase — миграции (`src/main/resources/db/migration`)
- OpenAPI/Swagger — спецификация `docs/openapi.yaml`
- Слои: controller → service → repository → entity (+ DTO/mapper)
- Глобальный обработчик ошибок с единым JSON-ответом
--------------------------------------------------------------------------------
##  Особенности реализации:
### Алгоритм Луна - валидация номера карты 
- Номер карты (PAN) проверяется по алгоритму Луна (Luhn) при создании/обновлении.
- PAN шифруется при сохранении, наружу возвращается только маска с последними 4 цифрами.

--------------------------------------------------------------------------------
## Структура проекта и оформление кода
### Код оформлен согласно с Google Java Style
- UpperCamelCase / PascalCase : Classes
- lowerCamelCase / методы и переменные 
```txt
Bank_REST
├─ docs/
│ └─ openapi.yaml  
├─ src/
│ ├─ main/
│ │ ├─ java/com/example/bankcards/
│ │ │ ├─ controller/ 
│ │ │ ├─ service/  
│ │ │ ├─ repository/  
│ │ │ ├─ entity/  
│ │ │ ├─ dto/  
│ │ │ └─ security/ 
│ │ └─ resources/
│ │ ├─ application.yaml 
│ │ └─ db/migration/ # миграции Liquibase (yaml/sql)
│ └─ test/
│ ├─ java/...  
│ └─ resources/
│ └─ application-test.yaml  
├─ Dockerfile
├─ docker-compose.yml  
└─ pom.xml
```

--------------------------------------------------------------------------------

## Сущности и правила доступа

Пользователь
- id, username, password (хеш), fullName, enabled, набор ролей, createdAt/updatedAt.

Карта
- id, ownerId (связь с пользователем), PAN (зашифрован), maskedNumber (последние 4), expiryMonth/expiryYear, статус (`ACTIVE`, `BLOCKED`, `EXPIRED`), balance, createdAt/updatedAt.
- При создании выполняется проверка по алгоритму Луна (Luhn). В БД хранится только шифротекст; наружу возвращается маска.

Перевод
- id, fromCardId, toCardId, amount, статус (`SUCCESS`/`FAILED`), createdAt.

Права
- ADMIN: управление пользователями; создание/обновление/удаление карт; смена статусов; просмотр всех карт.
- USER: видит только свои карты; запрос блокировки своей карты; переводы только между своими картами.

--------------------------------------------------------------------------------

## Безопасность

- JWT-аутентификация.
- Публичные эндпоинты: `/api/auth/**`, `/api/health` (если явно не защищен).
- Остальные эндпоинты требуют заголовок `Authorization: Bearer <JWT>`.
- Приложение stateless; для JWT, как правило, CSRF отключен для REST API. Если CSRF включен, нужно разрешить его для Swagger UI или отключить для путей `/api/**`.

--------------------------------------------------------------------------------

## API и документация

Подробности в `docs/openapi.yaml`. Сводка:

Auth
- `POST /api/auth/login` — логин, ответ: `{ accessToken, tokenType }`
- `POST /api/auth/register` — регистрация пользователя с ролью USER

Users (ADMIN)
- `GET /api/users` — список (пагинация/сортировка)
- `POST /api/users` — создать
- `GET /api/users/{id}` — получить
- `DELETE /api/users/{id}` — удалить

Cards
- `GET /api/cards` — список (ADMIN — все; USER — свои), фильтры: `ownerId`, `status`, `last4` + пагинация/сортировка
- `POST /api/cards` (ADMIN) — создать (Luhn-проверка, шифрование)
- `GET /api/cards/{id}` — получить (USER — только свою)
- `PUT /api/cards/{id}` (ADMIN) — обновить срок действия
- `DELETE /api/cards/{id}` (ADMIN) — удалить
- `PATCH /api/cards/{id}/status` (ADMIN) — смена статуса
- `POST /api/cards/{id}/request-block` (USER) — запрос блокировки своей карты

Transfers (USER)
- `POST /api/transfers` — перевод между своими картами

Swagger / OpenAPI
- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`

--------------------------------------------------------------------------------

## Переменные окружения

| Переменная                     | Назначение                               | Пример                                                     |
|--------------------------------|-------------------------------------------|------------------------------------------------------------|
| SPRING_DATASOURCE_URL          | JDBC URL БД                               | jdbc:postgresql://localhost:5432/CardMaster               |
| SPRING_DATASOURCE_USERNAME     | Пользователь БД                           | ayzek                                                      |
| SPRING_DATASOURCE_PASSWORD     | Пароль БД                                 | 123                                                        |
| SPRING_JPA_HIBERNATE_DDL_AUTO  | DDL-режим JPA                             | validate                                                   |
| APP_JWT_SECRET                 | Секрет JWT (Base64)                       | 8nswcQpoE3eTzWmknTdcaxiaSlbM0VpqDt7hD0QXGGA=              |
| APP_JWT_TTL_MS                 | TTL токена (мс)                           | 86400000                                                   |
| APP_CRYPTO_KEY                 | Ключ шифрования PAN (Base64/bytes)        | pHQQqThqHaXP64qN49swqzvK7QFd61HoL7ZJOdScBfQ=              |
| SPRING_PROFILES_ACTIVE         | Активный профиль                          | dev / prod / test                                          |

Примечание: в продакшене секреты хранить в безопасном хранилище (Secrets/Vault).

--------------------------------------------------------------------------------

## Сборка и запуск

### Локально с PostgreSQL

1) Поднять PostgreSQL (пример через Docker):

```bash
docker run --name bankcards-db -p 5432:5432
-e POSTGRES_USER=ayzek
-e POSTGRES_PASSWORD=123
-e POSTGRES_DB=CardMaster
-d postgres:15
```
2) Экспортировать переменные окружения:
```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/CardMaster'
export SPRING_DATASOURCE_USERNAME='ayzek'
export SPRING_DATASOURCE_PASSWORD='123'
export SPRING_JPA_HIBERNATE_DDL_AUTO='validate'
export APP_JWT_SECRET='8nswcQpoE3eTzWmknTdcaxiaSlbM0VpqDt7hD0QXGGA='
export APP_JWT_TTL_MS='86400000'
export APP_CRYPTO_KEY='pHQQqThqHaXP64qN49swqzvK7QFd61HoL7ZJOdScBfQ='
```
3) Сборка и запуск:
```bash
mvn -q -DskipTests package
java -jar target/*.jar
```
--------------------------------------------------------------------------------
## Либо
### Docker (только приложение, внешняя БД)

1) Сборка образа:
```bash
docker build -t bankcards-app .
```
2) Запуск (подключение к локальной БД с хоста):

```bash
docker run --rm --name bankcards-app -p 8080:8080
-e SPRING_DATASOURCE_URL='jdbc:postgresql://host.docker.internal:5432/CardMaster'
-e SPRING_DATASOURCE_USERNAME='ayzek'
-e SPRING_DATASOURCE_PASSWORD='123'
-e SPRING_JPA_HIBERNATE_DDL_AUTO='validate'
-e APP_JWT_SECRET='8nswcQpoE3eTzWmknTdcaxiaSlbM0VpqDt7hD0QXGGA='
-e APP_JWT_TTL_MS='86400000'
-e APP_CRYPTO_KEY='pHQQqThqHaXP64qN49swqzvK7QFd61HoL7ZJOdScBfQ='
bankcards-app
```
--------------------------------------------------------------------------------
## Основные команды запуска:
```bash
docker compose up --build
docker compose logs -f app
docker compose down -v # остановка и удаление volume (полная очистка БД)
```
--------------------------------------------------------------------------------
## Миграции БД (Liquibase)

- Скрипты в `src/main/resources/db/migration`.
- Применяются автоматически при старте приложения.
--------------------------------------------------------------------------------
## Проверка работоспособности (cURL)

Health: http://localhost:8080/api/health

### если эндпоинт защищен, вернется 401

## Регистрация 
```bash
curl -s -X POST http://localhost:8080/api/auth/register
-H 'Content-Type: application/json'
-d '{"username":"user1","password":"secret123","fullName":"User One"}'
```

### Логин $\rightarrow$ получить JSONWebToken
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login
-H 'Content-Type: application/json'
-d '{"username":"user1","password":"secret123"}' | jq -r .accessToken)
echo "$TOKEN"
```
### Список своих карт (USER):
```bash
curl -s http://localhost:8080/api/cards
-H "Authorization: Bearer $TOKEN"
```

### Создать карту (ADMIN) — пример PAN, проходящий по Луну: `4539578763621486`
```bash
ADMIN_TOKEN="<jwt_админа>"
curl -s -X POST http://localhost:8080/api/cards
-H "Authorization: Bearer $ADMIN_TOKEN"
-H 'Content-Type: application/json'
-d '{
"ownerId": 1,
"cardNumber": "4539578763621486",
"expiryMonth": 12,
"expiryYear": 2027,
"status": "ACTIVE",
"balance": 100000000.00
}'
```

### Запрос блокировки своей карты (USER)
```bash
curl -s -X POST http://localhost:8080/api/cards/42/request-block
-H "Authorization: Bearer $TOKEN"
```

### Перевод между своими картами (USER)
```bash
curl -s -X POST http://localhost:8080/api/cards/42/request-block
-H "Authorization: Bearer $TOKEN"
```

--------------------------------------------------------------------------------

## Swagger / OpenAPI

- Спецификация: `docs/openapi.yaml`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Если в Swagger при POST/PUT/PATCH/DELETE получаете 403:
- Для JWT-REST обычно CSRF отключают для `/api/**`.
- Либо включить выдачу CSRF-токена в UI и прокинуть его в запросы (сложнее).
- Убедитесь, что `Authorization: Bearer <JWT>` присутствует в запросах Swagger (в UI нажать Authorize и ввести токен).

--------------------------------------------------------------------------------

## Тестирование

Запуск тестов (профиль `test` — H2, без внешней БД):
```bash
mvn -Dspring.profiles.active=test test
```

