import axios from "axios";

axios.defaults.headers["content-type"] = "application/json";
axios.defaults.withCredentials = true;
axios.defaults.baseURL = "http://127.0.0.1:8082/";

axios.interceptors.response.use(null, (error) => {
  console.log(error.response.status);
  return Promise.reject(error);
});
