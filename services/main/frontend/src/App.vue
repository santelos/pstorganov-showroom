<template>
  <SignInButton/>
  <UserInfo/>
</template>

<script>
import { mapActions } from 'vuex'
import SignInButton from './components/SignInButton.vue'
import UserInfo from './components/UserInfo.vue'

export default {
  name: 'App',
  components: {
    SignInButton,
    UserInfo
  },
  methods: {
    ...mapActions('auth/api', ['getAccessToken'])
  },
  mounted() {
    this.getAccessToken()
  }
}
</script>

<style>
</style>
