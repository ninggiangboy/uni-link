---
title: Decisions on DX
description: A summary of the main choices implemented to enhance developer experience in this project
---

When you start using this project, you might have some of the following questions:

- Why choose Reka UI?
- Why do some components, especially form fields, seem a bit verbose to use?
- Why not use Redux, MobX, or Zustand?
- Why use VeeValidate instead of manual validation?
- If all the docs are accessible online, why is there a docs folder within the src directory?

These are all valid questions that many developers may have when first encountering this project, and this article aims to thoroughly address each one.

## Why choose Reka UI?

Currently, there are many popular component libraries such as Element Plus, Ant Design Vue, Vuetify, and PrimeVue. The common limitation of these libraries is that their customization options are quite restricted. You can only customize components through the available props, and if you want to make deeper changes, you often have to use very specific and complex CSS selectors.

In real-world projects, the UI doesn't just need to "look okay" — it often needs to match the design exactly, down to the pixel. Achieving a 100% match with a full component library and a custom design can be very difficult.

The solution is to use a headless UI library, which provides only the component behavior, leaving the visual styling entirely up to you. That's why we chose **Reka UI**: it provides headless primitives with accessible, composable components, and all the styling lives in your own source code, making customization straightforward and flexible.

## Why do some components, especially form fields, seem a bit verbose to use?

This was a common concern a few years ago when headless form libraries first appeared. The reason is simple: we aim to avoid over-abstraction in the codebase, which can make future changes much more difficult.

Now, with LLMs available, writing repetitive code is much faster and less of a burden. Believe us, if you try to over-simplify or over-abstract your form fields, as your project grows, no one will want to touch those fields anymore.

```vue
<!-- ❌ Convenient, but the Input component will quickly become too complex -->
<SmartInput name="email" label="Email" placeholder="Enter your email" />

<!-- ✅ Slightly more verbose, but clear and simple to update -->
<FormField name="email" :rules="emailField.ruleFn()" v-slot="{ componentField }">
  <FormItem>
    <FormLabel>Email</FormLabel>
    <FormControl v-slot="controlProps">
      <Input v-bind="{ ...componentField, ...controlProps }" placeholder="Enter your email" />
    </FormControl>
    <FormMessage />
  </FormItem>
</FormField>
```

## Why not use Redux, MobX, Zustand?

A state management library in a web app can generally be divided into two types: client state and server state.

Here's a quote from the TanStack Query documentation:

> TanStack Query is a server-state library, responsible for managing asynchronous operations between your server and client.

Redux, MobX, Zustand, etc. are client-state libraries that can be used to store asynchronous data, albeit inefficiently when compared to a tool like TanStack Query.

Based on experience across many projects of different scales, most applications only need a server-state library, since most of what's displayed on the screen comes from the server. **TanStack Query** handles this extremely well, so in most cases, Redux or MobX aren't necessary.

So when should you use a client-state library? You should consider it when most of your application's state lives on the client side. For example, if you're building an email template editor (an email builder), that's when a lightweight client-state library such as Pinia or a simple composable would make more sense.

## Why use VeeValidate instead of manual validation?

Manual validation leads to scattered error handling, inconsistent UX, and duplicated logic. VeeValidate provides:

- **Declarative validation** — rules defined alongside form fields, not in separate handlers
- **Zod integration** — type-safe schemas that serve as both validation rules and TypeScript types
- **Field-level control** — validation runs per-field, making conditional and dependent validation straightforward
- **Error management** — automatic error display via `FormMessage`, no manual error refs needed

## If all the docs are accessible online, why is there a docs folder within the src directory?

It's true that you can read documentation externally, but you also have the option to access documentation locally — it's entirely up to you.

Keeping the documentation locally offers several benefits:

- You can directly edit the docs or add your own internal company materials, such as coding conventions
- When you modify or add new components, the documentation can be updated accordingly
- You can create a completely new documentation site for internal use, based on the existing codebase

If you don't want the documentation in your local machine, just delete the `docs` folder — everything will still work as expected.

## Recap

In summary, making thoughtful decisions about your UI and state management libraries is crucial for long-term maintainability and developer happiness. Avoiding premature abstractions in components keeps your codebase flexible and approachable. Select tools that are actively maintained and fit your project's needs, and remember that most applications benefit more from robust server-state management than from complex client-state solutions.
