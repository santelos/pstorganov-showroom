import axios from 'axios';

export default {
    namespaced: true,
    state: {
        baseAuth: JSON.parse(localStorage.getItem('baseAuth')) || null,
        consentRequest: null,
    },
    getters: {
        baseAuth: s => s.baseAuth,
        consentScopes: s => s.consentRequest.requestedScope
    },
    mutations: {
        setBaseAuth: (s, baseAuth) => {
            s.baseAuth = baseAuth
            localStorage.setItem('baseAuth', JSON.stringify(baseAuth))
        },
        setConsentRequest: (s, consentRequest) => s.consentRequest = consentRequest
    },
    actions: {
        async acceptLogin({commit}, {loginChallenge, username, password}) {
            const auth = {
                username: username,
                password: password
            }
            await axios({
                url: '/user-auth/accept-login',
                params: {
                    login_challenge: loginChallenge
                },
                auth: auth,
                method: 'GET',
            }).then(response => {
                commit('setBaseAuth', auth)
                console.log(response.data)
                window.location = response.data.redirectTo
            })
        },
        async getConsent({commit, getters}, {consentChallenge}) {
            await axios({
                url: '/user-auth/get-consent',
                params: {
                    consent_challenge: consentChallenge
                },
                auth: getters.baseAuth,
                method: 'GET',
            }).then(response => {
                commit('setConsentRequest', response.data)
            })
        },
        async acceptConsent({getters}, {consentChallenge, acceptedScope}) {
            await axios({
                url: '/user-auth/accept-consent',
                method: 'POST',
                params: {
                    consent_challenge: consentChallenge
                },
                data: {
                    scope: acceptedScope
                },
                auth: getters.baseAuth,
            }).then(response => {
                console.log(response.data)
                window.location = response.data.redirectTo
            })
        },
        // async getAccessToken({commit}, {refresh_token}) {
        //     commit('setStatus', 'loading')
        //     await axios({
        //         url: 'v1/users/get_access_token',
        //         data: {
        //             refresh_token: refresh_token
        //         },
        //         method: 'POST',
        //         skipAuthRefresh: true
        //     }).then(response => {
        //         const token = response.data.token
        //         localStorage.setItem('access_token', token)
        //         commit('setAccessToken', token)
        //         commit('setStatus', 'success')
        //     })
        // }
    }
}
