import { useState } from "react";
import DashboardLayout from "../../layouts/DashboardLayout";
import MetricCard from "../../components/common/MetricCard";
import StatusBadge from "../../components/common/StatusBadge";
import {
    Navigation,
    Truck,
    Users,
    FileCheck2,
    CheckCircle2,
    Phone,
    MapPin,
    Clock,
} from "lucide-react";
import { MOCK_STUDENTS, MOCK_ROUTES } from "../../utils/mockData";

export default function DriverDashboard() {
    const [activeTab, setActiveTab] = useState("route");
    const [students, setStudents] = useState(
        MOCK_STUDENTS.map((s) => ({ ...s, boarded: false }))
    );

    const route = MOCK_ROUTES.find((r) => r.routeCode === "R-102") || MOCK_ROUTES[0];

    const toggleBoarding = (studentId) => {
        setStudents((prev) =>
            prev.map((s) => (s.id === studentId ? { ...s, boarded: !s.boarded } : s))
        );
    };

    const boardedCount = students.filter((s) => s.boarded).length;

    return (
        <DashboardLayout activeTab={activeTab} onTabChange={setActiveTab}>
            {/* Header */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
                <div>
                    <div className="flex items-center gap-2">
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-blue-100 text-blue-800">
                            Senior Driver: Rajesh Kumar
                        </span>
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800">
                            Bus {route.vehicleNumber}
                        </span>
                    </div>
                    <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight mt-1">
                        Driver Transport Cockpit
                    </h1>
                    <p className="text-sm text-slate-500">
                        Assigned Route: {route.routeCode} ({route.name})
                    </p>
                </div>

                <div className="flex items-center gap-2">
                    <span className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-emerald-50 text-emerald-700 text-xs font-bold border border-emerald-200">
                        <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                        Live GPS Beacon Active
                    </span>
                </div>
            </div>

            {/* KPI Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                <MetricCard
                    title="Designated Stops"
                    value={route.totalStops}
                    subtitle="Indiranagar to Campus"
                    icon={MapPin}
                    variant="blue"
                />
                <MetricCard
                    title="Student Commuters"
                    value={students.length}
                    subtitle={`${boardedCount} Boarded Today`}
                    icon={Users}
                    variant="indigo"
                />
                <MetricCard
                    title="Pickup Departure"
                    value={route.startTime}
                    subtitle={`Drop Schedule: ${route.dropTime}`}
                    icon={Clock}
                    variant="amber"
                />
                <MetricCard
                    title="DL Compliance"
                    value="VALID"
                    subtitle="Expires April 2028"
                    icon={FileCheck2}
                    variant="emerald"
                />
            </div>

            {/* Tab: Route & Stops */}
            {activeTab === "route" && (
                <div className="space-y-6">
                    {/* Live Vehicle Card */}
                    <div className="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
                        <div className="space-y-2">
                            <div className="flex items-center gap-2">
                                <Truck className="w-5 h-5 text-blue-600" />
                                <h2 className="text-lg font-bold text-slate-900">
                                    Assigned Vehicle: {route.vehicleNumber} (Ashok Leyland Falcon)
                                </h2>
                            </div>
                            <p className="text-xs text-slate-500">
                                Capacity: {route.capacity} Seats • Speed Governor Installed (Limit: 40 km/h) • First Aid Kit Checked
                            </p>
                        </div>
                        <div className="flex items-center gap-3">
                            <div className="text-right">
                                <div className="text-xs text-slate-400">Current Speed</div>
                                <div className="text-xl font-extrabold text-slate-900">28 km/h</div>
                            </div>
                            <div className="p-3 rounded-xl bg-blue-50 text-blue-600 border border-blue-100">
                                <Navigation className="w-6 h-6" />
                            </div>
                        </div>
                    </div>

                    {/* Route Stops List */}
                    <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6">
                        <h3 className="text-base font-bold text-slate-900 mb-4">Route Stop Waypoints</h3>
                        <div className="space-y-4">
                            {[
                                { stop: "Indiranagar Metro Station", time: "07:00 AM", students: 2 },
                                { stop: "HAL 2nd Stage", time: "07:15 AM", students: 1 },
                                { stop: "Marathahalli Bridge", time: "07:30 AM", students: 1 },
                                { stop: "Whitefield Inner Circle", time: "07:45 AM", students: 1 },
                                { stop: "Greenwood High Campus Gate", time: "08:15 AM", students: "Arrival" },
                            ].map((w, idx) => (
                                <div key={idx} className="flex items-center gap-4 text-xs">
                                    <div className="w-7 h-7 rounded-full bg-blue-600 text-white font-bold flex items-center justify-center flex-shrink-0">
                                        {idx + 1}
                                    </div>
                                    <div className="flex-1">
                                        <div className="font-bold text-slate-900 text-sm">{w.stop}</div>
                                        <div className="text-slate-400 text-[11px]">Scheduled: {w.time}</div>
                                    </div>
                                    <span className="font-semibold px-2.5 py-1 rounded-lg bg-slate-100 text-slate-700">
                                        {typeof w.students === "number" ? `${w.students} Students` : w.students}
                                    </span>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}

            {/* Tab: Student Roster */}
            {activeTab === "roster" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100 flex items-center justify-between">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">Passenger Manifest & Attendance</h2>
                            <p className="text-xs text-slate-500">Tap to toggle student boarding status during transit</p>
                        </div>
                        <span className="text-xs font-bold px-3 py-1 rounded-full bg-blue-100 text-blue-800">
                            {boardedCount} / {students.length} Boarded
                        </span>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Student Name</th>
                                    <th className="py-3.5 px-5">Grade</th>
                                    <th className="py-3.5 px-5">Designated Stop</th>
                                    <th className="py-3.5 px-5">Emergency Guardian</th>
                                    <th className="py-3.5 px-5">Attendance</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {students.map((s) => (
                                    <tr key={s.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 px-5">
                                            <div className="font-bold text-slate-900 text-sm">{s.firstName} {s.lastName}</div>
                                            <div className="text-slate-400 text-[11px] font-mono">{s.rollNumber}</div>
                                        </td>
                                        <td className="py-4 px-5">{s.grade}</td>
                                        <td className="py-4 px-5 font-semibold text-slate-800">{s.pickupStop}</td>
                                        <td className="py-4 px-5">
                                            <div className="flex items-center gap-2">
                                                <span>{s.parentName}</span>
                                                <a
                                                    href={`tel:${s.parentPhone}`}
                                                    className="p-1 rounded-lg text-blue-600 hover:bg-blue-50 transition"
                                                    title="Call Guardian"
                                                >
                                                    <Phone className="w-3.5 h-3.5" />
                                                </a>
                                            </div>
                                            <div className="text-slate-400 text-[11px]">{s.parentPhone}</div>
                                        </td>
                                        <td className="py-4 px-5">
                                            <button
                                                onClick={() => toggleBoarding(s.id)}
                                                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition flex items-center gap-1.5 ${
                                                    s.boarded
                                                        ? "bg-emerald-600 text-white shadow-sm"
                                                        : "bg-slate-100 text-slate-600 hover:bg-slate-200"
                                                }`}
                                            >
                                                <CheckCircle2 className="w-3.5 h-3.5" />
                                                {s.boarded ? "Boarded" : "Mark Boarded"}
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Tab: Vehicle Inspection */}
            {activeTab === "vehicle" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 space-y-6">
                    <div>
                        <h2 className="text-base font-bold text-slate-900">Pre-Trip Safety Inspection Checklist</h2>
                        <p className="text-xs text-slate-500">Regulatory checks mandated by School Transport Committee</p>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        {[
                            { title: "Brake & Tire Pressure Check", ok: true, note: "Pneumatic pressure optimal" },
                            { title: "Speed Governor Calibration", ok: true, note: "Locked at 40 km/h RTO standard" },
                            { title: "Emergency Exit & Window Latches", ok: true, note: "Unobstructed and functional" },
                            { title: "First Aid & Fire Extinguisher", ok: true, note: "Expiry date valid till 2027" },
                            { title: "CCTV Cameras & Storage DVR", ok: true, note: "Recording 4 angles with audio" },
                            { title: "GPS Telematics & SOS Beacon", ok: true, note: "Transmitting live telemetry" },
                        ].map((c, i) => (
                            <div key={i} className="p-4 rounded-xl border border-slate-100 bg-slate-50/60 flex items-start gap-3">
                                <CheckCircle2 className="w-5 h-5 text-emerald-600 mt-0.5 flex-shrink-0" />
                                <div>
                                    <div className="font-bold text-slate-900 text-xs">{c.title}</div>
                                    <p className="text-[11px] text-slate-500 mt-0.5">{c.note}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Tab: My Documents */}
            {activeTab === "compliance" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 space-y-6">
                    <div>
                        <h2 className="text-base font-bold text-slate-900">Driver Credentials & Licensing</h2>
                        <p className="text-xs text-slate-500">RTO Verified Transport Authorization Documents</p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="p-5 rounded-2xl border border-emerald-200 bg-emerald-50/50">
                            <div className="flex items-center justify-between">
                                <span className="font-bold text-emerald-900 text-sm">Commercial Heavy Vehicle Driving License</span>
                                <StatusBadge status="VALID" />
                            </div>
                            <div className="mt-3 text-xs text-slate-600 space-y-1 font-mono">
                                <div>License No: DL-KA01-2019009123</div>
                                <div>Authority: RTO Bangalore Central (KA-01)</div>
                                <div>Expiry Date: 2028-04-12</div>
                            </div>
                        </div>

                        <div className="p-5 rounded-2xl border border-emerald-200 bg-emerald-50/50">
                            <div className="flex items-center justify-between">
                                <span className="font-bold text-emerald-900 text-sm">Police Background Verification</span>
                                <StatusBadge status="VERIFIED" />
                            </div>
                            <div className="mt-3 text-xs text-slate-600 space-y-1 font-mono">
                                <div>Certificate No: PVC-KA-2025-9921</div>
                                <div>Issued By: Commissioner of Police, Bangalore</div>
                                <div>Clearance: Clean Criminal Record</div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </DashboardLayout>
    );
}