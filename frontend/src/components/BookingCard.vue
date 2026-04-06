<script setup lang="ts">
import Card from '@/components/ui/Card.vue'
import { formatBookingDateTime } from '@/lib/date'
import type { Booking } from '@/api/models/booking'
import type { EventType } from '@/api/models/event-type'

defineProps<{
  booking: Booking
  eventType?: EventType
}>()
</script>

<template>
  <Card class="h-full">
    <div class="flex h-full flex-col gap-4">
      <div class="flex items-start justify-between gap-3">
        <div>
          <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
            Бронирование
          </p>
          <h3 class="mt-2 font-display text-xl font-semibold">{{ booking.guestName }}</h3>
        </div>
        <span
          v-if="eventType"
          class="mt-1 h-4 w-4 rounded-full border border-black/10"
          :style="{ backgroundColor: eventType.color }"
        />
      </div>

      <dl class="space-y-3 text-sm">
        <div>
          <dt class="text-muted-foreground">Тип события</dt>
          <dd class="font-medium">{{ eventType?.title ?? booking.eventTypeId }}</dd>
        </div>
        <div>
          <dt class="text-muted-foreground">Когда</dt>
          <dd class="font-medium">{{ formatBookingDateTime(booking.startTime) }}</dd>
        </div>
        <div>
          <dt class="text-muted-foreground">Email</dt>
          <dd class="font-medium">{{ booking.guestEmail }}</dd>
        </div>
        <div v-if="booking.comment">
          <dt class="text-muted-foreground">Комментарий</dt>
          <dd class="font-medium">{{ booking.comment }}</dd>
        </div>
      </dl>
    </div>
  </Card>
</template>
