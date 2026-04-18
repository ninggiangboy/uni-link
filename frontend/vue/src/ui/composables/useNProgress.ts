import NProgress from 'nprogress'
import { onUnmounted, watch, type Ref } from 'vue'

let configured = false

function ensureConfigured() {
  if (configured) return
  configured = true
  NProgress.configure({
    showSpinner: false,
    trickleSpeed: 150,
  })
}

/** Drive global NProgress bar from a reactive `isFetching` flag */
export function useNProgress(isFetching: Ref<boolean>) {
  ensureConfigured()

  watch(
    isFetching,
    (v) => {
      queueMicrotask(() => {
        if (v) NProgress.start()
        else NProgress.done()
      })
    },
    { immediate: true },
  )

  onUnmounted(() => {
    NProgress.done()
  })
}
