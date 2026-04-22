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
  useFieldArray,
  useForm,
} from '@/ui/components/form';
import { Input } from '@/ui/components/textfield';
import { z } from 'zod';

const inviteUsersSchema = z.object({
  users: z.array(
    z.object({
      email: z.string().email('Enter a valid email'),
      name: z.string().min(1, 'Name is required'),
    }),
  ).min(1, 'Add at least one user'),
});

type InviteUsersFormValues = z.infer<typeof inviteUsersSchema>;

const { handleSubmit, errors } = useForm<InviteUsersFormValues>({
  validationSchema: toTypedSchema(inviteUsersSchema),
  initialValues: { users: [{ email: '', name: '' }] },
});

const { fields, push, remove } = useFieldArray<{ email: string; name: string }>('users');

function addUser() {
  push({ email: '', name: '' });
}

const onSubmit = handleSubmit((v) => docFormToast(v));
</script>

<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Invite Users</h2>

    <div
      v-for="(field, index) in fields"
      :key="field.key"
      class="grid grid-cols-[1fr_1fr_auto] gap-3"
    >
      <FormField v-slot="{ componentField }" :name="`users[${index}].email`">
        <FormItem>
          <FormLabel>Email</FormLabel>
          <FormControl v-slot="controlProps">
            <Input v-bind="{ ...componentField, ...controlProps }" placeholder="Email" />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>

      <FormField v-slot="{ componentField }" :name="`users[${index}].name`">
        <FormItem>
          <FormLabel>Name</FormLabel>
          <FormControl v-slot="controlProps">
            <Input v-bind="{ ...componentField, ...controlProps }" placeholder="Name" />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>

      <div class="flex items-end">
        <Button v-if="fields.length > 1" type="button" variant="destructive" @click="remove(index)">
          Remove
        </Button>
      </div>
    </div>

    <p v-if="errors.users" class="text-sm font-medium text-destructive">{{ errors.users }}</p>

    <Button type="button" variant="secondary" @click="addUser">+ Add User</Button>

    <hr />

    <div class="flex justify-end gap-3">
      <Button type="submit">Invite Users</Button>
    </div>
  </Form>
</template>
