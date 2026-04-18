import { cn } from '@/ui/lib/utils'

/** Reka date/time segment inputs — tight padding + primary highlight when editing (see frontend-sample Datefield.tsx DateSegment). */
export function dateFieldSegmentClassName(className?: string) {
  return cn(
    'inline rounded px-px py-0 text-sm tabular-nums outline-none caret-transparent',
    'data-[reka-date-field-segment=literal]:px-0 data-[reka-time-field-segment=literal]:px-0',
    'data-[placeholder]:text-muted-foreground',
    'data-[disabled]:cursor-not-allowed data-[disabled]:opacity-50',
    'focus:bg-primary focus:text-white focus:data-[placeholder]:text-white',
    className,
  )
}
