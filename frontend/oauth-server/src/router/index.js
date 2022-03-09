import { createRouter, createWebHistory } from "vue-router";
import LoginForm from "../views/LoginForm.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/oauth2/authorize",
      name: "home",
      component: LoginForm,
    },
  ],
});

export default router;
