import OwnerDashboardView from '@/views/OwnerDashboardView.vue'
import PublicBookingView from '@/views/PublicBookingView.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: PublicBookingView,
    },
    {
      path: '/admin',
      component: OwnerDashboardView,
    },
  ],
})

export default router
