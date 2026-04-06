<script setup lang="ts">
import BookingCard from '@/components/BookingCard.vue'
import Skeleton from '@/components/ui/Skeleton.vue'
import type { Booking } from '@/api/models/booking'
import type { EventType } from '@/api/models/event-type'

defineProps<{
  bookings: Booking[]
  eventTypesById: Record<string, EventType>
  isLoading?: boolean
}>()
</script>

<template>
  <div class="space-y-4">
    <div v-if="isLoading" class="grid gap-4 lg:grid-cols-2">
      <Skeleton v-for="index in 2" :key="index" class="h-52" />
    </div>

    <div v-else-if="bookings.length" class="grid gap-4 lg:grid-cols-2">
      <BookingCard
        v-for="booking in bookings"
        :key="booking.id"
        :booking="booking"
        :event-type="eventTypesById[booking.eventTypeId]"
      />
    </div>

    <div
      v-else
      class="rounded-[1.75rem] border border-dashed border-border bg-background/60 p-6 text-sm text-muted-foreground"
    >
      Предстоящих бронирований пока нет.
    </div>
  </div>
</template>
