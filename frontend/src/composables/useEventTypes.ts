import type { EventType } from '@/api/models/event-type'
import type { EventTypeCreateRequest } from '@/api/models/event-type-create-request'
import { apiClient } from '@/lib/api-client'
import { normalizeHourTime } from '@/lib/date'
import { useToast } from '@/composables/useToast'
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import { computed, ref } from 'vue'

const localEventTypes = ref<EventType[]>([])

function mergeEventTypes(baseItems: EventType[], createdItems: EventType[]) {
  const map = new Map<string, EventType>()

  for (const item of baseItems) {
    map.set(item.id, item)
  }

  for (const item of createdItems) {
    map.set(item.id, item)
  }

  return Array.from(map.values())
}

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
    eventTypes: computed(() =>
      mergeEventTypes(
        (query.data.value ?? []).map(normalizeEventType),
        localEventTypes.value,
      ),
    ),
  }
}

export function useCreateEventType() {
  const queryClient = useQueryClient()
  const { showToast } = useToast()

  return useMutation({
    mutationFn: async (payload: EventTypeCreateRequest) => {
      await apiClient.eventTypesCreate(payload)

      const createdEventType: EventType = {
        ...payload,
        id: crypto.randomUUID(),
      }

      return createdEventType
    },
    onSuccess: (createdEventType) => {
      localEventTypes.value = [createdEventType, ...localEventTypes.value]
      queryClient.setQueryData<EventType[]>(['event-types'], (current = []) =>
        mergeEventTypes(current, [createdEventType]),
      )
      showToast({
        variant: 'success',
        title: 'Тип события создан',
        description: 'Новая карточка уже добавлена в локальный интерфейс.',
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
