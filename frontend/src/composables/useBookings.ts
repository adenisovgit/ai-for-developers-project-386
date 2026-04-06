import type { Booking } from '@/api/models/booking'
import type { BookingRequest } from '@/api/models/booking-request'
import { useToast } from '@/composables/useToast'
import { apiClient } from '@/lib/api-client'
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import { computed, ref } from 'vue'

const localBookings = ref<Booking[]>([])

function mergeBookings(baseItems: Booking[], createdItems: Booking[]) {
  const map = new Map<string, Booking>()

  for (const item of baseItems) {
    map.set(item.id, item)
  }

  for (const item of createdItems) {
    map.set(item.id, item)
  }

  return Array.from(map.values()).sort(
    (left, right) =>
      new Date(left.startTime).getTime() - new Date(right.startTime).getTime(),
  )
}

export function useBookings() {
  const query = useQuery({
    queryKey: ['bookings'],
    queryFn: async () => (await apiClient.bookingsList()).data,
  })

  return {
    ...query,
    bookings: computed(() => mergeBookings(query.data.value ?? [], localBookings.value)),
  }
}

export function useCreateBooking() {
  const queryClient = useQueryClient()
  const { showToast } = useToast()

  return useMutation({
    mutationFn: async (payload: BookingRequest) => {
      await apiClient.bookingsCreate(payload)

      const createdBooking: Booking = {
        ...payload,
        id: crypto.randomUUID(),
      }

      return createdBooking
    },
    onSuccess: (createdBooking) => {
      localBookings.value = [createdBooking, ...localBookings.value]
      queryClient.setQueryData<Booking[]>(['bookings'], (current = []) =>
        mergeBookings(current, [createdBooking]),
      )
      showToast({
        variant: 'success',
        title: 'Бронирование отправлено',
        description: 'Заявка сохранена локально и показана в панели владельца.',
      })
    },
    onError: () => {
      showToast({
        variant: 'error',
        title: 'Не удалось отправить бронирование',
        description: 'Попробуй еще раз через пару секунд.',
      })
    },
  })
}
