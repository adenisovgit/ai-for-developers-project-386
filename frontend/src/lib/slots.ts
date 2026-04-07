import type { EventType } from '@/api/models/event-type'
import type { OccupiedSlot } from '@/api/models/occupied-slot'
import { addMinutes, format, parseISO } from 'date-fns'

export interface SlotStatus {
  startTime: string
  endTime: string
  isAvailable: boolean
}

function createLocalDate(date: string, time: string) {
  return new Date(`${date}T${time}:00`)
}

function intervalsOverlap(
  leftStart: Date,
  leftEnd: Date,
  rightStart: Date,
  rightEnd: Date,
) {
  return leftStart < rightEnd && leftEnd > rightStart
}

export function buildSlotStatuses(
  eventType: EventType | null,
  selectedDate: string,
  occupiedSlots: OccupiedSlot[],
  now: Date,
) {
  if (!eventType) {
    return [] as SlotStatus[]
  }

  const durationMinutes = Number(eventType.durationMinutes)
  const dayStart = createLocalDate(selectedDate, eventType.availableFrom)
  const dayEnd = createLocalDate(selectedDate, eventType.availableTo)

  const occupiedIntervals = occupiedSlots.map((slot) => ({
    start: parseISO(slot.startTime),
    end: parseISO(slot.endTime),
  }))
  const isSelectedDateToday = selectedDate === format(now, 'yyyy-MM-dd')

  const slotStatuses: SlotStatus[] = []
  let cursor = new Date(dayStart)

  while (addMinutes(cursor, durationMinutes) <= dayEnd) {
    const slotStart = new Date(cursor)
    const slotEnd = addMinutes(slotStart, durationMinutes)
    const isPastSlot = isSelectedDateToday && slotStart <= now

    const isAvailable = !isPastSlot && !occupiedIntervals.some((interval) =>
      intervalsOverlap(slotStart, slotEnd, interval.start, interval.end),
    )

    slotStatuses.push({
      startTime: slotStart.toISOString(),
      endTime: slotEnd.toISOString(),
      isAvailable,
    })

    cursor = addMinutes(cursor, durationMinutes)
  }

  return slotStatuses
}
