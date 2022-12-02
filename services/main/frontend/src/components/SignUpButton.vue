<template>
  <div class="main">
    <a v-bind:href="signUpUrl">SignUp</a>
  </div>
</template>

<script>
const baseIdUrl = process.env.VUE_APP_ID_BASE_API_URL
const baseUrl = process.env.VUE_APP_BASE_API_URL

export default {
  name: 'SignUpButton',
  computed: {
    signUpUrl() {
      return baseIdUrl + '/sign-up?source=' + encodeURIComponent(baseUrl)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
