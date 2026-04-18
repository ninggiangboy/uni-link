import { cva, type VariantProps } from 'class-variance-authority'

export const labelVariants = cva([
  'text-sm font-medium',
  'data-[disabled]:cursor-not-allowed data-[disabled]:opacity-70',
])

export const fieldGroupVariants = cva('', {
  variants: {
    variant: {
      default: [
        'shadow-sm bg-background-secondary relative flex h-8 w-full items-center overflow-hidden rounded-sm px-3 py-2 md:text-sm',
        'ring-inset ring ring-input',
        'transition-all focus-within:ring-primary! focus-within:ring-2 aria-invalid:ring-destructive',
        'data-[disabled]:opacity-80',
        'data-[invalid]:ring-destructive',
      ],
      ghost: '',
    },
  },
  defaultVariants: {
    variant: 'default',
  },
})

export type FieldGroupVariant = VariantProps<typeof fieldGroupVariants>['variant']
