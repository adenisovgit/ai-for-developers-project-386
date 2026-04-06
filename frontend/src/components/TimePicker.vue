<script setup lang="ts">
import Label from '@/components/ui/Label.vue'
import Select from '@/components/ui/Select.vue'

const model = defineModel<string>({ required: true })

defineProps<{
  label: string
}>()

const hourOptions = Array.from({ length: 24 }, (_, index) =>
  `${String(index).padStart(2, '0')}:00`,
)
</script>

<template>
  <div>
    <Label>{{ label }}</Label>
    <Select v-model="model">
      <option v-for="option in hourOptions" :key="option" :value="option">
        {{ option }}
      </option>
    </Select>
  </div>
</template>
