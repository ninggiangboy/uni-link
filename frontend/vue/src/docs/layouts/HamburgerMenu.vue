<script setup lang="ts">
import Logo from '@/docs/components/Logo.vue'
import { Button } from '@/ui/components/button'
import { Menu } from 'lucide-vue-next'
import { watch } from 'vue'
import { useRoute } from 'vue-router'
import ModulePicker from './ModulePicker.vue'
import SidebarMenu from './SidebarMenu.vue'

const route = useRoute()
const open = defineModel<boolean>({ default: false })

watch(
  () => route.path,
  () => {
    open.value = false
  },
)
</script>

<template>
  <div class="relative">
    <Button
      variant="ghost"
      size="icon"
      class="size-10 [&>svg]:size-6"
      aria-label="Open menu"
      @click="open = true"
    >
      <Menu />
    </Button>

    <Teleport to="body">
      <div
        v-if="open"
        class="fixed inset-0 z-[100] lg:hidden bg-background/80 backdrop-blur-sm"
        @click="open = false"
      />
      <aside
        v-if="open"
        class="fixed inset-y-0 left-0 z-[101] w-[min(100vw,320px)] border-r border-border bg-background lg:hidden shadow-lg overflow-y-auto"
        @click.stop
      >
        <div class="px-6 pt-4 pb-2">
          <Logo :with-name="false" />
        </div>
        <ModulePicker />
        <div class="max-h-[60vh] overflow-y-auto">
          <SidebarMenu />
        </div>
      </aside>
    </Teleport>
  </div>
</template>
