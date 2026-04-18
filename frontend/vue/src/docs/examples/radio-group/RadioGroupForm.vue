<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit'
import { z } from 'zod'
import { Button } from '@/ui/components/button'
import { Label } from '@/ui/components/field'
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormControl,
  useForm,
} from '@/ui/components/form'
import { RadioGroup, RadioGroupItem } from '@/ui/components/radio-group'

const { handleSubmit } = useForm({ initialValues: { plan: 'pro' } })
const onSubmit = handleSubmit((v) => docFormToast(v))
</script>
<template>
  <Form class="space-y-4" @submit="onSubmit">
    <FormField
      v-slot="{ componentField }"
      name="plan"
      type="radio"
      :rules="z.string().min(1).ruleFn()"
    >
      <FormItem>
        <FormLabel>Plan</FormLabel>
        <FormControl generic="string | undefined" v-slot="vm" :component-field="componentField">
          <RadioGroup v-bind="vm" class="max-w-xs">
            <Label class="flex items-center gap-2 font-normal">
              <RadioGroupItem value="free" />
              Free
            </Label>
            <Label class="flex items-center gap-2 font-normal">
              <RadioGroupItem value="pro" />
              Pro
            </Label>
          </RadioGroup>
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
