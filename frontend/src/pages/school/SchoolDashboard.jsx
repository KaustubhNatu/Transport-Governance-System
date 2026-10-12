import { useState, useEffect } from "react";
import DashboardLayout from "../../layouts/DashboardLayout";
import MetricCard from "../../components/common/MetricCard";
import StatusBadge from "../../components/common/StatusBadge";
import TransferTimeline from "../../components/common/TransferTimeline";
import Modal from "../../components/common/Modal";
import {
    Users,
    Navigation,
    ArrowLeftRight,
    Plus,
    Search,
    ShieldCheck,
    Clock,
    Bell,
} from "lucide-react";
import studentService from "../../services/studentService";
import routeService from "../../services/routeService";
import transferService from "../../services/transferService";
import complianceService from "../../services/complianceService";
import invoiceService from "../../services/invoiceService";

export default function SchoolDashboard() {
    const [activeTab, setActiveTab] = useState("overview");
    const [students, setStudents] = useState([]);
    const [routes, setRoutes] = useState([]);
    const [transfers, setTransfers] = useState([]);
    const [complianceDocs, setComplianceDocs] = useState([]);
    const [invoices, setInvoices] = useState([]);
    const [searchStudent, setSearchStudent] = useState("");

    // Notify Vendor of Transfer Modal State
    const [isNotifyTransferOpen, setIsNotifyTransferOpen] = useState(false);
    const [transferForm, setTransferForm] = useState({
        studentId: "",
        studentName: "",
        vendorId: 1,
        newVendorName: "SafeRide Educational Fleet",
        newRouteCode: "SR-301",
        reason: "",
    });

    // Enroll Student Modal State
    const [isEnrollStudentOpen, setIsEnrollStudentOpen] = useState(false);
    const [enrollForm, setEnrollForm] = useState({
        firstName: "",
        lastName: "",
        grade: "Grade 7-A",
        rollNumber: "",
        parentName: "",
        parentPhone: "",
        pickupStop: "",
        routeId: 101,
    });

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        const [studs, rts, xfers, docs, invs] = await Promise.all([
            studentService.getAll(),
            routeService.getAll(),
            transferService.getAll(),
            complianceService.getAllDocuments(),
            invoiceService.getAllInvoices(),
        ]);
        setStudents(studs);
        setRoutes(rts);
        setTransfers(xfers);
        setComplianceDocs(docs);
        setInvoices(invs);
    };

    const handleNotifyTransferSubmit = async (e) => {
        e.preventDefault();
        const selectedStudent = students.find((s) => s.id === Number(transferForm.studentId));
        await transferService.create({
            studentId: Number(transferForm.studentId),
            studentName: selectedStudent ? `${selectedStudent.firstName} ${selectedStudent.lastName}` : "Student",
            vendorId: 1,
            newVendorName: transferForm.newVendorName,
            newRouteCode: transferForm.newRouteCode,
            reason: transferForm.reason,
            schoolId: 1,
        });
        setIsNotifyTransferOpen(false);
        setTransferForm({
            studentId: "",
            studentName: "",
            vendorId: 1,
            newVendorName: "SafeRide Educational Fleet",
            newRouteCode: "SR-301",
            reason: "",
        });
        loadData();
    };

    const handleEnrollStudent = async (e) => {
        e.preventDefault();
        await studentService.create({
            schoolId: 1,
            firstName: enrollForm.firstName,
            lastName: enrollForm.lastName,
            grade: enrollForm.grade,
            rollNumber: enrollForm.rollNumber || `GW-2026-${Math.floor(100 + Math.random() * 900)}`,
            parentName: enrollForm.parentName,
            parentPhone: enrollForm.parentPhone,
            pickupStop: enrollForm.pickupStop,
            routeId: Number(enrollForm.routeId),
            routeCode: "R-101",
            vendorName: "City Transit Fleet Solutions",
        });
        setIsEnrollStudentOpen(false);
        setEnrollForm({
            firstName: "",
            lastName: "",
            grade: "Grade 7-A",
            rollNumber: "",
            parentName: "",
            parentPhone: "",
            pickupStop: "",
            routeId: 101,
        });
        loadData();
    };

    const handleCompleteTransfer = async (id) => {
        await transferService.completeTransfer(id);
        loadData();
    };

    const pendingTransfers = transfers.filter(
        (t) => t.duesStatus !== "TRANSFER_COMPLETED" && t.duesStatus !== "NOTIFICATION_COMPLETED"
    );

    return (
        <DashboardLayout activeTab={activeTab} onTabChange={setActiveTab}>
            {/* Header */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
                <div>
                    <div className="flex items-center gap-2">
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-blue-100 text-blue-800">
                            Greenwood High International
                        </span>
                    </div>
                    <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight mt-1">
                        School Transport Administration
                    </h1>
                    <p className="text-sm text-slate-500">
                        Operational monitoring for school bus routes, student safety, and vendor transfer notifications
                    </p>
                </div>

                <div className="flex items-center gap-2.5">
                    <button
                        onClick={() => setIsEnrollStudentOpen(true)}
                        className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-slate-900 text-white font-semibold text-xs shadow-sm hover:bg-slate-800 transition"
                    >
                        <Plus className="w-4 h-4" />
                        Enroll Student
                    </button>
                    <button
                        onClick={() => setIsNotifyTransferOpen(true)}
                        className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 text-white font-semibold text-xs shadow-md shadow-blue-500/20 hover:bg-blue-700 transition"
                    >
                        <Bell className="w-4 h-4" />
                        Notify Vendor of Transfer
                    </button>
                </div>
            </div>

            {/* KPI Metrics */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                <MetricCard
                    title="Enrolled Commuters"
                    value={students.length + 1416}
                    subtitle="Active Bus Pass Holders"
                    icon={Users}
                    variant="blue"
                />
                <MetricCard
                    title="Designated Routes"
                    value={routes.length}
                    subtitle="Serving 42 Transit Stops"
                    icon={Navigation}
                    variant="indigo"
                />
                <MetricCard
                    title="Active Transfer Notifications"
                    value={pendingTransfers.length}
                    subtitle="Vendors Alerted of Departure"
                    icon={ArrowLeftRight}
                    variant="amber"
                />
                <MetricCard
                    title="Vendor Compliance"
                    value="98.2%"
                    subtitle="28/28 Fleet Docs Verified"
                    icon={ShieldCheck}
                    variant="emerald"
                />
            </div>

            {/* Tab: Overview */}
            {activeTab === "overview" && (
                <div className="space-y-8">
                    {/* Notice */}
                    {pendingTransfers.length > 0 && (
                        <div className="p-5 rounded-2xl bg-amber-50/80 border border-amber-200/90 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
                            <div className="flex items-start gap-3">
                                <Clock className="w-5 h-5 text-amber-600 mt-0.5" />
                                <div>
                                    <h3 className="text-sm font-bold text-amber-900">
                                        {pendingTransfers.length} Student Departure Notifications in Progress
                                    </h3>
                                    <p className="text-xs text-amber-800/90 mt-0.5">
                                        The transport vendors have been formally notified of student transfers so their account ledgers can be audited and closed.
                                    </p>
                                </div>
                            </div>
                            <button
                                onClick={() => setActiveTab("transfers")}
                                className="px-3.5 py-2 rounded-xl bg-amber-600 text-white text-xs font-bold hover:bg-amber-700 transition shadow-sm whitespace-nowrap"
                            >
                                View Notifications
                            </button>
                        </div>
                    )}

                    {/* Active Transfers Preview */}
                    <div>
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-base font-bold text-slate-900">Recent Transfer Notifications</h2>
                            <button
                                onClick={() => setActiveTab("transfers")}
                                className="text-xs font-bold text-blue-600 hover:text-blue-700"
                            >
                                Manage All ({transfers.length})
                            </button>
                        </div>
                        <div className="space-y-4">
                            {transfers.slice(0, 1).map((t) => (
                                <TransferTimeline
                                    key={t.id}
                                    transfer={t}
                                    onCompleteTransfer={handleCompleteTransfer}
                                    userRole="SCHOOL_ADMIN"
                                />
                            ))}
                        </div>
                    </div>

                    {/* Active Routes Preview */}
                    <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6">
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-base font-bold text-slate-900">Assigned Campus Routes</h2>
                            <button
                                onClick={() => setActiveTab("routes")}
                                className="text-xs font-bold text-blue-600 hover:text-blue-700"
                            >
                                View Routes ({routes.length})
                            </button>
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {routes.map((r) => (
                                <div key={r.id} className="p-4 rounded-xl border border-slate-100 bg-slate-50/60 flex items-start justify-between">
                                    <div>
                                        <div className="flex items-center gap-2">
                                            <span className="font-bold text-slate-900 text-sm">{r.routeCode}</span>
                                            <span className="text-xs text-slate-500 font-medium">{r.name}</span>
                                        </div>
                                        <div className="text-xs text-slate-500 mt-2 space-y-0.5">
                                            <div>Bus: <span className="font-medium text-slate-800">{r.vehicleNumber}</span></div>
                                            <div>Driver: <span className="font-medium text-slate-800">{r.driverName}</span></div>
                                            <div>Schedule: {r.startTime} • Drop: {r.dropTime}</div>
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <span className="text-xs font-bold px-2 py-0.5 rounded-full bg-blue-100 text-blue-800">
                                            {r.enrolledStudents}/{r.capacity} Seats
                                        </span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}

            {/* Tab: Enrolled Students */}
            {activeTab === "students" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">Enrolled Commuters</h2>
                            <p className="text-xs text-slate-500">Student transport assignments and guardian contacts</p>
                        </div>
                        <div className="relative">
                            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
                            <input
                                type="text"
                                placeholder="Search by student name..."
                                className="pl-9 pr-4 py-2 rounded-xl border border-slate-200 text-xs w-64 outline-none focus:border-blue-500"
                                value={searchStudent}
                                onChange={(e) => setSearchStudent(e.target.value)}
                            />
                        </div>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Student & Roll No.</th>
                                    <th className="py-3.5 px-5">Grade</th>
                                    <th className="py-3.5 px-5">Assigned Route</th>
                                    <th className="py-3.5 px-5">Pickup & Drop Stop</th>
                                    <th className="py-3.5 px-5">Parent Contact</th>
                                    <th className="py-3.5 px-5">Transport Status</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {students
                                    .filter((s) => `${s.firstName} ${s.lastName}`.toLowerCase().includes(searchStudent.toLowerCase()))
                                    .map((s) => (
                                        <tr key={s.id} className="hover:bg-slate-50/60 transition">
                                            <td className="py-4 px-5">
                                                <div className="font-bold text-slate-900 text-sm">
                                                    {s.firstName} {s.lastName}
                                                </div>
                                                <div className="text-slate-400 text-[11px] font-mono">{s.rollNumber}</div>
                                            </td>
                                            <td className="py-4 px-5 font-semibold text-slate-800">{s.grade}</td>
                                            <td className="py-4 px-5">
                                                <span className="font-bold text-blue-600">{s.routeCode || "R-101"}</span>
                                            </td>
                                            <td className="py-4 px-5">{s.pickupStop}</td>
                                            <td className="py-4 px-5">
                                                <div>{s.parentName}</div>
                                                <div className="text-slate-400 text-[11px]">{s.parentPhone}</div>
                                            </td>
                                            <td className="py-4 px-5">
                                                <StatusBadge status={s.status} />
                                            </td>
                                        </tr>
                                    ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Tab: Routes */}
            {activeTab === "routes" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100">
                        <h2 className="text-base font-bold text-slate-900">Campus Transport Routes</h2>
                        <p className="text-xs text-slate-500">Route paths, timing, bus assignments, and driver verification</p>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Route Code & Name</th>
                                    <th className="py-3.5 px-5">Vehicle & Driver</th>
                                    <th className="py-3.5 px-5">Schedule (AM/PM)</th>
                                    <th className="py-3.5 px-5">Stops</th>
                                    <th className="py-3.5 px-5">Occupancy</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {routes.map((r) => (
                                    <tr key={r.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 px-5">
                                            <div className="font-bold text-slate-900 text-sm">{r.routeCode}</div>
                                            <div className="text-slate-500 text-xs">{r.name}</div>
                                        </td>
                                        <td className="py-4 px-5">
                                            <div className="font-bold text-slate-900">{r.vehicleNumber}</div>
                                            <div className="text-slate-400 text-[11px]">Driver: {r.driverName}</div>
                                        </td>
                                        <td className="py-4 px-5">
                                            <div>{r.startTime} (Pickup)</div>
                                            <div className="text-slate-400 text-[11px]">{r.dropTime} (Drop)</div>
                                        </td>
                                        <td className="py-4 px-5 font-semibold">{r.totalStops} Designated Stops</td>
                                        <td className="py-4 px-5 font-bold text-slate-900">
                                            {r.enrolledStudents} / {r.capacity} Seats
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Tab: Transfer Notifications */}
            {activeTab === "transfers" && (
                <div className="space-y-6">
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">Vendor Transfer Notifications</h2>
                            <p className="text-xs text-slate-500">
                                Official notices broadcast to transport vendors regarding student departure and ledger reconciliations
                            </p>
                        </div>
                        <button
                            onClick={() => setIsNotifyTransferOpen(true)}
                            className="inline-flex items-center gap-2 px-3.5 py-2 rounded-xl bg-blue-600 text-white font-semibold text-xs shadow-sm hover:bg-blue-700 transition"
                        >
                            <Bell className="w-4 h-4" />
                            Notify Vendor of Transfer
                        </button>
                    </div>

                    <div className="space-y-4">
                        {transfers.map((t) => (
                            <TransferTimeline
                                key={t.id}
                                transfer={t}
                                onCompleteTransfer={handleCompleteTransfer}
                                userRole="SCHOOL_ADMIN"
                            />
                        ))}
                    </div>
                </div>
            )}

            {/* Tab: Vendor Compliance */}
            {activeTab === "compliance" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100">
                        <h2 className="text-base font-bold text-slate-900">Vendor Fleet Compliance Audit</h2>
                        <p className="text-xs text-slate-500">
                            Validating driver licenses, fitness certificates, and insurance policies for campus transport
                        </p>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Target Entity</th>
                                    <th className="py-3.5 px-5">Document</th>
                                    <th className="py-3.5 px-5">Reg/License No.</th>
                                    <th className="py-3.5 px-5">Expiry Date</th>
                                    <th className="py-3.5 px-5">Status</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {complianceDocs.map((doc) => (
                                    <tr key={doc.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 px-5 font-bold text-slate-900">{doc.entityName}</td>
                                        <td className="py-4 px-5 font-semibold text-slate-800">{doc.docType.replace(/_/g, " ")}</td>
                                        <td className="py-4 px-5 font-mono text-slate-500">{doc.docNumber}</td>
                                        <td className="py-4 px-5 font-bold text-slate-900">{doc.expiryDate}</td>
                                        <td className="py-4 px-5">
                                            <StatusBadge status={doc.status} />
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Tab: Finance */}
            {activeTab === "finance" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100 flex items-center justify-between">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">Student Transport Invoices</h2>
                            <p className="text-xs text-slate-500">Reconciled monthly school bus fees and dues</p>
                        </div>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Invoice No.</th>
                                    <th className="py-3.5 px-5">Student & Guardian</th>
                                    <th className="py-3.5 px-5">Period</th>
                                    <th className="py-3.5 px-5">Total Fee</th>
                                    <th className="py-3.5 px-5">Due Date</th>
                                    <th className="py-3.5 px-5">Payment Status</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {invoices.map((inv) => (
                                    <tr key={inv.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 px-5 font-mono font-bold text-slate-900">{inv.invoiceNumber}</td>
                                        <td className="py-4 px-5">
                                            <div className="font-bold text-slate-900">{inv.studentName}</div>
                                            <div className="text-slate-400 text-[11px]">{inv.parentName}</div>
                                        </td>
                                        <td className="py-4 px-5">{inv.billingPeriod}</td>
                                        <td className="py-4 px-5 font-bold text-slate-900">₹{inv.amount.toLocaleString()}</td>
                                        <td className="py-4 px-5 text-slate-500">{inv.dueDate}</td>
                                        <td className="py-4 px-5">
                                            <StatusBadge status={inv.status} />
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Modal: Enroll Student */}
            <Modal isOpen={isEnrollStudentOpen} onClose={() => setIsEnrollStudentOpen(false)} title="Enroll Student to Transport">
                <form onSubmit={handleEnrollStudent} className="space-y-4">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">First Name</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={enrollForm.firstName}
                                onChange={(e) => setEnrollForm({ ...enrollForm, firstName: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Last Name</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={enrollForm.lastName}
                                onChange={(e) => setEnrollForm({ ...enrollForm, lastName: e.target.value })}
                            />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Grade / Class</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={enrollForm.grade}
                                onChange={(e) => setEnrollForm({ ...enrollForm, grade: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Assigned Route</label>
                            <select
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500 bg-white"
                                value={enrollForm.routeId}
                                onChange={(e) => setEnrollForm({ ...enrollForm, routeId: e.target.value })}
                            >
                                {routes.map((r) => (
                                    <option key={r.id} value={r.id}>{r.routeCode} - {r.name}</option>
                                ))}
                            </select>
                        </div>
                    </div>
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Designated Pickup Stop</label>
                        <input
                            type="text"
                            required
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                            placeholder="e.g. Indiranagar Metro Station"
                            value={enrollForm.pickupStop}
                            onChange={(e) => setEnrollForm({ ...enrollForm, pickupStop: e.target.value })}
                        />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Guardian Name</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={enrollForm.parentName}
                                onChange={(e) => setEnrollForm({ ...enrollForm, parentName: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Guardian Phone</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={enrollForm.parentPhone}
                                onChange={(e) => setEnrollForm({ ...enrollForm, parentPhone: e.target.value })}
                            />
                        </div>
                    </div>
                    <button
                        type="submit"
                        className="w-full mt-4 py-2.5 px-4 rounded-xl font-bold text-xs text-white bg-blue-600 hover:bg-blue-700 shadow-sm transition"
                    >
                        Enroll Student & Assign Route
                    </button>
                </form>
            </Modal>

            {/* Modal: Notify Vendor of Transfer */}
            <Modal isOpen={isNotifyTransferOpen} onClose={() => setIsNotifyTransferOpen(false)} title="Notify Vendor of Student Transfer">
                <form onSubmit={handleNotifyTransferSubmit} className="space-y-4">
                    <div className="p-3.5 rounded-xl bg-blue-50/80 border border-blue-200/90 text-xs text-blue-900">
                        <span className="font-bold block mb-1">Governance Policy:</span>
                        A school cannot hold back a student’s transfer. Submitting this form officially broadcasts a departure alert to the assigned transport vendor so they can update manifests and audit final ledger balances.
                    </div>

                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Select Transferring Student</label>
                        <select
                            required
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500 bg-white"
                            value={transferForm.studentId}
                            onChange={(e) => setTransferForm({ ...transferForm, studentId: e.target.value })}
                        >
                            <option value="">-- Choose Student --</option>
                            {students.map((s) => (
                                <option key={s.id} value={s.id}>
                                    {s.firstName} {s.lastName} ({s.grade} • Current Provider: {s.vendorName})
                                </option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">New Destination / Provider</label>
                        <input
                            type="text"
                            required
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                            placeholder="e.g. Relocating, Parent Carpool, or Alternate Vendor"
                            value={transferForm.newVendorName}
                            onChange={(e) => setTransferForm({ ...transferForm, newVendorName: e.target.value })}
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Departure Notes / Details</label>
                        <textarea
                            required
                            rows={3}
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                            placeholder="Details regarding departure date or reason for vendor record keeping..."
                            value={transferForm.reason}
                            onChange={(e) => setTransferForm({ ...transferForm, reason: e.target.value })}
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full mt-4 py-2.5 px-4 rounded-xl font-bold text-xs text-white bg-blue-600 hover:bg-blue-700 shadow-sm transition"
                    >
                        Broadcast Transfer Notification to Vendor
                    </button>
                </form>
            </Modal>
        </DashboardLayout>
    );
}