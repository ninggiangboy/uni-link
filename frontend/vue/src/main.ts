import '@/lib/validation/extendZod'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { VueQueryPlugin } from '@tanstack/vue-query'

import '@/assets/globals.css'
import 'vue-sonner/style.css'
import { queryClient } from '@/lib/query-client'
import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(VueQueryPlugin, { queryClient })
app.use(router)

app.mount('#app')
