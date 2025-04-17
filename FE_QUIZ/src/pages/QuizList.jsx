import { useState } from "react";
import {
  ArrowLeft,
  Brain,
  ChevronDown,
  Clock,
  HelpCircle,
  LogOut,
  User,
} from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import * as QuizService from "~/services/QuizService";
import * as AccessService from "~/services/AccessService";
import { useQuery } from "@tanstack/react-query";
import { useDispatch, useSelector } from "react-redux";
import { upperCase } from "lodash";
import { resetUser } from "~/redux/Slices/userSlice";

export default function QuizListPage() {
  const [category, setCategory] = useState("History");
  const { id } = useParams();
  const user = useSelector((state) => state?.user);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const fetchExams = async (id) => {
    const res = await QuizService.getExamsBySubjectId(id);
    return res?.result;
  };

  const { data, isLoading } = useQuery({
    queryKey: ["exams", id],
    queryFn: () => fetchExams(id),
    enabled: !!id,
  });

  const handleLogout = async () => {
    const res = await AccessService.logout(user?.token);
    if (res?.code === 1000) {
      dispatch(resetUser());
      navigate("/login");
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header - Same as HomePage */}
      <header className="bg-white shadow">
        <div className="container mx-auto px-4 py-6">
          <nav className="flex justify-between items-center">
            <div className="flex items-center">
              <Brain className="h-8 w-8 text-indigo-600" />
              <span className="ml-2 text-2xl font-bold text-indigo-600">
                QuizMaster
              </span>
            </div>
            {user?.token ? (
              <div>
                <button
                  className="flex text-white items-center space-x-2 bg-indigo-700 px-4 py-2 rounded-lg"
                  onClick={() => setIsProfileOpen(!isProfileOpen)}
                >
                  <User size={20} />
                  <span>{user.username}</span>
                  <ChevronDown size={16} />
                </button>

                {isProfileOpen && (
                  <div className="absolute right-0 mt-2 w-64 bg-white rounded-lg shadow-lg z-10">
                    <div className="p-4 border-b border-gray-200">
                      <p className="font-semibold">
                        {user?.firstName} {user?.lastName}
                      </p>
                      <p className="text-sm text-gray-600">{user.email}</p>
                      <p className="text-xs text-gray-500 mt-1">
                        {user.roles[0]?.name}
                      </p>
                    </div>
                    <div>
                      <button
                        onClick={handleLogout}
                        className="w-full cursor-pointer text-left p-3 hover:bg-gray-100 flex items-center space-x-2 text-red-600"
                      >
                        <LogOut size={16} />
                        <span>Logout</span>
                      </button>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <div className="flex items-center space-x-4">
                <button
                  onClick={() => navigate("/login")}
                  className="px-4 py-2 cursor-pointer text-gray-600 hover:text-indigo-600"
                >
                  Sign In
                </button>
                <button
                  onClick={() => navigate("/register")}
                  className="px-4 py-2 cursor-pointer bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
                >
                  Sign Up
                </button>
              </div>
            )}
          </nav>
        </div>
      </header>

      {/* Category Header */}
      <section className="bg-indigo-600 text-white py-12">
        <div className="container mx-auto px-4">
          <div className="flex items-center mb-4">
            <span
              onClick={() => navigate("/")}
              className="flex cursor-pointer items-center text-white hover:text-indigo-200"
            >
              <ArrowLeft className="h-5 w-5 mr-2" />
              Back to Categories
            </span>
          </div>
          <h1 className="text-4xl font-bold">Quizzes</h1>
        </div>
      </section>

      {/* Quiz List */}
      <section className="py-12">
        <div className="container mx-auto px-4">
          {/* Filter bar */}
          <div className="flex flex-col md:flex-row items-center justify-between mb-8 bg-white p-4 rounded-lg shadow">
            <div className="mb-4 md:mb-0">
              <h2 className="text-xl font-semibold">Available Quizzes</h2>
              <p className="text-gray-600">
                Found {data?.length} quizzes in this category
              </p>
            </div>
          </div>

          {/* Quiz Cards */}
          <div className="space-y-6">
            {data?.map((quiz) => (
              <div
                key={quiz.id}
                className="bg-white rounded-lg shadow-md overflow-hidden"
              >
                <div className="p-6">
                  <div className="flex justify-between items-start">
                    <div>
                      <h3 className="text-2xl font-semibold mb-2">
                        {quiz.title}
                      </h3>
                      <p className="text-gray-600 mb-4">
                        Môn {quiz.subjectName}
                      </p>

                      <div className="flex flex-wrap gap-4 text-sm text-gray-600 mb-6">
                        <div className="flex items-center">
                          <HelpCircle className="h-4 w-4 mr-1" />
                          <span>{quiz.durationMinutes} Minutes</span>
                        </div>
                        <div className="flex items-center">
                          <Clock className="h-4 w-4 mr-1" />
                          <span>
                            Time Limit: {`${upperCase(quiz?.hasNoTimeLimit)}`}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Time options and Start button */}
                  <div className="flex flex-col sm:flex-row items-center gap-4">
                    <span
                      onClick={() => navigate(`/quiz/${quiz.id}`)}
                      className="w-full cursor-pointer sm:w-auto px-6 py-2 bg-indigo-600 text-white font-medium rounded-md hover:bg-indigo-700 text-center"
                    >
                      Start Quiz
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Footer - Same as HomePage */}
      <footer className="bg-gray-800 text-white py-8">
        <div className="container mx-auto px-4">
          <div className="flex flex-col md:flex-row justify-between items-center">
            <div className="flex items-center mb-4 md:mb-0">
              <HelpCircle className="h-6 w-6 text-indigo-400" />
              <span className="ml-2 text-xl font-bold">QuizMaster</span>
            </div>
            <div className="flex space-x-4">
              <a href="#" className="hover:text-indigo-400">
                About
              </a>
              <a href="#" className="hover:text-indigo-400">
                Contact
              </a>
              <a href="#" className="hover:text-indigo-400">
                Privacy
              </a>
              <a href="#" className="hover:text-indigo-400">
                Terms
              </a>
            </div>
          </div>
          <div className="mt-4 text-center text-gray-400 text-sm">
            © 2025 QuizMaster. All rights reserved.
          </div>
        </div>
      </footer>
    </div>
  );
}
