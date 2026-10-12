import { Link } from "react-router-dom";
import { Bus, Home } from "lucide-react";

export default function NotFoundPage() {
    return (
        <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4">
            <div className="max-w-md w-full bg-slate-800/90 border border-slate-700/80 rounded-3xl p-8 text-center text-white shadow-2xl">
                <div className="w-16 h-16 rounded-2xl bg-blue-600/20 border border-blue-500/30 text-blue-400 flex items-center justify-center mx-auto mb-4">
                    <Bus className="w-8 h-8" />
                </div>
                <h1 className="text-4xl font-extrabold tracking-tight">404</h1>
                <h2 className="text-lg font-bold text-slate-200 mt-2">Route Not Found</h2>
                <p className="text-xs text-slate-400 mt-2 leading-relaxed">
                    The requested governance page or endpoint is not registered on the platform.
                </p>
                <Link
                    to="/"
                    className="mt-6 inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-blue-600 text-white font-bold text-xs hover:bg-blue-700 transition shadow-md shadow-blue-600/30"
                >
                    <Home className="w-4 h-4" />
                    Return to Login
                </Link>
            </div>
        </div>
    );
}