import { defineConfig, devices } from '@playwright/test'

const isCi = Boolean(process.env.CI)

export default defineConfig({
  testDir: './tests/e2e',
  fullyParallel: false,
  forbidOnly: isCi,
  retries: isCi ? 2 : 0,
  workers: isCi ? 1 : undefined,
  reporter: [
    ['list'],
    ['html', { open: 'never' }],
  ],
  use: {
    baseURL: 'http://127.0.0.1:4173',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    timezoneId: 'UTC',
  },
  webServer: [
    {
      command: "cd ../backend && TZ=UTC JAVA_TOOL_OPTIONS='-Duser.timezone=UTC' ./gradlew bootRun",
      url: 'http://127.0.0.1:8080/event-types',
      timeout: 120_000,
      reuseExistingServer: !isCi,
    },
    {
      command: 'npm run preview:e2e',
      url: 'http://127.0.0.1:4173',
      timeout: 120_000,
      reuseExistingServer: !isCi,
    },
  ],
  projects: [
    {
      name: 'chromium',
      use: {
        ...devices['Desktop Chrome'],
      },
    },
  ],
})
