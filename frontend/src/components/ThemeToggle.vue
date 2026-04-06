<script setup lang="ts">
import { cn } from '@/lib/utils'
import { useTheme, type ThemeMode } from '@/composables/useTheme'
import { Sun, Moon, Monitor } from 'lucide-vue-next'

const { themeMode, setTheme } = useTheme()

const options: Array<{ value: ThemeMode; label: string; icon: typeof Sun }> = [
  { value: 'auto', label: 'Авто', icon: Monitor },
  { value: 'light', label: 'Светлая', icon: Sun },
  { value: 'dark', label: 'Темная', icon: Moon },
]
</script>

<template>
  <div
    class="flex h-full items-stretch gap-0 overflow-hidden rounded-l-xl rounded-r-[0.95rem] border-l border-white/40 bg-background/80 dark:border-white/10"
  >
    <button
      v-for="option in options"
      :key="option.value"
      :class="
        cn(
          'inline-flex h-full items-center justify-center rounded-none px-3 text-sm font-medium transition-colors first:rounded-l-xl last:rounded-r-[0.95rem]',
          themeMode === option.value
            ? 'cursor-default bg-primary text-primary-foreground pointer-events-none'
            : 'text-foreground hover:bg-secondary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-inset',
        )
      "
      @click="setTheme(option.value)"
    >
      <component :is="option.icon" class="h-4 w-4 sm:mr-2" />
      <span class="hidden sm:inline">{{ option.label }}</span>
    </button>
  </div>
</template>
