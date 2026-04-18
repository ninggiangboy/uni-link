import { cn } from '@/ui/lib/utils'

/** Surface + enter/exit motion for Reka DatePicker / DateRangePicker popovers (Presence + tailwindcss-animate). */
export function datePickerPopoverContentClass(className?: string) {
  return cn(
    'z-[400] w-auto rounded-md border border-border bg-popover p-1 text-popover-foreground shadow-md outline-none',
    'origin-[var(--reka-popover-content-transform-origin)]',
    'data-[state=open]:animate-in data-[state=closed]:animate-out',
    'data-[state=open]:fade-in-0 data-[state=closed]:fade-out-0',
    'data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2',
    'duration-200 ease-out',
    className,
  )
}
