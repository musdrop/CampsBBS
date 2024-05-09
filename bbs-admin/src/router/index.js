import { createWebHistory, createRouter } from "vue-router";
import cookies from "vue-cookies";
// 创建路由
const router = createRouter({
  //
  history: createWebHistory(import.meta.env.BASE_URL),
  // 路由配置
  routes: [
    // 登录页
    {
      path: "/login",
      name: "login",
      component: () => import("../views/login.vue"),
      meta: {
        title: "管理员登录",
      },
    },
    // 首页
    {
      path: "/",
      name: "index",
      redirect: "/home",
      component: () => import("../views/index.vue"),
      children: [
        {
          path: "/home",
          name: "home",
          component: () => import("../views/home.vue"),
          meta: {
            title: "首页",
          },
        },
        {
          path: "/user",
          name: "user",
          component: () => import("../views/user.vue"),
          meta: {
            title: "用户管理",
          },
        },
        {
          path: "/forum",
          name: "forum",
          component: () => import("../views/forum.vue"),
          meta: {
            title: "帖子管理",
          },
        },
        {
          path: "/test",
          name: "test",
          component: () => import("../views/test.vue"),
          meta: {
            title: "测试首页",
          },
        },
      ],
    },
  ],
});

// 路由拦截器
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    // 判断是否有标题
    document.title = to.meta.title;
  } else {
    document.title = "校园论坛管理系统";
  }
  // 获取cookies里的bbs-admin属性
  let user = cookies.get("bbs-admin");
  //next()
  // 如果是登录页，直接放行
  if (to.name === "login") {
    next();
  } else if (user) {
    // 如果已经登录
    next();
  } else {
    // 否则跳转到登录页
    console.log("请先登录");
    next({
      path: "/login",
    });
  }
});

export default router;
