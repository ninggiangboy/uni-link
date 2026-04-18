import DocPage from '@/docs/pages/DocPage.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/docs',
    },
    {
      path: '/docs',
      redirect: '/docs/guide/introduction',
    },
    {
      path: '/docs/:section/:slug',
      name: 'doc',
      component: DocPage,
    },
  ],
})

export default router
