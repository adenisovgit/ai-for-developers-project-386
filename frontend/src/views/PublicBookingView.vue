<script setup lang="ts">
import DatePicker from '@/components/DatePicker.vue'
import EventTypeCard from '@/components/EventTypeCard.vue'
import BookingForm from '@/components/BookingForm.vue'
import TimeSlotGrid from '@/components/TimeSlotGrid.vue'
import Button from '@/components/ui/Button.vue'
import Card from '@/components/ui/Card.vue'
import Dialog from '@/components/ui/Dialog.vue'
import type { Booking } from '@/api/models/booking'
import type { EventType } from '@/api/models/event-type'
import { useCreateBooking } from '@/composables/useBookings'
import { useEventTypes } from '@/composables/useEventTypes'
import { useSlots } from '@/composables/useSlots'
import { formatBookingDateTime, formatHumanDate, getTodayDateValue } from '@/lib/date'
import { buildSlotStatuses, type SlotStatus } from '@/lib/slots'
import {
  CalendarDays,
  CheckCircle2,
  ChevronRight,
  Clock3,
  ListChecks,
  UserRound,
} from 'lucide-vue-next'
import { computed, nextTick, ref, watch } from 'vue'

const selectedEventType = ref<EventType | null>(null)
const selectedDate = ref<string | null>(null)
const selectedSlot = ref<SlotStatus | null>(null)
const successfulBooking = ref<Booking | null>(null)
const bookingFormRef = ref<InstanceType<typeof BookingForm> | null>(null)

const isEventTypeDialogOpen = ref(false)
const isSlotDialogOpen = ref(false)

const { eventTypes, isLoading: isEventTypesLoading } = useEventTypes()
const { occupiedSlots, isLoading: isSlotsLoading, refetchSelectedDate } = useSlots(selectedDate)
const createBookingMutation = useCreateBooking({
  onConflict: async () => {
    selectedSlot.value = null
    await refetchSelectedDate()
  },
})

const selectedFlowSummary = computed(() => [
  {
    label: 'Тип события',
    value: selectedEventType.value?.title ?? 'Пока не выбран',
    icon: ListChecks,
  },
  {
    label: 'Дата',
    value: !selectedEventType.value
      ? 'Сначала выбери событие'
      : selectedDate.value
        ? formatHumanDate(selectedDate.value)
        : 'Пока не выбрана',
    icon: CalendarDays,
  },
  {
    label: 'Время',
    value: selectedSlot.value ? formatBookingDateTime(selectedSlot.value.startTime) : 'Слот еще не выбран',
    icon: Clock3,
  },
])

const displayedSlots = computed(() => {
  if (!selectedDate.value) {
    return [] as SlotStatus[]
  }

  return buildSlotStatuses(
    selectedEventType.value,
    selectedDate.value,
    occupiedSlots.value,
  ).filter((slot) => slot.isAvailable)
})

const isReadyForBookingForm = computed(
  () => Boolean(selectedEventType.value) && Boolean(selectedSlot.value),
)

watch(selectedDate, () => {
  selectedSlot.value = null
})

function handleEventTypeSelect(eventType: EventType) {
  selectedEventType.value = eventType
  selectedDate.value = null
  selectedSlot.value = null
  successfulBooking.value = null
  isEventTypeDialogOpen.value = false
}

async function handleSlotSelect(slot: SlotStatus) {
  selectedSlot.value = slot
  isSlotDialogOpen.value = false
  await nextTick()
  bookingFormRef.value?.focusGuestName()
}

async function handleBookingSubmit(payload: {
  eventTypeId: string
  startTime: string
  guestName: string
  guestEmail: string
  comment?: string
}) {
  const createdBooking = await createBookingMutation.mutateAsync(payload)

  successfulBooking.value = createdBooking
  selectedEventType.value = null
  selectedDate.value = null
  selectedSlot.value = null
}

function resetFlow() {
  successfulBooking.value = null
  selectedDate.value = null
  selectedSlot.value = null
}

function openSlotDialog() {
  isSlotDialogOpen.value = true
}
</script>

<template>
  <div class="space-y-6">
    <Card class="w-full overflow-hidden">
      <div class="grid gap-6 lg:grid-cols-[1.1fr_0.9fr] lg:items-stretch">
        <div class="space-y-5">
          <span
            class="inline-flex rounded-full bg-sky-500/10 px-3 py-1 text-xs font-semibold uppercase tracking-[0.24em] text-sky-700 dark:text-sky-200"
          >
            Публичный сценарий
          </span>

          <div class="space-y-3">
            <h2 class="font-display text-3xl font-semibold sm:text-4xl">
              Выбери событие, день и точное время в одном понятном потоке
            </h2>
            <p class="max-w-3xl text-sm leading-7 text-muted-foreground sm:text-base">
              Сценарий устроен так, чтобы гость не принимал лишних решений. Сначала он
              выбирает формат звонка, потом видит слоты на выбранный день и только после
              этого оставляет контакты для уже выбранного времени.
            </p>
          </div>

          <div class="grid gap-3 sm:grid-cols-3">
            <div class="rounded-2xl bg-background/80 p-4">
              <p class="text-xs font-semibold uppercase tracking-[0.2em] text-muted-foreground">
                01
              </p>
              <p class="mt-3 text-sm font-medium">Выбрать тип события</p>
            </div>
            <div class="rounded-2xl bg-background/80 p-4">
              <p class="text-xs font-semibold uppercase tracking-[0.2em] text-muted-foreground">
                02
              </p>
              <p class="mt-3 text-sm font-medium">Подобрать дату и слот</p>
            </div>
            <div class="rounded-2xl bg-background/80 p-4">
              <p class="text-xs font-semibold uppercase tracking-[0.2em] text-muted-foreground">
                03
              </p>
              <p class="mt-3 text-sm font-medium">Оставить контактные данные</p>
            </div>
          </div>
        </div>

        <div class="rounded-[1.75rem] border border-white/40 bg-background/80 p-5 dark:border-white/10">
          <div class="flex items-start justify-between gap-4">
            <div>
              <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
                Текущий выбор
              </p>
              <h3 class="mt-2 font-display text-2xl font-semibold">
                {{ isReadyForBookingForm ? 'Можно бронировать' : 'Собираем детали звонка' }}
              </h3>
            </div>
            <span
              class="rounded-full px-3 py-1 text-xs font-semibold"
              :class="
                isReadyForBookingForm
                  ? 'bg-emerald-500/15 text-emerald-700 dark:text-emerald-300'
                  : 'bg-sky-500/15 text-sky-700 dark:text-sky-300'
              "
            >
              {{ isReadyForBookingForm ? 'Готово' : 'В процессе' }}
            </span>
          </div>

          <div class="mt-5 space-y-3">
            <div
              v-for="item in selectedFlowSummary"
              :key="item.label"
              class="flex items-start gap-3 rounded-2xl bg-secondary/40 p-4"
            >
              <component :is="item.icon" class="mt-0.5 h-5 w-5 shrink-0 text-sky-600 dark:text-sky-300" />
              <div class="min-w-0">
                <p class="text-xs font-semibold uppercase tracking-[0.2em] text-muted-foreground">
                  {{ item.label }}
                </p>
                <p class="mt-1 text-sm font-medium leading-6">
                  {{ item.value }}
                </p>
              </div>
            </div>
          </div>

        </div>
      </div>
    </Card>

    <div class="grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
      <section class="space-y-6">
        <Card>
          <div class="space-y-8">
            <section class="space-y-4">
              <div>
                <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
                  Шаг 1
                </p>
                <h3 class="mt-2 font-display text-2xl font-semibold">Выбери тип события</h3>
                <p class="mt-2 max-w-2xl text-sm leading-6 text-muted-foreground">
                  На странице показываем только одну карточку выбора. Список типов событий
                  открывается отдельно в модалке.
                </p>
              </div>

              <button
                type="button"
                class="w-full text-left"
                @click="isEventTypeDialogOpen = true"
              >
                <div
                  class="rounded-[1.75rem] border border-border bg-background/80 p-5 transition hover:border-primary/40 hover:shadow-md"
                >
                  <div class="flex items-start justify-between gap-4">
                    <div class="min-w-0">
                      <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
                        Тип события
                      </p>
                      <div v-if="selectedEventType" class="mt-3 space-y-2">
                        <div class="flex items-center gap-3">
                          <span
                            class="h-4 w-4 rounded-full border border-black/10"
                            :style="{ backgroundColor: selectedEventType.color }"
                          />
                          <p class="font-display text-2xl font-semibold">
                            {{ selectedEventType.title }}
                          </p>
                        </div>
                        <p class="text-sm leading-6 text-muted-foreground">
                          {{ selectedEventType.description }}
                        </p>
                        <div class="flex flex-wrap gap-2 text-xs font-medium">
                          <span class="rounded-full bg-secondary px-3 py-1 text-secondary-foreground">
                            {{ selectedEventType.durationMinutes }} минут
                          </span>
                          <span class="rounded-full bg-secondary px-3 py-1 text-secondary-foreground">
                            {{ selectedEventType.availableFrom }} - {{ selectedEventType.availableTo }}
                          </span>
                        </div>
                      </div>
                      <div v-else class="mt-3">
                        <p class="font-display text-2xl font-semibold">Выбери подходящий формат звонка</p>
                        <p class="mt-2 text-sm leading-6 text-muted-foreground">
                          Нажми на карточку, чтобы открыть список доступных типов событий.
                        </p>
                      </div>
                    </div>

                    <div class="flex shrink-0 items-center gap-3">
                      <span class="rounded-full bg-sky-500/10 px-3 py-1 text-xs font-semibold text-sky-700 dark:text-sky-300">
                        {{ selectedEventType ? 'Выбрано' : 'Выбрать' }}
                      </span>
                      <ChevronRight class="h-5 w-5 text-muted-foreground" />
                    </div>
                  </div>
                </div>
              </button>
            </section>

            <section class="space-y-4 border-t border-border/70 pt-8">
              <div>
                <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
                  Шаг 2
                </p>
                <h3 class="mt-2 font-display text-2xl font-semibold">Подбери дату и слот</h3>
                <p class="mt-2 max-w-2xl text-sm leading-6 text-muted-foreground">
                  На странице тоже одна карточка. Сам выбор даты и интервала происходит в
                  модалке со списком слотов.
                </p>
              </div>

              <button
                type="button"
                class="w-full text-left"
                @click="openSlotDialog"
              >
                <div
                  class="rounded-[1.75rem] border border-border bg-background/80 p-5 transition hover:border-primary/40 hover:shadow-md"
                >
                  <div class="flex items-start justify-between gap-4">
                    <div class="min-w-0">
                      <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
                        Дата и слот
                      </p>

                      <div v-if="selectedEventType && selectedSlot" class="mt-3 space-y-2">
                        <p class="font-display text-2xl font-semibold">
                          {{ formatHumanDate(selectedDate) }}
                        </p>
                        <p class="text-sm leading-6 text-muted-foreground">
                          {{ formatBookingDateTime(selectedSlot.startTime) }}
                        </p>
                        <p class="text-sm leading-6 text-muted-foreground">
                          Для события «{{ selectedEventType.title }}»
                        </p>
                      </div>

                      <div v-else-if="selectedEventType" class="mt-3">
                        <p class="font-display text-2xl font-semibold">Выбери день и время</p>
                        <p class="mt-2 text-sm leading-6 text-muted-foreground">
                          Откроем модалку с датой и полным списком слотов на выбранный день.
                        </p>
                      </div>

                      <div v-else class="mt-3">
                        <p class="font-display text-2xl font-semibold">Сначала выбери тип события</p>
                        <p class="mt-2 text-sm leading-6 text-muted-foreground">
                          После этого можно будет открыть список слотов и подобрать время.
                        </p>
                      </div>
                    </div>

                    <div class="flex shrink-0 items-center gap-3">
                      <span
                        class="rounded-full px-3 py-1 text-xs font-semibold"
                        :class="
                          selectedEventType && selectedSlot
                            ? 'bg-emerald-500/15 text-emerald-700 dark:text-emerald-300'
                            : 'bg-sky-500/10 text-sky-700 dark:text-sky-300'
                        "
                      >
                        {{ selectedEventType && selectedSlot ? 'Выбрано' : 'Выбрать' }}
                      </span>
                      <ChevronRight class="h-5 w-5 text-muted-foreground" />
                    </div>
                  </div>
                </div>
              </button>
            </section>
          </div>
        </Card>
      </section>

      <section class="space-y-6">
        <Card class="sticky top-6">
          <div class="mb-5">
            <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
              Шаг 3
            </p>
            <h3 class="mt-2 font-display text-2xl font-semibold">
              Оставь контактные данные
            </h3>
            <p class="mt-2 text-sm leading-6 text-muted-foreground">
              Этот блок активируется только после выбора конкретного слота.
            </p>
          </div>

          <div v-if="successfulBooking" class="space-y-5">
            <div class="flex items-start gap-3 rounded-[1.5rem] bg-emerald-500/10 p-4 text-emerald-700 dark:text-emerald-300">
              <CheckCircle2 class="mt-0.5 h-5 w-5 shrink-0" />
              <div>
                <p class="font-semibold">Бронирование успешно отправлено</p>
                <p class="mt-1 text-sm">
                  {{ formatBookingDateTime(successfulBooking.startTime) }}
                </p>
              </div>
            </div>

            <div class="rounded-[1.5rem] bg-secondary/50 p-4 text-sm">
              <p class="font-medium">{{ successfulBooking.guestName }}</p>
              <p class="mt-1 text-muted-foreground">{{ successfulBooking.guestEmail }}</p>
              <p
                v-if="successfulBooking.comment"
                class="mt-3 leading-6 text-muted-foreground"
              >
                {{ successfulBooking.comment }}
              </p>
            </div>

            <Button block variant="secondary" @click="resetFlow">
              Запланировать еще один звонок
            </Button>
          </div>

          <div v-else-if="selectedEventType && selectedSlot" class="space-y-5">
            <div class="rounded-[1.5rem] bg-secondary/50 p-4 text-sm">
              <p class="font-semibold">{{ selectedEventType.title }}</p>
              <p class="mt-1 text-muted-foreground">
                {{ formatBookingDateTime(selectedSlot.startTime) }}
              </p>
            </div>

            <BookingForm
              ref="bookingFormRef"
              :event-type-id="selectedEventType.id"
              :start-time="selectedSlot.startTime"
              :is-submitting="createBookingMutation.isPending.value"
              @submit="handleBookingSubmit"
            />
          </div>

          <div
            v-else
            class="rounded-[1.5rem] border border-dashed border-border bg-background/60 p-6 text-sm text-muted-foreground"
          >
            <div class="flex items-start gap-3">
              <UserRound class="mt-0.5 h-5 w-5 shrink-0 text-muted-foreground" />
              <p>
                После выбора слота здесь появится форма для имени, email и комментария.
              </p>
            </div>
          </div>
        </Card>
      </section>
    </div>

    <Dialog
      v-model:open="isEventTypeDialogOpen"
      title="Выбор типа события"
      description="Показываем список доступных форматов звонка. После выбора карточка на странице сразу заполняется."
      content-class="max-w-4xl"
    >
      <div v-if="isEventTypesLoading" class="grid gap-4 md:grid-cols-2">
        <div
          v-for="index in 4"
          :key="index"
          class="h-48 animate-pulse rounded-[1.75rem] bg-secondary/80"
        />
      </div>

      <div
        v-else-if="eventTypes.length"
        class="grid gap-4 md:grid-cols-2"
      >
        <button
          v-for="eventType in eventTypes"
          :key="eventType.id"
          type="button"
          class="text-left"
          @click="handleEventTypeSelect(eventType)"
        >
          <EventTypeCard
            :event-type="eventType"
            :selected="selectedEventType?.id === eventType.id"
            interactive
          />
        </button>
      </div>

      <div
        v-else
        class="rounded-[1.75rem] border border-dashed border-border bg-background/60 p-6 text-sm text-muted-foreground"
      >
        Пока нет типов событий. Создай первый тип в панели владельца.
      </div>
    </Dialog>

    <Dialog
      v-model:open="isSlotDialogOpen"
      title="Выбор даты и слота"
      description="Сначала выбираем день, затем загружаем занятость и строим дневную таблицу слотов."
      content-class="max-w-4xl"
    >
      <div v-if="!selectedEventType" class="rounded-[1.75rem] border border-dashed border-border bg-background/60 p-6 text-sm text-muted-foreground">
        Сначала выбери тип события на предыдущем шаге. После этого здесь появится выбор даты и статусы слотов.
      </div>

      <div v-else class="space-y-6">
        <div class="grid gap-4 rounded-[1.5rem] bg-secondary/40 p-4 md:grid-cols-[minmax(0,1fr)_18rem] md:items-end">
          <div>
            <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
              Текущее событие
            </p>
            <div class="mt-2 flex items-center gap-3">
              <span
                class="h-4 w-4 rounded-full border border-black/10"
                :style="{ backgroundColor: selectedEventType.color }"
              />
              <p class="font-medium">
                {{ selectedEventType.title }} · {{ selectedEventType.durationMinutes }} минут
              </p>
            </div>
            <p class="mt-2 text-sm text-muted-foreground">
              Окно записи: {{ selectedEventType.availableFrom }} - {{ selectedEventType.availableTo }}
            </p>
          </div>

          <DatePicker v-model="selectedDate" :min="getTodayDateValue()" />
        </div>

        <div v-if="!selectedDate" class="rounded-[1.75rem] border border-dashed border-border bg-background/60 p-6 text-sm leading-6 text-muted-foreground">
          Сначала выбери дату. После этого загрузим занятые интервалы на день и покажем полную таблицу свободных и занятых слотов.
        </div>

        <div v-else class="space-y-4">
          <div>
            <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
              Статус слотов
            </p>
            <h3 class="mt-2 font-display text-2xl font-semibold">
              {{ formatHumanDate(selectedDate) }}
            </h3>
          </div>

          <div
            v-if="isSlotsLoading"
            class="rounded-[1.75rem] border border-border/70 bg-background/50 p-4"
          >
            <div class="grid grid-cols-[repeat(auto-fit,minmax(min(100%,11rem),1fr))] gap-3">
              <div
                v-for="index in 8"
                :key="index"
                class="h-[4.5rem] animate-pulse rounded-2xl bg-secondary/80"
              />
            </div>
          </div>

          <div
            v-else-if="!displayedSlots.length"
            class="rounded-[1.75rem] border border-dashed border-border bg-background/60 p-6 text-sm leading-6 text-muted-foreground"
          >
            На этот день нет интервалов внутри окна доступности. Попробуй выбрать другую дату.
          </div>

          <div
            v-else
            class="max-h-[60vh] overflow-y-auto rounded-[1.75rem] border border-border/70 bg-background/50 p-4"
          >
            <TimeSlotGrid
              :slots="displayedSlots"
              :selected-start-time="selectedSlot?.startTime"
              @select="handleSlotSelect"
            />
          </div>
        </div>
      </div>
    </Dialog>
  </div>
</template>
