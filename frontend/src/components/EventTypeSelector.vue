<script setup lang="ts">
import EventTypeCard from '@/components/EventTypeCard.vue'
import Skeleton from '@/components/ui/Skeleton.vue'
import type { EventType } from '@/api/models/event-type'

defineProps<{
  eventTypes: EventType[]
  selectedId?: string
  isLoading?: boolean
}>()

const emit = defineEmits<{
  select: [eventType: EventType]
}>()
</script>

<template>
  <div class="space-y-4">
    <div v-if="isLoading" class="grid gap-4 md:grid-cols-2">
      <Skeleton v-for="index in 2" :key="index" class="h-48" />
    </div>

    <div v-else-if="eventTypes.length" class="grid gap-4 md:grid-cols-2">
      <button
        v-for="eventType in eventTypes"
        :key="eventType.id"
        type="button"
        class="text-left"
        @click="emit('select', eventType)"
      >
        <EventTypeCard
          :event-type="eventType"
          :selected="selectedId === eventType.id"
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
  </div>
</template>
