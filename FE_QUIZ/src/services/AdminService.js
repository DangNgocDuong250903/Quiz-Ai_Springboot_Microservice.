import instance from "~/services/instance"

export const createSubject = async (data) => {
    return await instance.post(`/identity/admin/quiz/subjects`, data)
}

export const deleteSubject = async (id) => {
    return await instance.delete(`/identity/admin/quiz/subjects/${id}`)
}

export const createExams = async (data) => {
    return await instance.post(`/identity/admin/quiz/quizzes`, data)
}

export const createQuestions = async (data) => {
    return await instance.post(`/identity/admin/quiz/questions`, data)
}

export const getQuestions = async () => {
    return await instance.get(`/identity/quizzes/questions`)
}

export const getAnswers = async () => {
    return await instance.get(`/identity/quizzes/answers`)
}

export const createAnswer = async (data) => {
    return await instance.post(`/identity/admin/quiz/answers`, data)
}
