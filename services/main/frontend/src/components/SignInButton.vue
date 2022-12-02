<template>
  <div class="main">
    <a v-bind:href="signInUrl">SignIn</a>
  </div>
</template>

<script>
const baseUrl = process.env.VUE_APP_BASE_API_URL

export default {
  name: 'SignInButton',
  computed: {
    signInUrl() {
      return baseUrl + '/o/login'
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
