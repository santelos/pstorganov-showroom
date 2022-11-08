import axios from 'axios'

const baseUrl = process.env.VUE_APP_BASE_API_URL

axios.defaults.baseURL = baseUrl
axios.defaults.headers['content-type'] = 'application/json'
