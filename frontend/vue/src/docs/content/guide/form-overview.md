---
title: Form Management Overview
description: Building forms with VeeValidate and Zod
---

## What is Form Management?

Form management is a critical aspect of modern web applications, handling user input, validation, and data submission. This guide covers everything you need to know about building robust, user-friendly forms using **VeeValidate** and **Zod**.

## Rethinking Validation

You might be used to placing all validation logic directly within the form declaration like this, but in this guide, we'll take a different approach:

```vue
<script setup lang="ts">
// We won't handle validation this way
const { handleSubmit } = useForm({
  validationSchema: toTypedSchema(
    z.object({
      email: z.string(),
      password: z.string(),
    }),
  ),
})
</script>
```

Placing all validation logic inside your form can lead to several issues:

- **Your field validation logic and your field UI rendering are separated.** You have to look in more than one place to understand the complete logic for a field.
- **It's difficult to handle conditional validation**, such as when a field's validation depends on the value of another field.
- **Dynamically enabling or disabling validation for certain fields is challenging.** For instance, if you conditionally hide a field, its validation is still enforced.

Validating at the form level is only suitable for simple forms. In most real-world projects, forms inevitably become more complex, so we've chosen to standardize validation at the **field level** instead.

## Field-Level Zod (Default)

In VeeValidate, you can perform field-level validation using the `:rules` prop on `FormField`. We've extended Zod to work seamlessly at the field level:

```vue
<FormField name="email" :rules="z.string().email('Invalid email').ruleFn()" v-slot="{ componentField }">
  <!-- ... -->
</FormField>
```

## Getting Started

Ready to build your first form? Start with the [Basic Form](/docs/guide/form-basic) guide to learn the fundamentals, or jump to specific topics that interest you. Each guide includes working examples and production-ready code patterns.
