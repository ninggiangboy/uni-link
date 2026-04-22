---
title: Validation Form
description: Add client-side validation rules to inputs using Zod helpers
---

## Usage

<ComponentPreview name="FormValidation" />

## Code Breakdown

### Field-Level Zod Validation

Each field uses `:rules` with a Zod schema and `.ruleFn()` for type-safe validation:

```vue
<FormField
  v-slot="{ componentField }"
  name="email"
  :rules="z.string().email('Enter a valid email address').ruleFn()"
>
  <FormItem>
    <FormLabel>Email</FormLabel>
    <FormControl v-slot="controlProps">
      <Input v-bind="{ ...componentField, ...controlProps }" placeholder="Enter your email" />
    </FormControl>
    <FormMessage />
  </FormItem>
</FormField>
```

### Common Validation Patterns

```ts
import { z } from 'zod'

// Required string with minimum length
z.string().min(2, 'At least 2 characters')

// Email validation
z.string().email('Enter a valid email address')

// URL validation
z.string().url('Enter a valid URL')

// Number with range
z.number({ invalid_type_error: 'Age must be a number' }).min(18, 'Must be 18 or older')

// Optional field
z.string().optional()

// Boolean (for checkboxes)
z.boolean().refine((v) => v, { message: 'You must accept the terms' })
```

## Key Points

- Every `z.string().min(n)` must include a human-readable message as the second argument
- `z.string().email()` must always carry a message — the default Zod message is too technical
- Optional fields use `.optional()` on the Zod field, not nullable inputs
- Never write `z.any()` in a form schema — if the type is unknown, use `z.unknown()` and narrow later
