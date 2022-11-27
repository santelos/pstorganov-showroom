<template>
  <div>
    <div><label>Email</label><input type="email" v-model="email"/></div>
    <div><label>Password</label><input type="password" v-model="password"/></div>
    <div><label>Password confirmation</label><input type="passwordConfirmation" v-model="passwordConfirmation"/></div>
    <div><button @click="onClick" :disabled="isButtonDisabled">SignIn In</button></div>
  </div>
</template>

<script>

export default {
  name: 'SignInView',
  data() {
    return {
      email: null,
      password: null,
      passwordConfirmation: null,
    }
  },
  computed: {
    isButtonDisabled() {
      return (this.email == null) || 
        (this.password == null) || 
        (this.password !== this.passwordConfirmation)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>