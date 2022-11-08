<template>
  <div>
    <label>Login</label><input type="username" v-model="username"/>
    <label>Password</label><input type="password" v-model="password"/>
    <button @click="onClick">Log In</button>
  </div>
</template>

<script>
import { mapActions } from 'vuex'; 

export default {
  name: 'LoginView',
  props: ['loginChallenge'],
  data() {
    return {
      username: null,
      password: null
    }
  },
  methods: {
    ...mapActions('auth/api', ['acceptLogin']),
    onClick() {
      this.acceptLogin({
        loginChallenge: this.loginChallenge,
        username: this.username,
        password: this.password
      })
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>