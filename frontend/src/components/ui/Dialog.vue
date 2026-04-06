<script setup lang="ts">
import Button from '@/components/ui/Button.vue'
import { cn } from '@/lib/utils'

const props = withDefaults(
  defineProps<{
    open: boolean
    title?: string
    description?: string
    contentClass?: string
  }>(),
  {
    title: '',
    description: '',
    contentClass: '',
  },
)

const emit = defineEmits<{
  'update:open': [value: boolean]
}>()

function closeDialog() {
  emit('update:open', false)
}
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="props.open"
        class="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4 backdrop-blur-sm"
        @click.self="closeDialog"
      >
        <div
          :class="
            cn(
              'w-full max-w-2xl rounded-[2rem] border border-white/10 bg-background p-6 shadow-2xl',
              props.contentClass,
            )
          "
        >
          <div class="mb-6 flex items-start justify-between gap-4">
            <div class="space-y-1">
              <h2 v-if="title" class="font-display text-2xl font-semibold">{{ title }}</h2>
              <p v-if="description" class="text-sm text-muted-foreground">
                {{ description }}
              </p>
            </div>

            <Button variant="ghost" size="sm" @click="closeDialog">Закрыть</Button>
          </div>

          <slot />
        </div>
      </div>
    </Transition>
  </Teleport>
</template>
