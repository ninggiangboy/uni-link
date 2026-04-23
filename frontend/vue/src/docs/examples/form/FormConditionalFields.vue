<script setup lang="ts">
import { computed } from 'vue';
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { toTypedSchema } from '@vee-validate/zod';
import { Button } from '@/ui/components/button';
import { Checkbox, CheckboxGroup } from '@/ui/components/checkbox';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form';
import { Select, type SelectOption } from '@/ui/components/select';
import { Input } from '@/ui/components/textfield';
import { z } from 'zod';

enum UserRole { User = 'user', Admin = 'admin' }

const createUserSchema = z.object({
  role: z.nativeEnum(UserRole),
  name: z.string().min(4, 'At least 4 characters'),
  permissions: z.array(z.string()).optional(),
}).refine(
  (data) => data.role !== UserRole.Admin || (data.permissions ?? []).length > 0,
  {
    message: 'Admin users require at least one permission',
    path: ['permissions'],
  },
);

type CreateUserFormValues = z.infer<typeof createUserSchema>;

const { handleSubmit, values } = useForm<CreateUserFormValues>({
  validationSchema: toTypedSchema(createUserSchema),
  initialValues: { role: UserRole.User, name: '', permissions: [] },
});

const isAdmin = computed(() => values.role === UserRole.Admin);

const roleOptions: SelectOption[] = [
  { id: UserRole.User, name: 'User' },
  { id: UserRole.Admin, name: 'Admin' },
];

const onSubmit = handleSubmit((v) => docFormToast(v));
</script>

<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Create User</h2>

    <FormField v-slot="{ componentField, errors }" name="role">
      <FormItem>
        <FormLabel>Role</FormLabel>
        <FormControl generic="string" v-slot="vm" :component-field="componentField">
          <Select
            v-bind="vm"
            :options="roleOptions"
            :class="errors.length ? 'ring-2 ring-destructive' : ''"
          />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField
      v-slot="{ componentField }"
      name="name"
      :rules="z.string().min(4, 'At least 4 characters').ruleFn()"
    >
      <FormItem>
        <FormLabel>Name</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" placeholder="Enter name" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <div v-if="isAdmin">
      <FormField v-slot="{ componentField }" name="permissions">
        <FormItem>
          <FormLabel>Permissions</FormLabel>
          <FormControl generic="string[]" v-slot="vm" :component-field="componentField">
            <CheckboxGroup v-bind="vm" class="flex flex-col gap-2">
              <Checkbox value="read">Read</Checkbox>
              <Checkbox value="write">Write</Checkbox>
              <Checkbox value="delete">Delete</Checkbox>
            </CheckboxGroup>
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>
    </div>

    <Button type="submit" class="w-full">Submit</Button>
  </Form>
</template>
