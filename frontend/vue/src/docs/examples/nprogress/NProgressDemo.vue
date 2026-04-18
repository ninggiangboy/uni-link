<script setup lang="ts">
import { ref } from 'vue'
import { Button } from '@/ui/components/button'
import { useNProgress } from '@/ui'

const isFetching = ref(false)
useNProgress(isFetching)

function refetch() {
  isFetching.value = true
  window.setTimeout(() => {
    isFetching.value = false
  }, 1000)
}
</script>

<template>
  <div>
    <Button variant="outline" :loading="isFetching" @click="refetch"> Refetch </Button>
  </div>
</template>
