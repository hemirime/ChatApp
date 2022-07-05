# ChatApp
Пет-проект чат-сервера, предоставляющего HTTP API для работы с чатами и сообщениями пользователя.

## Сборка и запуск
```bash
sbt docker:publishLocal
docker compose up
```

## Основные сущности

### User
Пользователь приложения

* **id** - уникальный идентификатор пользователя
* **username** - уникальное имя пользователя
* **created_at** - время создания

### Chat
Отдельный чат

* **id** - уникальный идентификатор чата
* **name** - уникальное имя чата
* **users** - список идентификаторов пользователей в чате, отношение многие-ко-многим
* **created_at** - время создания

### Message
Сообщение в чате

* **id** - уникальный идентификатор сообщения
* **author** - ссылка на идентификатор отправителя сообщения, отношение многие-к-одному
* **text** - текст отправленного сообщения
* **created_at** - время создания

# API
В ответ на любой запрос возвращается или успешный ответ c результатом в поле `result`
```json
{
  "result": "<result will be here>"
}
```
или HTTP-код ошибки и описание ошибки в JSON-формате
```json
{
  "error": "example of error description"
}
```

## /users
### Добавить нового пользователя
Запрос:
```bash
curl --request POST \
  --url http://localhost:8080/users \
  --header 'Content-Type: application/json' \
  --data '{"username": "user_1"}'
```
Ответ: `id` созданного пользователя
### Получить всех пользователей
Запрос:
```bash
curl --request GET \
  --url http://localhost:8080/users
```
Ответ: список всех пользователей
### Получить пользователя по `id`
Запрос:
```bash
curl --request GET \
  --url http://localhost:8080/users/<USER_ID_1>
```
Ответ: сущность пользователя

## /chats

### Создать новый чат между пользователями
Количество пользователей в чате не ограничено.  
Запрос:
```bash
curl --request POST \
  --url http://localhost:8080/chats \
  --header 'Content-Type: application/json' \
  --data '{"name": "chat_1", "users": ["<USER_ID_1>","<USER_ID_2>"]}'
```
Ответ: `id` созданного чата

### Получить список всех чатов
Запрос:
```bash
curl --request GET \
  --url http://localhost:8080/chats
```
Ответ: список чатов

### Получить список чатов конкретного пользователя
Запрос:
```bash
curl --request GET \
  --url http://localhost:8080/chats?user=<USER_ID_1>
```
Ответ: список чатов

### Получить чат по `id`
Запрос:
```bash
curl --request GET \
  --url http://localhost:8080/chats/<CHAT_ID_1>
```
Ответ: сущность чата

### Отправить сообщение в чат от лица пользователя
Запрос:
```bash
curl --request POST \
  --url http://localhost:8080/chats/<CHAT_ID_1>/messages \
  --header 'Content-Type: application/json' \
  --data '{"author": "<USER_ID_1>", "text": "hello, chat!"}'
```
Ответ: созданное сообщение

### Получить список сообщений в конкретном чате
Запрос:
```bash
curl --request GET \
  --url http://localhost:8080/chats/<CHAT_ID_1>/messages
```
Ответ: список сообщений в чате