import { Navigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

function RoleRoute({ allowedRoles, children }) {
    const { user, loading } = useAuth();

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-slate-900 text-slate-300 font-semibold text-xs">
                Verifying authorizations...
            </div>
        );
    }

    if (!user) {
        return <Navigate to="/" replace />;
    }

    const currentRole = user.role?.replace("ROLE_", "");

    if (!allowedRoles.includes(currentRole)) {
        return <Navigate to="/" replace />;
    }

    return children;
}

export default RoleRoute;