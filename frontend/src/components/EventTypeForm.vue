<script setup lang="ts">
import ColorPickerField from '@/components/ColorPickerField.vue'
import DurationSelect from '@/components/DurationSelect.vue'
import TimePicker from '@/components/TimePicker.vue'
import Button from '@/components/ui/Button.vue'
import Input from '@/components/ui/Input.vue'
import Label from '@/components/ui/Label.vue'
import type { EventTypeCreateRequest } from '@/api/models/event-type-create-request'
import type { EventTypeFormValues } from '@/lib/validation'
import { eventTypeSchema } from '@/lib/validation'
import { toTypedSchema } from '@vee-validate/zod'
import { useForm } from 'vee-validate'

const props = defineProps<{
  isSubmitting?: boolean
}>()

const emit = defineEmits<{
  submit: [payload: EventTypeCreateRequest]
}>()

const {
  errors,
  defineField,
  handleSubmit,
} = useForm<EventTypeFormValues>({
  validationSchema: toTypedSchema(eventTypeSchema),
  initialValues: {
    title: '',
    description: '',
    durationMinutes: 30,
    availableFrom: '09:00',
    availableTo: '18:00',
    color: '#3B82F6',
  },
})

const [title] = defineField('title')
const [description] = defineField('description')
const [durationMinutes] = defineField('durationMinutes')
const [availableFrom] = defineField('availableFrom')
const [availableTo] = defineField('availableTo')
const [color] = defineField('color')

const onSubmit = handleSubmit((values) => {
  emit('submit', values)
})
</script>

<template>
  <form class="space-y-5" @submit.prevent="onSubmit">
    <div>
      <Label>Название</Label>
      <Input
        v-model="title"
        data-testid="event-type-title-input"
        placeholder="Например, Знакомство"
      />
      <p v-if="errors.title" class="field-error">{{ errors.title }}</p>
    </div>

    <div>
      <Label>Описание</Label>
      <textarea
        v-model="description"
        rows="4"
        data-testid="event-type-description-input"
        class="min-h-[120px] w-full rounded-xl border border-input bg-background/80 px-3 py-3 text-sm text-foreground transition placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
        placeholder="Коротко опиши, о чем этот звонок."
      />
      <p v-if="errors.description" class="field-error">{{ errors.description }}</p>
    </div>

    <div class="grid gap-4 sm:grid-cols-2">
      <div>
        <DurationSelect v-model="durationMinutes" select-test-id="event-type-duration-select" />
        <p v-if="errors.durationMinutes" class="field-error">{{ errors.durationMinutes }}</p>
      </div>

      <div class="grid grid-cols-2 gap-4">
        <div>
          <TimePicker v-model="availableFrom" label="С" select-test-id="event-type-from-select" />
          <p v-if="errors.availableFrom" class="field-error">{{ errors.availableFrom }}</p>
        </div>
        <div>
          <TimePicker v-model="availableTo" label="До" select-test-id="event-type-to-select" />
          <p v-if="errors.availableTo" class="field-error">{{ errors.availableTo }}</p>
        </div>
      </div>
    </div>

    <ColorPickerField
      v-model="color"
      :error="errors.color"
      input-test-id="event-type-color-input"
    />

    <div class="flex justify-end">
      <Button type="submit" :disabled="isSubmitting" data-testid="event-type-submit-button">
        {{ isSubmitting ? 'Сохраняем...' : 'Создать тип события' }}
      </Button>
    </div>
  </form>
</template>
