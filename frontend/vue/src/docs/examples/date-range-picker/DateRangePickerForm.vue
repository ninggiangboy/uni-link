<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit'
import { z } from 'zod'
import { DateRangePicker } from '@/ui/components/date-range-picker'
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

const { handleSubmit } = useForm()
const onSubmit = handleSubmit((v) => docFormToast(v))
</script>
<template>
  <Form class="space-y-4" @submit="onSubmit">
    <FormField v-slot="{ componentField }" name="range" :rules="z.any().ruleFn()">
      <FormItem>
        <FormLabel>Date range</FormLabel>
        <FormControl generic="DateRangeFormValue" v-slot="vm" :component-field="componentField">
          <DateRangePicker v-bind="vm" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
