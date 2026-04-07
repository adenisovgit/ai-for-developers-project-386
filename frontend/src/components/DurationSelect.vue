<script setup lang="ts">
import Label from '@/components/ui/Label.vue'
import Select from '@/components/ui/Select.vue'
import { durationValues } from '@/lib/validation'

const model = defineModel<string | number>({ required: true })

defineProps<{
  selectTestId?: string
}>()
</script>

<template>
  <div>
    <Label>Длительность</Label>
    <Select v-model="model" :data-testid="selectTestId">
      <option v-for="duration in durationValues" :key="duration" :value="duration">
        {{ duration }} минут
      </option>
    </Select>
  </div>
</template>
