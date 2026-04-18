import { cn } from '@/ui/lib/utils'

/** Shared panel + enter/exit motion (Reka Presence + tailwindcss-animate). Avoid overflow/clip-path here so submenus can receive pointer events. */
export function dropdownMenuContentClass(className?: string) {
  return cn(
    'z-50 w-auto rounded-lg border bg-popover/80 backdrop-blur-xl text-popover-foreground shadow-popover outline-none p-1.5',
    'data-[state=open]:animate-in data-[state=open]:fade-in-0',
    'data-[state=closed]:animate-out data-[state=closed]:fade-out-0',
    'data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2',
    className,
  )
}
