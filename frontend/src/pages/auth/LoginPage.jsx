import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { Bus, Lock, Mail, Sparkles, ArrowRight, CheckCircle2 } from "lucide-react";
import { DEMO_USERS } from "../../utils/mockData";

export default function LoginPage() {
    const navigate = useNavigate();
    const { login, loginAsDemoRole } = useAuth();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const redirectToDashboard = (role) => {
        switch (role) {
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
                navigate("/");
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!email.trim() || !password.trim()) {
            setError("Email and password are required.");
            return;
        }

        try {
            setLoading(true);
            const loggedInUser = await login(email, password);
            redirectToDashboard(loggedInUser.role);
        } catch {
            setError("Authentication failed. Please verify credentials or select a Demo Role below.");
        } finally {
            setLoading(false);
        }
    };

    const handleQuickRoleLogin = (demoUser) => {
        setLoading(true);
        const loggedInUser = loginAsDemoRole(demoUser.role);
        setLoading(false);
        redirectToDashboard(loggedInUser.role);
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-indigo-950 flex flex-col justify-center py-12 sm:px-6 lg:px-8 relative overflow-hidden">
            {/* Background Glows */}
            <div className="absolute top-0 -left-40 w-96 h-96 bg-blue-500/10 rounded-full blur-3xl pointer-events-none" />
            <div className="absolute bottom-0 -right-40 w-96 h-96 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none" />

            {/* Header info */}
            <div className="sm:mx-auto sm:w-full sm:max-w-md text-center z-10">
                <div className="mx-auto w-14 h-14 rounded-2xl bg-gradient-to-tr from-blue-600 to-indigo-500 flex items-center justify-center text-white shadow-xl shadow-blue-500/25 mb-4">
                    <Bus className="w-8 h-8" />
                </div>
                <h1 className="text-3xl font-extrabold text-white tracking-tight">
                    Transport Governance System
                </h1>
                <p className="mt-2 text-sm text-slate-400 max-w-sm mx-auto">
                    Enterprise School Fleet Governance, Compliance Verification & Financial Reconciliation
                </p>
            </div>

            {/* Login Card */}
            <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-lg z-10 px-4">
                <div className="bg-white/95 backdrop-blur-xl py-8 px-6 sm:px-10 shadow-2xl rounded-3xl border border-white/20">
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                                Account Email
                            </label>
                            <div className="relative">
                                <Mail className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                                <input
                                    type="email"
                                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 text-sm focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 outline-none transition"
                                    placeholder="e.g. schooladmin@greenwood.edu"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                                Password
                            </label>
                            <div className="relative">
                                <Lock className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                                <input
                                    type="password"
                                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 text-sm focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 outline-none transition"
                                    placeholder="Enter your security password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                />
                            </div>
                        </div>

                        {error && (
                            <div className="p-3 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs font-medium">
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full mt-2 py-3 px-4 rounded-xl font-bold text-sm text-white bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-600/30 transition disabled:opacity-50 flex items-center justify-center gap-2"
                        >
                            {loading ? "Authenticating..." : "Sign In to Platform"}
                            <ArrowRight className="w-4 h-4" />
                        </button>
                    </form>

                    {/* Quick Demo Logins Section */}
                    <div className="mt-8 pt-6 border-t border-slate-200">
                        <div className="flex items-center justify-between mb-3">
                            <span className="text-xs font-bold uppercase tracking-wider text-slate-500 flex items-center gap-1.5">
                                <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                                Instant Demo Role Access
                            </span>
                            <span className="text-[11px] text-slate-400 font-medium">One-click test</span>
                        </div>

                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                            {DEMO_USERS.map((demo) => (
                                <button
                                    key={demo.id}
                                    type="button"
                                    onClick={() => handleQuickRoleLogin(demo)}
                                    className="text-left p-2.5 rounded-xl border border-slate-200/90 hover:border-blue-500 hover:bg-blue-50/50 transition group flex items-start justify-between"
                                >
                                    <div>
                                        <div className="text-xs font-bold text-slate-900 group-hover:text-blue-600">
                                            {demo.firstName} ({demo.role.replace("_", " ")})
                                        </div>
                                        <div className="text-[11px] text-slate-500 truncate max-w-[170px]">
                                            {demo.title}
                                        </div>
                                    </div>
                                    <CheckCircle2 className="w-3.5 h-3.5 text-slate-300 group-hover:text-blue-600 mt-0.5" />
                                </button>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Footer notes */}
                <div className="text-center mt-6 text-xs text-slate-400">
                    <p>Protected by Spring Security • Stateless JWT RBAC • Flyway Managed PostgreSQL</p>
                </div>
            </div>
        </div>
    );
}