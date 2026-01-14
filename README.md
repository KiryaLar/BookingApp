# BookingApp (микросервисы Бронирования)

Учебный проект на Spring Boot / Spring Cloud с мкросервисной архитектурой.

Состав:

- **api-gateway** — единая точка входа, маршрутизация и Swagger UI с несколькими спецификациями.
- **discovery-service** — Eureka Service Discovery.
- **booking-service** — управление бронированиями/процессом бонирования.
- **hotel-management-service** — управление отелями и номерами, проверка/резервирование доступности.

## Требования

- **JDK 17** (или версия, указанная в `gradle.properties`, если отличается)
- Gradle Wrapper (в репозитории: `./gradlew` / `gradlew.bat`)

Дополнительные внешние зависимости устанавливать не нужно — все подтягивается через Gradle.

## Как запустить систему

Открыть проект в IntelliJ IDEA как Gradle multi-module project или запускать из консоли.

### 1) Запустить Discovery (Eureka)

```bash
./gradlew :services:discovery-service:bootRun
```

Eureka будет доступна по адресу:

- http://localhost:8761

### 2) Запутить остальные сервисы

В отдельных терминалах:

```bash
./gradlew :services:hotel-management-service:bootRun
./gradlew :services:booking-service:bootRun
./gradlew :services:api-gateway:bootRun
```

После старта сервисы зарегистрируются в Eureka, а gateway начнёт проксировать запросы.

## Документация (Swagger / OpenAPI)

Swagger UI поднят на **api-gateway** и агрегирует спецификации сервисов.

Откройте:

- `http://localhost:<gateway-port>/swagger-ui/index.html`

В `api-gateway/src/main/resources/application.yaml` астроены источники:

- `booking-service`: `/bookings/v3/api-docs`
- `hotel-management-service`: `/hotels/v3/api-docs`

Примечание: точные порты зависят от конфигурации каждого сервиса (см. `application.yaml`/`application.properties` соответствующего модуля).

## Поддерживаемые эндпойнты

Ниже перечислены публичные контракты, реализуемые в сервисах. Детальные схемы запросов/ответов — в Swagger UI.

### hotel-management-service

Базовый префикс (как правило): `/api`

**ADMIN**

- `POST /api/hotels` — добавить отель.
- `POST /api/rooms` — добавить номр в отель.

**USER**

- `GET /api/hotels` — список отелей.
- `GET /api/rooms` — список *всех* свободных номеров (без специальной сортировки).
- `GET /api/rooms/recommend` — список рекомендованных свободных номеров (сортировка по `times_booked` по возрастанию).

**INTERNAL**

- `POST /api/rooms/{id}/confirm-availability` — подтвердить доступность на даты (временная блокировка слота).
- `POST /api/rooms/{id}/release` — компенсирующее действие: снять временную блокировку.

> Маршрут `/api/rooms/{id}/release` не публикуется через Gateway.

### booking-service

Эндпойнты бронирования доступны через Gateway и описаны в Swagger UI в разделе `booking-service`.

## Как пользоваться системой (типовой сценарий)

1. **Создать отель** (ADMIN): `POST /api/hotels`.
2. **Добавить номера** в отель (ADMIN): `POST /api/rooms`.
3. **Пользователь получает список**:
   - отелей: `GET /api/hotels`
   - свободных номеров: `GET /api/rooms`
   - рекомендованных номеров: `GET /api/rooms/recommend`
4. **Процесс бронирования** выполнется в `booking-service` и в рамках согласованности может вызывать внутренние методы `hotel-management-service`:
   - подтвердить доступность на период (временная блокировка слота)
   - при необходимости выполнить компенсацию (release)

## Как протестировать код

В проекте нет автотестов.

Для проверки сборки и корректности конфигурации запускайте стандартную Gradle-задачу:

```bash
./gradlew test
```

## Выполненные условия

- [ ] Система запускается через Gradle Wrapper.
- [ ] Все сервисы стартуют без ошибок и регистрируются в Eureka.
- [ ] Swagger UI через gateway открывается и показывает спецификации `booking-service` и `hotel-management-service`.
- [ ] Реализованные эндпойнты соответствуют ТЗ:
  - создание отелей/номеров
  - выбор свободных/рекомендованных номеров
  - подтверждение доступности и компенсирующий release
- [ ] Код оформлен и структурирован по стандартам (пакеты `controller/service/repository/dto/entity`, единый стиль, понятные имена).

## Документация по модулям

- `services/api-gateway` — маршрутизация, агрегация OpenAPI.
- `services/discovery-service` — Eureka Server.
- `services/hotel-management-service` — API отелей и номеров (включая внутренние эндпойнты согласованности).
- `services/booking-service` — API бронирований и orchestration процесса бронирования.

