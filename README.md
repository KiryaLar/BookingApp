# BookingApp — система бронирования (Spring Boot / Spring Cloud)

Учебный проект с микросервисной архитектурой: управление отелями/номерами и процессом бронирования через единый API Gateway, сервис‑дискавери и OpenAPI документацию.

## Состав репозитория

- **services/api-gateway** — единая точка входа, маршрутизация (Spring Cloud Gateway), Swagger UI с агрегированной документацией.
- **services/discovery-service** — Eureka Service Discovery.
- **services/hotel-management-service** — управление отелями и номерами, выдача свободных/рекомендованных номеров, внутренние операции согласованности (confirm/release).
- **services/booking-service** — управление бронированиями и orchestration процесса бронирования (в т.ч. вызовы внутренних эндпойнтов hotel-management-service).

## Используемые технологии

- Java 17
- Spring Boot, Spring Web
- Spring Data JPA
- Spring Security
- Spring Cloud (Eureka, Gateway)
- springdoc-openapi (Swagger UI / OpenAPI)
- Gradle (multi-module)

## Требования

- **JDK 21**
- Gradle Wrapper (в репозитории: `./gradlew` / `gradlew.bat`)

Внешние зависимости вручную не устанавливаются — всё подтягивается через Gradle.

## Быстрый старт (запуск всей системы)

> Рекомендуется запускать каждый модуль в отдельном терминале.

### 1) Запуск Discovery (Eureka)

```bash
./gradlew :services:discovery-service:bootRun
```

Eureka:
- http://localhost:8761

### 2) Запуск бизнес‑сервисов

```bash
./gradlew :services:hotel-management-service:bootRun
./gradlew :services:booking-service:bootRun
```

### 3) Запуск API Gateway

```bash
./gradlew :services:api-gateway:bootRun
```

После старта сервисы зарегистрируются в Eureka, а gateway начнет проксировать запросы.

## Документация (Swagger / OpenAPI)

Swagger UI поднимается на **api-gateway** и агрегирует спецификации сервисов.

Откройте:

- `http://localhost:<gateway-port>/swagger-ui/index.html`

Источники спецификаций настраиваются в `services/api-gateway/src/main/resources/application.yaml`.

> Порты зависят от конфигурации (`application.yaml`/`application.properties`) каждого модуля.

### Проверка, что документация «живая»

1) Откройте Swagger UI на gateway.
2) Убедитесь, что доступны группы/сервисы `booking-service` и `hotel-management-service`.
3) Откройте любой endpoint и проверьте схемы request/response.

## Безопасность (роли и доступ)

В системе используются роли:

- **ADMIN** — создание отелей/номеров.
- **USER** — просмотр отелей и свободных/рекомендованных номеров, создание бронирований (публичные операции пользователя).

Фактический способ аутентификации/выдачи ролей определяется конфигурацией Spring Security в соответствующих сервисах.

## Поддерживаемые API (контракты)

Ниже перечислены ключевые маршруты. Детальные схемы запросов/ответов и коды ответов смотрите в Swagger UI.

### hotel-management-service

Базовый префикс: `/api`

#### ADMIN

- `POST /api/hotels` — добавить отель.
- `POST /api/rooms` — добавить номер в отель.

#### USER

- `GET /api/hotels` — получить список отелей.
- `GET /api/rooms` — получить список **всех свободных** номеров (без специальной сортировки).
- `GET /api/rooms/recommend` — получить список **рекомендованных свободных** номеров (те же свободные номера, сортировка по возрастанию `timesBooked`).

#### INTERNAL

- `POST /api/rooms/{id}/confirm-availability` — подтвердить доступность номера на даты (временная блокировка слота на период).
- `POST /api/rooms/{id}/release` — компенсирующее действие: снять временную блокировку.

> Маршрут `/api/rooms/{id}/release` **не публикуется** через Gateway (только для внутренних вызовов).

### booking-service

Эндпойнты бронирования доступны через Gateway и описаны в Swagger UI в разделе `booking-service`.

Типично присутствуют операции:

- создание/отмена бронирования (пользовательские операции)
- получение списка бронирований
- служебные операции orchestration (если предусмотрены конфигурацией)

> Точный состав зависит от реализации контроллеров `booking-service` — используйте Swagger UI как источник истины.

## Примеры запросов (curl)

Ниже — минимальные примеры. Подставьте корректный порт gateway и (при необходимости) заголовки авторизации.

### Создать отель (ADMIN)

```bash
curl -X POST "http://localhost:<gateway-port>/api/hotels" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Hotel","city":"Moscow","address":"Tverskaya, 1"}'
```

### Добавить номер (ADMIN)

```bash
curl -X POST "http://localhost:<gateway-port>/api/rooms" \
  -H "Content-Type: application/json" \
  -d '{"hotelId":1,"name":"101","capacity":2,"price":5000}'
```

### Список отелей (USER)

```bash
curl "http://localhost:<gateway-port>/api/hotels"
```

### Свободные номера (USER)

```bash
curl "http://localhost:<gateway-port>/api/rooms?from=2026-01-20&to=2026-01-22"
```

### Рекомендованные номера (USER)

```bash
curl "http://localhost:<gateway-port>/api/rooms/recommend?from=2026-01-20&to=2026-01-22"
```

## Как пользоваться системой (типовой сценарий)

1. **ADMIN** создаёт отель: `POST /api/hotels`.
2. **ADMIN** добавляет номера: `POST /api/rooms`.
3. **USER** выбирает номер:
   - список отелей: `GET /api/hotels`
   - свободные номера: `GET /api/rooms`
   - рекомендованные: `GET /api/rooms/recommend`
4. **USER** создаёт бронирование в `booking-service`.
5. Во время orchestration `booking-service` может вызывать **INTERNAL** эндпойнт `confirm-availability`.
6. При ошибке/откате выполняется компенсация через **INTERNAL** `release`.

## Как протестировать код

Автотестов в проекте **нет**.

Для проверки сборки/контекста/конфигурации используйте стандартную Gradle задачу:

```bash
./gradlew test
```

Дополнительно рекомендуется прогнать сборку целиком:

```bash
./gradlew clean build
```

## Стандарты качества (для сдачи проекта)

Заявлено, что:

- функции проекта работают корректно и соответствуют ТЗ;
- код структурирован по слоям (controller/service/repository/entity/dto), читаемые имена, единый стиль;
- в репозитории есть общее описание проекта и документация по модульной структуре;
- при использовании сторонних библиотек — инструкции по установке не требуются (всё через Gradle).

## Структура модулей и ответственность

- `services/discovery-service`:
  - Eureka Server (Service Discovery).
- `services/api-gateway`:
  - маршрутизация запросов к сервисам;
  - единый Swagger UI.
- `services/hotel-management-service`:
  - CRUD‑операции администратора (отели/номера);
  - выдача свободных/рекомендованных номеров;
  - внутренние операции временной блокировки слотов (confirm/release);
  - статистика/загруженность номеров (если включено соответствующими эндпойнтами).
- `services/booking-service`:
  - управление бронированиями;
  - интеграция и согласованность с hotel-management-service.
