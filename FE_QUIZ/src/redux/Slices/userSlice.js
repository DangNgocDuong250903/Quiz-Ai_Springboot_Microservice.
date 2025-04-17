import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    id: "",
    profileId: "",
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    bio: "",
    city: "",
    mssv: "",
    emailVerified: false,
    createdAt: "",
    token: "",
    phoneNumber: "",
    dob: "",
    gender: "",
    status: "",
    roles: [],
    searchUser: null,
};


const userSlice = createSlice({
    name: "user",
    initialState,
    reducers: {
        updateUser(state, action) {
            const {
                id = "",
                firstName = "",
                profileId = "",
                lastName = "",
                email = "",
                username = "",
                phoneNumber = "",
                createdAt = "",
                mssv = "",
                bio = "",
                gender = "",
                city = "",
                token = "",
                dob = "",
                emailVerified = false,
                status = "",
                roles = [],
                imageUrl = "",
            } = action.payload

            state.lastName = lastName
            state.firstName = firstName
            state.profileId = profileId
            state.avatar = imageUrl
            state.id = id
            state.bio = bio
            state.mssv = mssv
            state.gender = gender
            state.email = email
            state.createdAt = createdAt
            state.dob = dob
            state.username = username
            state.phoneNumber = phoneNumber
            state.city = city
            state.emailVerified = emailVerified
            state.token = token
            state.status = status
            state.roles = roles
        },
        updateStatus(state, action) {
            state.status = action.payload
        },
        resetUser(state) {
            localStorage.removeItem("token")
            state.id = ""
            state.profileId = ""
            state.firstName = ""
            state.lastName = ""
            state.username = ""
            state.email = ""
            state.bio = ""
            state.mssv = ""
            state.city = ""
            state.emailVerified = false
            state.createdAt = ""
            state.token = ""
            state.phoneNumber = ""
            state.dob = ""
        }
    }
});

export const { updateUser, resetUser, updateStatus } = userSlice.actions

export default userSlice.reducer;
