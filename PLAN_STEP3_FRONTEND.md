# Детальный план реализации Шага 3: Frontend (Vue 3)

## Цель
Создать фронтенд для сервиса записи на звонок на Vue 3 с качественным UI, сгенерированным API-клиентом и контрактным mock-сервером Prism.

План должен быть согласован с утвержденными решениями:
- `color` хранится в TypeSpec/OpenAPI/домене как обязательная HEX-строка
- `POST /event-types` использует отдельный create-request без `id`
- `comment` в бронировании необязателен
- `GET /slots` возвращает календарную сетку слотов с флагом доступности
- шаг сетки слотов равен `durationMinutes` выбранного типа события
- при создании типа события владелец выбирает любое время, кратное часу
- значения по умолчанию для окна доступности: `09:00` и `18:00`
- при бронировании гость выбирает время только из сетки слотов, без отдельного `TimePicker`

---

## Утвержденный стек

| Компонент | Технология |
|-----------|------------|
| Framework | Vue 3 (`<script setup>`, Composition API) |
| Build Tool | Vite |
| UI Library | Reka UI |
| Styling | TailwindCSS + CSS Variables |
| Data Fetching | TanStack Query |
| Forms | VeeValidate + Zod |
| Date/Time | date-fns |
| Icons | Lucide Vue |
| API Client | openapi-generator-cli (`typescript-axios`) |
| Mock Server | Prism (`@stoplight/prism-cli`) |

---

## Контракт и UX, которые должны быть отражены в артефактах

Перед реализацией фронтенда нужно синхронизировать `api/main.tsp`, `api/openapi.yaml` и доменные документы с этими моделями.

### EventType

```typescript
type EventType = {
  id: string;
  title: string;
  description: string;
  durationMinutes: 15 | 30 | 45 | 60;
  availableFrom: string; // HH:mm
  availableTo: string;   // HH:mm
  color: string;         // #RRGGBB
};
```

### EventTypeCreateRequest

```typescript
type EventTypeCreateRequest = {
  title: string;
  description: string;
  durationMinutes: 15 | 30 | 45 | 60;
  availableFrom: string; // HH:mm
  availableTo: string;   // HH:mm
  color: string;         // #RRGGBB
};
```

### Booking

```typescript
type Booking = {
  id: string;
  eventTypeId: string;
  startTime: string;     // ISO UTC
  guestName: string;
  guestEmail: string;
  comment?: string;
};
```

### BookingRequest

```typescript
type BookingRequest = {
  eventTypeId: string;
  startTime: string;     // ISO UTC
  guestName: string;
  guestEmail: string;
  comment?: string;
};
```

### Slot

```typescript
type Slot = {
  startTime: string;     // ISO UTC
  isAvailable: boolean;
};
```

### Поведение `GET /slots`

- Запрос: `eventTypeId`, `startDate`, `endDate`
- Для публичной страницы фронтенд передает один выбранный день
- Ответ содержит дневную сетку стартовых точек для выбранного типа события
- Шаг сетки равен `durationMinutes` выбранного типа события
- Доступность каждой точки выражается через `isAvailable`

---

## Дизайн-система

### CSS Variables

```css
:root {
  --radius: 1rem;
  --radius-sm: 0.75rem;
  --radius-lg: 1.25rem;

  --background: 0 0% 100%;
  --foreground: 222.2 84% 4.9%;
  --card: 0 0% 100%;
  --card-foreground: 222.2 84% 4.9%;
  --popover: 0 0% 100%;
  --popover-foreground: 222.2 84% 4.9%;
  --primary: 217 91% 60%;
  --primary-foreground: 0 0% 100%;
  --secondary: 210 40% 96.1%;
  --secondary-foreground: 222.2 47.4% 11.2%;
  --muted: 210 40% 96.1%;
  --muted-foreground: 215.4 16.3% 46.9%;
  --accent: 217 91% 60%;
  --accent-hover: 221 83% 53%;
  --accent-foreground: 0 0% 100%;
  --destructive: 0 84.2% 60.2%;
  --destructive-foreground: 210 40% 98%;
  --border: 214.3 31.8% 91.4%;
  --input: 214.3 31.8% 91.4%;
  --ring: 217 91% 60%;
}

.dark {
  --background: 222.2 84% 4.9%;
  --foreground: 210 40% 98%;
  --card: 222.2 84% 4.9%;
  --card-foreground: 210 40% 98%;
  --popover: 222.2 84% 4.9%;
  --popover-foreground: 210 40% 98%;
  --primary: 217 91% 60%;
  --primary-foreground: 0 0% 100%;
  --secondary: 217.2 32.6% 17.5%;
  --secondary-foreground: 210 40% 98%;
  --muted: 217.2 32.6% 17.5%;
  --muted-foreground: 215 20.2% 65.1%;
  --accent: 217 91% 60%;
  --accent-foreground: 0 0% 100%;
  --destructive: 0 62.8% 30.6%;
  --destructive-foreground: 210 40% 98%;
  --border: 217.2 32.6% 17.5%;
  --input: 217.2 32.6% 17.5%;
  --ring: 217 91% 60%;
}
```

### Preset цветов для Event Types

`color` не декоративный локальный атрибут, а часть доменной модели и API.

```typescript
const PRESET_COLORS = [
  { name: 'Синий', value: '#3B82F6' },
  { name: 'Зеленый', value: '#22C55E' },
  { name: 'Желтый', value: '#EAB308' },
  { name: 'Оранжевый', value: '#F97316' },
  { name: 'Красный', value: '#EF4444' },
  { name: 'Фиолетовый', value: '#8B5CF6' },
  { name: 'Розовый', value: '#EC4899' },
  { name: 'Бирюзовый', value: '#14B8A6' },
];
```

---

## Структура проекта

```text
frontend/
├── src/
│   ├── api/
│   │   ├── api.ts
│   │   ├── base.ts
│   │   ├── configuration.ts
│   │   └── models/
│   │       ├── Booking.ts
│   │       ├── BookingRequest.ts
│   │       ├── EventType.ts
│   │       ├── EventTypeCreateRequest.ts
│   │       ├── Slot.ts
│   │       └── index.ts
│   ├── components/
│   │   ├── ui/
│   │   │   ├── Button.vue
│   │   │   ├── Card.vue
│   │   │   ├── Input.vue
│   │   │   ├── Label.vue
│   │   │   ├── Select.vue
│   │   │   ├── Dialog.vue
│   │   │   └── Skeleton.vue
│   │   ├── DatePicker.vue
│   │   ├── TimePicker.vue
│   │   ├── TimeSlotGrid.vue
│   │   ├── EventTypeCard.vue
│   │   ├── EventTypeForm.vue
│   │   ├── DurationSelect.vue
│   │   ├── ColorPickerField.vue
│   │   ├── BookingForm.vue
│   │   ├── BookingCard.vue
│   │   ├── BookingList.vue
│   │   ├── EventTypeSelector.vue
│   │   ├── ToastProvider.vue
│   │   └── ThemeToggle.vue
│   ├── composables/
│   │   ├── useToast.ts
│   │   ├── useTheme.ts
│   │   ├── useEventTypes.ts
│   │   ├── useBookings.ts
│   │   └── useSlots.ts
│   ├── lib/
│   │   ├── utils.ts
│   │   ├── validation.ts
│   │   └── date.ts
│   ├── views/
│   │   ├── PublicBookingView.vue
│   │   └── OwnerDashboardView.vue
│   ├── router/
│   │   └── index.ts
│   ├── styles/
│   │   └── index.css
│   ├── App.vue
│   └── main.ts
├── public/
├── package.json
├── tailwind.config.js
├── vite.config.ts
├── tsconfig.json
└── .env
```

---

## Этапы реализации

### Этап 1: Инициализация проекта

```bash
npm create vite@latest frontend -- --template vue-ts
cd frontend
npm install
npm install vue-router reka-ui @tanstack/vue-query vee-validate zod @vee-validate/zod date-fns lucide-vue-next clsx tailwind-merge
npm install -D tailwindcss postcss autoprefixer @openapitools/openapi-generator-cli @stoplight/prism-cli concurrently @tanstack/vue-query-devtools
npx tailwindcss init -p
```

Нужно создать:
- `tailwind.config.js`
- `src/styles/index.css`
- `.env`

### Этап 2: Генерация API клиента

Скрипт генерации:

```json
"generate-api": "openapi-generator-cli generate -i ../api/openapi.yaml -g typescript-axios -o src/api --additional-properties=supportsES6=true,typescriptThreePlus=true"
```

Фронтенд должен использовать сгенерированные модели:
- `EventType` для чтения и отображения
- `EventTypeCreateRequest` для создания типа события
- `BookingRequest` с optional `comment`
- `Slot` с `startTime` и `isAvailable`

### Этап 3: Инициализация приложения

Файлы:
- `src/main.ts` - Vue app, Router, Vue Query, глобальные стили
- `src/router/index.ts` - маршруты `/` и `/admin`
- `src/composables/useTheme.ts` - режимы `auto`, `light`, `dark`
- `src/composables/useToast.ts` - toast API

Логика темы:
- `auto` по умолчанию
- `light` принудительно задает светлую тему
- `dark` принудительно задает темную тему

### Этап 4: Базовые UI-примитивы

Компоненты в `src/components/ui/`:
- `Button.vue`
- `Card.vue`
- `Input.vue`
- `Label.vue`
- `Select.vue`
- `Dialog.vue`
- `Skeleton.vue`

Требования:
- используют CSS variables
- поддерживают `hover`, `focus-visible`, `disabled`
- подходят для mobile-first интерфейса

### Этап 5: Прикладные компоненты

#### DurationSelect.vue

- Выбор из `15 / 30 / 45 / 60`
- Используется в форме типа события

#### TimePicker.vue

- Используется только в форме владельца для `availableFrom` и `availableTo`
- Показывает значения, кратные часу: `00:00`, `01:00`, ..., `23:00`
- Значения по умолчанию в `EventTypeForm`: `09:00` и `18:00`
- Проверка формы: `availableFrom < availableTo`

#### ColorPickerField.vue

- Показывает preset-цвета
- Может позволять custom HEX, если это не усложняет реализацию
- Возвращает значение, совместимое с обязательным полем `color`

#### DatePicker.vue

- Выбор одного дня
- После выбора дня запускается загрузка сетки слотов через `useSlots`

#### TimeSlotGrid.vue

- Показывает дневную сетку слотов для выбранного типа события
- Шаг сетки равен `durationMinutes` выбранного типа события
- Каждый слот имеет `startTime` и `isAvailable`
- `isAvailable === false` отображается визуально, но не может быть выбран
- Выбор времени гостем происходит только здесь

### Этап 6: Формы и валидация

`src/lib/validation.ts`

```typescript
import { z } from 'zod';

export const eventTypeSchema = z.object({
  title: z.string().min(1).max(100),
  description: z.string().max(500),
  durationMinutes: z.union([
    z.literal(15),
    z.literal(30),
    z.literal(45),
    z.literal(60),
  ]),
  availableFrom: z.string().regex(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/),
  availableTo: z.string().regex(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/),
  color: z.string().regex(/^#[0-9A-Fa-f]{6}$/),
}).refine((data) => data.availableFrom < data.availableTo, {
  path: ['availableTo'],
  message: 'Время начала должно быть меньше времени окончания',
});

export const bookingSchema = z.object({
  eventTypeId: z.string().uuid(),
  startTime: z.string().datetime(),
  guestName: z.string().min(2).max(100),
  guestEmail: z.string().email(),
  comment: z.string().max(1000).optional(),
});
```

Примечания:
- `color` обязателен
- `comment` необязателен
- форма бронирования не запрашивает отдельно тип события и время вручную, а получает их из выбранных шагов

### Этап 7: Composables

#### useEventTypes.ts

- `useEventTypes()` читает список типов событий
- `useCreateEventType()` отправляет `EventTypeCreateRequest`
- после успеха инвалидирует `['event-types']`

#### useSlots.ts

- принимает `eventTypeId`, `startDate`, `endDate`
- используется для дневной сетки слотов выбранного типа события
- фронтенд на странице бронирования для выбранной даты передает одинаковые `startDate` и `endDate`

#### useBookings.ts

- `useBookings()` загружает список бронирований для `/admin`
- `useCreateBooking()` отправляет `BookingRequest` с optional `comment`
- после успеха инвалидирует `['bookings']`

### Этап 8: Страницы

#### PublicBookingView.vue (`/`)

Многошаговый сценарий:

1. Выбор типа события
2. Выбор даты
3. Выбор времени из `TimeSlotGrid`
4. Ввод контактных данных

Детали:
- шаг выбора времени показывает именно календарную сетку слотов с доступными и недоступными точками
- гость не использует отдельный `TimePicker`
- поле комментария необязательное
- после успешной отправки показывается состояние успеха и сбрасывается локальный прогресс

#### OwnerDashboardView.vue (`/admin`)

Содержимое:
- заголовок панели
- секция типов событий
- кнопка создания нового типа события
- диалог с `EventTypeForm`
- секция предстоящих бронирований

Детали формы:
- `TimePicker` с часовыми значениями
- дефолты `09:00` и `18:00`
- `color` обязателен
- отправка идет как `EventTypeCreateRequest`, без `id`

### Этап 9: Prism mock-сервер

`package.json` scripts:

```json
{
  "scripts": {
    "dev": "concurrently \"npm run mock\" \"vite\"",
    "dev:client": "vite",
    "mock": "prism mock ../api/openapi.yaml --port 3001 --dynamic",
    "build": "vue-tsc && vite build",
    "generate-api": "openapi-generator-cli generate -i ../api/openapi.yaml -g typescript-axios -o src/api --additional-properties=supportsES6=true,typescriptThreePlus=true",
    "preview": "vite preview",
    "type-check": "vue-tsc --noEmit"
  }
}
```

`.env`

```env
VITE_API_URL=http://localhost:3001
```

Правила использования Prism на шаге 3:
- Prism нужен как контрактный mock
- `POST`-операции должны быть достаточны для отладки UI-потока отправки формы
- не требуется stateful-симуляция, при которой последующие `GET` обязаны отражать созданные сущности
- отдельный mock-state поверх Prism в рамках шага 3 не планируется

### Этап 10: Финальная настройка

Маршруты:

```typescript
import { createRouter, createWebHistory } from 'vue-router';
import PublicBookingView from '@/views/PublicBookingView.vue';
import OwnerDashboardView from '@/views/OwnerDashboardView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: PublicBookingView },
    { path: '/admin', component: OwnerDashboardView },
  ],
});

export default router;
```

UI-требования:
- mobile-first
- hover-анимации карточек и кнопок
- skeleton во время загрузки
- видимые focus-стили
- адаптация под mobile, tablet, desktop

---

## package.json (полный ориентир)

```json
{
  "name": "call-booking-frontend",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "dev": "concurrently \"npm run mock\" \"vite\"",
    "dev:client": "vite",
    "mock": "prism mock ../api/openapi.yaml --port 3001 --dynamic",
    "build": "vue-tsc && vite build",
    "generate-api": "openapi-generator-cli generate -i ../api/openapi.yaml -g typescript-axios -o src/api --additional-properties=supportsES6=true,typescriptThreePlus=true",
    "preview": "vite preview",
    "type-check": "vue-tsc --noEmit"
  },
  "dependencies": {
    "@tanstack/vue-query": "^5.x",
    "@vee-validate/zod": "^4.x",
    "clsx": "^2.x",
    "date-fns": "^3.x",
    "lucide-vue-next": "^0.x",
    "reka-ui": "^2.x",
    "tailwind-merge": "^2.x",
    "vee-validate": "^4.x",
    "vue": "^3.4.x",
    "vue-router": "^4.x",
    "zod": "^3.x"
  },
  "devDependencies": {
    "@openapitools/openapi-generator-cli": "^2.x",
    "@stoplight/prism-cli": "^5.x",
    "@tanstack/vue-query-devtools": "^5.x",
    "@vitejs/plugin-vue": "^5.x",
    "autoprefixer": "^10.x",
    "concurrently": "^8.x",
    "postcss": "^8.x",
    "tailwindcss": "^3.x",
    "typescript": "^5.x",
    "vite": "^5.x",
    "vue-tsc": "^1.x"
  }
}
```

---

## Чек-лист реализации

### Инфраструктура
- [ ] `frontend/` создан через Vite + Vue + TypeScript
- [ ] зависимости установлены
- [ ] TailwindCSS настроен
- [ ] Vue Router настроен
- [ ] TanStack Query инициализирован
- [ ] Prism mock сервер запускается
- [ ] API клиент генерируется из `../api/openapi.yaml`

### Контракты
- [ ] `EventType` включает `color`
- [ ] `EventTypeCreateRequest` используется для `POST /event-types`
- [ ] `BookingRequest.comment` необязателен
- [ ] `Slot` включает `isAvailable`
- [ ] правило дневной сетки слотов зафиксировано в артефактах

### Компоненты
- [ ] Button, Card, Input, Label, Select, Dialog, Skeleton
- [ ] DatePicker
- [ ] TimePicker для владельца
- [ ] TimeSlotGrid для гостя
- [ ] DurationSelect
- [ ] ColorPickerField

### Формы
- [ ] `EventTypeForm` валидирует `color`
- [ ] `BookingForm` принимает optional `comment`
- [ ] значения по умолчанию `09:00` и `18:00` выставляются в форме типа события
- [ ] `TimePicker` владельца работает только по часовым значениям

### Страницы
- [ ] `/` реализован как 4-шаговый сценарий
- [ ] шаг времени использует календарную сетку слотов
- [ ] недоступные слоты видимы и невыбираемы
- [ ] `/admin` показывает типы событий и бронирования
- [ ] цвет Event Type отображается в UI

### Финальные детали
- [ ] светлая, темная и авто-тема работают
- [ ] toast показывается после успешных операций
- [ ] есть loading и empty states
- [ ] responsive поведение корректно
- [ ] фокусы и базовая a11y не потеряны
- [ ] план не предполагает stateful mock-слой поверх Prism

---

## Проверки перед переходом к реализации

- В тексте плана нет противоречия между optional `comment` и описанием формы гостя
- В тексте плана нет противоречия между `POST /event-types` и обязательным `id`
- `TimeSlotGrid` описан через `Slot.startTime` и `Slot.isAvailable`
- правило генерации слотов описано однозначно: шаг равен длительности выбранного типа события
- роль Prism зафиксирована как контрактный mock без обязательной persistence-симуляции

---

## Запуск после реализации

```bash
npm install
npm run generate-api
npm run dev
```

Локальные URL:
- Frontend: http://localhost:5173
- Mock API: http://localhost:3001

---

## Следующий шаг

После фиксации этого артефакта можно переходить к обновлению `api/main.tsp`, `api/openapi.yaml`, `domain_rules.md` и затем к реализации `frontend/`.
