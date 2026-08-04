import { Navigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

function RoleRoute({ allowedRoles, children }) {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/" replace />;
    }

    if (!allowedRoles.includes(user.role)) {
        return <Navigate to="/" replace />;
    }

    return children;
}

export default RoleRoute;