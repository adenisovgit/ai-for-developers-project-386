<script setup lang="ts">
import BookingList from '@/components/BookingList.vue'
import EventTypeCard from '@/components/EventTypeCard.vue'
import EventTypeForm from '@/components/EventTypeForm.vue'
import Button from '@/components/ui/Button.vue'
import Card from '@/components/ui/Card.vue'
import Dialog from '@/components/ui/Dialog.vue'
import { useBookings } from '@/composables/useBookings'
import { useCreateEventType, useEventTypes } from '@/composables/useEventTypes'
import type { EventTypeCreateRequest } from '@/api/models/event-type-create-request'
import { computed, ref } from 'vue'

const { eventTypes, isLoading: isEventTypesLoading } = useEventTypes()
const { bookings, isLoading: isBookingsLoading } = useBookings()
const createEventTypeMutation = useCreateEventType()

const isDialogOpen = ref(false)
const formKey = ref(0)

const eventTypesById = computed(() =>
  Object.fromEntries(eventTypes.value.map((eventType) => [eventType.id, eventType])),
)

async function handleCreateEventType(payload: EventTypeCreateRequest) {
  await createEventTypeMutation.mutateAsync(payload)
  isDialogOpen.value = false
  formKey.value += 1
}
</script>

<template>
  <div class="space-y-6">
    <Card>
      <div class="flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
        <div class="space-y-3">
          <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
            Панель владельца
          </p>
          <div class="space-y-2">
            <h2 class="font-display text-3xl font-semibold sm:text-4xl">
              Управляй типами событий и следи за входящими бронированиями
            </h2>
            <p class="max-w-3xl text-sm leading-6 text-muted-foreground sm:text-base">
              Здесь можно быстро добавить новый формат звонка с часовыми окнами
              доступности и видеть текущие бронирования, собранные из mock API и локального
              create-flow.
            </p>
          </div>
        </div>

        <Button size="lg" @click="isDialogOpen = true">
          Создать новый тип события
        </Button>
      </div>
    </Card>

    <section class="space-y-4">
      <div class="flex items-center justify-between gap-3">
        <div>
          <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
            Типы событий
          </p>
          <h3 class="mt-2 font-display text-2xl font-semibold">Каталог созвонов</h3>
        </div>
      </div>

      <div v-if="isEventTypesLoading" class="grid gap-4 lg:grid-cols-3">
        <div
          v-for="index in 3"
          :key="index"
          class="h-52 animate-pulse rounded-[1.75rem] bg-secondary/80"
        />
      </div>

      <div v-else class="grid gap-4 lg:grid-cols-3">
        <EventTypeCard
          v-for="eventType in eventTypes"
          :key="eventType.id"
          :event-type="eventType"
        />
      </div>
    </section>

    <section class="space-y-4">
      <div>
        <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
          Предстоящие бронирования
        </p>
        <h3 class="mt-2 font-display text-2xl font-semibold">Очередь созвонов</h3>
      </div>

      <BookingList
        :bookings="bookings"
        :event-types-by-id="eventTypesById"
        :is-loading="isBookingsLoading"
      />
    </section>

    <Dialog
      v-model:open="isDialogOpen"
      title="Новый тип события"
      description="Форма использует только часовые значения для окна доступности и обязательный HEX-цвет."
    >
      <EventTypeForm
        :key="formKey"
        :is-submitting="createEventTypeMutation.isPending.value"
        @submit="handleCreateEventType"
      />
    </Dialog>
  </div>
</template>
