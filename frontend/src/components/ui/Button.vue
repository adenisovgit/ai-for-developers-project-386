<script setup lang="ts">
import { cn } from '@/lib/utils'
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    variant?: 'primary' | 'secondary' | 'ghost' | 'destructive'
    size?: 'sm' | 'md' | 'lg'
    block?: boolean
    type?: 'button' | 'submit' | 'reset'
    disabled?: boolean
  }>(),
  {
    variant: 'primary',
    size: 'md',
    block: false,
    type: 'button',
    disabled: false,
  },
)

const classes = computed(() =>
  cn(
    'inline-flex items-center justify-center rounded-xl font-medium transition duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background disabled:cursor-not-allowed disabled:opacity-60',
    props.block && 'w-full',
    props.size === 'sm' && 'h-9 px-3 text-sm',
    props.size === 'md' && 'h-11 px-4 text-sm',
    props.size === 'lg' && 'h-12 px-5 text-base',
    props.variant === 'primary' &&
      'bg-primary text-primary-foreground shadow-sm hover:-translate-y-0.5 hover:shadow-lg',
    props.variant === 'secondary' &&
      'bg-secondary text-secondary-foreground hover:bg-secondary/80',
    props.variant === 'ghost' &&
      'bg-transparent text-foreground hover:bg-secondary',
    props.variant === 'destructive' &&
      'bg-destructive text-destructive-foreground hover:bg-destructive/90',
  ),
)
</script>

<template>
  <button :type="type" :disabled="disabled" :class="classes">
    <slot />
  </button>
</template>
