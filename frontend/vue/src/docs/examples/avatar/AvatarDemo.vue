<script setup lang="ts">
import { Avatar, AvatarFallback, AvatarImage } from '@/ui/components/avatar'
</script>

<template>
  <Avatar>
    <AvatarImage src="https://github.com/henry-phm.png" alt="@henry-phm" />
    <AvatarFallback>HP</AvatarFallback>
  </Avatar>
</template>
