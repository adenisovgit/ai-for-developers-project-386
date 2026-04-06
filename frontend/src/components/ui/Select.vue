<script setup lang="ts">
import { cn } from '@/lib/utils'

const model = defineModel<string | number>()

defineProps<{
  disabled?: boolean
}>()
</script>

<template>
  <select
    v-model="model"
    :disabled="disabled"
    :class="
      cn(
        'h-11 w-full rounded-xl border border-input bg-background/80 px-3 text-sm text-foreground transition focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-60',
      )
    "
  >
    <slot />
  </select>
</template>
