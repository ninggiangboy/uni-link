<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { Button } from '@/ui/components/button';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
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
      v-slot="{ componentField }"
      name="email"
      :rules="z.string().email('Enter a valid email address').ruleFn()"
    >
      <FormItem>
        <FormLabel>Email</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" placeholder="Enter your email" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField
      v-slot="{ componentField }"
      name="name"
      :rules="z.string().min(6, 'Name must be at least 6 characters').ruleFn()"
    >
      <FormItem>
        <FormLabel>Name</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" placeholder="Enter your name (min 6 characters)" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <Button type="submit" class="w-full">Sign up</Button>
  </Form>
</template>
