<script setup lang="ts">
import { Select } from '@/ui/components/select'
import { languageOptions } from './selectLanguages'
</script>

<template>
  <div class="w-full space-y-4">
    <Select multiple disabled :options="languageOptions" />
  </div>
</template>
