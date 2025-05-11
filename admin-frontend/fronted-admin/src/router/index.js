import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'

// Fix the issue of vue-router reporting errors when frequently clicking menu items in version 3.0 and above.
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    // meta: { title: 'Home' },
    children: [
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import('../views/Feedback.vue'),
        meta: { title: 'Feedback' }
      },
      {
        path: 'scooter',
        name: 'Scooter',
        component: () => import('../views/ScooterView.vue'),
        meta: { title: 'Scooter Configuration' }
      },
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('../views/UserManagement.vue'),
        meta: { title: 'User Management' }
      },
      {
        path: 'order',
        name: 'OrderManagement',
        component: () => import('../views/OrderManagement.vue'),
        meta: { title: 'Order Management' }
      },
      {
        path: 'station',
        name: 'StationManagement',
        component: () => import('../views/StationView.vue'),
        meta: { title: 'Station Management' }
      }
    ]
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// Global route guard for login check
router.beforeEach((to, from, next) => {
  // Allow direct access to login page
  if (to.path === '/login') {
    return next()
  }
  // Require login for all other pages
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    return next('/login')
  }
  next()
})

export default router
