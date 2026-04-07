import { expect, test, type APIRequestContext, type Page } from '@playwright/test'

const apiBaseUrl = 'http://127.0.0.1:8080'
const eventTypeTitle = 'Знакомство'

type Diagnostics = {
  consoleErrors: string[]
  pageErrors: string[]
  requestUrls: string[]
}

function getUtcDateOffset(daysFromToday: number) {
  const date = new Date()
  date.setUTCHours(0, 0, 0, 0)
  date.setUTCDate(date.getUTCDate() + daysFromToday)

  return date.toISOString().slice(0, 10)
}

function createGuestEmail(testSlug: string) {
  return `e2e-${testSlug}-${Date.now()}@example.com`
}

function attachDiagnostics(page: Page): Diagnostics {
  const consoleErrors: string[] = []
  const pageErrors: string[] = []
  const requestUrls: string[] = []

  page.on('console', (message) => {
    if (message.type() === 'error') {
      consoleErrors.push(message.text())
    }
  })

  page.on('pageerror', (error) => {
    pageErrors.push(error.message)
  })

  page.on('requestfinished', (request) => {
    requestUrls.push(request.url())
  })

  return {
    consoleErrors,
    pageErrors,
    requestUrls,
  }
}

async function expectNoCriticalBrowserErrors(
  diagnostics: Diagnostics,
  allowedConsoleErrorPatterns: RegExp[] = [],
) {
  const criticalConsoleErrors = diagnostics.consoleErrors.filter((message) =>
    !allowedConsoleErrorPatterns.some((pattern) => pattern.test(message)),
  )

  expect(
    criticalConsoleErrors,
    criticalConsoleErrors.join('\n'),
  ).toEqual([])
  expect(
    diagnostics.pageErrors,
    diagnostics.pageErrors.join('\n'),
  ).toEqual([])
}

function expectApiTrafficUsesBackend(diagnostics: Diagnostics) {
  expect(diagnostics.requestUrls.some((url) => url.startsWith(apiBaseUrl))).toBeTruthy()
  expect(diagnostics.requestUrls.some((url) => url.includes('127.0.0.1:3001'))).toBeFalsy()
  expect(diagnostics.requestUrls.some((url) => url.includes('localhost:3001'))).toBeFalsy()
}

async function getEventTypeId(request: APIRequestContext, title: string) {
  const response = await request.get(`${apiBaseUrl}/event-types`)
  expect(response.ok()).toBeTruthy()

  const eventTypes = (await response.json()) as Array<{ id: string; title: string }>
  const match = eventTypes.find((eventType) => eventType.title === title)

  expect(match, `Не найден event type с названием "${title}"`).toBeDefined()

  return match!.id
}

async function selectEventType(page: Page, title: string) {
  await page.getByTestId('event-type-trigger').click()

  const dialog = page.getByRole('dialog', { name: 'Выбор типа события' })
  await expect(dialog).toBeVisible()
  await dialog.getByRole('button', { name: new RegExp(title, 'i') }).click()
}

async function createEventType(page: Page, params: {
  title: string
  description: string
  durationMinutes: string
  availableFrom: string
  availableTo: string
  color: string
}) {
  await page.goto('/admin')
  await page.getByRole('button', { name: 'Создать новый тип события' }).click()

  const dialog = page.getByRole('dialog', { name: 'Новый тип события' })
  await expect(dialog).toBeVisible()

  await dialog.getByPlaceholder('Например, Знакомство').fill(params.title)
  await dialog.getByPlaceholder('Коротко опиши, о чем этот звонок.').fill(params.description)
  await dialog.locator('select').nth(0).selectOption(params.durationMinutes)
  await dialog.locator('select').nth(1).selectOption(params.availableFrom)
  await dialog.locator('select').nth(2).selectOption(params.availableTo)
  await dialog.getByRole('button', { name: 'Красный' }).click()
  await dialog.getByRole('button', { name: 'Создать тип события' }).click()

  await expect(page.getByText('Тип события создан')).toBeVisible()
  await expect(page.getByText(params.title)).toBeVisible()
}

async function expectSlotAvailability(page: Page, labels: {
  visible: string[]
  hidden: string[]
}) {
  const dialog = page.getByRole('dialog', { name: 'Выбор даты и слота' })

  for (const label of labels.visible) {
    await expect(dialog.getByTestId('slot-option').filter({ hasText: label })).toHaveCount(1)
  }

  for (const label of labels.hidden) {
    await expect(dialog.getByTestId('slot-option').filter({ hasText: label })).toHaveCount(0)
  }
}

async function selectSlot(page: Page, date: string) {
  await page.getByTestId('slot-trigger').click()

  const dialog = page.getByRole('dialog', { name: 'Выбор даты и слота' })
  await expect(dialog).toBeVisible()

  await dialog.getByTestId('booking-date-input').fill(date)

  const slot = dialog.getByTestId('slot-option').first()
  await expect(slot).toBeVisible()

  const startTime = await slot.getAttribute('data-start-time')
  const slotLabel = (await slot.textContent())?.trim() ?? ''

  expect(startTime).toBeTruthy()

  await slot.click()

  return {
    startTime: startTime!,
    slotLabel,
  }
}

test('happy path бронирования сохраняет запись и показывает ее в панели владельца', async ({
  page,
}) => {
  const diagnostics = attachDiagnostics(page)
  const bookingDate = getUtcDateOffset(1)
  const uniqueSuffix = Date.now()
  const guestName = `Playwright Happy Path ${uniqueSuffix}`
  const guestEmail = createGuestEmail('happy-path')
  const guestComment = `Проверка основного пути бронирования ${uniqueSuffix}.`

  await page.goto('/')

  await selectEventType(page, eventTypeTitle)
  await selectSlot(page, bookingDate)

  const guestNameInput = page.getByTestId('guest-name-input')
  await expect(guestNameInput).toBeFocused()

  await guestNameInput.fill(guestName)
  await page.getByTestId('guest-email-input').fill(guestEmail)
  await page.getByTestId('guest-comment-input').fill(guestComment)
  await page.getByTestId('booking-submit-button').click()

  await expect(page.getByTestId('booking-success-state')).toBeVisible()
  await expect(page.getByText('Бронирование успешно отправлено')).toBeVisible()
  await expect(page.getByText(guestEmail)).toBeVisible()

  await page.goto('/admin')

  await expect(page.getByRole('heading', { name: 'Очередь созвонов' })).toBeVisible()
  await expect(page.getByText(guestName)).toBeVisible()
  await expect(page.getByText(guestEmail)).toBeVisible()
  await expect(page.getByText(guestComment)).toBeVisible()

  expectApiTrafficUsesBackend(diagnostics)
  await expectNoCriticalBrowserErrors(diagnostics)
})

test('конфликт слота показывает 409 toast, сбрасывает выбор и повторно загружает занятость', async ({
  page,
  request,
}) => {
  const diagnostics = attachDiagnostics(page)
  const bookingDate = getUtcDateOffset(2)
  const guestName = `Playwright Conflict ${Date.now()}`
  const guestEmail = createGuestEmail('conflict-ui')
  const eventTypeId = await getEventTypeId(request, eventTypeTitle)

  await page.goto('/')

  await selectEventType(page, eventTypeTitle)
  const { startTime } = await selectSlot(page, bookingDate)

  const guestNameInput = page.getByTestId('guest-name-input')
  await expect(guestNameInput).toBeFocused()
  await guestNameInput.fill(guestName)
  await page.getByTestId('guest-email-input').fill(guestEmail)

  const apiReservationResponse = await request.post(`${apiBaseUrl}/bookings`, {
    data: {
      eventTypeId,
      startTime,
      guestName: 'Parallel Booking',
      guestEmail: createGuestEmail('conflict-api'),
      comment: 'Создано через API для конфликта.',
    },
  })
  expect(apiReservationResponse.status()).toBe(201)

  const conflictResponsePromise = page.waitForResponse((response) =>
    response.url().includes('/bookings')
      && response.request().method() === 'POST'
      && response.status() === 409,
  )
  const slotsReloadPromise = page.waitForResponse((response) =>
    response.url().includes(`/slots?startDate=${bookingDate}&endDate=${bookingDate}`)
      && response.request().method() === 'GET'
      && response.ok(),
  )

  await page.getByTestId('booking-submit-button').click()

  await conflictResponsePromise
  await slotsReloadPromise

  await expect(page.getByText('Слот уже занят')).toBeVisible()
  await expect(page.getByTestId('booking-form-placeholder')).toBeVisible()
  await expect(page.getByTestId('guest-name-input')).toHaveCount(0)

  await page.getByTestId('slot-trigger').click()
  const slotDialog = page.getByRole('dialog', { name: 'Выбор даты и слота' })
  await expect(slotDialog).toBeVisible()
  await expect(slotDialog.locator(`[data-start-time="${startTime}"]`)).toHaveCount(0)

  expectApiTrafficUsesBackend(diagnostics)
  await expectNoCriticalBrowserErrors(diagnostics, [
    /Failed to load resource: the server responded with a status of 409/,
    /AxiosError: Request failed with status code 409/,
  ])
})

test('новый тип события можно создать в панели владельца и затем забронировать через публичную страницу', async ({
  page,
}) => {
  const diagnostics = attachDiagnostics(page)
  const uniqueSuffix = Date.now()
  const bookingDate = getUtcDateOffset(3)
  const eventTypeTitle = `Playwright Event Type ${uniqueSuffix}`
  const eventTypeDescription = `Новый тип события для e2e ${uniqueSuffix}`
  const guestName = `Playwright New Event ${uniqueSuffix}`
  const guestEmail = createGuestEmail('new-event-type')
  const guestComment = `Бронирование по новому типу ${uniqueSuffix}.`

  await createEventType(page, {
    title: eventTypeTitle,
    description: eventTypeDescription,
    durationMinutes: '60',
    availableFrom: '11:00',
    availableTo: '13:00',
    color: '#EF4444',
  })

  await page.goto('/')

  await selectEventType(page, eventTypeTitle)
  await selectSlot(page, bookingDate)

  const guestNameInput = page.getByTestId('guest-name-input')
  await expect(guestNameInput).toBeFocused()

  await guestNameInput.fill(guestName)
  await page.getByTestId('guest-email-input').fill(guestEmail)
  await page.getByTestId('guest-comment-input').fill(guestComment)
  await page.getByTestId('booking-submit-button').click()

  await expect(page.getByTestId('booking-success-state')).toBeVisible()
  await expect(page.getByText('Бронирование успешно отправлено')).toBeVisible()
  await expect(page.getByText(guestEmail)).toBeVisible()

  await page.goto('/admin')

  await expect(page.getByRole('heading', { name: eventTypeTitle, exact: true })).toBeVisible()
  await expect(page.getByText(eventTypeDescription)).toBeVisible()
  await expect(page.getByText(guestName)).toBeVisible()
  await expect(page.getByText(guestEmail)).toBeVisible()
  await expect(page.getByText(guestComment)).toBeVisible()

  expectApiTrafficUsesBackend(diagnostics)
  await expectNoCriticalBrowserErrors(diagnostics)
})

test('сегодняшние прошедшие слоты скрываются в UI, а backend отклоняет бронирование в прошлом', async ({
  page,
  request,
}) => {
  const diagnostics = attachDiagnostics(page)
  const bookingDate = getUtcDateOffset(0)
  const pastDate = getUtcDateOffset(-1)
  const uniqueSuffix = Date.now()
  const eventTypeTitle = `Playwright Today Slots ${uniqueSuffix}`
  const eventTypeDescription = `Проверка прошедших слотов ${uniqueSuffix}`

  await page.clock.install({ time: new Date(`${bookingDate}T12:00:00Z`) })

  await createEventType(page, {
    title: eventTypeTitle,
    description: eventTypeDescription,
    durationMinutes: '60',
    availableFrom: '10:00',
    availableTo: '14:00',
    color: '#EF4444',
  })

  await page.goto('/')

  await selectEventType(page, eventTypeTitle)
  await page.getByTestId('slot-trigger').click()

  const slotDialog = page.getByRole('dialog', { name: 'Выбор даты и слота' })
  await expect(slotDialog).toBeVisible()
  await slotDialog.getByTestId('booking-date-input').fill(bookingDate)

  await expectSlotAvailability(page, {
    visible: ['13:00 - 14:00'],
    hidden: ['10:00 - 11:00', '11:00 - 12:00', '12:00 - 13:00'],
  })

  const eventTypeId = await getEventTypeId(request, eventTypeTitle)
  const response = await request.post(`${apiBaseUrl}/bookings`, {
    data: {
      eventTypeId,
      startTime: `${pastDate}T10:00:00Z`,
      guestName: 'Past Slot',
      guestEmail: createGuestEmail('past-slot'),
    },
  })

  expect(response.status()).toBe(400)
  await expect(response.text()).resolves.toContain('Нельзя бронировать слоты в прошлом.')

  expectApiTrafficUsesBackend(diagnostics)
  await expectNoCriticalBrowserErrors(diagnostics)
})
