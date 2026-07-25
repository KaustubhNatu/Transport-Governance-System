import { BrowserRouter, Routes, Route } from "react-router-dom";

import LoginPage from "../pages/auth/LoginPage";
import SuperAdminDashboard from "../pages/superAdmin/SuperAdminDashboard";
import SchoolDashboard from "../pages/school/SchoolDashboard";
import VendorDashboard from "../pages/vendor/VendorDashboard";
import DriverDashboard from "../pages/driver/DriverDashboard";
import ParentDashboard from "../pages/parent/ParentDashboard";
import NotFoundPage from "../pages/error/NotFoundPage";

function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>

                <Route path="/" element={<LoginPage />} />

                <Route
                    path="/super-admin/dashboard"
                    element={<SuperAdminDashboard />}
                />

                <Route
                    path="/school/dashboard"
                    element={<SchoolDashboard />}
                />

                <Route
                    path="/vendor/dashboard"
                    element={<VendorDashboard />}
                />

                <Route
                    path="/driver/dashboard"
                    element={<DriverDashboard />}
                />

                <Route
                    path="/parent/dashboard"
                    element={<ParentDashboard />}
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