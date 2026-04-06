# TypeSpec Contract Flow

Этот репозиторий использует TypeSpec как источник правды для API.

## Обязательный порядок работы

1. Сначала обновите `api/main.tsp`.
2. Затем пересоберите `api/openapi.yaml` из TypeSpec.
3. Затем перегенерируйте `frontend/src/api` из `api/openapi.yaml`.
4. Только после этого правьте прикладной код, зависящий от контракта.

## Правила

- Не редактируйте `api/openapi.yaml` вручную.
- Не редактируйте файлы в `frontend/src/api` вручную.
- Если изменение в домене или frontend требует изменения контракта, сначала обновляйте TypeSpec.
- При существенных изменениях контракта синхронизируйте `domain_rules.md` и `PROJECT_MEMORY.md`.

## Актуальные особенности контракта

- `GET /slots` принимает только `startDate` и `endDate`.
- `GET /slots` возвращает busy intervals, а не готовые статусы слотов.
- `availableFrom` и `availableTo` должны оставаться в формате `HH:00`.
- `EventTypeCreateRequest` отделен от `EventType`.
- `Booking.comment` и `BookingRequest.comment` являются optional.

