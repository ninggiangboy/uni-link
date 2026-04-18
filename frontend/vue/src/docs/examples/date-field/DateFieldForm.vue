<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit'
import { z } from 'zod'
import { Button } from '@/ui/components/button'
import { DateField } from '@/ui/components/datefield'
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormControl,
  useForm,
} from '@/ui/components/form'

const { handleSubmit } = useForm()
const onSubmit = handleSubmit((v) => docFormToast(v))
</script>
<template>
  <Form class="w-full max-w-xs space-y-4" @submit="onSubmit">
    <FormField v-slot="{ componentField }" name="dob" :rules="z.any().ruleFn()">
      <FormItem>
        <FormLabel>Date of birth</FormLabel>
        <FormControl generic="DateValue | undefined" v-slot="vm" :component-field="componentField">
          <DateField v-bind="vm" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
