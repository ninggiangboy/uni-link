<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { Button } from '@/ui/components/button';
import { Form, FormField, FormLabel, useForm } from '@/ui/components/form';
import { Switch } from '@/ui/components/switch-ui';
import { z } from 'zod';

const { handleSubmit } = useForm({ initialValues: { notify: true } });
const onSubmit = handleSubmit((v) => docFormToast(v));
</script>
<template>
  <Form class="space-y-4" @submit="onSubmit">
    <FormField
      v-slot="{ vmBinds }"
      name="notify"
      type="checkbox"
      :rules="z.boolean().ruleFn()"
      class="space-y-2"
    >
      <div class="flex items-center gap-2">
        <Switch v-bind="vmBinds" />
        <FormLabel class="!mt-0 cursor-pointer font-normal">Notifications</FormLabel>
      </div>
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
