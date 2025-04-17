import {
  Brain,
  User,
  ChevronDown,
  LogOut,
  Magnet,
  SettingsIcon,
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import * as AccessService from "~/services/AccessService";
import * as QuizService from "~/services/QuizService";
import { resetUser } from "~/redux/Slices/userSlice";
import { useQuery } from "@tanstack/react-query";
import { useRef, useState } from "react";
import { useGetCategoryIcon } from "~/hooks/useGetCategoryIcon";

export default function HomePage() {
  const navigate = useNavigate();
  const user = useSelector((state) => state?.user);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const dispatch = useDispatch();
  const categoryRef = useRef(null);

  const handleScrollToCategory = () => {
    categoryRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const handleLogout = async () => {
    const res = await AccessService.logout(user?.token);
    if (res?.code === 1000) {
      dispatch(resetUser());
      navigate("/login");
    }
  };

  const fetchSubject = async () => {
    const res = await QuizService.getSubjects();
    return res?.result;
  };

  const { data, isLoading } = useQuery({
    queryKey: ["subjects"],
    queryFn: fetchSubject,
  });

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
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
                        onClick={() => navigate("/admin")}
                        className="w-full cursor-pointer border-b border-gray-200 text-left p-3 hover:bg-gray-100 flex items-center space-x-2 text-gray-600"
                      >
                        <SettingsIcon size={16} />
                        <span>Admin Page</span>
                      </button>
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

      {/* Hero Section */}
      <section className="bg-indigo-600 text-white py-16">
        <div className="container mx-auto px-4 text-center">
          <h1 className="text-4xl md:text-5xl font-bold mb-4">
            Test Your Knowledge
          </h1>
          <p className="text-xl md:text-2xl mb-8">
            Choose from various quiz categories and challenge yourself!
          </p>
          <button
            onClick={handleScrollToCategory}
            className="px-8 py-3 cursor-pointer bg-white text-indigo-600 font-medium rounded-md hover:bg-gray-100"
          >
            Get Started
          </button>
        </div>
      </section>

      {/* Quiz Categories */}
      <section ref={categoryRef} className="py-16">
        <div className="container mx-auto px-4">
          <h2 className="text-3xl font-bold text-center mb-12">
            Quiz Categories
          </h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {isLoading ? (
              <div className="text-center col-span-full">
                Đang tải danh mục...
              </div>
            ) : (
              data?.map((category, index) => (
                <span
                  onClick={() => navigate(`/exams/${category?.id}`)}
                  key={index}
                  className="bg-white cursor-pointer rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow flex flex-col items-center text-center"
                >
                  {useGetCategoryIcon(category?.name)}
                  <h3 className="text-xl font-semibold mt-4 mb-2">
                    {category.name}
                  </h3>
                </span>
              ))
            )}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 bg-gray-100">
        <div className="container mx-auto px-4">
          <h2 className="text-3xl font-bold text-center mb-12">
            Why Choose QuizMaster?
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-white p-6 rounded-lg shadow">
              <h3 className="text-xl font-semibold mb-3">Diverse Categories</h3>
              <p className="text-gray-600">
                Access quizzes from a wide range of topics to expand your
                knowledge.
              </p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow">
              <h3 className="text-xl font-semibold mb-3">Flexible Timing</h3>
              <p className="text-gray-600">
                Choose between timed quizzes or take them at your own pace.
              </p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow">
              <h3 className="text-xl font-semibold mb-3">Track Progress</h3>
              <p className="text-gray-600">
                Monitor your performance and see how you improve over time.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-800 text-white py-8">
        <div className="container mx-auto px-4">
          <div className="flex flex-col md:flex-row justify-between items-center">
            <div className="flex items-center mb-4 md:mb-0">
              <Brain className="h-6 w-6 text-indigo-400" />
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
