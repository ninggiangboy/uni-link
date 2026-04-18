<script setup lang="ts">
import { Avatar, AvatarFallback } from '@/ui/components/avatar'
import { Select, type SelectOption } from '@/ui/components/select'

const users: (SelectOption & { email: string; color: string })[] = [
  { id: 1, name: 'John Doe', email: 'john@example.com', color: 'bg-sky-500' },
  { id: 2, name: 'Jane Smith', email: 'jane@example.com', color: 'bg-red-500' },
  { id: 3, name: 'Bob Johnson', email: 'bob@example.com', color: 'bg-green-500' },
]
</script>

<template>
  <Select :options="users" default-value="1">
    <template #value="{ modelValue: mv }">
      <template v-if="mv">
        <template v-for="u in users" :key="u.id">
          <div v-if="String(u.id) === String(mv)" class="flex items-center gap-2">
            <Avatar class="size-5 text-xs">
              <AvatarFallback :class="u.color">{{ u.name.charAt(0) }}</AvatarFallback>
            </Avatar>
            <div class="flex flex-col">{{ u.name }}</div>
          </div>
        </template>
      </template>
    </template>
    <template #option="{ option }">
      <div class="flex items-center gap-2">
        <Avatar>
          <AvatarFallback :class="(option as (typeof users)[0]).color">
            {{ option.name.charAt(0) }}
          </AvatarFallback>
        </Avatar>
        <div class="flex flex-col">
          <span>{{ option.name }}</span>
          <span class="text-xs opacity-60">{{ (option as (typeof users)[0]).email }}</span>
        </div>
      </div>
    </template>
  </Select>
</template>
