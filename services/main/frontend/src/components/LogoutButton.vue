<template>
  <div>
    <button @click="logout">Logout</button>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'LogoutButton',
  methods: {
    ...mapActions('auth/api', ['logout'])
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
