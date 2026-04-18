import { onMounted, onUnmounted, ref } from 'vue'

const DEFAULT_MOBILE_BREAKPOINT = 768

interface UseIsMobileProps {
  breakpointPx?: number
}

export function useIsMobile({ breakpointPx = DEFAULT_MOBILE_BREAKPOINT }: UseIsMobileProps = {}) {
  function matches() {
    return (
      typeof window !== 'undefined' &&
      window.matchMedia(`(max-width: ${breakpointPx - 1}px)`).matches
    )
  }

  const isMobile = ref(matches())

  function update() {
    isMobile.value = matches()
  }

  let mq: MediaQueryList | null = null

  onMounted(() => {
    mq = window.matchMedia(`(max-width: ${breakpointPx - 1}px)`)
    update()
    mq.addEventListener('change', update)
  })

  onUnmounted(() => {
    mq?.removeEventListener('change', update)
  })

  return { isMobile }
}
