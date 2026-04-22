---
title: Conditional Fields
description: Show or hide fields based on other field values
---

## Usage

When fields appear or disappear based on another field's value, read the condition from `values` (VeeValidate's reactive form state) and use `v-if` to mount/unmount the field. Fields that are not mounted are automatically excluded from validation.

<ComponentPreview name="FormConditionalFields" />

## Code Breakdown

### 1. Read Condition from `values`

```vue
<script setup lang="ts">
import { computed } from 'vue'
import { useForm } from '@/ui/components/form'

const { handleSubmit, values } = useForm({
  initialValues: { role: 'user', name: '', permissions: [] },
})

const isAdmin = computed(() => values.role === 'admin')
</script>
```

### 2. Use `v-if` (Not `v-show`)

```vue
<div v-if="isAdmin">
  <FormField v-slot="{ componentField }" name="permissions">
    <FormItem>
      <FormLabel>Permissions</FormLabel>
      <FormControl v-slot="controlProps">
        <BsSelect v-bind="controlProps" multiple :options="permissionOptions" />
      </FormControl>
      <FormMessage />
    </FormItem>
  </FormField>
</div>
```

### 3. Schema with Optional Conditional Field

```ts
import { z } from 'zod'

export const createUserSchema = z.object({
  role: z.enum(['user', 'admin']),
  name: z.string().min(4, 'At least 4 characters'),
  permissions: z.array(z.string()).optional(),
}).refine(
  (data) => data.role !== 'admin' || (data.permissions ?? []).length > 0,
  {
    message: 'Admin users require at least one permission',
    path: ['permissions'],
  },
)
```

## Key Points

- Read conditional state from `values` returned by `useForm` — never from a separate `ref`
- Wrap the condition in a `computed` — keep template expressions simple
- Use `v-if` (not `v-show`) for conditional fields — unmounted fields are excluded from schema validation automatically
- When a conditional field becomes hidden, VeeValidate drops its error and its value resets to `undefined` — account for this in the schema with `.optional()`
