<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { useMutation } from '@tanstack/vue-query';
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
import { LoadingOverlay } from '@/ui/components/loading-overlay';
import { Input } from '@/ui/components/textfield';
import { z } from 'zod';

const { handleSubmit } = useForm({
  initialValues: { email: '', name: '' },
});

const signUpMutation = useMutation({
  mutationFn: (data: { email: string; name: string }) =>
    new Promise<typeof data>((resolve) => setTimeout(() => resolve(data), 2000)),
});

const onSubmit = handleSubmit((values) => {
  signUpMutation.mutate(values, {
    onSuccess: (data) => docFormToast(data),
  });
});
</script>

<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Sign up</h2>

    <LoadingOverlay :is-loading="signUpMutation.isPending.value">
      <div class="grid gap-4">
        <FormField
          v-slot="{ componentField }"
          name="email"
          :rules="z.string().email('Invalid email').ruleFn()"
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
          :rules="z.string().min(2, 'At least 2 characters').ruleFn()"
        >
          <FormItem>
            <FormLabel>Name</FormLabel>
            <FormControl v-slot="controlProps">
              <Input v-bind="{ ...componentField, ...controlProps }" placeholder="Enter your name" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <Button type="submit" :disabled="signUpMutation.isPending.value" class="w-full">
          Sign up
        </Button>
      </div>
    </LoadingOverlay>
  </Form>
</template>
