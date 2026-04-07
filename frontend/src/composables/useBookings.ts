import type { Booking } from '@/api/models/booking'
import type { BookingRequest } from '@/api/models/booking-request'
import { useToast } from '@/composables/useToast'
import { apiClient } from '@/lib/api-client'
import axios from 'axios'
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import { computed } from 'vue'

export function useBookings() {
  const query = useQuery({
    queryKey: ['bookings'],
    queryFn: async () => (await apiClient.bookingsList()).data,
  })

  return {
    ...query,
    bookings: computed(() =>
      [...(query.data.value ?? [])].sort(
        (left, right) =>
          new Date(left.startTime).getTime() - new Date(right.startTime).getTime(),
      ),
    ),
  }
}

export function useCreateBooking(options?: { onConflict?: () => Promise<void> | void }) {
  const queryClient = useQueryClient()
  const { showToast } = useToast()

  return useMutation({
    mutationFn: async (payload: BookingRequest) =>
      (await apiClient.bookingsCreate(payload)).data,
    onSuccess: (createdBooking) => {
      queryClient.setQueryData<Booking[]>(['bookings'], (current = []) => [
        ...current.filter((item) => item.id !== createdBooking.id),
        createdBooking,
      ])
      showToast({
        variant: 'success',
        title: 'Бронирование отправлено',
        description: 'Заявка сохранена на сервере и появилась в панели владельца.',
      })
    },
    onError: async (error) => {
      if (axios.isAxiosError(error) && error.response?.status === 409) {
        await options?.onConflict?.()
        showToast({
          variant: 'error',
          title: 'Слот уже занят',
          description: 'Кто-то успел забронировать это время чуть раньше. Мы уже обновили занятость на день.',
        })

        return
      }

      showToast({
        variant: 'error',
        title: 'Не удалось отправить бронирование',
        description: 'Попробуй еще раз через пару секунд.',
      })
    },
  })
}
