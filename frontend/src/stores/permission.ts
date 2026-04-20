import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { MenuItem } from '@/types/user'

export const usePermissionStore = defineStore('permission', () => {
  const menus = ref<MenuItem[]>([])
  const permissions = ref<string[]>([])

  function setMenus(menuList: MenuItem[]) {
    menus.value = menuList
  }

  function setPermissions(permList: string[]) {
    permissions.value = permList
  }

  function hasPermission(code: string) {
    return permissions.value.includes(code)
  }

  return { menus, permissions, setMenus, setPermissions, hasPermission }
})
