---
title: Dependent Validation
description: Cross-field validation using Zod refine
---

## Usage

When a field's validity depends on another field's value (e.g. password confirmation), add the cross-field rule at the **schema level** using `.refine()` — never in an imperative `watch`.

<ComponentPreview name="FormDependantValidation" />

## Code Breakdown

### 1. Schema with Cross-Field Refine

```ts
import { z } from 'zod'

export const registerSchema = z.object({
  email: z.string().email('Enter a valid email'),
  password: z.string().min(8, 'At least 8 characters'),
  confirmPassword: z.string().min(1, 'Please confirm your password'),
}).refine(
  (data) => data.password === data.confirmPassword,
  {
    message: "Passwords don't match",
    path: ['confirmPassword'], // error is attached to confirmPassword field
  },
)
```

### 2. Form Setup with validationSchema

For cross-field validation, use `validationSchema` on `useForm` with `toTypedSchema`:

```vue
<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import { useForm } from '@/ui/components/form'

const { handleSubmit } = useForm({
  validationSchema: toTypedSchema(registerSchema),
  initialValues: { email: '', password: '', confirmPassword: '' },
})
</script>
```

## Key Points

- Cross-field rules live in `.refine()` / `.superRefine()` on the schema — not in `watch` or event handlers
- Always provide `path` in the refine config so VeeValidate attaches the error to the correct field
- VeeValidate re-runs the full schema on every field change, so cross-field errors update automatically
