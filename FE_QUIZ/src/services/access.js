import { notification } from 'antd';
import axios from 'axios';
import { baseURL } from '~/utils';

let access = axios.create({
    baseURL: baseURL
});

access.interceptors.request.use((config) => {
    return config;
}, (error) => {
    return Promise.reject(error);
});

access.interceptors.response.use(
    async (response) => {
        return response.data;
    },
    async (error) => {

        if (error.response?.status !== 410) {
            if (error.response?.data?.message === "Unauthenticated") {
                notification.open({
                    type: "warning",
                    placement: "bottomRight",
                    message: "Tên tài khoản hoặc mật khẩu không đúng",
                })
            } else {
                notification.open({
                    type: "warning",
                    placement: "bottomRight",
                    message: error.response?.data?.message || "Something went wrong!"
                })
            }

        }

        return Promise.reject(error);
    }
);


export default access;