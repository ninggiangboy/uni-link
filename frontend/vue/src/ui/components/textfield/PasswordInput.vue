<script setup lang="ts">
import { ref } from 'vue'
import { Eye, EyeOff } from 'lucide-vue-next'
import { FieldGroup } from '@/ui/components/field'
import { Button } from '@/ui/components/button'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: string
  placeholder?: string
}>()

const visible = ref(false)
</script>

<template>
  <div data-slot="password-field" :class="cn('grid gap-1', $props.class)">
    <FieldGroup class="pl-3 pr-1 py-0">
      <input
        v-bind="$attrs"
        :type="visible ? 'text' : 'password'"
        :placeholder="placeholder"
        class="h-full flex-1 bg-transparent focus-visible:outline-none placeholder:text-muted-foreground"
      />
      <Button type="button" variant="ghost" size="iconSm" @click="visible = !visible">
        <Eye v-if="visible" class="size-4" />
        <EyeOff v-else class="size-4" />
      </Button>
    </FieldGroup>
  </div>
</template>
