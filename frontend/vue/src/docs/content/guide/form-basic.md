---
title: Basic Form
description: Learn how to create and use a basic form, including setup, input fields, and form submission handling
---

## Usage

<ComponentPreview name="FormBasic" />

## Code Breakdown

### 1. Form Setup

Use `useForm` from `@/ui/components/form` with `initialValues` for every field:

```vue
<script setup lang="ts">
import { useForm } from '@/ui/components/form'

const { handleSubmit } = useForm({
  initialValues: { email: '', name: '' },
})

const onSubmit = handleSubmit((values) => {
  console.log(values)
})
</script>
```

### 2. FormField with Validation

Each field uses `FormField` with a `name` and `:rules` for Zod validation:

```vue
<FormField
  v-slot="{ componentField }"
  name="email"
  :rules="z.string().email('Invalid email').ruleFn()"
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

### 3. Submit Button

The submit button triggers the form's `handleSubmit` callback:

```vue
<Button type="submit" class="w-full">Sign up</Button>
```

## Key Points

- Import `useForm` from `@/ui/components/form` (re-exported from `vee-validate`)
- Use `initialValues` for every field — no `validationSchema` needed on `useForm` for basic forms
- Prefer `:rules="z…ruleFn()"` on each `FormField` for field-level validation
- Use `Form` + `FormField` + `FormMessage` — no hand-rolled error spans
- Use `FormControl` for both native controls and custom model controls
