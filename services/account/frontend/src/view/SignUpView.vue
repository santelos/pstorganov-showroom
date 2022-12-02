<template>
    <div>
      <div><label>Email: </label><input type="email" v-model="email"/></div>
      <div><label>Password: </label><input type="password" v-model="password"/></div>
      <div><label>Password confirmation: </label><input type="password" v-model="passwordConfirmation"/></div>
      <div><label>Name: </label><input type="name" v-model="name"/></div>
      <div><button @click="onClick" :disabled="!isButtonEnabled">Submit SignUp</button></div>
      <div v-if="registrationSuccess"><b>SIGN-UP SUCCESS</b></div>
    </div>
  </template>
  
  <script>
  import { mapActions } from 'vuex'; 
  
  export default {
    name: 'SignUpView',
    props: ['source'],
    data() {
      return {
        email: "",
        password: "",
        passwordConfirmation: "",
        name: "",
        registrationSuccess: false,
      }
    },
    computed: {
      isButtonEnabled() {
        return (this.email !== "") && 
          (this.password !== "") &&
          (this.password === this.passwordConfirmation) &&
          (this.name !== "")
      }
    },
    methods: {
      ...mapActions('signUp/api', ['registrationNew']),
      onClick() {
        this.registrationNew({
          login: this.email, 
          password: this.password, 
          name: this.name
        }).then(() => {
          this.registrationSuccess = true
          return new Promise(resolve => setTimeout(() => resolve(), 5000))
        }).then(() => window.location = this.source)
      }
    }
  }
  </script>
  
  <!-- Add "scoped" attribute to limit CSS to this component only -->
  <style scoped>
  </style>