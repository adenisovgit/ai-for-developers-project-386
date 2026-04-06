<script setup lang="ts">
import Input from '@/components/ui/Input.vue'
import Label from '@/components/ui/Label.vue'
import { cn } from '@/lib/utils'

const model = defineModel<string>({ required: true })

defineProps<{
  error?: string
}>()

const presetColors = [
  { name: 'Синий', value: '#3B82F6' },
  { name: 'Зеленый', value: '#22C55E' },
  { name: 'Желтый', value: '#EAB308' },
  { name: 'Оранжевый', value: '#F97316' },
  { name: 'Красный', value: '#EF4444' },
  { name: 'Фиолетовый', value: '#8B5CF6' },
  { name: 'Розовый', value: '#EC4899' },
  { name: 'Бирюзовый', value: '#14B8A6' },
]
</script>

<template>
  <div>
    <Label>Цвет события</Label>
    <div class="mb-3 grid grid-cols-4 gap-2 sm:grid-cols-8">
      <button
        v-for="color in presetColors"
        :key="color.value"
        type="button"
        :title="color.name"
        :class="
          cn(
            'h-11 rounded-xl border-2 transition hover:-translate-y-0.5',
            model === color.value ? 'border-foreground scale-[1.02]' : 'border-transparent',
          )
        "
        :style="{ backgroundColor: color.value }"
        @click="model = color.value"
      />
    </div>
    <Input v-model="model" placeholder="#22C55E" />
    <p v-if="error" class="field-error">{{ error }}</p>
  </div>
</template>
