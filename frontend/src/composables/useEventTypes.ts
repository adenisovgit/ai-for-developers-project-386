import type { EventType } from '@/api/models/event-type'
import type { EventTypeCreateRequest } from '@/api/models/event-type-create-request'
import { apiClient } from '@/lib/api-client'
import { normalizeHourTime } from '@/lib/date'
import { useToast } from '@/composables/useToast'
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import { computed } from 'vue'

function normalizeEventType(eventType: EventType): EventType {
  return {
    ...eventType,
    availableFrom: normalizeHourTime(eventType.availableFrom),
    availableTo: normalizeHourTime(eventType.availableTo),
  }
}

export function useEventTypes() {
  const query = useQuery({
    queryKey: ['event-types'],
    queryFn: async () => (await apiClient.eventTypesList()).data,
  })

  return {
    ...query,
    eventTypes: computed(() => (query.data.value ?? []).map(normalizeEventType)),
  }
}

export function useCreateEventType() {
  const queryClient = useQueryClient()
  const { showToast } = useToast()

  return useMutation({
    mutationFn: async (payload: EventTypeCreateRequest) =>
      normalizeEventType((await apiClient.eventTypesCreate(payload)).data),
    onSuccess: (createdEventType) => {
      queryClient.setQueryData<EventType[]>(['event-types'], (current = []) => [
        ...current.filter((item) => item.id !== createdEventType.id),
        createdEventType,
      ])
      showToast({
        variant: 'success',
        title: 'Тип события создан',
        description: 'Новая карточка сохранена на сервере и добавлена в каталог.',
      })
    },
    onError: () => {
      showToast({
        variant: 'error',
        title: 'Не удалось создать тип события',
        description: 'Проверь форму и повтори отправку.',
      })
    },
  })
}
