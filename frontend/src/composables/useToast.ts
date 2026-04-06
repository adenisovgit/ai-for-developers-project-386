import { computed, ref } from 'vue'

export type ToastVariant = 'success' | 'error' | 'info'

export interface Toast {
  id: string
  title: string
  description?: string
  variant: ToastVariant
}

const toasts = ref<Toast[]>([])

function removeToast(id: string) {
  toasts.value = toasts.value.filter((toast) => toast.id !== id)
}

function showToast({
  title,
  description,
  variant = 'info',
}: Omit<Toast, 'id'>) {
  const id = crypto.randomUUID()

  toasts.value = [...toasts.value, { id, title, description, variant }]

  window.setTimeout(() => removeToast(id), 4000)
}

export function useToast() {
  return {
    toasts: computed(() => toasts.value),
    showToast,
    removeToast,
  }
}
