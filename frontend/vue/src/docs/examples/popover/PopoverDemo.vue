<script setup lang="ts">
import { ref } from 'vue'
import { Button } from '@/ui/components/button'
import { Popover, PopoverContent, PopoverTrigger } from '@/ui/components/popover'
import { Switch } from '@/ui/components/switch-ui'

const wifi = ref(true)
const bt = ref(true)
const mute = ref(false)
</script>

<template>
  <Popover>
    <PopoverTrigger as-child>
      <Button variant="outline"> Settings </Button>
    </PopoverTrigger>
    <PopoverContent side="bottom" align="start">
      <div class="flex flex-col gap-4">
        <div class="inline-flex items-center gap-2 text-sm font-medium leading-none">
          <Switch v-model="wifi" />
          Wi-Fi
        </div>
        <div class="inline-flex items-center gap-2 text-sm font-medium leading-none">
          <Switch v-model="bt" />
          Bluetooth
        </div>
        <div class="inline-flex items-center gap-2 text-sm font-medium leading-none">
          <Switch v-model="mute" />
          Mute
        </div>
      </div>
    </PopoverContent>
  </Popover>
</template>
