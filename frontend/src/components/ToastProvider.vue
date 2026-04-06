<script setup lang="ts">
import { useToast } from '@/composables/useToast'
import { CheckCircle2, CircleAlert, Info } from 'lucide-vue-next'

const { toasts, removeToast } = useToast()

function getIcon(variant: 'success' | 'error' | 'info') {
  if (variant === 'success') {
    return CheckCircle2
  }

  if (variant === 'error') {
    return CircleAlert
  }

  return Info
}
</script>

<template>
  <div class="pointer-events-none fixed inset-x-0 bottom-4 z-[60] flex flex-col items-center gap-3 px-4">
    <TransitionGroup
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="translate-y-2 opacity-0"
      enter-to-class="translate-y-0 opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="translate-y-0 opacity-100"
      leave-to-class="translate-y-2 opacity-0"
    >
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="pointer-events-auto flex w-full max-w-md items-start gap-3 rounded-2xl border border-white/40 bg-white/90 p-4 shadow-card backdrop-blur dark:border-white/10 dark:bg-slate-950/90"
      >
        <component
          :is="getIcon(toast.variant)"
          class="mt-0.5 h-5 w-5 shrink-0"
          :class="
            toast.variant === 'success'
              ? 'text-emerald-500'
              : toast.variant === 'error'
                ? 'text-red-500'
                : 'text-sky-500'
          "
        />
        <div class="min-w-0 flex-1">
          <p class="text-sm font-semibold">{{ toast.title }}</p>
          <p v-if="toast.description" class="mt-1 text-sm text-muted-foreground">
            {{ toast.description }}
          </p>
        </div>
        <button
          class="text-sm text-muted-foreground transition hover:text-foreground"
          @click="removeToast(toast.id)"
        >
          Закрыть
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>
