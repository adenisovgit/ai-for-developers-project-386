# Call Booking

[![Actions Status](https://github.com/adenisovgit/ai-for-developers-project-386/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/adenisovgit/ai-for-developers-project-386/actions)

Монорепозиторий упрощенного сервиса записи на звонок. В production backend и frontend собираются в один Docker-образ: Spring Boot раздает API и собранный Vue frontend с того же origin.

## Local development

- `make install` - установить frontend зависимости
- `make backend` - запустить Spring Boot backend на `http://localhost:8080`
- `make frontend` - запустить frontend против backend через `VITE_API_URL`
- `make mock` - запустить Prism mock на `http://localhost:3001`
- `make frontend-mock` - запустить frontend в mock-режиме
- `make e2e` - собрать frontend для backend API и прогнать Playwright

## Docker

Сборка production-образа из корня репозитория:

```bash
docker build -t call-booking .
```

Локальный запуск:

```bash
docker run --rm -e PORT=8080 -p 8080:8080 call-booking
```

После старта проверьте:

- `http://localhost:8080/` - публичный booking UI
- `http://localhost:8080/admin` - owner UI без `404` при прямом открытии
- `http://localhost:8080/event-types` - backend API

Во время production-сборки frontend собирается с `VITE_API_URL=""`, поэтому браузер использует same-origin запросы к backend без отдельной CORS-конфигурации.

## Render

Для Render нужен `Docker Web Service` из корня репозитория со следующими параметрами:

- `Language: Docker`
- `Branch: main`
- `Root Directory:` пусто
- `Build Command:` пусто
- `Start Command:` пусто
- `Health Check Path: /event-types`

`PORT` вручную задавать не нужно: приложение читает системную переменную Render через `server.port=${PORT:8080}`.

Публичная ссылка Render будет добавлена после первого deploy.
