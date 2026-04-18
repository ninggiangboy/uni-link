import { toast } from '@/ui/components/sonner'

export function docFormToast(values: unknown) {
  toast.message('You submitted the following values', {
    description: typeof values === 'string' ? values : JSON.stringify(values, null, 2),
  })
}
