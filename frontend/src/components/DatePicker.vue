<script setup lang="ts">
import Input from '@/components/ui/Input.vue'
import Label from '@/components/ui/Label.vue'

const model = defineModel<string | null>({ required: true })

defineProps<{
  min?: string
}>()
</script>

<template>
  <div>
    <Label>Дата звонка</Label>
    <Input v-model="model" type="date" :min="min" data-testid="booking-date-input" />
  </div>
</template>
