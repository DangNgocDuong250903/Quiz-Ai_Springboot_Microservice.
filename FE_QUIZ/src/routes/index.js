import AdminPage from "~/pages/Admin";
import HomePage from "~/pages/Home";
import NotFoundPage from "~/pages/NotFound";
import QuizPage from "~/pages/Quiz";
import QuizListPage from "~/pages/QuizList";


export const route = [
    {
        path: '/',
        element: HomePage,
    },
    {
        path: '/quiz/:examId',
        element: QuizPage,
    },
    {
        path: '/exams/:id',
        element: QuizListPage,
    },
    {
        path: '/admin',
        element: AdminPage,
        isPrivate: true
    },
    {
        path: '*',
        element: NotFoundPage,
    },
]

