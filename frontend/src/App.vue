<script setup lang="ts">
import ThemeToggle from '@/components/ThemeToggle.vue'
import ToastProvider from '@/components/ToastProvider.vue'
import { cn } from '@/lib/utils'
import { RouterLink, RouterView, useRoute } from 'vue-router'

const route = useRoute()

const navigationItems = [
  { to: '/', label: 'Бронирование' },
  { to: '/admin', label: 'Панель владельца' },
]
</script>

<template>
  <div class="relative min-h-screen overflow-hidden bg-background text-foreground">
    <div class="pointer-events-none absolute inset-0">
      <div class="absolute left-0 top-0 h-72 w-72 rounded-full bg-sky-400/20 blur-3xl" />
      <div class="absolute right-0 top-24 h-80 w-80 rounded-full bg-emerald-400/20 blur-3xl" />
      <div class="absolute bottom-0 left-1/3 h-96 w-96 rounded-full bg-amber-300/20 blur-3xl" />
    </div>

    <div class="relative mx-auto flex min-h-screen max-w-7xl flex-col px-4 py-4 sm:px-6 lg:px-8">
      <header
        class="mb-6 flex h-10 items-center justify-between rounded-2xl border border-white/40 bg-white/75 pl-0 pr-0 shadow-card backdrop-blur dark:border-white/10 dark:bg-slate-950/75"
      >
        <nav class="flex h-full items-stretch overflow-hidden rounded-l-[0.95rem] rounded-r-xl bg-background/80">
          <RouterLink
            v-for="item in navigationItems"
            :key="item.to"
            :to="item.to"
            :class="
              cn(
                'inline-flex h-full items-center px-4 text-sm font-medium leading-5 transition first:rounded-l-[0.95rem] last:rounded-r-xl',
                route.path === item.to
                  ? 'cursor-default bg-primary text-primary-foreground pointer-events-none'
                  : 'text-muted-foreground hover:bg-secondary',
              )
            "
          >
            {{ item.label }}
          </RouterLink>
        </nav>

        <ThemeToggle />
      </header>

      <section class="mb-8">
        <div class="surface-panel w-full overflow-hidden px-6 py-7 sm:px-8 sm:py-9">
          <div class="grid gap-6 lg:grid-cols-[1.15fr_0.85fr] lg:items-stretch">
            <div class="max-w-3xl space-y-3">
              <p
                class="text-sm font-semibold uppercase tracking-[0.28em] text-sky-600 dark:text-sky-300"
              >
                Call Booking
              </p>
              <div class="space-y-2">
                <h1 class="font-display text-3xl font-semibold leading-tight sm:text-4xl">
                  Созвоны без хаоса и лишней ручной координации
                </h1>
              </div>
            </div>

            <div
              class="flex h-full items-center rounded-[1.75rem] border border-white/40 bg-background/80 p-5 shadow-sm dark:border-white/10"
            >
              <p class="text-sm leading-7 text-muted-foreground sm:text-base">
                Публичный поток для гостя и отдельная панель владельца живут в одном
                интерфейсе с контрактным mock API, быстрым созданием типов событий и
                понятным сценарием бронирования.
              </p>
            </div>
          </div>
        </div>
      </section>

      <main class="flex-1">
        <RouterView />
      </main>
    </div>

    <ToastProvider />
  </div>
</template>
