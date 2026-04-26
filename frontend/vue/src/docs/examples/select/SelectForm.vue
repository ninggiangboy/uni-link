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
import { Select, type SelectOption } from '@/ui/components/select';
import { z } from 'zod';

const languageOptions: SelectOption[] = [
  { id: 'en', name: 'English' },
  { id: 'es', name: 'Spanish' },
  { id: 'fr', name: 'French' },
  { id: 'de', name: 'German' },
  { id: 'it', name: 'Italian' },
];

const roleOptions: SelectOption[] = [
  { id: 'admin', name: 'Admin' },
  { id: 'user', name: 'User' },
];

const { handleSubmit } = useForm({
  initialValues: {
    role: 'admin',
    languages: [] as string[],
  },
});

const onSubmit = handleSubmit((values) => {
  docFormToast(values);
});
</script>

<template>
  <Form class="w-full space-y-3" @submit="onSubmit">
    <FormField v-slot="{ vmBinds, errors }" name="role" :rules="z.string().min(1).ruleFn()" class="space-y-2">
      <FormLabel>Role</FormLabel>
      <Select
        v-bind="vmBinds"
        clearable
        :options="roleOptions"
        :class="errors.length ? 'ring-2 ring-destructive' : ''"
      />
      <FormMessage />
    </FormField>
    <FormField
      v-slot="{ vmBinds }"
      name="languages"
      :rules="z.array(z.string()).optional().ruleFn()"
      class="space-y-2"
    >
      <FormLabel>Language</FormLabel>
      <Select v-bind="vmBinds" multiple :options="languageOptions" />
      <FormMessage />
    </FormField>
    <div class="grid grid-cols-2 gap-2 py-2">
      <Button type="button" variant="outline"> Cancel </Button>
      <Button type="submit"> Save </Button>
    </div>
  </Form>
</template>
