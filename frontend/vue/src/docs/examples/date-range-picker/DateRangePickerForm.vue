<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { z } from 'zod';
import { DateRangePicker } from '@/ui/components/date-range-picker';
import { Button } from '@/ui/components/button';
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
  <Form class="space-y-4" @submit="onSubmit">
    <FormField v-slot="{ vmBinds }" name="range" :rules="z.any().ruleFn()" class="space-y-2">
      <FormLabel>Date range</FormLabel>
      <DateRangePicker v-bind="vmBinds" />
      <FormMessage />
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
