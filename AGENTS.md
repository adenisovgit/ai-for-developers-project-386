# Call Booking

Монорепозиторий для упрощенного сервиса записи на звонок.

## Продуктовые ограничения
- Один владелец без авторизации и личных кабинетов.
- Гость выбирает тип события, дату, слот и оставляет контакты.
- Внешних интеграций календарей нет.
- `availableFrom` и `availableTo` задаются только значениями `HH:00`.

## Источники правды
- Источник правды для API: `api/main.tsp`.
- `api/openapi.yaml` генерируется из `api/main.tsp` и не редактируется вручную.
- Frontend API-клиент в `frontend/src/api` генерируется из `api/openapi.yaml`.

## Текущий контракт
- `GET /event-types` возвращает типы событий.
- `GET /slots` принимает только `startDate` и `endDate`.
- `GET /slots` возвращает только занятые интервалы.
- Дневная сетка слотов строится на фронтенде из `durationMinutes`, `availableFrom`, `availableTo` и busy intervals.

## Работа с памятью проекта
- Перед существенными изменениями читайте `PROJECT_MEMORY.md`.
- Если меняется контракт, архитектура, инфраструктура или ключевая бизнес-логика, обновляйте `PROJECT_MEMORY.md` в рамках той же задачи.

## Repo-local playbooks
- `docs/agent-playbooks/repo-memory.md`
- `docs/agent-playbooks/typespec-contract-flow.md`
- `docs/agent-playbooks/call-booking-frontend.md`

## Коммуникация
- При подготовке текстов не используйте длинное тире.
