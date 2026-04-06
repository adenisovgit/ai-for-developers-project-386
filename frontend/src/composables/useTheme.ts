import { computed, ref } from 'vue'

export type ThemeMode = 'auto' | 'light' | 'dark'

const STORAGE_KEY = 'call-booking-theme'
const themeMode = ref<ThemeMode>('auto')
let initialized = false

function getResolvedTheme(mode: ThemeMode) {
  if (mode !== 'auto') {
    return mode
  }

  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

function applyTheme(mode: ThemeMode) {
  const resolved = getResolvedTheme(mode)
  const root = document.documentElement

  root.classList.toggle('dark', resolved === 'dark')
  root.style.colorScheme = resolved
}

function initializeTheme() {
  if (initialized || typeof window === 'undefined') {
    return
  }

  initialized = true

  const stored = window.localStorage.getItem(STORAGE_KEY)

  if (stored === 'light' || stored === 'dark' || stored === 'auto') {
    themeMode.value = stored
  }

  applyTheme(themeMode.value)

  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  mediaQuery.addEventListener('change', () => {
    if (themeMode.value === 'auto') {
      applyTheme('auto')
    }
  })
}

function setTheme(nextTheme: ThemeMode) {
  themeMode.value = nextTheme
  window.localStorage.setItem(STORAGE_KEY, nextTheme)
  applyTheme(nextTheme)
}

export function useTheme() {
  return {
    themeMode,
    resolvedTheme: computed(() => getResolvedTheme(themeMode.value)),
    initializeTheme,
    setTheme,
  }
}
