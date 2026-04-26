<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { useMutation } from '@tanstack/vue-query';
import { toTypedSchema } from '@vee-validate/zod';
import { Button } from '@/ui/components/button';
import {
  Form,
  FormField,
  FormLabel,
  FormMessage,
  setSubmitErrors,
  useForm,
} from '@/ui/components/form';
import { LoadingOverlay } from '@/ui/components/loading-overlay';
import { toast } from '@/ui/components/sonner';
import { Input } from '@/ui/components/textfield';
import { z } from 'zod';

const signUpSchema = z.object({
  email: z.string().email('Enter a valid email'),
  name: z.string().min(2, 'At least 2 characters'),
});

type SignUpFormValues = z.infer<typeof signUpSchema>;

const form = useForm<SignUpFormValues>({
  validationSchema: toTypedSchema(signUpSchema),
  initialValues: { email: '', name: '' },
});

const { handleSubmit } = form;

// Simulated mutation that returns a 422 error
const signUpMutation = useMutation({
  mutationFn: async (values: SignUpFormValues) => {
    await new Promise((resolve) => setTimeout(resolve, 1500));
    // Simulate server rejecting email as already taken
    const error = new Error('Validation failed') as Error & { status?: number; body?: string };
    error.status = 422;
    error.body = JSON.stringify({
      errors: { email: 'This email is already registered' },
    });
    throw error;
  },
});

const onSubmit = handleSubmit((values) => {
  signUpMutation.mutate(values, {
    onSuccess: () => {
      toast.success('Account created!');
    },
    onError: (error) => {
      const err = error as Error & { status?: number; body?: string };
      if (err.status === 422 && err.body) {
        try {
          const body = JSON.parse(err.body) as { errors?: Record<string, string> };
          if (body.errors) {
            setSubmitErrors(form, body.errors);
            return;
          }
        } catch {
          /* body is not JSON */
        }
      }
      toast.error(err.message || 'Something went wrong');
    },
  });
});
</script>

<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Sign up</h2>

    <LoadingOverlay :is-loading="signUpMutation.isPending.value">
      <div class="grid gap-4">
        <FormField v-slot="{ vmBinds }" name="email" class="space-y-2">
          <FormLabel>Email</FormLabel>
          <Input v-bind="vmBinds" placeholder="Enter your email" />
          <FormMessage />
        </FormField>

        <FormField v-slot="{ vmBinds }" name="name" class="space-y-2">
          <FormLabel>Name</FormLabel>
          <Input v-bind="vmBinds" placeholder="Enter your name" />
          <FormMessage />
        </FormField>

        <Button type="submit" :disabled="signUpMutation.isPending.value" class="w-full">
          Sign up
        </Button>
      </div>
    </LoadingOverlay>
  </Form>
</template>
