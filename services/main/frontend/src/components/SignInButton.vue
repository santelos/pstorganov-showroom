<template>
  <div class="main">
    <a v-if="!accessToken" v-bind:href="signInUrl">SignIn</a>
    <button v-else v-on:click="logout">LogOut</button>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

const baseUrl = process.env.VUE_APP_BASE_API_URL

export default {
  name: 'SignInButton',
  computed: {
    ...mapGetters('auth/api', ['accessToken']),
    signInUrl() {
      return baseUrl + '/o/login'
    }
  },
  methods: {
    ...mapActions('auth/api', ['logout']),
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
