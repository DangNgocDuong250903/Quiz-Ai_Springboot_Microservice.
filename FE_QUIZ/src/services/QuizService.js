import instance from "~/services/instance"

export const getExamsBySubjectId = async (id) => {
    return await instance.get(`/identity/quizzes/by-subject/${id}`, {})
}

export const getExams = async () => {
    return await instance.get(`/identity/quizzes/quizzes`)
}

export const start = async (data) => {
    return await instance.post(`/identity/quizzes/start`, data)
}

export const submit = async ({ data }) => {
    return await instance.post(`/identity/quizzes/submit`, data)
}

export const getSubjects = async () => {
    return await instance.get(`/identity/quizzes/subjects`)
}







