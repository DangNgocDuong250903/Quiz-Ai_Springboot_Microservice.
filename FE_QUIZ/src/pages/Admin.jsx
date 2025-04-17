import { useState } from "react";
import {
  LogOut,
  User,
  Book,
  FileText,
  HelpCircle,
  ChevronDown,
  Plus,
  Trash,
  Save,
  X,
  Home,
} from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import * as QuizService from "~/services/QuizService";
import * as AdminService from "~/services/AdminService";
import * as AccessService from "~/services/AccessService";
import { useQuery } from "@tanstack/react-query";
import { resetUser } from "~/redux/Slices/userSlice";
import { useNavigate } from "react-router-dom";
import { DatePicker, Spin } from "antd";
import dayjs from "dayjs";
import { useForm } from "react-hook-form";
import { QUIZ_SUBJECT } from "~/constants";

export default function Admin() {
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const user = useSelector((state) => state?.user);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    setValue,
    watch,
    getValues,
    formState: { errors },
  } = useForm({
    mode: "onChange",
  });

  const [adminInfo, setAdminInfo] = useState({
    name: user?.username,
    email: user?.email,
    role: user?.roles[0]?.name,
  });

  const [isEditingProfile, setIsEditingProfile] = useState(false);
  const [editedAdminInfo, setEditedAdminInfo] = useState({ ...adminInfo });

  const [activeSection, setActiveSection] = useState("subjects");

  const [subjects, setSubjects] = useState([]);
  const fetchAllSubjects = async () => {
    const res = await QuizService.getSubjects();
    setSubjects(res?.result);
  };
  const { isLoading, refetch } = useQuery({
    queryKey: ["subjects"],
    queryFn: fetchAllSubjects,
  });

  const [newSubject, setNewSubject] = useState({ name: "" });
  const [isAddingSubject, setIsAddingSubject] = useState(false);

  const [exams, setExams] = useState([]);

  const fetchAllExams = async () => {
    const res = await QuizService.getExams();
    setExams(res?.result);
  };
  const { isLoading: isLoadingExams, refetch: refetchExams } = useQuery({
    queryKey: ["exams"],
    queryFn: fetchAllExams,
  });

  const [isAddingExam, setIsAddingExam] = useState(false);

  const [questions, setQuestions] = useState([]);
  const [isAddingQuestion, setIsAddingQuestion] = useState(false);

  const fetchQuestions = async () => {
    const res = await AdminService.getQuestions();
    setQuestions(res?.result);
  };

  const { refetch: refetchQuestions } = useQuery({
    queryKey: ["questions"],
    queryFn: fetchQuestions,
  });

  const [answers, setAnswers] = useState([]);

  const [selectedQuestionId, setSelectedQuestionId] = useState(1);
  const [newAnswer, setNewAnswer] = useState({
    questionId: 1,
    text: "",
    isCorrect: false,
  });
  const [isAddingAnswer, setIsAddingAnswer] = useState(false);

  const fetchAnswers = async () => {
    const res = await AdminService.getAnswers();
    setAnswers(res?.result);
  };

  const { refetch: refetchAnswers } = useQuery({
    queryKey: ["answers"],
    queryFn: fetchAnswers,
  });

  const handleSaveProfile = () => {
    setAdminInfo({ ...editedAdminInfo });
    setIsEditingProfile(false);
  };

  const onSubmitCreateSubject = async (data) => {
    const res = await AdminService.createSubject(data);
    if (res?.code === 1000) refetch();
    setNewSubject({ name: "" });
    setIsAddingSubject(false);
  };

  const onSubmit = async (data) => {
    const res = await AdminService.createExams(data);
    if (res?.code === 1000) {
      refetchExams();
      setIsAddingExam(false);
    }
  };

  // Handle question creation
  const onSubmitAddQuestion = async (data) => {
    const res = await AdminService.createQuestions(data);
    if (res) {
      refetchQuestions();
      setIsAddingQuestion(false);
    }
  };

  const handleDeleteSubject = async (id) => {
    const res = await AdminService.deleteSubject(id);
    if (res) {
      refetch();
    }
  };

  //log out
  const handleLogout = async () => {
    const res = await AccessService.logout(user?.token);
    if (res?.code === 1000) {
      dispatch(resetUser());
      navigate("/login");
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-indigo-600 text-white p-4 shadow-md">
        <div className="container mx-auto flex justify-between items-center">
          <h1 className="text-2xl font-bold">Quiz Master Admin</h1>

          {/* Profile Dropdown */}
          <div className="relative">
            <button
              className="flex items-center space-x-2 bg-indigo-700 px-4 py-2 rounded-lg"
              onClick={() => setIsProfileOpen(!isProfileOpen)}
            >
              <User size={20} />
              <span>{user.username}</span>
              <ChevronDown size={16} />
            </button>

            {isProfileOpen && (
              <div className="absolute right-0 mt-2 w-64 bg-white rounded-lg shadow-lg z-10">
                <div className="p-4 border-b border-gray-200">
                  <p className="font-semibold text-gray-700">{user.username}</p>
                  <p className="text-sm text-gray-600">
                    {user.email || "No email"}
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    Role {user.roles[0]?.name}
                  </p>
                </div>
                <div>
                  <button
                    onClick={() => navigate("/")}
                    className="w-full cursor-pointer border-b border-gray-200 text-left p-3 hover:bg-gray-100 flex items-center space-x-2 text-gray-600"
                  >
                    <Home size={16} />
                    <span>Home Page</span>
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
        </div>
      </header>

      {/* Main Content */}
      <div className="flex flex-1">
        {/* Sidebar */}
        <aside className="w-64 bg-white shadow-md">
          <nav className="p-4">
            <ul>
              <li>
                <button
                  className={`flex items-center space-x-3 w-full p-3 rounded-lg ${
                    activeSection === "subjects"
                      ? "bg-indigo-100 text-indigo-700"
                      : "text-gray-600 hover:bg-gray-100"
                  }`}
                  onClick={() => setActiveSection("subjects")}
                >
                  <Book size={20} />
                  <span>Subjects</span>
                </button>
              </li>
              <li>
                <button
                  className={`flex items-center space-x-3 w-full p-3 rounded-lg ${
                    activeSection === "exams"
                      ? "bg-indigo-100 text-indigo-700"
                      : "text-gray-600 hover:bg-gray-100"
                  }`}
                  onClick={() => setActiveSection("exams")}
                >
                  <FileText size={20} />
                  <span>Exams</span>
                </button>
              </li>
              <li>
                <button
                  className={`flex items-center space-x-3 w-full p-3 rounded-lg ${
                    activeSection === "questions"
                      ? "bg-indigo-100 text-indigo-700"
                      : "text-gray-600 hover:bg-gray-100"
                  }`}
                  onClick={() => setActiveSection("questions")}
                >
                  <HelpCircle size={20} />
                  <span>Questions & Answers</span>
                </button>
              </li>
              <li>
                <button
                  className={`flex items-center space-x-3 w-full p-3 rounded-lg ${
                    activeSection === "profile"
                      ? "bg-indigo-100 text-indigo-700"
                      : "text-gray-600 hover:bg-gray-100"
                  }`}
                  onClick={() => setActiveSection("profile")}
                >
                  <User size={20} />
                  <span>Profile</span>
                </button>
              </li>
            </ul>
          </nav>
        </aside>

        {/* Content Area */}
        <main className="flex-1 p-6">
          {/* Profile Edit Section */}
          {activeSection === "profile" && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-xl font-semibold mb-4">Admin Profile</h2>

              {isEditingProfile ? (
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Full Name
                    </label>
                    <input
                      type="text"
                      className="w-full p-2 border border-gray-300 rounded-md"
                      value={editedAdminInfo.name}
                      onChange={(e) =>
                        setEditedAdminInfo({
                          ...editedAdminInfo,
                          name: e.target.value,
                        })
                      }
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Role
                    </label>
                    <input
                      type="text"
                      className="w-full p-2 border border-gray-300 rounded-md"
                      value={editedAdminInfo.role}
                      onChange={(e) =>
                        setEditedAdminInfo({
                          ...editedAdminInfo,
                          role: e.target.value,
                        })
                      }
                      disabled
                    />
                    <p className="text-xs text-gray-500 mt-1">
                      Role cannot be changed
                    </p>
                  </div>
                  <div className="flex space-x-3 pt-3">
                    <button
                      className="bg-indigo-600 text-white px-4 py-2 rounded-md flex items-center space-x-2"
                      onClick={handleSaveProfile}
                    >
                      <Save size={16} />
                      <span>Save Changes</span>
                    </button>
                    <button
                      className="border border-gray-300 text-gray-700 px-4 py-2 rounded-md flex items-center space-x-2"
                      onClick={() => {
                        setEditedAdminInfo({ ...adminInfo });
                        setIsEditingProfile(false);
                      }}
                    >
                      <X size={16} />
                      <span>Cancel</span>
                    </button>
                  </div>
                </div>
              ) : (
                <div className="space-y-4">
                  <div className="flex justify-between items-center">
                    <div>
                      <p className="text-sm text-gray-500">Full Name</p>
                      <p className="font-medium">{adminInfo.name}</p>
                    </div>
                    {/* <button
                      className="text-indigo-600 hover:text-indigo-800"
                      onClick={() => setIsEditingProfile(true)}
                    >
                      <Edit size={18} />
                    </button> */}
                  </div>

                  <div>
                    <p className="text-sm text-gray-500">Role</p>
                    <p className="font-medium">{adminInfo.role}</p>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Subjects Section */}
          {activeSection === "subjects" && (
            <div className="bg-white rounded-lg shadow">
              <div className="p-4 border-b border-gray-200 flex justify-between items-center">
                <h2 className="text-xl font-semibold">Subjects</h2>
                <button
                  className="bg-indigo-600 text-white px-3 py-2 rounded-md flex items-center space-x-1"
                  onClick={() => setIsAddingSubject(true)}
                >
                  <Plus size={16} />
                  <span>Add Subject</span>
                </button>
              </div>

              {isAddingSubject && (
                <form
                  onSubmit={handleSubmit(onSubmitCreateSubject)}
                  className="p-4 border-b border-gray-200 bg-gray-50"
                >
                  <h3 className="font-medium mb-3">Add New Subject</h3>
                  <div className="space-y-3">
                    <div>
                      <label className="block text-sm text-gray-700 mb-1">
                        Subject Name
                      </label>
                      <select
                        className="w-full p-2 border border-gray-300 rounded-md"
                        {...register("name")}
                      >
                        {QUIZ_SUBJECT.map((subject, i) => (
                          <option key={i} value={subject}>
                            {subject}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div className="flex space-x-3 pt-2">
                      <button
                        type="submit"
                        className="bg-indigo-600 text-white px-4 py-2 rounded-md"
                      >
                        Add Subject
                      </button>
                      <button
                        className="border border-gray-300 text-gray-700 px-4 py-2 rounded-md"
                        onClick={() => setIsAddingSubject(false)}
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                </form>
              )}

              <div className="p-4">
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead>
                      {subjects?.length > 0 ? (
                        <tr>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            ID
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Subject Name
                          </th>
                          <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Actions
                          </th>
                        </tr>
                      ) : (
                        <></>
                      )}
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                      {isLoading ? (
                        <tr>
                          <td
                            colSpan={3}
                            className="px-4 py-3 text-center text-sm text-gray-500"
                          >
                            <Spin />
                          </td>
                        </tr>
                      ) : subjects?.length > 0 ? (
                        subjects.map((subject) => (
                          <tr key={subject.id}>
                            <td className="px-4 py-3 whitespace-nowrap text-sm">
                              {subject.id}
                            </td>
                            <td className="px-4 py-3 whitespace-nowrap text-sm font-medium">
                              {subject.name}
                            </td>
                            <td className="px-4 py-3 whitespace-nowrap text-sm text-right">
                              <button
                                className="text-red-600 cursor-pointer hover:text-red-800"
                                onClick={() => handleDeleteSubject(subject.id)}
                              >
                                <Trash size={16} />
                              </button>
                            </td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td
                            colSpan={3}
                            className="px-4 py-3 text-center text-sm text-gray-500"
                          >
                            Chưa có môn học nào
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* Exams Section */}
          {activeSection === "exams" && (
            <div className="bg-white rounded-lg shadow">
              <div className="p-4 border-b border-gray-200 flex justify-between items-center">
                <h2 className="text-xl font-semibold">Exams</h2>
                <button
                  className="bg-indigo-600 text-white px-3 py-2 rounded-md flex items-center space-x-1"
                  onClick={() => setIsAddingExam(true)}
                >
                  <Plus size={16} />
                  <span>Add Exam</span>
                </button>
              </div>

              {isAddingExam && (
                <form
                  onSubmit={handleSubmit(onSubmit)}
                  className="p-4 border-b border-gray-200 bg-gray-50"
                >
                  <h3 className="font-medium mb-3">Add New Exam</h3>
                  <div className="space-y-3">
                    {/* Exam Title */}
                    <div>
                      <label className="block text-sm text-gray-700 mb-1">
                        Exam Title
                      </label>
                      <input
                        type="text"
                        required
                        className="w-full p-2 border border-gray-300 rounded-md"
                        {...register("title")}
                        placeholder="Enter exam title"
                      />
                    </div>

                    {/* Subject */}
                    <div>
                      <label className="block text-sm text-gray-700 mb-1">
                        Subject
                      </label>
                      <select
                        className="w-full p-2 border border-gray-300 rounded-md"
                        {...register("subjectId", { valueAsNumber: true })}
                      >
                        {subjects.map((subject) => (
                          <option key={subject.id} value={subject.id}>
                            {subject?.id}: {subject.name}
                          </option>
                        ))}
                      </select>
                    </div>

                    {/* Duration + Expiration */}
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm text-gray-700 mb-1">
                          Duration (minutes)
                        </label>
                        <input
                          type="number"
                          required
                          className="w-full p-2 border border-gray-300 rounded-md"
                          {...register("durationMinutes", {
                            valueAsNumber: true,
                          })}
                          min={1}
                        />
                      </div>
                    </div>

                    {/* Has No Time Limit */}
                    <div>
                      <label className="block text-sm text-gray-700 mb-1">
                        Has No Time Limit
                      </label>
                      <select
                        className="w-full p-2 border border-gray-300 rounded-md"
                        {...register("hasNoTimeLimit", {
                          setValueAs: (v) => v === "true",
                        })}
                      >
                        <option value="false">No</option>
                        <option value="true">Yes</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-sm text-gray-700 mb-1">
                        Expiration Time
                      </label>
                      <DatePicker
                        showTime
                        className="w-full"
                        placeholder="Select expiration time"
                        onChange={(value) => {
                          setValue(
                            "expirationTime",
                            dayjs(value).format("YYYY-MM-DDTHH:mm:ss")
                          );
                        }}
                      />
                    </div>

                    {/* Buttons */}
                    <div className="flex space-x-3 pt-2">
                      <button
                        type="submit"
                        className="bg-indigo-600 text-white px-4 py-2 rounded-md"
                      >
                        Add Exam
                      </button>
                      <button
                        type="button"
                        onClick={() => setIsAddingExam(false)}
                        className="border border-gray-300 text-gray-700 px-4 py-2 rounded-md"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                </form>
              )}

              <div className="p-4">
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead>
                      <tr>
                        <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          ID
                        </th>
                        <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Exam Title
                        </th>
                        <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Subject
                        </th>
                        <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Duration
                        </th>
                        <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Time limit
                        </th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                      {isLoadingExams ? (
                        <tr>
                          <td
                            colSpan={6}
                            className="px-4 py-3 text-center text-sm text-gray-500"
                          >
                            <Spin />
                          </td>
                        </tr>
                      ) : exams?.length > 0 ? (
                        exams.map((exam) => (
                          <tr key={exam.id}>
                            <td className="px-4 py-3 whitespace-nowrap text-sm">
                              {exam?.id}
                            </td>
                            <td className="px-4 py-3 whitespace-nowrap text-sm font-medium">
                              {exam?.title}
                            </td>
                            <td className="px-4 py-3 whitespace-nowrap text-sm">
                              {exam?.subjectName}
                            </td>
                            <td className="px-4 py-3 whitespace-nowrap text-sm">
                              {exam.durationMinutes} mins
                            </td>
                            <td className="px-4 py-3 whitespace-nowrap text-sm">
                              {`${exam?.hasNoTimeLimit}`}
                            </td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td
                            colSpan={6}
                            className="px-4 py-3 text-center text-sm text-gray-500"
                          >
                            Chưa có đề thi nào
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* Questions Section */}
          {activeSection === "questions" && (
            <div className="space-y-6">
              {/* Questions List */}
              <div className="bg-white rounded-lg shadow">
                <div className="p-4 border-b border-gray-200 flex justify-between items-center">
                  <h2 className="text-xl font-semibold">Questions</h2>
                  <button
                    className="bg-indigo-600 text-white px-3 py-2 rounded-md flex items-center space-x-1"
                    onClick={() => setIsAddingQuestion(true)}
                  >
                    <Plus size={16} />
                    <span>Add Question</span>
                  </button>
                </div>

                {isAddingQuestion && (
                  <form
                    onSubmit={handleSubmit(onSubmitAddQuestion)}
                    className="p-4 border-b border-gray-200 bg-gray-50"
                  >
                    <h3 className="font-medium mb-3">Add New Question</h3>
                    <div className="space-y-3">
                      <div>
                        <label className="block text-sm text-gray-700 mb-1">
                          Exam ID
                        </label>
                        <select
                          className="w-full p-2 border border-gray-300 rounded-md"
                          {...register("quizId")}
                        >
                          {exams.map((subject) => (
                            <option key={subject.id} value={subject.id}>
                              ExamID: {subject?.id} - {subject.title}
                            </option>
                          ))}
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm text-gray-700 mb-1">
                          Question Text
                        </label>
                        <textarea
                          className="w-full p-2 border border-gray-300 rounded-md"
                          name="content"
                          required
                          {...register("content")}
                          placeholder="Enter question text"
                          rows={3}
                        />
                      </div>
                      <div className="flex space-x-3 pt-2">
                        <button
                          type="submit"
                          className="bg-indigo-600 text-white px-4 py-2 rounded-md"
                        >
                          Add Question
                        </button>
                        <button
                          className="border border-gray-300 text-gray-700 px-4 py-2 rounded-md"
                          onClick={() => setIsAddingQuestion(false)}
                        >
                          Cancel
                        </button>
                      </div>
                    </div>
                  </form>
                )}

                <div className="p-4">
                  <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                      <thead>
                        <tr>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            ID
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Question
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Answers
                          </th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-gray-200">
                        {questions.map((question) => (
                          <tr
                            key={question.id}
                            className={
                              selectedQuestionId === question.id
                                ? "bg-indigo-50"
                                : ""
                            }
                            onClick={() => setSelectedQuestionId(question.id)}
                          >
                            <td className="px-4 py-3 whitespace-nowrap text-sm">
                              {question.id}
                            </td>
                            <td className="px-4 py-3 text-sm">
                              {question.content}
                            </td>
                            <td className="px-4 py-3 whitespace-nowrap text-sm">
                              {question?.answers?.length}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

              {/* Answers for Selected Question */}
              <div className="bg-white rounded-lg shadow">
                <div className="p-4 border-b border-gray-200 flex justify-between items-center">
                  <h2 className="text-xl font-semibold">Answers</h2>
                  <button
                    className="bg-indigo-600 text-white px-3 py-2 rounded-md flex items-center space-x-1"
                    onClick={() => {
                      setNewAnswer({
                        questionId: selectedQuestionId,
                        text: "",
                        isCorrect: false,
                      });
                      setIsAddingAnswer(true);
                    }}
                  >
                    <Plus size={16} />
                    <span>Add Answer</span>
                  </button>
                </div>

                {isAddingAnswer && (
                  <form
                    onSubmit={handleSubmit(async (data) => {
                      const answerData = {
                        questionId: parseInt(data.questionId),
                        content: data.content,
                        isCorrect: data.isCorrect,
                      };
                      const res = await AdminService.createAnswer(answerData);
                      if (res) {
                        refetchAnswers();
                        setIsAddingAnswer(false);
                        setValue("content", "");
                        setValue("isCorrect", false);
                      }
                    })}
                    className="p-4 border-b border-gray-200 bg-gray-50"
                  >
                    <h3 className="font-medium mb-3">Add New Answer</h3>
                    <div className="space-y-3">
                      <div>
                        <label className="block text-sm text-gray-700 mb-1">
                          Question ID
                        </label>
                        <select
                          className="w-full p-2 border border-gray-300 rounded-md"
                          {...register("questionId", { valueAsNumber: true })}
                          defaultValue={selectedQuestionId}
                        >
                          {questions.map((question) => (
                            <option key={question.id} value={question.id}>
                              QuestionID {question?.id}: {question?.content}
                            </option>
                          ))}
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm text-gray-700 mb-1">
                          Answer Text
                        </label>
                        <input
                          type="text"
                          required
                          className="w-full p-2 border border-gray-300 rounded-md"
                          placeholder="Enter answer text"
                          {...register("content", { required: true })}
                        />
                      </div>
                      <div className="flex items-center">
                        <input
                          type="checkbox"
                          id="isCorrect"
                          className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                          {...register("isCorrect")}
                        />
                        <label
                          htmlFor="isCorrect"
                          className="ml-2 block text-sm text-gray-700"
                        >
                          Is this the correct answer?
                        </label>
                      </div>
                      <div className="flex space-x-3 pt-2">
                        <button
                          type="submit"
                          className="bg-indigo-600 text-white px-4 py-2 rounded-md"
                        >
                          Add Answer
                        </button>
                        <button
                          type="button"
                          className="border border-gray-300 text-gray-700 px-4 py-2 rounded-md"
                          onClick={() => setIsAddingAnswer(false)}
                        >
                          Cancel
                        </button>
                      </div>
                    </div>
                  </form>
                )}

                <div className="p-4">
                  <div className="overflow-x-auto">
                    {answers.length > 0 ? (
                      <table className="min-w-full divide-y divide-gray-200">
                        <thead>
                          <tr>
                            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                              ID
                            </th>
                            <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                              Answer Text
                            </th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                          {answers.map((answer) => (
                            <tr key={answer.id}>
                              <td className="px-4 py-3 whitespace-nowrap text-sm">
                                {answer?.id}
                              </td>
                              <td className="px-4 py-3 text-sm">
                                {answer?.content}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    ) : (
                      <div className="text-center py-6 text-gray-500">
                        <p>No answers added for this question yet.</p>
                        <p className="text-sm mt-1">
                          Click the "Add Answer" button to create answers.
                        </p>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          )}
        </main>
      </div>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 p-4">
        <div className="container mx-auto">
          <p className="text-center text-gray-500 text-sm">
            © 2025 Quiz Master Admin. All rights reserved.
          </p>
        </div>
      </footer>
    </div>
  );
}
