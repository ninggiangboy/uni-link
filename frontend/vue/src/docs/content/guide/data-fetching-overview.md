---
title: Data Fetching Overview
description: Building robust data fetching with TanStack Query and structured API clients
---

## What is Data Fetching?

Data fetching is the process of retrieving data from external sources (APIs) and managing that data in your application. In modern Vue applications, this involves handling loading states, error conditions, caching, synchronization, and keeping your UI in sync with server state.

This guide covers everything you need to know about building robust, performant data fetching using **TanStack Query** and structured API clients.

## Why TanStack Query?

Instead of managing server state manually with `ref` and `watch`, TanStack Query provides:

- **Automatic caching** — Data is cached and shared across components
- **Background updates** — Keeps data fresh automatically
- **Optimistic updates** — Update UI before server confirmation
- **Error handling** — Built-in retry logic and error states
- **Loading states** — Automatic loading, success, and error states
- **Deduplication** — Prevents duplicate requests
- **Synchronization** — Keeps multiple components in sync

## Server State vs Client State

A state management library in a web app can generally be divided into two types:

> TanStack Query is a server-state library, responsible for managing asynchronous operations between your server and client.

Redux, MobX, Zustand, etc. are client-state libraries that can be used to store asynchronous data, albeit inefficiently when compared to a tool like TanStack Query.

Based on experience across many projects of different scales, most applications only need a server-state library, since most of what's displayed on the screen comes from the server. **TanStack Query** handles this extremely well.

## Getting Started

Ready to build your data fetching layer? Start with [Build an API Client](/docs/guide/data-fetching-api-client) to learn how to structure your API modules, or jump to specific topics that interest you.
