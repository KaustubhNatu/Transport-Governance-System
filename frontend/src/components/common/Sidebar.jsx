import {
    LayoutDashboard,
    School,
    Truck,
    Users,
    FileCheck2,
    ArrowLeftRight,
    Receipt,
    History,
    Navigation,
    CreditCard,
    Baby,
    ShieldAlert,
} from "lucide-react";

export default function Sidebar({ role, activeTab, onTabChange }) {
    const getNavItems = () => {
        switch (role) {
            case "SUPER_ADMIN":
                return [
                    { id: "overview", label: "Platform Overview", icon: LayoutDashboard },
                    { id: "schools", label: "Schools Registry", icon: School },
                    { id: "vendors", label: "Transport Vendors", icon: Truck },
                    { id: "transfers", label: "Transfer Notifications", icon: ArrowLeftRight },
                    { id: "compliance", label: "Compliance Radar", icon: FileCheck2 },
                    { id: "audit", label: "Security & Audit Logs", icon: History },
                ];

            case "SCHOOL_ADMIN":
                return [
                    { id: "overview", label: "School Operations", icon: LayoutDashboard },
                    { id: "students", label: "Enrolled Students", icon: Users },
                    { id: "routes", label: "Transport Routes", icon: Navigation },
                    { id: "transfers", label: "Transfer Notifications", icon: ArrowLeftRight },
                    { id: "compliance", label: "Vendor Compliance", icon: FileCheck2 },
                    { id: "finance", label: "Fee Invoices", icon: Receipt },
                ];

            case "VENDOR_ADMIN":
                return [
                    { id: "overview", label: "Fleet Command", icon: LayoutDashboard },
                    { id: "vehicles", label: "Vehicles & GPS", icon: Truck },
                    { id: "drivers", label: "Drivers & Licenses", icon: Users },
                    { id: "compliance", label: "RTO Compliance Docs", icon: FileCheck2 },
                    { id: "transfers", label: "Transfer Notifications & Dues", icon: ArrowLeftRight },
                    { id: "billing", label: "Invoices & Payments", icon: Receipt },
                ];

            case "DRIVER":
                return [
                    { id: "route", label: "My Route & Stops", icon: Navigation },
                    { id: "roster", label: "Student Roster", icon: Users },
                    { id: "vehicle", label: "Vehicle Inspection", icon: Truck },
                    { id: "compliance", label: "My Documents & DL", icon: FileCheck2 },
                ];

            case "PARENT":
                return [
                    { id: "children", label: "My Children", icon: Baby },
                    { id: "transfers", label: "Transfer Notifications", icon: ArrowLeftRight },
                    { id: "invoices", label: "Fee Invoices & Pay", icon: CreditCard },
                    { id: "support", label: "Safety & Emergency", icon: ShieldAlert },
                ];

            default:
                return [{ id: "overview", label: "Dashboard", icon: LayoutDashboard }];
        }
    };

    const items = getNavItems();

    return (
        <aside className="w-64 border-r border-slate-200/80 bg-slate-900 text-slate-300 flex flex-col justify-between hidden md:flex min-h-[calc(100vh-4rem)]">
            <div className="p-4 space-y-1">
                <div className="px-3 py-2 text-[11px] font-bold uppercase tracking-wider text-slate-400">
                    Menu Navigation
                </div>
                {items.map((item) => {
                    const Icon = item.icon;
                    const isActive = activeTab === item.id;
                    return (
                        <button
                            key={item.id}
                            onClick={() => onTabChange && onTabChange(item.id)}
                            className={`w-full flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                                isActive
                                    ? "bg-blue-600 text-white shadow-md shadow-blue-600/30 font-bold"
                                    : "text-slate-400 hover:text-white hover:bg-slate-800/80"
                            }`}
                        >
                            <Icon className={`w-4 h-4 ${isActive ? "text-white" : "text-slate-400"}`} />
                            {item.label}
                        </button>
                    );
                })}
            </div>

            {/* Bottom info widget */}
            <div className="p-4 m-4 rounded-2xl bg-slate-800/60 border border-slate-700/50">
                <div className="flex items-center gap-2 text-xs font-semibold text-slate-200">
                    <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
                    System Active
                </div>
                <p className="text-[11px] text-slate-400 mt-1 leading-relaxed">
                    Spring Boot REST API • Flyway V11 • Transfer Notifications
                </p>
            </div>
        </aside>
    );
}
