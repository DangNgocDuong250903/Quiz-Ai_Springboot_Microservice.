export const baseURL = import.meta.env.VITE_API_URL_BACKEND

export const notificationURL = import.meta.env.VITE_API_URL_BACKEND_NOTI

export const getToken = () => {
    const token = localStorage.getItem("token");
    return token;
};

