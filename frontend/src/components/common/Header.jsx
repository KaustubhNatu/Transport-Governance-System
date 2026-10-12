import { useAuth } from "../../contexts/AuthContext";
import { LogOut, Bus, User, Sparkles } from "lucide-react";
import { useNavigate } from "react-router-dom";

export default function Header() {
    const { user, logout, isDemoMode, toggleDemoMode, loginAsDemoRole } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/");
    };

    const handleRoleSwitch = (e) => {
        const newRole = e.target.value;
        if (!newRole) return;
        loginAsDemoRole(newRole);

        switch (newRole) {
            case "SUPER_ADMIN":
                navigate("/super-admin/dashboard");
                break;
            case "SCHOOL_ADMIN":
                navigate("/school/dashboard");
                break;
            case "VENDOR_ADMIN":
                navigate("/vendor/dashboard");
                break;
            case "DRIVER":
                navigate("/driver/dashboard");
                break;
            case "PARENT":
                navigate("/parent/dashboard");
                break;
            default:
                break;
        }
    };

    return (
        <header className="sticky top-0 z-30 flex h-16 w-full items-center justify-between border-b border-slate-200/80 bg-white/95 px-6 backdrop-blur-md">
            {/* Left: Brand & Mobile Title */}
            <div className="flex items-center gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-600 text-white shadow-md shadow-blue-500/20">
                    <Bus className="h-5 w-5" />
                </div>
                <div>
                    <h1 className="text-base font-bold text-slate-900 leading-tight">
                        Transport Governance System
                    </h1>
                    <span className="text-[11px] font-medium text-slate-400">
                        Enterprise Multi-Tenant Fleet Governance
                    </span>
                </div>
            </div>

            {/* Right: Role Switcher, Demo Indicator, User Profile & Logout */}
            <div className="flex items-center gap-3 sm:gap-4">
                {/* Demo Mode Toggle */}
                <button
                    onClick={() => toggleDemoMode()}
                    className={`hidden sm:inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold border transition ${
                        isDemoMode
                            ? "bg-amber-50 text-amber-700 border-amber-200 hover:bg-amber-100"
                            : "bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100"
                    }`}
                    title="Toggle between Live API and Seeded Demo Data"
                >
                    <Sparkles className="w-3.5 h-3.5" />
                    {isDemoMode ? "Demo Mode: Active" : "Live Backend: Connected"}
                </button>

                {/* Quick Role Switcher for Portfolio Reviewers */}
                <div className="hidden md:flex items-center gap-1.5 bg-slate-50 border border-slate-200/80 px-2.5 py-1 rounded-xl text-xs">
                    <span className="text-slate-400 font-medium">Switch View:</span>
                    <select
                        value={user?.role || "SUPER_ADMIN"}
                        onChange={handleRoleSwitch}
                        className="bg-transparent font-semibold text-slate-700 outline-none cursor-pointer text-xs"
                    >
                        <option value="SUPER_ADMIN">Super Admin</option>
                        <option value="SCHOOL_ADMIN">School Admin</option>
                        <option value="VENDOR_ADMIN">Vendor Admin</option>
                        <option value="DRIVER">Driver</option>
                        <option value="PARENT">Parent</option>
                    </select>
                </div>

                {/* User Info Pill */}
                <div className="flex items-center gap-2.5 pl-2 border-l border-slate-200">
                    <div className="w-8 h-8 rounded-full bg-slate-100 border border-slate-200 flex items-center justify-center text-slate-700 font-bold text-xs">
                        {user?.firstName ? user.firstName[0] : <User className="w-4 h-4" />}
                    </div>
                    <div className="hidden lg:block text-left">
                        <p className="text-xs font-bold text-slate-900 leading-none">
                            {user?.firstName ? `${user.firstName} ${user.lastName || ""}` : user?.email}
                        </p>
                        <span className="text-[10px] font-semibold text-blue-600 uppercase tracking-wider">
                            {user?.role?.replace("_", " ")}
                        </span>
                    </div>

                    {/* Logout Button */}
                    <button
                        onClick={handleLogout}
                        className="p-2 rounded-xl text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition ml-1"
                        title="Sign Out"
                    >
                        <LogOut className="w-4 h-4" />
                    </button>
                </div>
            </div>
        </header>
    );
}
