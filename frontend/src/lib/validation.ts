import { z } from 'zod'

export const durationValues = [15, 30, 45, 60] as const
export const hourOnlyTimeRegex = /^(?:[01][0-9]|2[0-3]):00$/
export const hexColorRegex = /^#[0-9A-Fa-f]{6}$/

const durationSchema = z.preprocess(
  (value) => Number(value),
  z.union([
    z.literal(15),
    z.literal(30),
    z.literal(45),
    z.literal(60),
  ]),
)

export const eventTypeSchema = z
  .object({
    title: z.string().trim().min(1, 'Укажите название').max(100, 'Максимум 100 символов'),
    description: z
      .string()
      .trim()
      .min(1, 'Добавьте короткое описание')
      .max(500, 'Максимум 500 символов'),
    durationMinutes: durationSchema,
    availableFrom: z
      .string()
      .regex(hourOnlyTimeRegex, 'Допустимы только значения, кратные часу'),
    availableTo: z
      .string()
      .regex(hourOnlyTimeRegex, 'Допустимы только значения, кратные часу'),
    color: z.string().regex(hexColorRegex, 'Укажите HEX-цвет вида #RRGGBB'),
  })
  .refine((data) => data.availableFrom < data.availableTo, {
    path: ['availableTo'],
    message: 'Время начала должно быть меньше времени окончания',
  })

export const bookingSchema = z.object({
  eventTypeId: z.string().uuid('Выберите тип события'),
  startTime: z.string().datetime(),
  guestName: z.string().trim().min(2, 'Введите имя').max(100, 'Максимум 100 символов'),
  guestEmail: z.string().trim().email('Введите корректный email'),
  comment: z.preprocess(
    (value) => (typeof value === 'string' && value.trim() === '' ? undefined : value),
    z.string().trim().max(1000, 'Максимум 1000 символов').optional(),
  ),
})

export type EventTypeFormValues = z.infer<typeof eventTypeSchema>
export type BookingFormValues = z.infer<typeof bookingSchema>
