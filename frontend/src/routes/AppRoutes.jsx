import { BrowserRouter, Routes, Route } from "react-router-dom";

import LoginPage from "../pages/auth/LoginPage";
import SuperAdminDashboard from "../pages/superAdmin/SuperAdminDashboard";
import SchoolDashboard from "../pages/school/SchoolDashboard";
import VendorDashboard from "../pages/vendor/VendorDashboard";
import DriverDashboard from "../pages/driver/DriverDashboard";
import ParentDashboard from "../pages/parent/ParentDashboard";
import NotFoundPage from "../pages/error/NotFoundPage";
import ProtectedRoute from "./ProtectedRoute.jsx";
import RoleRoute from "./RoleRoute.jsx";

function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>

                <Route path="/" element={<LoginPage />} />

                <Route
                    path="/super-admin/dashboard"
                    element={
                        <ProtectedRoute>
                            <RoleRoute allowedRoles={["SUPER_ADMIN"]}>
                                <SuperAdminDashboard />
                            </RoleRoute>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/school/dashboard"
                    element={
                        <ProtectedRoute>
                            <RoleRoute allowedRoles={["SCHOOL_ADMIN"]}>
                                <SchoolDashboard />
                            </RoleRoute>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/vendor/dashboard"
                    element={
                        <ProtectedRoute>
                            <RoleRoute allowedRoles={["VENDOR_ADMIN"]}>
                                <VendorDashboard />
                            </RoleRoute>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/driver/dashboard"
                    element={
                        <ProtectedRoute>
                            <RoleRoute allowedRoles={["DRIVER"]}>
                                <DriverDashboard />
                            </RoleRoute>
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/parent/dashboard"
                    element={
                        <ProtectedRoute>
                            <RoleRoute allowedRoles={["PARENT"]}>
                                <ParentDashboard />
                            </RoleRoute>
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