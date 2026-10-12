import { useState, useEffect } from "react";
import DashboardLayout from "../../layouts/DashboardLayout";
import MetricCard from "../../components/common/MetricCard";
import StatusBadge from "../../components/common/StatusBadge";
import TransferTimeline from "../../components/common/TransferTimeline";
import Modal from "../../components/common/Modal";
import {
    Truck,
    Users,
    Receipt,
    Plus,
    AlertTriangle,
    CheckCircle2,
    ShieldAlert,
} from "lucide-react";
import vehicleService from "../../services/vehicleService";
import driverService from "../../services/driverService";
import transferService from "../../services/transferService";
import complianceService from "../../services/complianceService";
import invoiceService from "../../services/invoiceService";

export default function VendorDashboard() {
    const [activeTab, setActiveTab] = useState("overview");
    const [vehicles, setVehicles] = useState([]);
    const [drivers, setDrivers] = useState([]);
    const [transfers, setTransfers] = useState([]);
    const [complianceDocs, setComplianceDocs] = useState([]);
    const [invoices, setInvoices] = useState([]);
    const [payments, setPayments] = useState([]);

    // Modal: Report Dues on Transfer
    const [selectedTransferForDues, setSelectedTransferForDues] = useState(null);
    const [duesForm, setDuesForm] = useState({ amount: "", notes: "" });

    // Modal: Add Vehicle
    const [isAddVehicleOpen, setIsAddVehicleOpen] = useState(false);
    const [vehicleForm, setVehicleForm] = useState({
        registrationNumber: "",
        model: "Tata Starbus 32S",
        capacity: 32,
        assignedRoute: "R-101",
        driverName: "Suresh Patil",
    });

    // Modal: Add Driver
    const [isAddDriverOpen, setIsAddDriverOpen] = useState(false);
    const [driverForm, setDriverForm] = useState({
        firstName: "",
        lastName: "",
        phone: "",
        licenseNumber: "",
        licenseExpiry: "",
        assignedVehicle: "KA-01-AB-1234",
    });

    // Modal: Generate Invoice
    const [isCreateInvoiceOpen, setIsCreateInvoiceOpen] = useState(false);
    const [invoiceForm, setInvoiceForm] = useState({
        studentName: "Aarav Sharma",
        parentName: "Priya Sharma",
        amount: 4200,
        billingPeriod: "November 2026",
        dueDate: "2026-11-15",
    });

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        const [vehs, drvs, xfers, docs, invs, pmts] = await Promise.all([
            vehicleService.getAll(),
            driverService.getAll(),
            transferService.getAll(),
            complianceService.getAllDocuments(),
            invoiceService.getAllInvoices(),
            invoiceService.getAllPayments(),
        ]);
        setVehicles(vehs);
        setDrivers(drvs);
        setTransfers(xfers);
        setComplianceDocs(docs);
        setInvoices(invs);
        setPayments(pmts);
    };

    const handleReportDuesSubmit = async (e) => {
        e.preventDefault();
        if (!selectedTransferForDues) return;
        await transferService.updateDues(selectedTransferForDues.id, {
            amount: Number(duesForm.amount),
            notes: duesForm.notes,
        });
        setSelectedTransferForDues(null);
        setDuesForm({ amount: "", notes: "" });
        loadData();
    };

    const handleAddVehicle = async (e) => {
        e.preventDefault();
        await vehicleService.create({
            vendorId: 1,
            vendorName: "City Transit Fleet Solutions",
            registrationNumber: vehicleForm.registrationNumber,
            model: vehicleForm.model,
            capacity: Number(vehicleForm.capacity),
            assignedRoute: vehicleForm.assignedRoute,
            driverName: vehicleForm.driverName,
        });
        setIsAddVehicleOpen(false);
        setVehicleForm({
            registrationNumber: "",
            model: "Tata Starbus 32S",
            capacity: 32,
            assignedRoute: "R-101",
            driverName: "Suresh Patil",
        });
        loadData();
    };

    const handleAddDriver = async (e) => {
        e.preventDefault();
        await driverService.create({
            vendorId: 1,
            vendorName: "City Transit Fleet Solutions",
            firstName: driverForm.firstName,
            lastName: driverForm.lastName,
            phone: driverForm.phone,
            licenseNumber: driverForm.licenseNumber,
            licenseExpiry: driverForm.licenseExpiry,
            assignedVehicle: driverForm.assignedVehicle,
        });
        setIsAddDriverOpen(false);
        setDriverForm({
            firstName: "",
            lastName: "",
            phone: "",
            licenseNumber: "",
            licenseExpiry: "",
            assignedVehicle: "KA-01-AB-1234",
        });
        loadData();
    };

    const handleCreateInvoice = async (e) => {
        e.preventDefault();
        await invoiceService.createInvoice({
            vendorId: 1,
            vendorName: "City Transit Fleet Solutions",
            studentName: invoiceForm.studentName,
            parentName: invoiceForm.parentName,
            amount: Number(invoiceForm.amount),
            billingPeriod: invoiceForm.billingPeriod,
            dueDate: invoiceForm.dueDate,
        });
        setIsCreateInvoiceOpen(false);
        loadData();
    };

    const expiringDocs = complianceDocs.filter(
        (d) => d.status === "EXPIRING_SOON" || d.status === "EXPIRED"
    );

    return (
        <DashboardLayout activeTab={activeTab} onTabChange={setActiveTab}>
            {/* Header */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
                <div>
                    <div className="flex items-center gap-2">
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-indigo-100 text-indigo-800">
                            City Transit Fleet Solutions
                        </span>
                    </div>
                    <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight mt-1">
                        Vendor Operations & Fleet Control
                    </h1>
                    <p className="text-sm text-slate-500">
                        Fleet compliance audits, driver license verifications, transfer dues settlements, and billing
                    </p>
                </div>

                <div className="flex items-center gap-2.5">
                    <button
                        onClick={() => setIsAddVehicleOpen(true)}
                        className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-slate-900 text-white font-semibold text-xs shadow-sm hover:bg-slate-800 transition"
                    >
                        <Plus className="w-4 h-4" />
                        Add Vehicle
                    </button>
                    <button
                        onClick={() => setIsCreateInvoiceOpen(true)}
                        className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 text-white font-semibold text-xs shadow-md shadow-blue-500/20 hover:bg-blue-700 transition"
                    >
                        <Receipt className="w-4 h-4" />
                        Generate Invoice
                    </button>
                </div>
            </div>

            {/* KPI Row */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                <MetricCard
                    title="Active Fleet"
                    value={vehicles.length}
                    subtitle="100% GPS Equipped"
                    icon={Truck}
                    variant="blue"
                />
                <MetricCard
                    title="Verified Drivers"
                    value={drivers.length}
                    subtitle="Police & DL Cleared"
                    icon={Users}
                    variant="indigo"
                />
                <MetricCard
                    title="Departure Notices"
                    value={transfers.filter((t) => t.duesStatus === "VENDOR_NOTIFIED" || t.duesStatus === "UNKNOWN").length}
                    subtitle="Audit Pending Dues"
                    icon={AlertTriangle}
                    variant="amber"
                />
                <MetricCard
                    title="Compliance Alerts"
                    value={expiringDocs.length}
                    subtitle="Licenses or Insurance Expiring"
                    icon={ShieldAlert}
                    variant={expiringDocs.length > 0 ? "rose" : "emerald"}
                />
            </div>

            {/* Tab: Overview */}
            {activeTab === "overview" && (
                <div className="space-y-8">
                    {/* Compliance Alert Box */}
                    {expiringDocs.length > 0 && (
                        <div className="p-5 rounded-2xl bg-rose-50/80 border border-rose-200/90 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
                            <div className="flex items-start gap-3">
                                <AlertTriangle className="w-5 h-5 text-rose-600 mt-0.5" />
                                <div>
                                    <h3 className="text-sm font-bold text-rose-900">
                                        RTO Compliance Action Required ({expiringDocs.length} documents)
                                    </h3>
                                    <p className="text-xs text-rose-800/90 mt-0.5">
                                        Fitness Certificate on Bus KA-01-CD-5678 and Driver License on file expire within 30 days. Renew immediately to prevent governance suspensions.
                                    </p>
                                </div>
                            </div>
                            <button
                                onClick={() => setActiveTab("compliance")}
                                className="px-3.5 py-2 rounded-xl bg-rose-600 text-white text-xs font-bold hover:bg-rose-700 transition shadow-sm whitespace-nowrap"
                            >
                                Inspect Documents
                            </button>
                        </div>
                    )}

                    {/* Transfers Requiring Action */}
                    <div>
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-base font-bold text-slate-900">Student Transfer Notifications</h2>
                            <button
                                onClick={() => setActiveTab("transfers")}
                                className="text-xs font-bold text-blue-600 hover:text-blue-700"
                            >
                                View All Notices ({transfers.length})
                            </button>
                        </div>
                        <div className="space-y-4">
                            {transfers.map((t) => (
                                <TransferTimeline
                                    key={t.id}
                                    transfer={t}
                                    onReportDues={(tr) => {
                                        setSelectedTransferForDues(tr);
                                        setDuesForm({
                                            amount: tr.duesAmount || "",
                                            notes: tr.duesDescription || "",
                                        });
                                    }}
                                    userRole="VENDOR_ADMIN"
                                />
                            ))}
                        </div>
                    </div>
                </div>
            )}

            {/* Tab: Vehicles */}
            {activeTab === "vehicles" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100 flex items-center justify-between">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">Fleet Vehicles & Live Telematics</h2>
                            <p className="text-xs text-slate-500">Buses, capacity, assigned routes, and maintenance records</p>
                        </div>
                        <button
                            onClick={() => setIsAddVehicleOpen(true)}
                            className="inline-flex items-center gap-2 px-3.5 py-2 rounded-xl bg-blue-600 text-white text-xs font-semibold shadow-sm hover:bg-blue-700 transition"
                        >
                            <Plus className="w-4 h-4" />
                            Add Vehicle
                        </button>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Reg Number & Model</th>
                                    <th className="py-3.5 px-5">Seating Capacity</th>
                                    <th className="py-3.5 px-5">Assigned Route</th>
                                    <th className="py-3.5 px-5">Driver</th>
                                    <th className="py-3.5 px-5">GPS Telematics</th>
                                    <th className="py-3.5 px-5">Status</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {vehicles.map((v) => (
                                    <tr key={v.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 px-5">
                                            <div className="font-bold text-slate-900 text-sm font-mono">{v.registrationNumber}</div>
                                            <div className="text-slate-500 text-xs">{v.model}</div>
                                        </td>
                                        <td className="py-4 px-5 font-bold text-slate-900">{v.capacity} Seats</td>
                                        <td className="py-4 px-5">{v.assignedRoute}</td>
                                        <td className="py-4 px-5 font-semibold text-slate-800">{v.driverName}</td>
                                        <td className="py-4 px-5">
                                            <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700">
                                                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
                                                {v.gpsStatus || "ONLINE"} • {v.speedKmph || 30} km/h
                                            </span>
                                        </td>
                                        <td className="py-4 px-5">
                                            <StatusBadge status={v.status} />
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Tab: Drivers */}
            {activeTab === "drivers" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100 flex items-center justify-between">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">Drivers Roster & Police Clearance</h2>
                            <p className="text-xs text-slate-500">Commercial Heavy Vehicle Driving Licenses and background records</p>
                        </div>
                        <button
                            onClick={() => setIsAddDriverOpen(true)}
                            className="inline-flex items-center gap-2 px-3.5 py-2 rounded-xl bg-slate-900 text-white text-xs font-semibold shadow-sm hover:bg-slate-800 transition"
                        >
                            <Plus className="w-4 h-4" />
                            Add Driver
                        </button>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Driver Name & Contact</th>
                                    <th className="py-3.5 px-5">License Number</th>
                                    <th className="py-3.5 px-5">DL Expiry</th>
                                    <th className="py-3.5 px-5">Assigned Bus</th>
                                    <th className="py-3.5 px-5">Police Verification</th>
                                    <th className="py-3.5 px-5">Status</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {drivers.map((d) => (
                                    <tr key={d.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 px-5">
                                            <div className="font-bold text-slate-900 text-sm">{d.firstName} {d.lastName}</div>
                                            <div className="text-slate-400 text-[11px]">{d.phone}</div>
                                        </td>
                                        <td className="py-4 px-5 font-mono text-slate-600 font-semibold">{d.licenseNumber}</td>
                                        <td className="py-4 px-5 font-bold text-slate-900">{d.licenseExpiry}</td>
                                        <td className="py-4 px-5 font-mono text-blue-600">{d.assignedVehicle}</td>
                                        <td className="py-4 px-5">
                                            <span className="px-2 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700">
                                                {d.policeVerification}
                                            </span>
                                        </td>
                                        <td className="py-4 px-5">
                                            <StatusBadge status={d.status} />
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Tab: Compliance */}
            {activeTab === "compliance" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100">
                        <h2 className="text-base font-bold text-slate-900">Regulatory Compliance Documents</h2>
                        <p className="text-xs text-slate-500">RTO certificates, Fitness test reports, and Insurance policies</p>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Entity Target</th>
                                    <th className="py-3.5 px-5">Document Type</th>
                                    <th className="py-3.5 px-5">Certificate/Policy No.</th>
                                    <th className="py-3.5 px-5">Expiry Date</th>
                                    <th className="py-3.5 px-5">RTO Verification</th>
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
                                            <span className="text-xs font-semibold text-emerald-600 flex items-center gap-1">
                                                <CheckCircle2 className="w-3.5 h-3.5" /> Verified
                                            </span>
                                        </td>
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

            {/* Tab: Transfers */}
            {activeTab === "transfers" && (
                <div className="space-y-6">
                    <div>
                        <h2 className="text-base font-bold text-slate-900">Transfer Notifications & Dues Audit</h2>
                        <p className="text-xs text-slate-500">
                            Schools notify you when students are transferring out of school transport. Audit your ledger and report any pending dues.
                        </p>
                    </div>

                    <div className="space-y-4">
                        {transfers.map((t) => (
                            <TransferTimeline
                                key={t.id}
                                transfer={t}
                                onReportDues={(tr) => {
                                    setSelectedTransferForDues(tr);
                                    setDuesForm({
                                        amount: tr.duesAmount || "",
                                        notes: tr.duesDescription || "",
                                    });
                                }}
                                userRole="VENDOR_ADMIN"
                            />
                        ))}
                    </div>
                </div>
            )}

            {/* Tab: Billing */}
            {activeTab === "billing" && (
                <div className="space-y-8">
                    {/* Invoices */}
                    <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                        <div className="p-5 border-b border-slate-100 flex items-center justify-between">
                            <div>
                                <h2 className="text-base font-bold text-slate-900">Transport Invoices Issued</h2>
                                <p className="text-xs text-slate-500">Monthly student transport fee receivables</p>
                            </div>
                            <button
                                onClick={() => setIsCreateInvoiceOpen(true)}
                                className="inline-flex items-center gap-2 px-3.5 py-2 rounded-xl bg-blue-600 text-white text-xs font-semibold shadow-sm hover:bg-blue-700 transition"
                            >
                                <Plus className="w-4 h-4" />
                                Generate Invoice
                            </button>
                        </div>

                        <div className="overflow-x-auto">
                            <table className="w-full text-left text-xs">
                                <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                    <tr>
                                        <th className="py-3.5 px-5">Invoice No.</th>
                                        <th className="py-3.5 px-5">Student & Guardian</th>
                                        <th className="py-3.5 px-5">Billing Period</th>
                                        <th className="py-3.5 px-5">Amount</th>
                                        <th className="py-3.5 px-5">Due Date</th>
                                        <th className="py-3.5 px-5">Status</th>
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

                    {/* Payments Reconciled */}
                    <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                        <div className="p-5 border-b border-slate-100">
                            <h2 className="text-base font-bold text-slate-900">Settled Payment Transactions</h2>
                            <p className="text-xs text-slate-500">Reconciled parent fees with transaction references</p>
                        </div>

                        <div className="overflow-x-auto">
                            <table className="w-full text-left text-xs">
                                <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                    <tr>
                                        <th className="py-3.5 px-5">Transaction Ref</th>
                                        <th className="py-3.5 px-5">Invoice Matched</th>
                                        <th className="py-3.5 px-5">Student</th>
                                        <th className="py-3.5 px-5">Settled Amount</th>
                                        <th className="py-3.5 px-5">Payment Method</th>
                                        <th className="py-3.5 px-5">Date</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                    {payments.map((p) => (
                                        <tr key={p.id} className="hover:bg-slate-50/60 transition">
                                            <td className="py-4 px-5 font-mono text-blue-600 font-bold">{p.transactionRef}</td>
                                            <td className="py-4 px-5 font-mono text-slate-600">{p.invoiceNumber}</td>
                                            <td className="py-4 px-5 font-bold text-slate-900">{p.studentName}</td>
                                            <td className="py-4 px-5 font-bold text-emerald-600">₹{p.amount.toLocaleString()}</td>
                                            <td className="py-4 px-5">{p.paymentMethod}</td>
                                            <td className="py-4 px-5 text-slate-500">{new Date(p.paymentDate).toLocaleDateString()}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal: Report Dues */}
            <Modal
                isOpen={!!selectedTransferForDues}
                onClose={() => setSelectedTransferForDues(null)}
                title={`Audit Dues for Transfer #${selectedTransferForDues?.id} (${selectedTransferForDues?.studentName})`}
            >
                <form onSubmit={handleReportDuesSubmit} className="space-y-4">
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                            Pending Ledger Dues Amount (₹)
                        </label>
                        <input
                            type="number"
                            required
                            min="0"
                            step="50"
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500 font-bold text-slate-900"
                            placeholder="e.g. 4200"
                            value={duesForm.amount}
                            onChange={(e) => setDuesForm({ ...duesForm, amount: e.target.value })}
                        />
                        <p className="text-[11px] text-slate-400 mt-1">
                            Enter 0 if the student has zero pending dues.
                        </p>
                    </div>
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                            Audit Notes / Ledger Breakdown
                        </label>
                        <textarea
                            required
                            rows={3}
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                            placeholder="e.g. Pending Q3 fuel surcharge and bus maintenance installment..."
                            value={duesForm.notes}
                            onChange={(e) => setDuesForm({ ...duesForm, notes: e.target.value })}
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full mt-4 py-2.5 px-4 rounded-xl font-bold text-xs text-white bg-orange-600 hover:bg-orange-700 shadow-sm transition"
                    >
                        Submit Dues & Update Transfer Status
                    </button>
                </form>
            </Modal>

            {/* Modal: Add Vehicle */}
            <Modal isOpen={isAddVehicleOpen} onClose={() => setIsAddVehicleOpen(false)} title="Register Vehicle to Fleet">
                <form onSubmit={handleAddVehicle} className="space-y-4">
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Registration Number</label>
                        <input
                            type="text"
                            required
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500 uppercase font-mono"
                            placeholder="KA-01-EF-9988"
                            value={vehicleForm.registrationNumber}
                            onChange={(e) => setVehicleForm({ ...vehicleForm, registrationNumber: e.target.value })}
                        />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Model</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={vehicleForm.model}
                                onChange={(e) => setVehicleForm({ ...vehicleForm, model: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Seating Capacity</label>
                            <input
                                type="number"
                                required
                                min="10"
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={vehicleForm.capacity}
                                onChange={(e) => setVehicleForm({ ...vehicleForm, capacity: e.target.value })}
                            />
                        </div>
                    </div>
                    <button
                        type="submit"
                        className="w-full mt-4 py-2.5 px-4 rounded-xl font-bold text-xs text-white bg-blue-600 hover:bg-blue-700 shadow-sm transition"
                    >
                        Register Bus & Activate GPS
                    </button>
                </form>
            </Modal>

            {/* Modal: Add Driver */}
            <Modal isOpen={isAddDriverOpen} onClose={() => setIsAddDriverOpen(false)} title="Register Driver to Fleet">
                <form onSubmit={handleAddDriver} className="space-y-4">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">First Name</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={driverForm.firstName}
                                onChange={(e) => setDriverForm({ ...driverForm, firstName: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Last Name</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={driverForm.lastName}
                                onChange={(e) => setDriverForm({ ...driverForm, lastName: e.target.value })}
                            />
                        </div>
                    </div>
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Phone Number</label>
                        <input
                            type="text"
                            required
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                            placeholder="+91 98000 11223"
                            value={driverForm.phone}
                            onChange={(e) => setDriverForm({ ...driverForm, phone: e.target.value })}
                        />
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Driving License (DL)</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500 font-mono"
                                placeholder="DL-KA01-2024001234"
                                value={driverForm.licenseNumber}
                                onChange={(e) => setDriverForm({ ...driverForm, licenseNumber: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">DL Expiry Date</label>
                            <input
                                type="date"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={driverForm.licenseExpiry}
                                onChange={(e) => setDriverForm({ ...driverForm, licenseExpiry: e.target.value })}
                            />
                        </div>
                    </div>
                    <button
                        type="submit"
                        className="w-full mt-4 py-2.5 px-4 rounded-xl font-bold text-xs text-white bg-slate-900 hover:bg-slate-800 shadow-sm transition"
                    >
                        Register Driver & Police Verification
                    </button>
                </form>
            </Modal>

            {/* Modal: Generate Invoice */}
            <Modal isOpen={isCreateInvoiceOpen} onClose={() => setIsCreateInvoiceOpen(false)} title="Generate Transport Fee Invoice">
                <form onSubmit={handleCreateInvoice} className="space-y-4">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Student Name</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={invoiceForm.studentName}
                                onChange={(e) => setInvoiceForm({ ...invoiceForm, studentName: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Guardian Name</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={invoiceForm.parentName}
                                onChange={(e) => setInvoiceForm({ ...invoiceForm, parentName: e.target.value })}
                            />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Billing Period</label>
                            <input
                                type="text"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                                value={invoiceForm.billingPeriod}
                                onChange={(e) => setInvoiceForm({ ...invoiceForm, billingPeriod: e.target.value })}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Fee Amount (₹)</label>
                            <input
                                type="number"
                                required
                                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500 font-bold"
                                value={invoiceForm.amount}
                                onChange={(e) => setInvoiceForm({ ...invoiceForm, amount: e.target.value })}
                            />
                        </div>
                    </div>
                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Payment Due Date</label>
                        <input
                            type="date"
                            required
                            className="w-full p-2.5 rounded-xl border border-slate-200 text-xs outline-none focus:border-blue-500"
                            value={invoiceForm.dueDate}
                            onChange={(e) => setInvoiceForm({ ...invoiceForm, dueDate: e.target.value })}
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full mt-4 py-2.5 px-4 rounded-xl font-bold text-xs text-white bg-blue-600 hover:bg-blue-700 shadow-sm transition"
                    >
                        Issue Invoice to Parent
                    </button>
                </form>
            </Modal>
        </DashboardLayout>
    );
}