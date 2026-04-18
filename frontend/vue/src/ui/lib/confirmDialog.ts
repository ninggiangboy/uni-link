import { ref } from 'vue'

export type ConfirmAction = {
  label: string
  onClick: () => void | Promise<void>
}

export type ConfirmPayload = {
  title: string
  description?: string
  action?: ConfirmAction
  cancel?: ConfirmAction
  variant?: 'default' | 'destructive'
}

export const confirmDialogOpen = ref(false)
export const confirmDialogData = ref<ConfirmPayload | undefined>(undefined)

export function confirm(data: ConfirmPayload) {
  confirmDialogData.value = data
  confirmDialogOpen.value = true
}

export function closeConfirm() {
  confirmDialogOpen.value = false
}
