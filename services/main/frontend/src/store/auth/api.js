import axios from 'axios';

export default {
    namespaced: true,
    state: {
        accessToken: null
    },
    getters: {
        accessToken: s => s.accessToken
    },
    mutations: {
        setAccessToken: (s, token) => s.accessToken = token
    },
    actions: {
        async getAccessToken({commit}) {
            await axios({
                url: 'o/token',
                method: 'GET',
            }).then(response => {
                const accessToken = response.data.accessToken
                commit('setAccessToken', accessToken)
            })
        },
        async logout({commit}) {
            await axios({
                url: 'o/logout',
                method: 'POST',
            }).then(() => {
                commit('setAccessToken', null)
            })
        }
    }
}