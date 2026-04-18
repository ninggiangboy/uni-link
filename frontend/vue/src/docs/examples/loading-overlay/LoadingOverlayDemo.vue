<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { Label } from '@/ui/components/field'
import { LoadingOverlay } from '@/ui/components/loading-overlay'
import { Input } from '@/ui/components/textfield'
</script>

<template>
  <LoadingOverlay :is-loading="true" class="w-full max-w-[350px]">
    <div class="space-y-4">
      <div class="space-y-2">
        <Label for="loe">Email</Label>
        <Input id="loe" placeholder="Enter your email" />
      </div>
      <div class="space-y-2">
        <Label for="lop">Password</Label>
        <Input id="lop" placeholder="Enter your password" />
      </div>
      <Button class="w-full"> Login </Button>
    </div>
  </LoadingOverlay>
</template>
