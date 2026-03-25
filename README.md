# Разработка web-приложения для банка: система уведомлений о сроке действия карт

*Проект по производственной практике*

---

## Что сделано

- **Клиенты** — создание, просмотр, удаление, обновление email
- **Карты** — выпуск (с генерацией уникального номера), аннулирование, просмотр истекающих
- **Уведомления** — автоматическая рассылка email для уведомления об истечении карт
- **База данных** — PostgreSQL, JPA, Hibernate
- **REST API** — управление клиентами и картами через HTTP запросы
- **React UI** — интерфейс для управления клиентами и картами
- **Docker** — запуск всего приложения

---

## 🛠 Технологии

| Бэкенд | Фронтенд    | База данных |
|--------|-------------|------------|
| Java 21 | React 19    | PostgreSQL |
| Spring Boot 4 | Bootstrap 5 | Hibernate / JPA |
| Maven | Axios       | |

**Контейнеризация:** Docker, Docker Compose

---

## ⚙️ Переменные окружения

| Переменная | Описание | Значение по умолчанию |
|------------|----------|----------------------|
| `REPOSITORY_TYPE` | Тип репозитория (jpa/memory) | jpa |
| `SCHEDULER_ENABLED` | Включить планировщик уведомлений | false |
| `MAIL_USERNAME` | Email для уведомлений | — |
| `MAIL_PASSWORD` | Пароль приложения Gmail | — |

В Docker Compose эти переменные можно переопределить в секции `environment`.

---

## Быстрый запуск

### Через Docker 

```bash
# Клонировать проект
git clone https://github.com/Shkobarev/bank-notification.git
cd bank-notification

# Создать файл .env
touch .env
# Открыть в редакторе и добавить:
# MAIL_USERNAME=your-email@gmail.com
# MAIL_PASSWORD=your-app-password
# При необходимости изменить переменные окружения в Docker Compose

# Запустить всё одной командой
docker-compose up --build
```
## После запуска

- Интерфейс: http://localhost:3000
- API: http://localhost:8080

---

## Основные API

| Метод | URL | Описание |
|-------|-----|----------|
| **GET** | `/api/clients` | Получить список всех клиентов |
| **POST** | `/api/clients` | Создать нового клиента |
| **GET** | `/api/clients/{id}` | Получить клиента по ID |
| **PUT** | `/api/clients/{id}/email` | Обновить email клиента |
| **DELETE** | `/api/clients/{id}` | Удалить клиента |
| **GET** | `/api/clients/search?email=` | Найти клиента по email |
| **GET** | `/api/clients/{clientId}/cards` | Получить все карты клиента |
| **POST** | `/api/clients/{clientId}/cards` | Выпустить новую карту |
| **GET** | `/api/cards/{cardId}` | Получить карту по ID |
| **DELETE** | `/api/cards/{cardId}` | Аннулировать карту |
| **GET** | `/api/cards/expiring?days=` | Получить карты, истекающие через N дней |

---

## 👨‍💻 Автор

**Шкобарев Данила Александрович**
- GitHub: [Shkobarev](https://github.com/Shkobarev)
- Email: danila564321@gmail.com