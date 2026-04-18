<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit'
import { z } from 'zod'
import { Button } from '@/ui/components/button'
import { DatePicker } from '@/ui/components/date-picker'
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
  <Form class="w-full max-w-[280px] space-y-4" @submit="onSubmit">
    <FormField v-slot="{ componentField }" name="date" :rules="z.any().ruleFn()">
      <FormItem>
        <FormLabel>Date</FormLabel>
        <FormControl generic="string | undefined" v-slot="vm" :component-field="componentField">
          <DatePicker v-bind="vm" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
