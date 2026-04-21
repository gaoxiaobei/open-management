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
        path: 'system/menus',
        name: 'SystemMenus',
        component: () => import('@/views/system/menu/MenuListView.vue'),
        meta: { title: '菜单管理' },
      },
      {
        path: 'system/dicts',
        name: 'SystemDicts',
        component: () => import('@/views/system/dict/DictListView.vue'),
        meta: { title: '字典管理' },
      },
      {
        path: 'system/configs',
        name: 'SystemConfigs',
        component: () => import('@/views/system/config/ConfigListView.vue'),
        meta: { title: '参数配置' },
      },
      {
        path: 'org/dept',
        name: 'OrgDept',
        component: () => import('@/views/org/dept/DeptTreeView.vue'),
        meta: { title: '部门管理' },
      },
      {
        path: 'org/positions',
        name: 'OrgPositions',
        component: () => import('@/views/org/position/PositionListView.vue'),
        meta: { title: '岗位管理' },
      },
      {
        path: 'audit/login-logs',
        name: 'AuditLoginLogs',
        component: () => import('@/views/audit/login/LoginLogListView.vue'),
        meta: { title: '登录日志' },
      },
      {
        path: 'audit/operate-logs',
        name: 'AuditOperateLogs',
        component: () => import('@/views/audit/operate/OperateLogListView.vue'),
        meta: { title: '操作日志' },
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/views/message/MessageCenterView.vue'),
        meta: { title: '消息中心' },
      },
      {
        path: 'workflow/todo',
        name: 'WorkflowTodo',
        component: () => import('@/views/workflow/todo/TodoListView.vue'),
        meta: { title: '我的待办' },
      },
      {
        path: 'workflow/process',
        name: 'WorkflowProcess',
        component: () => import('@/views/workflow/process/ProcessDetailView.vue'),
        meta: { title: '流程详情' },
      },
      {
        path: 'workflow/manage',
        name: 'WorkflowManage',
        component: () => import('@/views/workflow/manage/WorkflowManageView.vue'),
        meta: { title: '流程管理' },
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
