<script setup lang="ts">
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit'
import { z } from 'zod'
import { Button } from '@/ui/components/button'
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormControl,
  useForm,
} from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'
import { Uploader } from '@/ui/components/uploader'
import type { UploaderFile } from '@/ui/components/uploader/uploaderTypes'
import { TmpfilesUploaderAction } from '@/docs/examples/uploader/tmpfilesUploaderAction'

const { handleSubmit } = useForm<{ name: string; attachments: UploaderFile[] }>({
  initialValues: { name: '', attachments: [] },
})
const onSubmit = handleSubmit((v) => docFormToast(v))
</script>
<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Uploader</h2>
    <FormField
      v-slot="{ componentField }"
      name="name"
      :rules="z.string().min(1, 'Please enter your name').ruleFn()"
    >
      <FormItem>
        <FormLabel>Name</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" placeholder="John Doe" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <FormField
      v-slot="{ field }"
      name="attachments"
      :rules="z.array(z.any()).min(1, 'Please upload at least one file').ruleFn()"
    >
      <FormItem>
        <FormLabel>Attachments</FormLabel>
        <FormControl v-slot="controlProps">
          <div v-bind="controlProps" class="w-full">
            <Uploader
              :default-file-list="field.value"
              :action="new TmpfilesUploaderAction()"
              :max-file-size="100 * 1024 * 1024"
              :accepted-file-extensions="['pdf', 'docx', 'png', 'csv']"
              :aria-invalid="controlProps['aria-invalid']"
              @file-list-change="field.onChange"
            />
          </div>
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <Button type="submit">Submit</Button>
  </Form>
</template>
