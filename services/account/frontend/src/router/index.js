import {createWebHistory, createRouter} from "vue-router";

const routes = [
    {
        path: '/login',
        name: 'LoginView',
        props: route => ({loginChallenge: route.query.login_challenge}),
        component: () => import('@/view/LoginView.vue')
    },
    {
        path: '/consent',
        name: 'ConsentView',
        props: route => ({consentChallenge: route.query.consent_challenge}),
        component: () => import('@/view/ConsentView.vue')
    },
    {
        path: '/sign-up',
        name: 'SignUp',
        props: route => ({source: route.query.source}),
        component: () => import('@/view/SignUpView.vue')
    }
]
const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
