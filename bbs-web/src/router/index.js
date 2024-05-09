import { createWebHistory, createRouter } from "vue-router";
import cookies from "vue-cookies";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "login",
      component: () => import("../views/login.vue"),
      meta: {
        title: "用户登录",
      },
    },
    {
      path: "/register",
      name: "register",
      component: () => import("../views/register.vue"),
      meta: {
        title: "用户注册",
      },
    },
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
          path: "/profile",
          name: "profile",
          component: () => import("../views/profile.vue"),
          meta: {
            title: "个人中心",
          },
        },
        {
          path: "/publish",
          name: "publish",
          component: () => import("../views/publish.vue"),
          meta: {
            title: "发帖",
          },
        }
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
    document.title = "NUAA校园论坛";
  }
  let user = cookies.get("bbs-web");
  if (to.name === "login") {
    next();
  } else if (user) {
    // 如果已经登录
    next();
  } else {
    // 否则跳转到登录页
    console.log("路由拦截，转到登录页");
    next({
      path: "/login",
    });
  }
});

export default router;
