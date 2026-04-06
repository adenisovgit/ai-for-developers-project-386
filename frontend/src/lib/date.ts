import { addMinutes, format, parseISO } from 'date-fns'
import { ru } from 'date-fns/locale'

export function getTodayDateValue() {
  return format(new Date(), 'yyyy-MM-dd')
}

export function formatHumanDate(date: string) {
  return format(parseISO(`${date}T00:00:00`), 'EEEE, d MMMM', { locale: ru })
}

export function normalizeHourTime(value: string) {
  const plainMatch = value.match(/^(\d{2}:\d{2})/)

  if (plainMatch) {
    return plainMatch[1]
  }

  const isoMatch = value.match(/T(\d{2}:\d{2})/)

  if (isoMatch) {
    return isoMatch[1]
  }

  return value
}

export function formatSlotTime(dateTime: string) {
  return format(parseISO(dateTime), 'HH:mm')
}

export function formatSlotInterval(dateTime: string, durationMinutes: number) {
  const start = parseISO(dateTime)
  const end = addMinutes(start, durationMinutes)

  return `${format(start, 'HH:mm')} - ${format(end, 'HH:mm')}`
}

export function formatSlotIntervalRange(startTime: string, endTime: string) {
  const start = parseISO(startTime)
  const end = parseISO(endTime)

  return `${format(start, 'HH:mm')} - ${format(end, 'HH:mm')}`
}

export function formatBookingDateTime(dateTime: string) {
  return format(parseISO(dateTime), "d MMMM yyyy, HH:mm", { locale: ru })
}
