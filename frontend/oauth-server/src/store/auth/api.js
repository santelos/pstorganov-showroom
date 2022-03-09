import axios from "axios";

export default {
  namespaced: true,
  state: {},
  getters: {},
  mutations: {},
  actions: {
    async auth(_, { login, password }) {
      const formData = new FormData();
      formData.append("username", login);
      formData.append("password", password);
      await axios.post("/login", formData, {
        withCredentials: true,
      });
    },
    async oauthAutorize(_, { params }) {
      await axios.get("oauth2/authorize", {
        params: params,
      });
    },
  },
};
