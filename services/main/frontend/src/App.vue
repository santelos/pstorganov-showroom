<template>
  <div>
    <div v-if="accessToken">
      <LogoutButton/>
      <UserInfo/>
    </div>
    <div v-else>
      <div><SignInButton/></div>
      or
      <div><SignUpButton/></div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import SignInButton from './components/SignInButton.vue'
import SignUpButton from './components/SignUpButton.vue'
import LogoutButton from './components/LogoutButton.vue'
import UserInfo from './components/UserInfo.vue'

export default {
  name: 'App',
  components: {
    SignInButton,
    SignUpButton,
    LogoutButton,
    UserInfo
  },
  methods: {
    ...mapActions('auth/api', ['getAccessToken'])
  },
  computed: {
    ...mapGetters('auth/api', ['accessToken'])
  },
  mounted() {
    this.getAccessToken()
  }
}
</script>

<style>
</style>
