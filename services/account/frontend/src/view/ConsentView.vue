<template>
  <div>
    <div>
      Your consent:
      <b v-for="scope in consentScopes" v-bind:key="scope">{{scope}}</b>
    </div>
    <button @click="onClick">Accept</button>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'; 

export default {
  name: 'ConsentView',
  props: ['consentChallenge'],
  beforeRouteEnter(to, from, next) {
    next(vm => vm.getConsent({consentChallenge: vm.consentChallenge}));
  },
  beforeRouteUpdate(to, from, next) {
    next(vm => vm.getConsent({consentChallenge: vm.consentChallenge}));
  },
  computed: {
    ...mapGetters('auth/api', ['consentScopes'])
  },
  methods: {
    ...mapActions('auth/api', ['getConsent', 'acceptConsent']),
    onClick() {
      this.acceptConsent({
        consentChallenge: this.consentChallenge,
        acceptedScope: this.consentScopes,
      })
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>