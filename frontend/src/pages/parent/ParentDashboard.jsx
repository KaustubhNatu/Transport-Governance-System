import { useState, useEffect } from "react";
import DashboardLayout from "../../layouts/DashboardLayout";
import MetricCard from "../../components/common/MetricCard";
import StatusBadge from "../../components/common/StatusBadge";
import TransferTimeline from "../../components/common/TransferTimeline";
import Modal from "../../components/common/Modal";
import {
    Baby,
    CreditCard,
    ArrowLeftRight,
    Phone,
    CheckCircle2,
    Navigation,
} from "lucide-react";
import parentService from "../../services/parentService";
import invoiceService from "../../services/invoiceService";
import transferService from "../../services/transferService";

export default function ParentDashboard() {
    const [activeTab, setActiveTab] = useState("children");
    const [children, setChildren] = useState([]);
    const [invoices, setInvoices] = useState([]);
    const [transfers, setTransfers] = useState([]);

    // Pay Invoice Modal
    const [selectedInvoiceForPayment, setSelectedInvoiceForPayment] = useState(null);
    const [paymentMethod, setPaymentMethod] = useState("UPI_PHONEPE");
    const [isPaying, setIsPaying] = useState(false);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        const kids = parentService.getChildren(5);
        const invs = await invoiceService.getAllInvoices();
        const xfers = await transferService.getAll();

        setChildren(kids);
        setInvoices(invs.filter((i) => i.parentId === 5));
        setTransfers(xfers.filter((t) => t.studentId === 1)); // Aarav Sharma's transfer
    };

    const handlePaySubmit = async (e) => {
        e.preventDefault();
        if (!selectedInvoiceForPayment) return;

        setIsPaying(true);
        await invoiceService.recordPayment({
            invoiceId: selectedInvoiceForPayment.id,
            amount: selectedInvoiceForPayment.amount,
            paymentMethod,
            studentName: selectedInvoiceForPayment.studentName,
        });
        setIsPaying(false);
        setSelectedInvoiceForPayment(null);
        loadData();
    };

    const pendingInvoices = invoices.filter((i) => i.status === "PENDING" || i.status === "OVERDUE");
    const totalPendingDues = pendingInvoices.reduce((sum, i) => sum + i.amount, 0);

    return (
        <DashboardLayout activeTab={activeTab} onTabChange={setActiveTab}>
            {/* Header */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
                <div>
                    <div className="flex items-center gap-2">
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-purple-100 text-purple-800">
                            Registered Parent: Priya Sharma
                        </span>
                    </div>
                    <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight mt-1">
                        Parent Transport Portal
                    </h1>
                    <p className="text-sm text-slate-500">
                        Daily school commute visibility, fee invoice settlement, and transport transfer requests
                    </p>
                </div>

                <div className="flex items-center gap-2">
                    <span className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-emerald-50 text-emerald-700 text-xs font-bold border border-emerald-200">
                        <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                        Campus Buses On Route
                    </span>
                </div>
            </div>

            {/* KPI Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                <MetricCard
                    title="Enrolled Children"
                    value={children.length}
                    subtitle="Greenwood High International"
                    icon={Baby}
                    variant="purple"
                />
                <MetricCard
                    title="Designated Bus & Route"
                    value="R-102"
                    subtitle="Bus KA-01-CD-5678"
                    icon={Navigation}
                    variant="blue"
                />
                <MetricCard
                    title="Active Transfer"
                    value={transfers.length > 0 ? transfers[0].duesStatus : "NONE"}
                    subtitle={transfers.length > 0 ? "Aarav Sharma" : "All Enrolled"}
                    icon={ArrowLeftRight}
                    variant="amber"
                />
                <MetricCard
                    title="Outstanding Dues"
                    value={`₹${totalPendingDues.toLocaleString()}`}
                    subtitle={`${pendingInvoices.length} Unsettled Invoices`}
                    icon={CreditCard}
                    variant={totalPendingDues > 0 ? "rose" : "emerald"}
                />
            </div>

            {/* Tab: My Children */}
            {activeTab === "children" && (
                <div className="space-y-6">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        {children.map((child) => (
                            <div
                                key={child.id}
                                className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-sm hover:shadow-md transition"
                            >
                                <div className="flex items-start justify-between">
                                    <div>
                                        <div className="flex items-center gap-2">
                                            <h3 className="text-lg font-bold text-slate-900">
                                                {child.firstName} {child.lastName}
                                            </h3>
                                            <StatusBadge status={child.status} />
                                        </div>
                                        <p className="text-xs text-slate-500 mt-0.5">
                                            {child.grade} • Roll No: {child.rollNumber}
                                        </p>
                                    </div>
                                    <div className="w-10 h-10 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center font-bold text-sm">
                                        {child.firstName[0]}
                                    </div>
                                </div>

                                <div className="mt-6 pt-4 border-t border-slate-100 space-y-3 text-xs">
                                    <div className="flex items-center justify-between">
                                        <span className="text-slate-400">Assigned Route</span>
                                        <span className="font-bold text-blue-600">{child.routeCode} (Indiranagar - Whitefield)</span>
                                    </div>
                                    <div className="flex items-center justify-between">
                                        <span className="text-slate-400">Pickup & Drop Stop</span>
                                        <span className="font-semibold text-slate-800">{child.pickupStop}</span>
                                    </div>
                                    <div className="flex items-center justify-between">
                                        <span className="text-slate-400">Transport Vendor</span>
                                        <span className="font-semibold text-slate-800">{child.vendorName}</span>
                                    </div>
                                    <div className="flex items-center justify-between pt-2 border-t border-slate-50">
                                        <span className="text-slate-400">Driver & Contact</span>
                                        <div className="flex items-center gap-2 font-bold text-slate-900">
                                            Rajesh Kumar
                                            <a
                                                href="tel:+919123456789"
                                                className="p-1 rounded-lg bg-blue-50 text-blue-600 hover:bg-blue-100 transition"
                                                title="Call Driver"
                                            >
                                                <Phone className="w-3.5 h-3.5" />
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Tab: Transfer Requests */}
            {activeTab === "transfers" && (
                <div className="space-y-6">
                    <div>
                        <h2 className="text-base font-bold text-slate-900">Student Transfer Notification</h2>
                        <p className="text-xs text-slate-500">
                            Status of transfer notice sent to your child's transport vendor for final account reconciliation
                        </p>
                    </div>

                    <div className="space-y-4">
                        {transfers.map((t) => (
                            <TransferTimeline key={t.id} transfer={t} userRole="PARENT" />
                        ))}
                    </div>
                </div>
            )}

            {/* Tab: Fee Invoices & Pay */}
            {activeTab === "invoices" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
                    <div className="p-5 border-b border-slate-100 flex items-center justify-between">
                        <div>
                            <h2 className="text-base font-bold text-slate-900">Transport Fee Invoices</h2>
                            <p className="text-xs text-slate-500">Monthly billing receipts and online dues settlement</p>
                        </div>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-xs">
                            <thead className="bg-slate-50/75 border-b border-slate-100 text-slate-500 font-bold uppercase tracking-wider">
                                <tr>
                                    <th className="py-3.5 px-5">Invoice No.</th>
                                    <th className="py-3.5 px-5">Student</th>
                                    <th className="py-3.5 px-5">Billing Period</th>
                                    <th className="py-3.5 px-5">Amount</th>
                                    <th className="py-3.5 px-5">Due Date</th>
                                    <th className="py-3.5 px-5">Status</th>
                                    <th className="py-3.5 px-5 text-right">Action</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-700">
                                {invoices.map((inv) => (
                                    <tr key={inv.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 px-5 font-mono font-bold text-slate-900">{inv.invoiceNumber}</td>
                                        <td className="py-4 px-5 font-bold text-slate-900">{inv.studentName}</td>
                                        <td className="py-4 px-5">{inv.billingPeriod}</td>
                                        <td className="py-4 px-5 font-bold text-slate-900">₹{inv.amount.toLocaleString()}</td>
                                        <td className="py-4 px-5 text-slate-500">{inv.dueDate}</td>
                                        <td className="py-4 px-5">
                                            <StatusBadge status={inv.status} />
                                        </td>
                                        <td className="py-4 px-5 text-right">
                                            {inv.status === "PAID" ? (
                                                <span className="text-emerald-600 font-bold text-xs inline-flex items-center gap-1">
                                                    <CheckCircle2 className="w-3.5 h-3.5" /> Settled
                                                </span>
                                            ) : (
                                                <button
                                                    onClick={() => setSelectedInvoiceForPayment(inv)}
                                                    className="px-3.5 py-1.5 rounded-xl bg-blue-600 text-white font-bold text-xs hover:bg-blue-700 shadow-sm transition"
                                                >
                                                    Pay ₹{inv.amount.toLocaleString()}
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Tab: Safety & Emergency */}
            {activeTab === "support" && (
                <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 space-y-6">
                    <div>
                        <h2 className="text-base font-bold text-slate-900">Emergency & Dispatch Contacts</h2>
                        <p className="text-xs text-slate-500">24/7 School transport helpdesk and supervisor escalations</p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div className="p-5 rounded-2xl bg-blue-50/70 border border-blue-200/80">
                            <h3 className="font-bold text-slate-900 text-sm">School Transport Dean</h3>
                            <p className="text-xs text-slate-600 mt-1">Greenwood High Operations</p>
                            <a
                                href="tel:+918049108888"
                                className="mt-4 inline-flex items-center gap-2 text-xs font-bold text-blue-600 hover:underline"
                            >
                                <Phone className="w-3.5 h-3.5" /> +91 80 4910 8888
                            </a>
                        </div>

                        <div className="p-5 rounded-2xl bg-indigo-50/70 border border-indigo-200/80">
                            <h3 className="font-bold text-slate-900 text-sm">Vendor Fleet Dispatch</h3>
                            <p className="text-xs text-slate-600 mt-1">City Transit Operations Center</p>
                            <a
                                href="tel:+919411233445"
                                className="mt-4 inline-flex items-center gap-2 text-xs font-bold text-indigo-600 hover:underline"
                            >
                                <Phone className="w-3.5 h-3.5" /> +91 94112 33445
                            </a>
                        </div>

                        <div className="p-5 rounded-2xl bg-rose-50/70 border border-rose-200/80">
                            <h3 className="font-bold text-slate-900 text-sm">RTO Emergency Safety Helpline</h3>
                            <p className="text-xs text-slate-600 mt-1">State Transport Safety Cell</p>
                            <a
                                href="tel:112"
                                className="mt-4 inline-flex items-center gap-2 text-xs font-bold text-rose-600 hover:underline"
                            >
                                <Phone className="w-3.5 h-3.5" /> Dial 112 (Emergency)
                            </a>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal: Pay Invoice */}
            <Modal
                isOpen={!!selectedInvoiceForPayment}
                onClose={() => setSelectedInvoiceForPayment(null)}
                title={`Settle Invoice ${selectedInvoiceForPayment?.invoiceNumber}`}
            >
                <form onSubmit={handlePaySubmit} className="space-y-4">
                    <div className="p-4 rounded-xl bg-slate-50 border border-slate-200/80 space-y-1 text-xs">
                        <div className="flex justify-between">
                            <span className="text-slate-500">Student:</span>
                            <span className="font-bold text-slate-900">{selectedInvoiceForPayment?.studentName}</span>
                        </div>
                        <div className="flex justify-between">
                            <span className="text-slate-500">Billing Period:</span>
                            <span className="font-semibold text-slate-800">{selectedInvoiceForPayment?.billingPeriod}</span>
                        </div>
                        <div className="flex justify-between pt-2 border-t border-slate-200">
                            <span className="text-slate-700 font-bold text-sm">Total Due:</span>
                            <span className="font-extrabold text-blue-600 text-base">
                                ₹{selectedInvoiceForPayment?.amount?.toLocaleString()}
                            </span>
                        </div>
                    </div>

                    <div>
                        <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                            Select Payment Method
                        </label>
                        <div className="space-y-2 text-xs">
                            {[
                                { id: "UPI_PHONEPE", name: "Instant UPI (PhonePe / Google Pay / Paytm)" },
                                { id: "NET_BANKING_HDFC", name: "Net Banking (All Indian Banks)" },
                                { id: "DEBIT_CARD", name: "Debit / Credit Card (Visa, Mastercard, RuPay)" },
                            ].map((method) => (
                                <label
                                    key={method.id}
                                    className={`flex items-center gap-3 p-3 rounded-xl border cursor-pointer transition ${
                                        paymentMethod === method.id
                                            ? "border-blue-600 bg-blue-50/50 font-bold text-blue-900"
                                            : "border-slate-200 text-slate-700 hover:bg-slate-50"
                                    }`}
                                >
                                    <input
                                        type="radio"
                                        name="paymentMethod"
                                        value={method.id}
                                        checked={paymentMethod === method.id}
                                        onChange={(e) => setPaymentMethod(e.target.value)}
                                        className="text-blue-600"
                                    />
                                    <span>{method.name}</span>
                                </label>
                            ))}
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={isPaying}
                        className="w-full mt-4 py-3 px-4 rounded-xl font-bold text-xs text-white bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-600/30 transition disabled:opacity-50"
                    >
                        {isPaying ? "Processing Payment..." : `Authorize Payment of ₹${selectedInvoiceForPayment?.amount?.toLocaleString()}`}
                    </button>
                </form>
            </Modal>
        </DashboardLayout>
    );
}