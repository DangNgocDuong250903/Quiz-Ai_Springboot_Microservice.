import { useDispatch, useSelector } from "react-redux";
import { Routes, Route } from "react-router-dom";
import { route } from "./routes";
import { useEffect } from "react";
import * as AccessService from "~/services/AccessService";
import { updateUser } from "./redux/Slices/userSlice";
import ProtectedRoute from "~/components/ProtectedRoute";
import { jwtDecode } from "jwt-decode";
import LoginPage from "~/pages/Login";
import RegisterPage from "~/pages/Register";

function App() {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.user);
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (token && token !== "undefined") {
      const decoded = jwtDecode(token);
      if (decoded && decoded?.userId) {
        handleGetDetailUser({ id: decoded?.userId, token });
      }
    }
  }, [token]);

  const handleGetDetailUser = async ({ id, token }) => {
    const res = await AccessService.getDetailUserByUserId({ id });
    dispatch(updateUser({ ...res?.result, token }));
  };

  const isLoggedIn = !!localStorage.getItem("token");

  return (
    <div>
      <div className="w-full min-h-[100vh] antialiased">
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          {route.map((route, i) => {
            const Page = route.element;
            const isCheckAuth =
              !route.isPrivate || user.roles[0]?.name.includes("ADMIN");
            return (
              <Route
                key={i}
                path={isCheckAuth ? route.path : ""}
                element={
                  <ProtectedRoute isLoggedIn={isLoggedIn}>
                    <Page />
                  </ProtectedRoute>
                }
              />
            );
          })}
        </Routes>
      </div>
    </div>
  );
}

export default App;
