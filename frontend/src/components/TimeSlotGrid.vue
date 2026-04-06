<script setup lang="ts">
import type { SlotStatus } from '@/lib/slots'
import { formatSlotIntervalRange } from '@/lib/date'
import { cn } from '@/lib/utils'

defineProps<{
  slots: SlotStatus[]
  selectedStartTime?: string
  disabled?: boolean
}>()

const emit = defineEmits<{
  select: [slot: SlotStatus]
}>()
</script>

<template>
  <div class="grid grid-cols-[repeat(auto-fit,minmax(min(100%,11rem),1fr))] gap-3">
    <button
      v-for="slot in slots"
      :key="slot.startTime"
      type="button"
      :disabled="disabled"
      :class="
        cn(
          'flex w-full items-center justify-center rounded-2xl border px-5 py-4 text-center transition focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-60',
          'border-slate-300 bg-background/90 text-foreground hover:border-primary/40 hover:shadow-md dark:border-slate-600',
          selectedStartTime === slot.startTime &&
            'border-orange-500 bg-orange-500 text-white shadow-lg',
        )
      "
      @click="emit('select', slot)"
    >
      <span class="font-display text-xl font-semibold tracking-tight">
        {{ formatSlotIntervalRange(slot.startTime, slot.endTime) }}
      </span>
    </button>
  </div>
</template>
