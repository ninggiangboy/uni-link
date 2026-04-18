<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit'
import { Button } from '@/ui/components/button'
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormControl,
  useForm,
} from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'
import { z } from 'zod'

const { handleSubmit } = useForm({
  initialValues: { username: '' },
})

const onSubmit = handleSubmit((v) => docFormToast(v))
</script>
<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <FormField
      v-slot="{ componentField }"
      name="username"
      :rules="z.string().min(2, 'At least 2 characters').ruleFn()"
    >
      <FormItem>
        <FormLabel>Username</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" placeholder="henry" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
