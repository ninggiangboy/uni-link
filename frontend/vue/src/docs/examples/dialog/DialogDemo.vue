<script setup lang="ts">
import { Button } from '@/ui/components/button'
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/ui/components/dialog'
import { Label } from '@/ui/components/field'
import { Input } from '@/ui/components/textfield'
</script>

<template>
  <Dialog>
    <DialogTrigger as-child>
      <Button>Sign up</Button>
    </DialogTrigger>
    <DialogContent class="md:max-w-[425px]">
      <DialogHeader>
        <DialogTitle>Sign up</DialogTitle>
        <DialogDescription>Please fill out the form below to sign up.</DialogDescription>
      </DialogHeader>
      <div class="flex-1 space-y-4">
        <div class="flex flex-col gap-2">
          <Label for="dfn">First Name</Label>
          <Input id="dfn" placeholder="First Name" autofocus />
        </div>
        <div class="flex flex-col gap-2">
          <Label for="dln">Last Name</Label>
          <Input id="dln" placeholder="Last Name" />
        </div>
      </div>
      <DialogFooter>
        <DialogClose as-child>
          <Button variant="outline"> Cancel </Button>
        </DialogClose>
        <DialogClose as-child>
          <Button type="button"> Save changes </Button>
        </DialogClose>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
