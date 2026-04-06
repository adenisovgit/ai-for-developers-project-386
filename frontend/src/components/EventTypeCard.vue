<script setup lang="ts">
import Card from '@/components/ui/Card.vue'
import { cn } from '@/lib/utils'
import type { EventType } from '@/api/models/event-type'

defineProps<{
  eventType: EventType
  selected?: boolean
  interactive?: boolean
}>()
</script>

<template>
  <Card
    :class="
      cn(
        'h-full transition duration-200',
        interactive && 'hover:-translate-y-1 hover:shadow-2xl',
        selected && 'ring-2 ring-ring',
      )
    "
  >
    <div class="flex h-full flex-col gap-4">
      <div class="flex items-start justify-between gap-3">
        <div>
          <p class="text-xs font-semibold uppercase tracking-[0.24em] text-muted-foreground">
            Тип события
          </p>
          <h3 class="mt-2 font-display text-xl font-semibold">{{ eventType.title }}</h3>
        </div>
        <span
          class="mt-1 h-4 w-4 shrink-0 rounded-full border border-black/10"
          :style="{ backgroundColor: eventType.color }"
        />
      </div>

      <p class="text-sm leading-6 text-muted-foreground">
        {{ eventType.description }}
      </p>

      <div class="mt-auto flex flex-wrap gap-2 text-xs font-medium">
        <span class="rounded-full bg-secondary px-3 py-1 text-secondary-foreground">
          {{ eventType.durationMinutes }} минут
        </span>
        <span class="rounded-full bg-secondary px-3 py-1 text-secondary-foreground">
          {{ eventType.availableFrom }} - {{ eventType.availableTo }}
        </span>
      </div>
    </div>
  </Card>
</template>
