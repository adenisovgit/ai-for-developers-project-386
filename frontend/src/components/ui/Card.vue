<script setup lang="ts">
import { cn } from '@/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <div
    :class="
      cn(
        'rounded-[1.75rem] border border-white/40 bg-white/75 p-5 shadow-card backdrop-blur dark:border-white/10 dark:bg-slate-950/70',
        $props.class,
      )
    "
  >
    <slot />
  </div>
</template>
