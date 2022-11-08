<template>
  <div>
      <div v-if="accessToken">AccessToken: [{{accessToken}}]</div>
      <div v-else>Unauthorized</div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'UserInfo',
  computed: {
    ...mapGetters('auth/api', ['accessToken'])
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
