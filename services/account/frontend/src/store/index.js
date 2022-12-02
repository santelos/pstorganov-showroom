import { createStore } from "vuex";
import auth from "./auth";
import signUp from "./signUp";

export default createStore({
  namespaced: true,
  modules: {
    auth,
    signUp
  },
  state: {},
  getters: {},
  actions: {},
  mutations: {},
  strict: process.env.NODE_ENV !== 'production'
})
