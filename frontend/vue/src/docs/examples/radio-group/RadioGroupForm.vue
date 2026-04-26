<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { z } from 'zod';
import { Button } from '@/ui/components/button';
import { Label } from '@/ui/components/field';
import {
  Form,
  FormField,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form';
import { RadioGroup, RadioGroupItem } from '@/ui/components/radio-group';

const { handleSubmit } = useForm({ initialValues: { plan: 'pro' } });
const onSubmit = handleSubmit((v) => docFormToast(v));
</script>
<template>
  <Form class="space-y-4" @submit="onSubmit">
    <FormField
      v-slot="{ vmBinds }"
      name="plan"
      type="radio"
      :rules="z.string().min(1).ruleFn()"
      class="space-y-2"
    >
      <FormLabel>Plan</FormLabel>
      <RadioGroup v-bind="vmBinds" class="max-w-xs">
        <Label class="flex items-center gap-2 font-normal">
          <RadioGroupItem value="free" />
          Free
        </Label>
        <Label class="flex items-center gap-2 font-normal">
          <RadioGroupItem value="pro" />
          Pro
        </Label>
      </RadioGroup>
      <FormMessage />
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
