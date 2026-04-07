import { DefaultApi } from '@/api/apis/default-api'
import { Configuration } from '@/api/configuration'

const basePath = import.meta.env.VITE_API_URL ?? (import.meta.env.DEV ? 'http://localhost:3001' : '')

export const apiClient = new DefaultApi(
  new Configuration({
    basePath,
  }),
)
