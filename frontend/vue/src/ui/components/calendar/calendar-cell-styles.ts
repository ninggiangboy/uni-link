import { cva } from 'class-variance-authority'

/** Range calendar inner cell label variants (ported from React Calendar.helper) */
export const rangeCalendarCellLabelVariants = cva(
  [
    'w-full h-full flex items-center justify-center rounded-full text-sm font-medium text-neutral-600 dark:text-neutral-200',
    'data-[disabled=true]:text-muted-foreground data-[disabled=true]:opacity-50',
  ],
  {
    variants: {
      selectionState: {
        none: 'group-pressed:bg-neutral-200 group-data-[highlighted]:bg-neutral-400/20',
        middle: [
          'group-invalid:group-hover:bg-red-200 group-data-[highlighted]:bg-neutral-400/20',
          'group-pressed:bg-primary/80',
          'group-invalid:group-pressed:bg-red-300',
        ],
        cap: 'bg-primary group-invalid:bg-red-600 text-white',
      },
    },
  },
)
