<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { ExternalLink } from 'lucide-vue-next'
</script>

<template>
  <Button
    as="a"
    href="https://react-spectrum.adobe.com/react-aria/index.html"
    target="_blank"
    rel="noopener noreferrer"
  >
    React Aria Components
    <ExternalLink />
  </Button>
</template>
