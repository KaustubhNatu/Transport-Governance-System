import { BrowserRouter, Routes, Route } from "react-router-dom";

import LoginPage from "../pages/auth/LoginPage";
import SuperAdminDashboard from "../pages/superAdmin/SuperAdminDashboard";
import SchoolDashboard from "../pages/school/SchoolDashboard";
import VendorDashboard from "../pages/vendor/VendorDashboard";
import DriverDashboard from "../pages/driver/DriverDashboard";
import ParentDashboard from "../pages/parent/ParentDashboard";
import NotFoundPage from "../pages/error/NotFoundPage";
import ProtectedRoute from "./ProtectedRoute.jsx";

function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>

                <Route path="/" element={<LoginPage />} />

                <Route
                    path="/super-admin/dashboard"
                    element={
                        <ProtectedRoute>
                            <SuperAdminDashboard />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/school/dashboard"
                    element={
                        <ProtectedRoute>
                            <SchoolDashboard />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/vendor/dashboard"
                    element={
                        <ProtectedRoute>
                            <VendorDashboard />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/driver/dashboard"
                    element={
                        <ProtectedRoute>
                            <DriverDashboard />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/parent/dashboard"
                    element={
                        <ProtectedRoute>
                            <ParentDashboard />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="*"
                    element={<NotFoundPage />}
                />

            </Routes>
        </BrowserRouter>
    );
}

export default AppRoutes;