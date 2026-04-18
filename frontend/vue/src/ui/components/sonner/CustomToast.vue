<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { cn } from '@/ui/lib/utils'
import { AlertTriangle, CircleCheck, CircleX, Info, X } from 'lucide-vue-next'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  title: string
  description?: string
  variant?: 'success' | 'error' | 'info' | 'warning' | 'neutral'
  onCloseToast?: () => void
}>()
</script>

<template>
  <div
    :class="
      cn(
        'group relative flex w-full min-h-[64px] items-center gap-3 rounded-xl border bg-popover py-2.5 pr-7 pl-4 shadow-popover md:w-[364px]',
      )
    "
  >
    <template v-if="variant && variant !== 'neutral'">
      <CircleCheck
        v-if="variant === 'success'"
        class="size-5 shrink-0 text-green-500 dark:text-green-400"
      />
      <CircleX
        v-else-if="variant === 'error'"
        class="size-5 shrink-0 text-destructive-foreground"
      />
      <Info
        v-else-if="variant === 'info'"
        class="size-5 shrink-0 text-blue-500 dark:text-blue-400"
      />
      <AlertTriangle
        v-else-if="variant === 'warning'"
        class="size-5 shrink-0 text-yellow-500 dark:text-yellow-400"
      />
    </template>

    <div class="flex flex-1 items-center">
      <div class="w-full">
        <p class="text-sm font-semibold text-foreground">
          {{ props.title }}
        </p>
        <div
          v-if="props.description"
          class="mt-0.5 whitespace-pre-wrap break-words text-sm text-muted-foreground"
        >
          {{ props.description }}
        </div>
      </div>
    </div>

    <Button
      size="iconSm"
      variant="ghost"
      class="absolute top-1.5 right-1.5 opacity-0 transition-opacity group-hover:opacity-100"
      type="button"
      aria-label="Dismiss"
      @click="props.onCloseToast?.()"
    >
      <X class="size-4 text-muted-foreground" />
    </Button>
  </div>
</template>
