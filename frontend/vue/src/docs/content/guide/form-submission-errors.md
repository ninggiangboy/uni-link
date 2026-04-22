---
title: Submission Errors
description: Handle server-side validation errors and map them back to form fields
---

## Usage

After a failed mutation, map server error responses back to the form. Two categories of errors exist and each has a distinct UX response:

| Error type | What it is | How to display it |
|---|---|---|
| **Field-specific** | Server rejects a specific value (e.g. "Email already taken") | `setSubmitErrors(form, { fieldName: 'message' })` — appears inline under the field |
| **General** | Server failure unrelated to a specific field (network error, 500, etc.) | `toast.error(...)` — non-blocking notification |

<ComponentPreview name="FormSubmissionErrors" />

## Code Breakdown

### 1. Error Handling in Mutation

```vue
<script setup lang="ts">
import { useMutation } from '@tanstack/vue-query'
import { FetchHttpError } from '@/lib/api'
import { toast } from '@/ui/components/sonner'
import { setSubmitErrors, useForm } from '@/ui/components/form'

const form = useForm({
  initialValues: { email: '', name: '' },
})

const signUpMutation = useMutation({
  mutationFn: (values) => api.auth.signUp(values),
})

const onSubmit = handleSubmit((values) => {
  signUpMutation.mutate(values, {
    onSuccess: () => {
      toast.success('Account created!')
    },
    onError: (error) => {
      if (error instanceof FetchHttpError && error.status === 422) {
        try {
          const body = JSON.parse(error.body) as { errors?: Record<string, string> }
          if (body.errors) {
            setSubmitErrors(form, body.errors)
            return
          }
        } catch {
          /* body is not JSON */
        }
      }
      toast.error(error instanceof Error ? error.message : 'Something went wrong')
    },
  })
})
</script>
```

### 2. LoadingOverlay with Submit Button

```vue
<LoadingOverlay :is-loading="signUpMutation.isPending.value">
  <div class="grid gap-4">
    <!-- Form fields -->
    <Button type="submit" :disabled="signUpMutation.isPending.value">Sign up</Button>
  </div>
</LoadingOverlay>
```

## Key Points

- Always check `error instanceof FetchHttpError` before reading `status` / `body` (raw string from the server — often `JSON.parse` for structured errors)
- `setSubmitErrors` keys must match VeeValidate field names exactly
- After applying field errors, `return` — do not also toast the same failure
- General errors (non-field-specific) must surface via toast — never swallow them silently
