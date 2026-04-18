<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit'
import { z } from 'zod'
import { Button } from '@/ui/components/button'
import { DatePicker } from '@/ui/components/date-picker'
import { BsTimeField } from '@/ui/components/datefield'
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
  <Form class="flex w-full max-w-lg flex-col gap-4" @submit="onSubmit">
    <div class="flex gap-2">
      <div class="min-w-0 flex-1">
        <FormField v-slot="{ componentField }" name="appointmentDate" :rules="z.any().ruleFn()">
          <FormItem>
            <FormLabel>Appointment date</FormLabel>
            <FormControl generic="string | undefined" v-slot="vm" :component-field="componentField">
              <DatePicker v-bind="vm" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>
      </div>
      <FormField v-slot="{ componentField }" name="appointmentTime" :rules="z.any().ruleFn()">
        <FormItem>
          <FormLabel class="opacity-0">Time</FormLabel>
          <FormControl generic="string | undefined" v-slot="vm" :component-field="componentField">
            <BsTimeField class="w-18 min-w-0 shrink-0" v-bind="vm" />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>
    </div>
    <Button type="submit">Submit</Button>
  </Form>
</template>
