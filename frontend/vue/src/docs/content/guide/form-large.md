---
title: Large Form
description: Split large forms into sub-sections using useFormContext
---

## When to Split

Forms with more than 6–8 fields should be split into sub-section components. Each section accesses the shared form state via `useFormContext`.

```
ParentForm.vue
  ├── ProfileSection.vue      ← useFormContext<ProfileFormValues>()
  ├── WorkHistorySection.vue  ← useFormContext<ProfileFormValues>() + useFieldArray
  └── NotificationsSection.vue ← useFormContext<ProfileFormValues>()
```

## Code Breakdown

### 1. Parent Owns `useForm`

```vue
<!-- ParentForm.vue -->
<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import { Form, useForm } from '@/ui/components/form'
import { profileSchema, type ProfileFormValues } from './profile.types'
import ProfileSection from './ProfileSection.vue'
import WorkHistorySection from './WorkHistorySection.vue'

const { handleSubmit } = useForm<ProfileFormValues>({
  validationSchema: toTypedSchema(profileSchema),
  initialValues: {
    isPublic: false,
    name: '',
    bio: '',
    gender: '',
    enableNotify: false,
    notifyType: '',
    works: [],
  },
})

const onSubmit = handleSubmit((values) => {
  // submit
})
</script>

<template>
  <Form class="divide-y" @submit="onSubmit">
    <ProfileSection />
    <WorkHistorySection />
    <div class="py-6">
      <Button type="submit">Save Profile</Button>
    </div>
  </Form>
</template>
```

### 2. Child Sections Use `useFormContext`

```vue
<!-- ProfileSection.vue -->
<script setup lang="ts">
import { useFormContext } from 'vee-validate'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/ui/components/form'
import { Input, TextArea } from '@/ui/components/textfield'

// Optional: get imperative API from parent form
const formContext = useFormContext<ProfileFormValues>()
</script>

<template>
  <div class="space-y-4 py-6">
    <h2 class="text-lg font-semibold">Profile</h2>

    <FormField v-slot="{ componentField }" name="name">
      <FormItem>
        <FormLabel>Name</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField v-slot="{ componentField }" name="bio">
      <FormItem>
        <FormLabel>Bio</FormLabel>
        <FormControl v-slot="controlProps">
          <TextArea v-bind="{ ...componentField, ...controlProps }" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
  </div>
</template>
```

### 3. Child with Field Array

```vue
<!-- WorkHistorySection.vue -->
<script setup lang="ts">
import { useFieldArray } from '@/ui/components/form'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'

const { fields, push, remove } = useFieldArray('works')

function addWork() {
  push({ position: '', company: '' })
}
</script>

<template>
  <div class="space-y-4 py-6">
    <h2 class="text-lg font-semibold">Work History</h2>

    <div v-for="(field, index) in fields" :key="field.key" class="border rounded p-4 grid grid-cols-2 gap-3">
      <FormField v-slot="{ componentField }" :name="`works[${index}].position`">
        <FormItem>
          <FormLabel>Position</FormLabel>
          <FormControl v-slot="controlProps">
            <Input v-bind="{ ...componentField, ...controlProps }" />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>

      <FormField v-slot="{ componentField }" :name="`works[${index}].company`">
        <FormItem>
          <FormLabel>Company</FormLabel>
          <FormControl v-slot="controlProps">
            <Input v-bind="{ ...componentField, ...controlProps }" />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>

      <div class="col-span-2 flex justify-end">
        <Button type="button" variant="destructive" @click="remove(index)">Delete</Button>
      </div>
    </div>

    <Button type="button" variant="secondary" @click="addWork">+ Add Work</Button>
  </div>
</template>
```

## Key Points

- Only the **parent component** calls `useForm` — child sections call `useFormContext`
- `useFormContext` must be typed with the same generic as the parent `useForm<T>`
- Never pass the form instance as a prop — `useFormContext` handles this automatically via Vue's provide/inject
- The submit button lives in the parent — child sections render fields only
- Each section component follows the 300-line limit independently
