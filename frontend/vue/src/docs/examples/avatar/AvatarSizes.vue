<script setup lang="ts">
import { Avatar, AvatarFallback, AvatarImage } from '@/ui/components/avatar'
</script>

<template>
  <div class="flex items-center space-x-4">
    <Avatar class="size-8">
      <AvatarImage src="https://github.com/henry-phm.png" alt="@henry-phm" />
      <AvatarFallback>HP</AvatarFallback>
    </Avatar>
    <Avatar class="size-12">
      <AvatarImage src="https://github.com/henry-phm.png" alt="@henry-phm" />
      <AvatarFallback>HP</AvatarFallback>
    </Avatar>
    <Avatar class="size-16">
      <AvatarImage src="https://github.com/henry-phm.png" alt="@henry-phm" />
      <AvatarFallback>HP</AvatarFallback>
    </Avatar>
  </div>
</template>
