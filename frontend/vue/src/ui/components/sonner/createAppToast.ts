import type { ExternalToast } from 'vue-sonner'
import { toast as vsToast } from 'vue-sonner'
import CustomToast from './CustomToast.vue'

export type AppToastPayload = { title: string; description?: string }

type Variant = 'success' | 'error' | 'info' | 'warning' | 'neutral'

function createToast(variant: Variant) {
  return (messageOrProps: string | AppToastPayload, data?: ExternalToast) => {
    let title: string
    let description: string | undefined

    if (typeof messageOrProps === 'string') {
      title = messageOrProps
      description = typeof data?.description === 'string' ? data.description : undefined
    } else {
      title = messageOrProps.title
      description =
        messageOrProps.description ??
        (typeof data?.description === 'string' ? data.description : undefined)
    }

    const { description: _, ...rest } = data ?? {}

    return vsToast.custom(CustomToast, {
      position: 'top-right',
      ...rest,
      componentProps: {
        title,
        description,
        variant,
      },
    })
  }
}

/** Mirrors `frontend-sample/packages/ui/src/components/Sonner.tsx` API + string form for vue-sonner call sites */
export const appToast = Object.assign(vsToast, {
  success: createToast('success'),
  error: createToast('error'),
  info: createToast('info'),
  warning: createToast('warning'),
  neutral: createToast('neutral'),
  message: createToast('neutral'),
})
