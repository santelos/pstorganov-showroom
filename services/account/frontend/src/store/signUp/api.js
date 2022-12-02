import axios from 'axios';

export default {
    namespaced: true,
    actions: {
        async registrationNew(_, {login, password, name}) {
            await axios({
                url: '/user-auth/registration/new',
                method: 'POST',
                data: {
                    login: login,
                    password: password,
                    name: name,
                },
            })
        }
    }
}
