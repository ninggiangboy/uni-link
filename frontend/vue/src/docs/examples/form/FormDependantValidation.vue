<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { toTypedSchema } from '@vee-validate/zod';
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
      v-slot="{ componentField }"
      name="email"
      :rules="z.string().email('Enter a valid email').ruleFn()"
    >
      <FormItem>
        <FormLabel>Email</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" type="email" placeholder="Enter your email" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField
      v-slot="{ componentField }"
      name="password"
      :rules="z.string().min(8, 'At least 8 characters').ruleFn()"
    >
      <FormItem>
        <FormLabel>Password</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" type="password" placeholder="Enter your password" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField v-slot="{ componentField }" name="confirmPassword">
      <FormItem>
        <FormLabel>Confirm password</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" type="password" placeholder="Confirm your password" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <Button type="submit" class="w-full">Register</Button>
  </Form>
</template>
