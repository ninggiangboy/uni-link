<script setup lang="ts">
import { z } from 'zod';
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { Button } from '@/ui/components/button';
import {
  Form,
  FormField,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form';
import { TextArea } from '@/ui/components/textfield';

const { handleSubmit } = useForm({
  initialValues: { bio: '' },
});
const onSubmit = handleSubmit((v) => docFormToast(v));
</script>

<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <FormField v-slot="{ vmBinds }" name="bio" :rules="z.string().min(2).ruleFn()" class="space-y-2">
      <FormLabel>Bio</FormLabel>
      <TextArea
        v-bind="vmBinds"
        placeholder="Type your bio here..."
        class="min-h-24"
      />
      <FormMessage />
    </FormField>
    <Button type="submit"> Submit </Button>
  </Form>
</template>
