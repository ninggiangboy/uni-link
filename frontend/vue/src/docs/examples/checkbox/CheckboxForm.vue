<script setup lang="ts">
import { z } from 'zod'
import { docFormToast } from '@/docs/examples/_internal/docFormSubmit'
import { Button } from '@/ui/components/button'
import { Checkbox, CheckboxGroup } from '@/ui/components/checkbox'
import {
  Form,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormControl,
  useForm,
} from '@/ui/components/form'
import { TextArea } from '@/ui/components/textfield'

const { handleSubmit } = useForm({
  initialValues: {
    interest: [] as string[],
    bio: '',
    acceptTerm: false,
  },
})

const onSubmit = handleSubmit((v) => docFormToast(v))
</script>

<template>
  <Form class="space-y-5" @submit="onSubmit">
    <FormField
      v-slot="{ componentField }"
      name="interest"
      :rules="z.array(z.string()).min(1, 'Pick at least one').ruleFn()"
    >
      <FormItem>
        <FormLabel>Select your interests</FormLabel>
        <FormDescription>Pick one or more.</FormDescription>
        <FormControl generic="string[]" v-slot="vm" :component-field="componentField">
          <CheckboxGroup v-bind="vm" class="grid grid-cols-3 gap-4">
            <Checkbox value="reading"> Reading </Checkbox>
            <Checkbox value="writing"> Writing </Checkbox>
            <Checkbox value="coding"> Coding </Checkbox>
          </CheckboxGroup>
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField v-slot="{ componentField }" name="bio" :rules="z.string().min(1, 'Required').ruleFn()">
      <FormItem>
        <FormLabel>Bio</FormLabel>
        <FormControl v-slot="controlProps">
          <TextArea
            v-bind="{ ...componentField, ...controlProps }"
            placeholder="Type your bio here..."
            class="min-h-24"
          />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField
      v-slot="{ componentField }"
      name="acceptTerm"
      type="checkbox"
      :rules="z.boolean().refine((v) => v, { message: 'Please accept the terms and conditions' }).ruleFn()"
    >
      <FormItem>
        <FormControl generic="boolean | 'indeterminate'" v-slot="vm" :component-field="componentField">
          <Checkbox v-bind="vm"> I accept the terms and conditions </Checkbox>
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <Button type="submit"> Submit </Button>
  </Form>
</template>
