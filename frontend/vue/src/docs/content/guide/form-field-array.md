---
title: Field Array
description: Dynamic lists of grouped fields using useFieldArray
---

## Usage

Use `useFieldArray` for dynamic lists of grouped fields (e.g. multiple addresses, work history entries).

<ComponentPreview name="FormFieldArray" />

## Code Breakdown

### 1. Schema with Array Validation

```ts
import { z } from 'zod'

export const inviteUsersSchema = z.object({
  users: z.array(
    z.object({
      email: z.string().email('Enter a valid email'),
      name: z.string().min(1, 'Name is required'),
    }),
  ).min(1, 'Add at least one user'),
})
```

### 2. useFieldArray Setup

```vue
<script setup lang="ts">
import { useFieldArray, useForm } from '@/ui/components/form'

const { handleSubmit, errors } = useForm({
  validationSchema: toTypedSchema(inviteUsersSchema),
  initialValues: { users: [{ email: '', name: '' }] },
})

const { fields, push, remove } = useFieldArray('users')

function addUser() {
  push({ email: '', name: '' })
}
</script>
```

### 3. Template with Dynamic Fields

```vue
<div v-for="(field, index) in fields" :key="field.key">
  <FormField v-slot="{ componentField }" :name="`users[${index}].email`">
    <!-- ... -->
  </FormField>
  <FormField v-slot="{ componentField }" :name="`users[${index}].name`">
    <!-- ... -->
  </FormField>
  <Button type="button" @click="remove(index)">Remove</Button>
</div>

<!-- Array-level errors are not wired to FormMessage -->
<p v-if="errors.users" class="text-sm text-destructive">{{ errors.users }}</p>

<Button type="button" @click="addUser">+ Add User</Button>
```

## Key Points

- Always use `field.key` as the `:key` in `v-for` — **never** use the loop `index` (causes input state bugs when rows are removed)
- Validate array-level constraints (min length, etc.) in the Zod schema via `.min()` on `z.array()`
- Array-level errors (e.g. `errors.users`) are **not** wired to `FormMessage` — render them with plain text
- Call `remove(index)` from `useFieldArray` — never splice the array manually
- Use `push(item)` to add new items to the array
- Import `useFieldArray` from `@/ui/components/form` alongside `useForm` / `Form` / `FormField`
