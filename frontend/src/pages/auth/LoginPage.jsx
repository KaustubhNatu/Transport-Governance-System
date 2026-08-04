import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

function Login() {
    const navigate = useNavigate();
    const { login } = useAuth();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

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

            switch (loggedInUser.role) {
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
        } catch (err) {
            setError("Invalid email or password.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center bg-slate-100 px-4">
            <div className="w-full max-w-md rounded-xl bg-white p-8 shadow-lg">
                <h1 className="mb-2 text-center text-3xl font-bold text-slate-800">
                    Transport Governance System
                </h1>

                <p className="mb-8 text-center text-sm text-slate-500">
                    Sign in to continue
                </p>

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label className="mb-2 block text-sm font-medium text-slate-700">
                            Email
                        </label>

                        <input
                            type="email"
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none focus:border-blue-500"
                            placeholder="Enter your email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-medium text-slate-700">
                            Password
                        </label>

                        <input
                            type="password"
                            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none focus:border-blue-500"
                            placeholder="Enter your password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>

                    {error && (
                        <div className="rounded-md bg-red-100 px-4 py-2 text-sm text-red-700">
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full rounded-lg bg-blue-600 py-2 font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-blue-400"
                    >
                        {loading ? "Signing In..." : "Sign In"}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default Login;