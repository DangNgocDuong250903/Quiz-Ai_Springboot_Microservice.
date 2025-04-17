import { useState, useEffect } from "react";
import {
  ArrowLeft,
  ArrowRight,
  Clock,
  Flag,
  AlertTriangle,
  Check,
  X,
} from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import * as QuizService from "~/services/QuizService";
import { useQuery } from "@tanstack/react-query";

export default function QuizPage() {
  const { examId } = useParams();
  const navigate = useNavigate();
  const fetchQuestions = async (examId) => {
    const data = { quizId: examId };
    const res = await QuizService.start(data);
    return res?.result;
  };

  const { data, isLoading } = useQuery({
    queryKey: ["questions", examId],
    queryFn: () => fetchQuestions(examId),
    enabled: !!examId,
  });

  // Using the data from API response if available, otherwise use placeholder data
  const quizData = data;

  // Default time limit if not provided by API
  const timeLimit = 15 * 60; // 15 minutes in seconds

  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [timeRemaining, setTimeRemaining] = useState(timeLimit);
  const [quizSubmitted, setQuizSubmitted] = useState(false);
  const [isConfirmationOpen, setIsConfirmationOpen] = useState(false);
  const [quizResults, setQuizResults] = useState(null);

  // Timer effect
  useEffect(() => {
    if (timeRemaining > 0 && !quizSubmitted) {
      const timer = setTimeout(() => {
        setTimeRemaining(timeRemaining - 1);
      }, 1000);

      return () => clearTimeout(timer);
    } else if (timeRemaining === 0 && !quizSubmitted) {
      handleSubmitQuiz();
    }
  }, [timeRemaining, quizSubmitted]);

  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds < 10 ? "0" : ""}${remainingSeconds}`;
  };

  const handleAnswerSelect = (questionId, optionId) => {
    setAnswers({
      ...answers,
      [questionId]: optionId,
    });
  };

  const handleNextQuestion = () => {
    if (currentQuestionIndex < quizData.questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    }
  };

  const handlePreviousQuestion = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(currentQuestionIndex - 1);
    }
  };

  const handleSubmitQuiz = async () => {
    // Format answers in the required structure for submission
    const formattedAnswers = Object.keys(answers).map((questionId) => ({
      questionId: questionId,
      answerId: answers[questionId],
    }));

    const submissionData = {
      submissionId: quizData.submissionId.toString(),
      answers: formattedAnswers,
    };

    // Log the submission data
    const res = await QuizService.submit({ data: submissionData });

    setQuizResults(res?.result);

    setQuizSubmitted(true);
  };

  // Handle loading state
  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-xl">Loading quiz...</div>
      </div>
    );
  }

  const currentQuestion = quizData.questions[currentQuestionIndex];
  const answeredQuestionsCount = Object.keys(answers).length;

  // Calculate if time is running low (less than 2 minutes)
  const isTimeLow = timeRemaining < 120;

  // If quiz is submitted, show results
  if (quizSubmitted && quizResults) {
    return (
      <div className="min-h-screen bg-gray-50">
        {/* Header simplified */}
        <header className="bg-white shadow">
          <div className="container mx-auto px-4 py-6">
            <div
              onClick={() => navigate(-1)}
              className="cursor-pointer flex items-center"
            >
              <Clock className="h-8 w-8 text-indigo-600" />
              <span className="ml-2 cursor-pointer text-2xl font-bold text-indigo-600">
                QuizMaster
              </span>
            </div>
          </div>
        </header>

        {/* Results Section */}
        <section className="py-12">
          <div className="container mx-auto px-4 max-w-4xl">
            <div className="bg-white rounded-lg shadow-lg overflow-hidden">
              <div className="bg-indigo-600 text-white p-6">
                <h1 className="text-3xl font-bold">
                  {quizResults.quizTitle} - Results
                </h1>
                <p className="mt-2">
                  You've completed the quiz! Here are your results.
                </p>
              </div>

              <div className="p-6">
                <div className="flex flex-col items-center justify-center py-8">
                  <div className="text-6xl font-bold mb-2">
                    {quizResults.scorePercent}%
                  </div>
                  <div className="text-xl mb-6">
                    You got{" "}
                    <span className="font-semibold">
                      {quizResults.correctAnswers}
                    </span>{" "}
                    out of{" "}
                    <span className="font-semibold">
                      {quizResults.totalQuestions}
                    </span>{" "}
                    questions correct
                  </div>

                  <div className="w-full max-w-md h-4 bg-gray-200 rounded-full mb-6">
                    <div
                      className={`h-full rounded-full ${
                        quizResults.scorePercent >= 70
                          ? "bg-green-500"
                          : quizResults.scorePercent >= 40
                          ? "bg-yellow-500"
                          : "bg-red-500"
                      }`}
                      style={{ width: `${quizResults.scorePercent}%` }}
                    ></div>
                  </div>

                  {/* Detailed Results */}
                  <div className="w-full max-w-2xl mt-8 mb-8">
                    <h2 className="text-2xl font-semibold mb-4">
                      Question Details
                    </h2>
                    <div className="space-y-4">
                      {quizResults.results.map((result, index) => (
                        <div
                          key={index}
                          className="border rounded-lg overflow-hidden"
                        >
                          <div
                            className={`p-4 flex items-start justify-between ${
                              result.correct ? "bg-green-50" : "bg-red-50"
                            }`}
                          >
                            <div className="flex-1">
                              <div className="font-medium">
                                Question {index + 1}: {result.questionContent}
                              </div>
                              <div className="mt-2">
                                <span className="text-gray-700">
                                  Your answer:{" "}
                                </span>
                                <span
                                  className={
                                    result.correct
                                      ? "text-green-600 font-medium"
                                      : "text-red-600 font-medium"
                                  }
                                >
                                  {result.selectedAnswer}
                                </span>
                              </div>
                            </div>
                            <div
                              className={`flex items-center justify-center h-8 w-8 rounded-full ${
                                result.correct
                                  ? "bg-green-100 text-green-600"
                                  : "bg-red-100 text-red-600"
                              }`}
                            >
                              {result.correct ? (
                                <Check size={16} />
                              ) : (
                                <X size={16} />
                              )}
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6 w-full max-w-lg">
                    <button
                      onClick={() => setQuizSubmitted(false)}
                      className="px-6 py-3 bg-indigo-600 text-white font-medium rounded-md hover:bg-indigo-700"
                    >
                      Review Questions
                    </button>
                    <button
                      onClick={() => navigate("/")}
                      className="px-6 py-3 bg-gray-200 text-gray-800 font-medium rounded-md hover:bg-gray-300 text-center"
                    >
                      Back to Home
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Confirmation Modal */}
      {isConfirmationOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h3 className="text-xl font-semibold mb-4">Submit Quiz?</h3>
            <p className="mb-6">
              You have answered {answeredQuestionsCount} out of{" "}
              {quizData.questions.length} questions. Are you sure you want to
              submit?
            </p>
            <div className="flex justify-end space-x-4">
              <button
                className="px-4 py-2 text-gray-600 hover:text-gray-800"
                onClick={() => setIsConfirmationOpen(false)}
              >
                Cancel
              </button>
              <button
                className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
                onClick={() => {
                  setIsConfirmationOpen(false);
                  handleSubmitQuiz();
                }}
              >
                Submit
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Header */}
      <header className="bg-white shadow">
        <div className="container mx-auto px-4 py-6">
          <div className="flex items-center justify-between">
            <div
              onClick={() => navigate(-1)}
              className="flex cursor-pointer items-center"
            >
              <Clock className="h-8 w-8 text-indigo-600" />
              <span className="ml-2 text-2xl font-bold text-indigo-600">
                QuizMaster
              </span>
            </div>

            <div
              className={`flex items-center ${
                isTimeLow ? "text-red-600 animate-pulse" : "text-gray-600"
              }`}
            >
              <Clock className="h-5 w-5 mr-2" />
              <span className="font-mono text-lg">
                {formatTime(timeRemaining)}
              </span>
            </div>
          </div>
        </div>
      </header>

      {/* Quiz Content */}
      <section className="py-8">
        <div className="container mx-auto px-4 max-w-4xl">
          {/* Quiz Header */}
          <div className="bg-white rounded-lg shadow-md mb-6 p-6">
            <div className="flex justify-between items-center">
              <div>
                <h1 className="text-2xl font-bold">{quizData.quizTitle}</h1>
                <p className="text-gray-600">
                  Submission ID: {quizData.submissionId}
                </p>
              </div>
              <div className="flex items-center">
                <span className="text-gray-600 mr-2">
                  Question {currentQuestionIndex + 1} of{" "}
                  {quizData.questions.length}
                </span>
                <span className="px-3 py-1 bg-indigo-100 text-indigo-800 rounded-full text-sm">
                  {answeredQuestionsCount} Answered
                </span>
              </div>
            </div>
          </div>

          {/* Question Card */}
          <div className="bg-white rounded-lg shadow-md mb-6">
            <div className="p-6">
              <h2 className="text-xl font-semibold mb-6">
                {currentQuestion.content}
              </h2>

              <div className="space-y-4">
                {currentQuestion.answers &&
                currentQuestion.answers.length > 0 ? (
                  currentQuestion.answers.map((option) => (
                    <div
                      key={option.id}
                      className={`p-4 border rounded-lg cursor-pointer transition-colors ${
                        answers[currentQuestion.id] === option.id
                          ? "border-indigo-600 bg-indigo-50"
                          : "border-gray-200 hover:border-indigo-300"
                      }`}
                      onClick={() =>
                        handleAnswerSelect(currentQuestion.id, option.id)
                      }
                    >
                      <div className="flex items-center">
                        <div
                          className={`w-6 h-6 flex items-center justify-center rounded-full mr-3 ${
                            answers[currentQuestion.id] === option.id
                              ? "bg-indigo-600 text-white"
                              : "border border-gray-300"
                          }`}
                        >
                          {answers[currentQuestion.id] === option.id && "✓"}
                        </div>
                        <span>{option.content}</span>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="p-4 border rounded-lg text-gray-500">
                    No answer options available for this question. Please
                    provide a free-form answer.
                  </div>
                )}
              </div>
            </div>

            {/* Navigation Buttons */}
            <div className="flex justify-between p-6 border-t">
              <button
                onClick={handlePreviousQuestion}
                disabled={currentQuestionIndex === 0}
                className={`flex items-center px-4 py-2 ${
                  currentQuestionIndex === 0
                    ? "text-gray-400 cursor-not-allowed"
                    : "text-indigo-600 hover:text-indigo-800"
                }`}
              >
                <ArrowLeft className="h-5 w-5 mr-1" />
                Previous
              </button>

              <div className="flex space-x-4">
                <button
                  onClick={() => setIsConfirmationOpen(true)}
                  className="px-6 py-2 bg-green-600 text-white font-medium rounded-md hover:bg-green-700 flex items-center"
                >
                  <Flag className="h-5 w-5 mr-2" />
                  Submit Quiz
                </button>

                {currentQuestionIndex < quizData.questions.length - 1 && (
                  <button
                    onClick={handleNextQuestion}
                    className="flex items-center px-6 py-2 bg-indigo-600 text-white font-medium rounded-md hover:bg-indigo-700"
                  >
                    Next
                    <ArrowRight className="h-5 w-5 ml-1" />
                  </button>
                )}
              </div>
            </div>
          </div>

          {/* Question Navigation */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-lg font-medium mb-4">Question Navigator</h3>
            <div className="flex flex-wrap gap-2">
              {quizData.questions.map((question, index) => (
                <button
                  key={index}
                  onClick={() => setCurrentQuestionIndex(index)}
                  className={`w-10 h-10 flex items-center justify-center rounded-full font-medium ${
                    currentQuestionIndex === index
                      ? "bg-indigo-600 text-white"
                      : answers[question.id]
                      ? "bg-green-100 text-green-800 border border-green-300"
                      : "bg-gray-100 text-gray-800 hover:bg-gray-200"
                  }`}
                >
                  {index + 1}
                </button>
              ))}
            </div>

            {isTimeLow && (
              <div className="mt-4 flex items-center text-red-600">
                <AlertTriangle className="h-5 w-5 mr-2" />
                <span>
                  Time is running out! You have less than 2 minutes remaining.
                </span>
              </div>
            )}
          </div>
        </div>
      </section>
    </div>
  );
}
