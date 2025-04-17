import axios from 'axios'
import { baseURL, notificationURL } from '~/utils'
import access from '~/services/access'
import instance from '~/services/instance'

export const register = async (data) => {
    return await access.post(`/identity/users/registration`, data)
}

export const login = async (newData) => {
    return await access.post(`/identity/auth/token`, newData)
}

export const loginGoogle = async (data) => {
    return await instance.post(`/identity/auth/google`, data)
}

export const logout = async (token) => {
    return await instance.post(
        `/identity/auth/logout`, { token });
};

export const update = async (data) => {
    return await instance.patch(`/identity/users/my-profile`, data)
}

export const getLoginHistory = async () => {
    return await instance.get(`/identity/login-history/me`)
}

export const block = async (id) => {
    return await instance.post(`/profile/block?targetUserId=${id}`, {})
}

export const changePassword = async ({ data }) => {
    return await instance.post(`/identity/auth/change-password`, data)
}

export const unBlock = async ({ id }) => {
    return await instance.post(`/profile/unblock?targetUserId=${id}`, {})
}

export const blockList = async ({ page, size }) => {
    return await instance.get(`/profile/block-list?page=1&size=10`)
}

export const getDetailUser = async ({ id }) => {
    return await instance.get(`/profile/users/${id}`)
}

export const getDetailUserByUserId = async ({ id }) => {
    return await instance.get(`/identity/users/${id}`)

}

export const updateStatus = async ({ status }) => {
    return await instance.patch(`/identity/users/my-profile/status?status=${status}`, {})
}

export const handleRefreshToken = async (token) => {
    return await instance.post(
        `${baseURL}/identity/auth/refresh`,
        { token },
    );
};

export const forgotPassword = async (data) => {
    return await access.post(
        `${notificationURL}/notification/email/send-forget-pass?email=${data.email}`);
}

export const resetPassword = async ({ token, password }) => {
    return await access.post(`${notificationURL}/notification/email/reset-password?token=${token}&newPassword=${password}`);
}

export const verifyEmail = async (data) => {
    return await axios.post(`${notificationURL}/notification/email/send-verification?email=${data}`);
}

export const verify = async ({ data }) => {
    return await axios.get(`${notificationURL}/notification/email/verify?token=${data}`);
}

export const setAvatar = async ({ data }) => {
    const formData = new FormData
    formData.append("request", JSON.stringify(data?.request));
    formData.append("avatar", data?.file);
    return await instance.post(`/post/set-avatar`, formData)
}

export const disableAcount = async ({ password }) => {
    return await instance.delete(`/identity/users/delete?password=${password}`)
}

export const deleteAccount = async ({ password }) => {
    return await instance.delete(`/identity/users/delete-permanently?password=${password}`)
}



