import { cva, type VariantProps } from 'class-variance-authority'

export const toggleVariants = cva(
  [
    'inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors',
    'disabled:pointer-events-none disabled:opacity-50',
    'hover:bg-muted hover:text-muted-foreground',
    'data-[state=on]:bg-accent data-[state=on]:text-accent-foreground',
    'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2',
  ],
  {
    variants: {
      variant: {
        default: 'bg-transparent',
        outline: 'border bg-transparent hover:bg-accent hover:text-accent-foreground',
      },
      size: {
        default: 'h-10 px-3',
        sm: 'h-9 px-2.5',
        lg: 'h-11 px-5',
      },
    },
    defaultVariants: {
      variant: 'default',
      size: 'default',
    },
  },
)

export type ToggleVariants = VariantProps<typeof toggleVariants>
