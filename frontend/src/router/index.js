import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/Home.vue'),
    redirect: '/create',
    children: [
      {
        path: 'create',
        name: 'CreateCard',
        component: () => import('../views/CreateCard.vue'),
        meta: {
          title: '创建校园卡'
        }
      },
      {
        path: 'query',
        name: 'QueryCard',
        component: () => import('../views/QueryCard.vue'),
        meta: {
          title: '校园卡查询'
        }
      },
      {
        path: 'recharge',
        name: 'RechargeCard',
        component: () => import('../views/RechargeCard.vue'),
        meta: {
          title: '余额充值'
        }
      },
      {
        path: 'consume',
        name: 'ConsumeCard',
        component: () => import('../views/ConsumeCard.vue'),
        meta: {
          title: '校园卡消费'
        }
      },
      {
        path: 'delete',
        name: 'DeleteCard',
        component: () => import('../views/DeleteCard.vue'),
        meta: {
          title: '校园卡注销'
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title
    ? `${to.meta.title} - ${import.meta.env.VITE_APP_TITLE}`
    : import.meta.env.VITE_APP_TITLE

  // 这里可以添加其他路由守卫逻辑，比如：
  // - 权限验证
  // - 登录状态检查
  // - 路由切换动画
  // - 页面加载进度条

  next()
})

// 路由错误处理
router.onError((error) => {
  console.error('路由错误:', error)
  // 这里可以添加错误处理逻辑，比如：
  // - 显示错误提示
  // - 重定向到错误页面
  // - 记录错误日志
})

export default router
