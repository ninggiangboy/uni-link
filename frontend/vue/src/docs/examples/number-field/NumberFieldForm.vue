<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit'
import { z } from 'zod'
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
import { NumberField } from '@/ui/components/numberfield'

const { handleSubmit } = useForm({ initialValues: { qty: 1 } })
const onSubmit = handleSubmit((v) => docFormToast(v))
</script>
<template>
  <Form class="w-full max-w-xs space-y-4" @submit="onSubmit">
    <FormField v-slot="{ componentField }" name="qty" :rules="z.number().min(1).ruleFn()">
      <FormItem>
        <FormLabel>Quantity</FormLabel>
        <FormControl generic="number | null" v-slot="vm" :component-field="componentField">
          <NumberField v-bind="vm" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
