import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/discover',
    name: 'Discover',
    component: () => import('@/views/Discover.vue')
  },
  {
    path: '/browse',
    name: 'BrowseSkills',
    component: () => import('@/views/BrowseSkills.vue')
  },
  {
    path: '/skill/:id',
    name: 'SkillDetail',
    component: () => import('@/views/SkillDetail.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/Orders.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue')
  },
  {
    path: '/wallet',
    name: 'Wallet',
    component: () => import('@/views/Wallet.vue')
  },
  {
    path: '/publish-skill',
    name: 'PublishSkill',
    component: () => import('@/views/PublishSkill.vue')
  },
  {
    path: '/my-skills',
    name: 'MySkills',
    component: () => import('@/views/MySkills.vue')
  },
  {
    path: '/edit-skill/:id',
    name: 'EditSkill',
    component: () => import('@/views/EditSkill.vue')
  },
  {
    path: '/skill-orders',
    name: 'SkillOrders',
    component: () => import('@/views/SkillOrders.vue')
  },
  {
    path: '/earnings',
    name: 'Earnings',
    component: () => import('@/views/Earnings.vue')
  },
  {
    path: '/profile-edit',
    name: 'ProfileEdit',
    component: () => import('@/views/ProfileEdit.vue')
  },
  {
    path: '/address',
    name: 'AddressManagement',
    component: () => import('@/views/AddressManagement.vue')
  },
  {
    path: '/password-change',
    name: 'PasswordChange',
    component: () => import('@/views/PasswordChange.vue')
  },
  {
    path: '/real-name-auth',
    name: 'RealNameAuth',
    component: () => import('@/views/RealNameAuth.vue')
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/Settings.vue')
  },
  {
    path: '/notices',
    name: 'Notices',
    component: () => import('@/views/Notices.vue')
  },
  {
    path: '/worker-application',
    name: 'WorkerApplication',
    component: () => import('@/views/WorkerApplication.vue')
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue')
  },
  {
    path: '/history',
    name: 'BrowseHistory',
    component: () => import('@/views/BrowseHistory.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue')
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/Categories.vue')
      },
      {
        path: 'skills',
        name: 'AdminSkills',
        component: () => import('@/views/admin/Skills.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue')
      },
      {
        path: 'reviews',
        name: 'AdminReviews',
        component: () => import('@/views/admin/ReviewManagement.vue')
      },
      {
        path: 'finance',
        name: 'AdminFinance',
        component: () => import('@/views/admin/FinanceManagement.vue')
      },
      {
        path: 'notices',
        name: 'AdminNotices',
        component: () => import('@/views/admin/NoticeManagement.vue')
      },
      {
        path: 'certifications',
        name: 'AdminCertifications',
        component: () => import('@/views/admin/CertificationReview.vue')
      },
      {
        path: 'worker-approvals',
        name: 'AdminWorkerApprovals',
        component: () => import('@/views/admin/WorkerApprovals.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const rawUserInfo = localStorage.getItem('userInfo')
  let role = 0
  if (rawUserInfo) {
    try {
      role = JSON.parse(rawUserInfo)?.role ?? 0
    } catch {
      role = 0
    }
  }

  const workerOnlyPaths = ['/publish-skill', '/my-skills', '/skill-orders', '/earnings']
  const isWorkerOnlyRoute = workerOnlyPaths.includes(to.path) ||
    (typeof to.name === 'string' && to.name === 'EditSkill')
  const isWorkerApplicationRoute = to.path === '/worker-application'

  if (!token && (to.path.startsWith('/admin') || isWorkerOnlyRoute || isWorkerApplicationRoute)) {
    next('/login')
    return
  }

  if (to.path.startsWith('/admin') && role !== 2) {
    next('/home')
    return
  }

  if (isWorkerOnlyRoute && role !== 1 && role !== 2) {
    next('/profile')
    return
  }

  if (isWorkerApplicationRoute && role !== 0) {
    next('/profile')
    return
  }

  next()
})

export default router
