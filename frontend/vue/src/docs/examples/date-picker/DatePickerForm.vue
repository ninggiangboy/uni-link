<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { z } from 'zod';
import { Button } from '@/ui/components/button';
import { DatePicker } from '@/ui/components/date-picker';
import {
  Form,
  FormField,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form';

const { handleSubmit } = useForm();
const onSubmit = handleSubmit((v) => docFormToast(v));
</script>
<template>
  <Form class="w-full max-w-[280px] space-y-4" @submit="onSubmit">
    <FormField v-slot="{ vmBinds }" name="date" :rules="z.any().ruleFn()" class="space-y-2">
      <FormLabel>Date</FormLabel>
      <DatePicker v-bind="vmBinds" />
      <FormMessage />
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
