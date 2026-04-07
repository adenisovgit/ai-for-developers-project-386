<script setup lang="ts">
import Button from '@/components/ui/Button.vue'
import Input from '@/components/ui/Input.vue'
import Label from '@/components/ui/Label.vue'
import type { BookingRequest } from '@/api/models/booking-request'
import type { BookingFormValues } from '@/lib/validation'
import { bookingSchema } from '@/lib/validation'
import { toTypedSchema } from '@vee-validate/zod'
import { useForm } from 'vee-validate'
import { ref, watch } from 'vue'

const props = defineProps<{
  eventTypeId: string
  startTime: string
  isSubmitting?: boolean
}>()

const emit = defineEmits<{
  submit: [payload: BookingRequest]
}>()

const { errors, defineField, handleSubmit, setFieldValue, resetForm } =
  useForm<BookingFormValues>({
    validationSchema: toTypedSchema(bookingSchema),
    initialValues: {
      eventTypeId: props.eventTypeId,
      startTime: props.startTime,
      guestName: '',
      guestEmail: '',
      comment: undefined,
    },
  })

const [guestName] = defineField('guestName')
const [guestEmail] = defineField('guestEmail')
const [comment] = defineField('comment')
const guestNameInputRef = ref<InstanceType<typeof Input> | null>(null)

watch(
  () => [props.eventTypeId, props.startTime] as const,
  ([nextEventTypeId, nextStartTime]) => {
    setFieldValue('eventTypeId', nextEventTypeId)
    setFieldValue('startTime', nextStartTime)
  },
  { immediate: true },
)

const onSubmit = handleSubmit((values) => {
  emit('submit', values)
  resetForm({
    values: {
      eventTypeId: props.eventTypeId,
      startTime: props.startTime,
      guestName: '',
      guestEmail: '',
      comment: undefined,
    },
  })
})

defineExpose({
  focusGuestName() {
    guestNameInputRef.value?.focus()
  },
})
</script>

<template>
  <form class="space-y-5" @submit.prevent="onSubmit">
    <div>
      <Label>Имя</Label>
      <Input
        ref="guestNameInputRef"
        v-model="guestName"
        placeholder="Как к тебе обращаться?"
        data-testid="guest-name-input"
      />
      <p v-if="errors.guestName" class="field-error">{{ errors.guestName }}</p>
    </div>

    <div>
      <Label>Email</Label>
      <Input
        v-model="guestEmail"
        type="email"
        placeholder="you@example.com"
        data-testid="guest-email-input"
      />
      <p v-if="errors.guestEmail" class="field-error">{{ errors.guestEmail }}</p>
    </div>

    <div>
      <Label>Комментарий</Label>
      <textarea
        v-model="comment"
        rows="4"
        data-testid="guest-comment-input"
        class="min-h-[120px] w-full rounded-xl border border-input bg-background/80 px-3 py-3 text-sm text-foreground transition placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
        placeholder="Необязательно, но можно коротко описать контекст."
      />
      <p v-if="errors.comment" class="field-error">{{ errors.comment }}</p>
    </div>

    <Button block type="submit" :disabled="isSubmitting" data-testid="booking-submit-button">
      {{ isSubmitting ? 'Отправляем...' : 'Забронировать слот' }}
    </Button>
  </form>
</template>
