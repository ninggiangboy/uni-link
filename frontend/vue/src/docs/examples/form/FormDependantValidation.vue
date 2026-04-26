<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { toTypedSchema } from '@vee-validate/zod';
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

const registerSchema = z.object({
  email: z.string().email('Enter a valid email'),
  password: z.string().min(8, 'At least 8 characters'),
  confirmPassword: z.string().min(1, 'Please confirm your password'),
}).refine(
  (data) => data.password === data.confirmPassword,
  {
    message: "Passwords don't match",
    path: ['confirmPassword'],
  },
);

type RegisterFormValues = z.infer<typeof registerSchema>;

const { handleSubmit } = useForm<RegisterFormValues>({
  validationSchema: toTypedSchema(registerSchema),
  initialValues: { email: '', password: '', confirmPassword: '' },
});

const onSubmit = handleSubmit((v) => docFormToast(v));
</script>

<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Register</h2>

    <FormField
      v-slot="{ vmBinds }"
      name="email"
      :rules="z.string().email('Enter a valid email').ruleFn()"
      class="space-y-2"
    >
      <FormLabel>Email</FormLabel>
      <Input v-bind="vmBinds" type="email" placeholder="Enter your email" />
      <FormMessage />
    </FormField>

    <FormField
      v-slot="{ vmBinds }"
      name="password"
      :rules="z.string().min(8, 'At least 8 characters').ruleFn()"
      class="space-y-2"
    >
      <FormLabel>Password</FormLabel>
      <Input v-bind="vmBinds" type="password" placeholder="Enter your password" />
      <FormMessage />
    </FormField>

    <FormField v-slot="{ vmBinds }" name="confirmPassword" class="space-y-2">
      <FormLabel>Confirm password</FormLabel>
      <Input v-bind="vmBinds" type="password" placeholder="Confirm your password" />
      <FormMessage />
    </FormField>

    <Button type="submit" class="w-full">Register</Button>
  </Form>
</template>
