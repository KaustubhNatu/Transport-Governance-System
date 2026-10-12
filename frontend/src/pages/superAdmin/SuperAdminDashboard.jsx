import { useState, useEffect } from "react";
import DashboardLayout from "../../layouts/DashboardLayout";
import MetricCard from "../../components/common/MetricCard";
import StatusBadge from "../../components/common/StatusBadge";
import TransferTimeline from "../../components/common/TransferTimeline";
import Modal from "../../components/common/Modal";
import {
    School,
    Truck,
    ShieldCheck,
    AlertTriangle,
    Plus,
    Search,
} from "lucide-react";
import schoolService from "../../services/schoolService";
import vendorService from "../../services/vendorService";
import transferService from "../../services/transferService";
import complianceService from "../../services/complianceService";
import { MOCK_AUDIT_LOGS } from "../../utils/mockData";

export default function SuperAdminDashboard() {
    const [activeTab, setActiveTab] = useState("overview");
    const [schools, setSchools] = useState([]);
    const [vendors, setVendors] = useState([]);
    const [transfers, setTransfers] = useState([]);
    const [complianceDocs, setComplianceDocs] = useState([]);
    const [auditLogs] = useState(MOCK_AUDIT_LOGS);
    const [searchQuery, setSearchQuery] = useState("");

    // Modal state for Add School
    const [isAddSchoolOpen, setIsAddSchoolOpen] = useState(false);
    const [newSchool, setNewSchool] = useState({
        name: "",
        address: "",
        contactEmail: "",
        contactPhone: "",
    });

    // Modal state for Add Vendor
    const [isAddVendorOpen, setIsAddVendorOpen] = useState(false);
    const [newVendor, setNewVendor] = useState({
        name: "",
        registrationNumber: "",
        contactPerson: "",
        email: "",
        phone: "",
    });

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        const [schoolsData, vendorsData, transfersData, docsData] = await Promise.all([
            schoolService.getAll(),
            vendorService.getAll(),
            transferService.getAll(),
            complianceService.getAllDocuments(),
        ]);
        setSchools(schoolsData);
        setVendors(vendorsData);
        setTransfers(transfersData);
        setComplianceDocs(docsData);
    };

    const handleCreateSchool = async (e) => {
        e.preventDefault();
        await schoolService.create(newSchool);
        setIsAddSchoolOpen(false);
        setNewSchool({ name: "", address: "", contactEmail: "", contactPhone: "" });
        loadData();
    };

    const handleCreateVendor = async (e) => {
        e.preventDefault();
        await vendorService.create(newVendor);
        setIsAddVendorOpen(false);
        setNewVendor({ name: "", registrationNumber: "", contactPerson: "", email: "", phone: "" });
        loadData();
    };

    const expiringDocsCount = complianceDocs.filter(
        (d) => d.status === "EXPIRING_SOON" || d.status === "EXPIRED"
    ).length;

    return (
        <DashboardLayout activeTab={activeTab} onTabChange={setActiveTab}>
            {/* Top Bar / Page Header */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
                <div>
                    <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">
                        Super Admin Governance Command
                    </h1>
                    <p className="text-sm text-slate-500 mt-1">
                        Platform-wide monitoring of institutions, transport vendors, and compliance audits
                    </p>
                </div>

                <div className="flex items-center gap-2.5">
                    <button
                        onClick={() => setIsAddSchoolOpen(true)}
                        className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 text-white font-semibold text-xs shadow-md shadow-blue-500/20 hover:bg-blue-700 transition"
                    >
                        <Plus className="w-4 h-4" />
                        Add School
                    </button>
                    <button
                        onClick={() => setIsAddVendorOpen(true)}
                        className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-slate-900 text-white font-semibold text-xs shadow-sm hover:bg-slate-800 transition"
                    >
                        <Plus className="w-4 h-4" />
                        Add Vendor
                    </button>
                </div>
            </div>

            {/* Metrics KPI Row */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                <MetricCard
                    title="Registered Schools"
                    value={schools.length}
                    subtitle="3 Tier-1 Metro Cities"
                    icon={School}
                    trend={{ positive: true, text: "+1 this month" }}
                    variant="blue"
                />
                <MetricCard
                    title="Transport Vendors"
                    value={vendors.length}
                    subtitle="29 Vehicles • 36 Drivers"
                    icon={Truck}
                    trend={{ positive: true, text: "All Certified" }}
                    variant="indigo"
                />
                <MetricCard
                    title="Transfer Notifications"
                    value={transfers.filter((t) => t.duesStatus !== "TRANSFER_COMPLETED" && t.duesStatus !== "NOTIFICATION_COMPLETED").length}
                    subtitle="Vendor Departure Alerts"
                    icon={AlertTriangle}
                    variant="amber"
                />
                <MetricCard
                    title="Compliance Alerts"
                    value={expiringDocsCount}
                    subtitle="Expiring or Overdue Documents"
                    icon={ShieldCheck}
                    trend={{ positive: false, text: "Attention Needed" }}
                    variant={expiringDocsCount > 0 ? "rose" : "emerald"}
                />
            </div>

            {/* Tab: Platform Overview */}
            {activeTab === "overview" && (
                <div className="space-y-8">
                    {/* Quick Compliance Notice */}
                    <div className="p-5 rounded-2xl bg-gradient-to-r from-blue-900 via-indigo-900 to-slate-900 text-white shadow-lg flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
                        <div>
                            <span className="text-xs font-bold tracking-wider uppercase px-2.5 py-1 rounded-full bg-blue-500/20 text-blue-300 border border-blue-400/30">
                                System Governance Active
                            </span>
                            <h3 className="text-lg font-bold mt-2">
                                Real-Time Transport Compliance & Transfer State Machine
                            </h3>
                            <p className="text-xs text-slate-300 max-w-xl mt-1">
                                Enforcing RTO document validity, automated dues reporting between schools and vendors, and verifiable student transfer safety certificates.
                            </p>
                        </div>
                        <button
                            onClick={() => setActiveTab("compliance")}
                            className="px-4 py-2 rounded-xl bg-white text-slate-900 font-bold text-xs hover:bg-slate-100 transition whitespace-nowrap shadow-sm"
                        >
                            Review Compliance Radar
                        </button>
                    </div>

                    {/* Active Transfers */}
                    <div>
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-base font-bold text-slate-900">
                                Recent Student Transfer Workflows
                            </h2>
                            <button
                                onClick={() => setActiveTab("transfers")}
                                className="text-xs font-bold text-blue-600 hover:text-blue-700"
                            >
                                View All ({transfers.length})
                            </button>
                        </div>
                        <div className="space-y-4">
                            {transfers.slice(0, 2).map((t) => (
                                <TransferTimeline key={t.id} transfer={t} userRole="SUPER_ADMIN" />
                            ))}
                        </div>
                    </div>
                </div>
            )}

            {/* Tab: Schools Registry */}
            {activeTab === "schools" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">Registered Schools</h2>
                            <p className="text-xs text-slate-500">Institutions using the governance portal</p>
                        </div>
                        <div className="relative">
                            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
                            <input
                                type="text"
                                placeholder="Search schools..."
                                className="pl-9 pr-4 py-2 rounded-xl border border-slate-200 text-xs w-64 outline-none focus:border-blue-500"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                            />
                        </div>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">School Name & Address</th>
                                    <th className="py-3.5 px-5">Contact Details</th>
                                    <th className="py-3.5 px-5">Enrolled Students</th>
                                    <th className="py-3.5 px-5">Buses</th>
                                    <th className="py-3.5 px-5">Compliance</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {schools
                                    .filter((s) => s.name.toLowerCase().includes(searchQuery.toLowerCase()))
                                    .map((s) => (
                                        <tr key={s.id} className="hover:bg-slate-50/60 transition">
                                            <td className="py-4 px-5">
                                                <div className="font-bold text-slate-900 text-sm">{s.name}</div>
                                                <div className="text-slate-400 text-[11px] truncate max-w-xs">{s.address}</div>
                                            </td>
                                            <td className="py-4 px-5">
                                                <div>{s.contactEmail}</div>
                                                <div className="text-slate-400 text-[11px]">{s.contactPhone}</div>
                                            </td>
                                            <td className="py-4 px-5 font-bold text-slate-900">
                                                {s.enrolledStudents || 1200}
                                            </td>
                                            <td className="py-4 px-5">{s.activeBuses || 15} Buses</td>
                                            <td className="py-4 px-5">
                                                <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                    {s.complianceScore || 95}% Compliant
                                                </span>
                                            </td>
                                        </tr>
                                    ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Tab: Transport Vendors */}
            {activeTab === "vendors" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100 flex items-center justify-between">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">Transport Vendors</h2>
                            <p className="text-xs text-slate-500">Contracted bus fleet operators</p>
                        </div>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Vendor & Reg No.</th>
                                    <th className="py-3.5 px-5">Contact Person</th>
                                    <th className="py-3.5 px-5">Fleet Size</th>
                                    <th className="py-3.5 px-5">Compliance Status</th>
                                    <th className="py-3.5 px-5">Rating</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {vendors.map((v) => (
                                    <tr key={v.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 px-5">
                                            <div className="font-bold text-slate-900 text-sm">{v.name}</div>
                                            <div className="text-slate-400 text-[11px] font-mono">{v.registrationNumber}</div>
                                        </td>
                                        <td className="py-4 px-5">
                                            <div>{v.contactPerson}</div>
                                            <div className="text-slate-400 text-[11px]">{v.email} • {v.phone}</div>
                                        </td>
                                        <td className="py-4 px-5">
                                            <span className="font-bold text-slate-900">{v.vehicleCount || 10}</span> Vehicles •{" "}
                                            <span className="font-bold text-slate-900">{v.driverCount || 12}</span> Drivers
                                        </td>
                                        <td className="py-4 px-5">
                                            <StatusBadge status={v.complianceStatus || "COMPLIANT"} />
                                        </td>
                                        <td className="py-4 px-5 font-bold text-slate-900">
                                            ⭐ {v.rating || 4.7} / 5.0
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Tab: System Transfers */}
            {activeTab === "transfers" && (
                <div className="space-y-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">Transfer Notifications Monitor</h2>
                            <p className="text-xs text-slate-500">
                                Cross-institution tracking of departure notices sent to transport vendors for final ledger reconciliation
                            </p>
                        </div>
                    </div>

                    <div className="space-y-4">
                        {transfers.map((t) => (
                            <TransferTimeline key={t.id} transfer={t} userRole="SUPER_ADMIN" />
                        ))}
                    </div>
                </div>
            )}

            {/* Tab: Compliance Radar */}
            {activeTab === "compliance" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100">
                        <h2 className="text-base font-bold text-slate-900">Compliance & Regulatory Documents</h2>
                        <p className="text-xs text-slate-500">
                            RTO verified Commercial Insurance, Fitness Certificates, and Driver Heavy Vehicle Licenses
                        </p>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Document Target</th>
                                    <th className="py-3.5 px-5">Document Type & Number</th>
                                    <th className="py-3.5 px-5">Issued Date</th>
                                    <th className="py-3.5 px-5">Expiry Date</th>
                                    <th className="py-3.5 px-5">Status</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {complianceDocs.map((doc) => (
                                    <tr key={doc.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 px-5">
                                            <div className="font-bold text-slate-900">{doc.entityName}</div>
                                            <div className="text-slate-400 text-[11px]">{doc.vendorName}</div>
                                        </td>
                                        <td className="py-4 px-5">
                                            <div className="font-semibold text-slate-800">{doc.docType.replace(/_/g, " ")}</div>
                                            <div className="text-slate-400 text-[11px] font-mono">{doc.docNumber}</div>
                                        </td>
                                        <td className="py-4 px-5">{doc.issueDate}</td>
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

            {/* Tab: Security & Audit Logs */}
            {activeTab === "audit" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100 flex items-center justify-between">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">System Security Audit Logs</h2>
                            <p className="text-xs text-slate-500">
                                Immutable event log tracking financial adjustments, transfer updates, and RBAC actions
                            </p>
                        </div>
                        <span className="px-3 py-1 rounded-full text-xs font-bold bg-slate-100 text-slate-700">
                            PostgreSQL audit_logs Table
                        </span>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Action</th>
                                    <th className="py-3.5 px-5">Entity</th>
                                    <th className="py-3.5 px-5">Performed By</th>
                                    <th className="py-3.5 px-5">IP Address</th>
                                    <th className="py-3.5 px-5">Timestamp</th>
                                    <th className="py-3.5 px-5">Details</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {auditLogs.map((log) => (
                                    <tr key={log.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 px-5">
                                            <span className="px-2 py-0.5 rounded font-mono font-bold text-[11px] bg-slate-100 text-slate-800">
                                                {log.action}
                                            </span>
                                        </td>
                                        <td className="py-4 px-5 font-bold text-slate-900">{log.entity}</td>
                                        <td className="py-4 px-5 text-blue-600 font-semibold">{log.performedBy}</td>
                                        <td className="py-4 px-5 font-mono text-slate-400">{log.ipAddress}</td>
                                        <td className="py-4 px-5 text-slate-500">{new Date(log.timestamp).toLocaleString()}</td>
                                        <td className="py-4 px-5 text-slate-600 truncate max-w-xs">{log.details}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Modal: Add School */}
            <Modal isOpen={isAddSchoolOpen} onClose={() => setIsAddSchoolOpen(false)} title="Register New School">
                <form onSubmit={handleCreateSchool} className="space-y-4">
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                            School Name
                        </label>
                        <input
                            type="text"
                            required
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                            placeholder="e.g. Delhi Public School International"
                            value={newSchool.name}
                            onChange={(e) => setNewSchool({ ...newSchool, name: e.target.value })}
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                            Address
                        </label>
                        <input
                            type="text"
                            required
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                            placeholder="Full campus location address"
                            value={newSchool.address}
                            onChange={(e) => setNewSchool({ ...newSchool, address: e.target.value })}
                        />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                                Email
                            </label>
                            <input
                                type="email"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                placeholder="admin@dps.edu"
                                value={newSchool.contactEmail}
                                onChange={(e) => setNewSchool({ ...newSchool, contactEmail: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                                Phone
                            </label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                placeholder="+91 80 1234 5678"
                                value={newSchool.contactPhone}
                                onChange={(e) => setNewSchool({ ...newSchool, contactPhone: e.target.value })}
                            />
                        </div>
                    </div>
                    <button
                        type="submit"
                        className="w-full mt-4 py-2.5 px-4 rounded-xl font-bold text-xs text-white bg-blue-600 hover:bg-blue-700 shadow-sm transition"
                    >
                        Register School & Provision Admin
                    </button>
                </form>
            </Modal>

            {/* Modal: Add Vendor */}
            <Modal isOpen={isAddVendorOpen} onClose={() => setIsAddVendorOpen(false)} title="Register Transport Vendor">
                <form onSubmit={handleCreateVendor} className="space-y-4">
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                            Vendor Company Name
                        </label>
                        <input
                            type="text"
                            required
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                            placeholder="e.g. Metro Transit Logistics Ltd."
                            value={newVendor.name}
                            onChange={(e) => setNewVendor({ ...newVendor, name: e.target.value })}
                        />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                                Registration Number
                            </label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                placeholder="REG-KA-2026-9901"
                                value={newVendor.registrationNumber}
                                onChange={(e) => setNewVendor({ ...newVendor, registrationNumber: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                                Contact Person
                            </label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                placeholder="Operations Director"
                                value={newVendor.contactPerson}
                                onChange={(e) => setNewVendor({ ...newVendor, contactPerson: e.target.value })}
                            />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                                Dispatch Email
                            </label>
                            <input
                                type="email"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                placeholder="dispatch@vendor.com"
                                value={newVendor.email}
                                onChange={(e) => setNewVendor({ ...newVendor, email: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                                Phone
                            </label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                placeholder="+91 98000 11223"
                                value={newVendor.phone}
                                onChange={(e) => setNewVendor({ ...newVendor, phone: e.target.value })}
                            />
                        </div>
                    </div>
                    <button
                        type="submit"
                        className="w-full mt-4 py-2.5 px-4 rounded-xl font-bold text-xs text-white bg-slate-900 hover:bg-slate-800 shadow-sm transition"
                    >
                        Register Transport Vendor
                    </button>
                </form>
            </Modal>
        </DashboardLayout>
    );
}