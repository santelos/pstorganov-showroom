<script setup></script>

<template>
  <form @submit.prevent="submit">
    <table>
      <tr>
        <td>Login:</td>
        <td><input type="text" name="login" v-model="login" /></td>
      </tr>
      <tr>
        <td>Password:</td>
        <td><input type="password" name="password" v-model="password" /></td>
      </tr>
      <tr>
        <td><button name="submit" type="submit">Log in</button></td>
      </tr>
    </table>
  </form>
</template>

<script>
import { mapActions } from "vuex";
export default {
  name: "LoginForm",
  data() {
    return {
      login: null,
      password: null,
    };
  },
  methods: {
    ...mapActions("auth/api", ["auth", "oauthAutorize"]),
    submit() {
      const route = this.$route;
      console.log(route);
      this.auth({ login: this.login, password: this.password }).then(() => {
        this.oauthAutorize({ params: route.query });
      });
    },
  },
};
</script>
