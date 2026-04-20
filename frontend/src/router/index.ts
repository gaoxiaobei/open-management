import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { public: true },
  },
  {
    path: '/',
    component: () => import('@/layout/BasicLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '工作台' },
      },
      {
        path: 'system/users',
        name: 'SystemUsers',
        component: () => import('@/views/system/user/UserListView.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'system/roles',
        name: 'SystemRoles',
        component: () => import('@/views/system/role/RoleListView.vue'),
        meta: { title: '角色管理' },
      },
      {
        path: 'org/dept',
        name: 'OrgDept',
        component: () => import('@/views/org/dept/DeptTreeView.vue'),
        meta: { title: '部门管理' },
      },
      {
        path: 'hr/employees',
        name: 'HrEmployees',
        component: () => import('@/views/hr/employee/EmployeeListView.vue'),
        meta: { title: '员工档案' },
      },
      {
        path: 'oa/leave',
        name: 'OaLeave',
        component: () => import('@/views/oa/leave/LeaveListView.vue'),
        meta: { title: '请假申请' },
      },
      {
        path: 'asset',
        name: 'Asset',
        component: () => import('@/views/asset/AssetListView.vue'),
        meta: { title: '资产台账' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  if (!to.meta.public && !userStore.isLoggedIn()) {
    next('/login')
  } else {
    next()
  }
})

export default router
