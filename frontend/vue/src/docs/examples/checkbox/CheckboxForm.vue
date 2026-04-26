<script setup lang="ts">
import { z } from 'zod';
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { Button } from '@/ui/components/button';
import { Checkbox, CheckboxGroup } from '@/ui/components/checkbox';
import {
  Form,
  FormDescription,
  FormField,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form';
import { TextArea } from '@/ui/components/textfield';

const { handleSubmit } = useForm({
  initialValues: {
    interest: [] as string[],
    bio: '',
    acceptTerm: false,
  },
});

const onSubmit = handleSubmit((v) => docFormToast(v));
</script>

<template>
  <Form class="space-y-5" @submit="onSubmit">
    <FormField
      v-slot="{ vmBinds }"
      name="interest"
      :rules="z.array(z.string()).min(1, 'Pick at least one').ruleFn()"
      class="space-y-2"
    >
      <FormLabel>Select your interests</FormLabel>
      <FormDescription>Pick one or more.</FormDescription>
      <CheckboxGroup v-bind="vmBinds" class="grid grid-cols-3 gap-4">
        <Checkbox value="reading"> Reading </Checkbox>
        <Checkbox value="writing"> Writing </Checkbox>
        <Checkbox value="coding"> Coding </Checkbox>
      </CheckboxGroup>
      <FormMessage />
    </FormField>

    <FormField
      v-slot="{ vmBinds }"
      name="bio"
      :rules="z.string().min(1, 'Required').ruleFn()"
      class="space-y-2"
    >
      <FormLabel>Bio</FormLabel>
      <TextArea
        v-bind="vmBinds"
        placeholder="Type your bio here..."
        class="min-h-24"
      />
      <FormMessage />
    </FormField>

    <FormField
      v-slot="{ vmBinds }"
      name="acceptTerm"
      type="checkbox"
      :rules="
        z
          .boolean()
          .refine((v) => v, { message: 'Please accept the terms and conditions' })
          .ruleFn()
      "
      class="space-y-2"
    >
      <Checkbox v-bind="vmBinds"> I accept the terms and conditions </Checkbox>
      <FormMessage />
    </FormField>

    <Button type="submit"> Submit </Button>
  </Form>
</template>
