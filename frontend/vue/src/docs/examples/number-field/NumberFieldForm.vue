<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { z } from 'zod';
import { Button } from '@/ui/components/button';
import {
  Form,
  FormField,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form';
import { NumberField } from '@/ui/components/numberfield';

const { handleSubmit } = useForm({ initialValues: { qty: 1 } });
const onSubmit = handleSubmit((v) => docFormToast(v));
</script>
<template>
  <Form class="w-full max-w-xs space-y-4" @submit="onSubmit">
    <FormField v-slot="{ vmBinds }" name="qty" :rules="z.number().min(1).ruleFn()" class="space-y-2">
      <FormLabel>Quantity</FormLabel>
      <NumberField v-bind="vmBinds" />
      <FormMessage />
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
