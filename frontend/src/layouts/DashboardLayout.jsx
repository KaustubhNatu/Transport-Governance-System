import Header from "../components/common/Header";
import Sidebar from "../components/common/Sidebar";
import { useAuth } from "../contexts/AuthContext";

export default function DashboardLayout({ activeTab, onTabChange, children }) {
    const { user } = useAuth();

    return (
        <div className="min-h-screen bg-slate-50 text-slate-800 flex flex-col font-sans antialiased">
            <Header />

            <div className="flex flex-1">
                <Sidebar
                    role={user?.role || "SUPER_ADMIN"}
                    activeTab={activeTab}
                    onTabChange={onTabChange}
                />

                <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto w-full overflow-y-auto">
                    {children}
                </main>
            </div>
        </div>
    );
}
