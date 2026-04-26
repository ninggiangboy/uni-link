<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit';
import { z } from 'zod';
import { Button } from '@/ui/components/button';
import {
  Form,
  FormField,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form';
import { Input } from '@/ui/components/textfield';
import { Uploader } from '@/ui/components/uploader';
import type { UploaderFile } from '@/ui/components/uploader/uploaderTypes';
import { TmpfilesUploaderAction } from '@/docs/examples/uploader/tmpfilesUploaderAction';

const { handleSubmit } = useForm<{ name: string; attachments: UploaderFile[] }>({
  initialValues: { name: '', attachments: [] },
});
const onSubmit = handleSubmit((v) => docFormToast(v));
</script>
<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Uploader</h2>
    <FormField
      v-slot="{ vmBinds }"
      name="name"
      :rules="z.string().min(1, 'Please enter your name').ruleFn()"
      class="space-y-2"
    >
      <FormLabel>Name</FormLabel>
      <Input v-bind="vmBinds" placeholder="John Doe" />
      <FormMessage />
    </FormField>
    <FormField
      v-slot="{ field, ariaBinds }"
      name="attachments"
      :rules="z.array(z.any()).min(1, 'Please upload at least one file').ruleFn()"
      class="space-y-2"
    >
      <FormLabel>Attachments</FormLabel>
      <div :aria-invalid="ariaBinds['aria-invalid']" class="w-full">
        <Uploader
          :default-file-list="(field.value as UploaderFile[])"
          :action="new TmpfilesUploaderAction()"
          :max-file-size="100 * 1024 * 1024"
          :accepted-file-extensions="['pdf', 'docx', 'png', 'csv']"
          @file-list-change="field.onChange"
        />
      </div>
      <FormMessage />
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
