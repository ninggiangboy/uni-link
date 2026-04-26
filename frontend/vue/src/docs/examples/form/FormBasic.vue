<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { Button } from '@/ui/components/button';
import {
  Form,
  FormField,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form';
import { Input } from '@/ui/components/textfield';
import { z } from 'zod';

const { handleSubmit } = useForm({
  initialValues: { email: '', name: '' },
});

const onSubmit = handleSubmit((v) => docFormToast(v));
</script>

<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Sign up</h2>

    <FormField
      v-slot="{ vmBinds }"
      name="email"
      :rules="z.string().email('Invalid email').ruleFn()"
      class="space-y-2"
    >
      <FormLabel>Email</FormLabel>
      <Input v-bind="vmBinds" placeholder="Enter your email" />
      <FormMessage />
    </FormField>

    <FormField
      v-slot="{ vmBinds }"
      name="name"
      :rules="z.string().min(2, 'At least 2 characters').ruleFn()"
      class="space-y-2"
    >
      <FormLabel>Name</FormLabel>
      <Input v-bind="vmBinds" placeholder="Enter your name" />
      <FormMessage />
    </FormField>

    <Button type="submit" class="w-full">Sign up</Button>
  </Form>
</template>
