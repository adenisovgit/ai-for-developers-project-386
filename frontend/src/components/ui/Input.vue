<script setup lang="ts">
import { cn } from '@/lib/utils'
import { ref } from 'vue'

const model = defineModel<string | number>()
const inputRef = ref<HTMLInputElement | null>(null)

withDefaults(
  defineProps<{
    type?: string
    placeholder?: string
    disabled?: boolean
    min?: string
    max?: string
    step?: string
  }>(),
  {
    type: 'text',
    placeholder: '',
    disabled: false,
  },
)

defineExpose({
  focus() {
    inputRef.value?.focus()
  },
})
</script>

<template>
  <input
    ref="inputRef"
    v-model="model"
    :type="type"
    :placeholder="placeholder"
    :disabled="disabled"
    :min="min"
    :max="max"
    :step="step"
    :class="
      cn(
        'h-11 w-full rounded-xl border border-input bg-background/80 px-3 text-sm text-foreground transition placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-60',
      )
    "
  />
</template>
