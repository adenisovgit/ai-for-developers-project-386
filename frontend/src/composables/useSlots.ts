import type { OccupiedSlot } from '@/api/models/occupied-slot'
import { apiClient } from '@/lib/api-client'
import { useQuery } from '@tanstack/vue-query'
import { computed, toValue, type MaybeRefOrGetter } from 'vue'

export function useSlots(
  selectedDate: MaybeRefOrGetter<string | null | undefined>,
) {
  const queryKey = computed(() => ['slots', toValue(selectedDate) ?? ''])

  const enabled = computed(() => Boolean(toValue(selectedDate)))

  const query = useQuery({
    queryKey,
    enabled,
    queryFn: async () => {
      const currentDate = toValue(selectedDate)

      if (!currentDate) {
        return [] as OccupiedSlot[]
      }

      return (await apiClient.slotsList(currentDate, currentDate)).data
    },
  })

  return {
    ...query,
    occupiedSlots: computed(() => query.data.value ?? []),
  }
}
