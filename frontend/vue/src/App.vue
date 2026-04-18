<script setup lang="ts">
import Provider from '@/ui/components/provider/Provider.vue'
import { RouterView } from 'vue-router'
</script>

<template>
  <Provider>
    <RouterView />
  </Provider>
</template>
