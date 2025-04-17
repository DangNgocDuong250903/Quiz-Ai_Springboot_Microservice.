import { Navigate, useLocation } from "react-router-dom";

const ProtectedRoute = ({ children, isLoggedIn }) => {
  let location = useLocation();

  if (!isLoggedIn) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  return children;
};

export default ProtectedRoute;
