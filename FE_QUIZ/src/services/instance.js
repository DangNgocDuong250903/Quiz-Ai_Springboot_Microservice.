import { notification } from 'antd';
import axios from 'axios';
import { baseURL, getToken } from '~/utils';

let instance = axios.create({
    baseURL: baseURL
});

instance.interceptors.request.use(
    async (config) => {
        const token = getToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);


instance.interceptors.response.use(
    async (response) => {
        return response.data;
    },
    async (error) => {

        if (error.response?.data?.code === 1401) {
            localStorage.removeItem("token");
            window.location.href = "/login";
            return Promise.reject(error);
        }

        if (error.response?.status !== 410) {
            notification.open({
                type: "warning",
                placement: "bottomRight",
                message: error.response?.data?.message || "Something went wrong!"
            })
        }

        return Promise.reject(error);
    }
);


export default instance;