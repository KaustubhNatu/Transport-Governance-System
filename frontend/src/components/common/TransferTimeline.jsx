import { CheckCircle2, Clock, AlertTriangle, ArrowRight, ShieldCheck, Bell } from "lucide-react";
import StatusBadge from "./StatusBadge";

export default function TransferTimeline({
    transfer,
    onReportDues,
    onCompleteTransfer,
    userRole,
}) {
    if (!transfer) return null;

    const stages = [
        {
            key: "NOTIFICATION_CREATED",
            label: "Notification Sent",
            sub: "School alerted vendor",
            icon: Bell,
        },
        {
            key: "VENDOR_NOTIFIED",
            label: "Vendor Acknowledged",
            sub: "Dispatch notified",
            icon: Clock,
        },
        {
            key: "DUES_REPORTED",
            label: "Dues Audited",
            sub: transfer.duesAmount > 0 ? `₹${transfer.duesAmount.toLocaleString()} Reported` : "Zero Dues",
            icon: AlertTriangle,
        },
        {
            key: "NOTIFICATION_COMPLETED",
            label: "Completed",
            sub: "Archived & Settled",
            icon: ShieldCheck,
        },
    ];

    const getStageIndex = (status) => {
        switch (status) {
            case "NOTIFICATION_CREATED":
            case "TRANSFER_INITIATED":
                return 0;
            case "VENDOR_NOTIFIED":
                return 1;
            case "DUES_REPORTED":
            case "DUES_UPDATED":
                return 2;
            case "NOTIFICATION_COMPLETED":
            case "TRANSFER_COMPLETED":
                return 3;
            default:
                return 0;
        }
    };

    const currentStageIndex = getStageIndex(transfer.duesStatus);
    const isCompleted = currentStageIndex === 3;

    return (
        <div className="bg-white rounded-2xl border border-slate-200/80 p-6 shadow-sm">
            {/* Header */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-100">
                <div>
                    <div className="flex items-center gap-3">
                        <h3 className="text-lg font-bold text-slate-900">
                            Transfer Notification #{transfer.id}: {transfer.studentName}
                        </h3>
                        <StatusBadge status={transfer.duesStatus} />
                    </div>
                    <p className="text-sm text-slate-500 mt-1">
                        {transfer.grade} • Current Vendor: <span className="font-semibold text-slate-700">{transfer.vendorName}</span>{" "}
                        <ArrowRight className="inline w-3.5 h-3.5 text-slate-400 mx-1" /> Destination:{" "}
                        <span className="font-semibold text-blue-600">{transfer.newVendorName || "Withdrawing / Transferring"}</span>
                    </p>
                </div>

                {/* Actions depending on Role */}
                <div className="flex items-center gap-2">
                    {userRole === "VENDOR_ADMIN" && !isCompleted && (
                        <button
                            onClick={() => onReportDues && onReportDues(transfer)}
                            className="inline-flex items-center gap-2 px-3.5 py-2 text-xs font-semibold rounded-xl bg-orange-600 text-white hover:bg-orange-700 shadow-sm transition"
                        >
                            <AlertTriangle className="w-3.5 h-3.5" />
                            Report / Update Dues
                        </button>
                    )}

                    {userRole === "SCHOOL_ADMIN" && !isCompleted && (
                        <button
                            onClick={() => onCompleteTransfer && onCompleteTransfer(transfer.id)}
                            className="inline-flex items-center gap-2 px-3.5 py-2 text-xs font-semibold rounded-xl bg-slate-900 text-white hover:bg-slate-800 shadow-sm transition"
                        >
                            <CheckCircle2 className="w-3.5 h-3.5" />
                            Close Notification
                        </button>
                    )}
                </div>
            </div>

            {/* Stepper Progress */}
            <div className="py-8">
                <div className="relative flex items-center justify-between max-w-2xl mx-auto">
                    {/* Connecting Line */}
                    <div className="absolute left-6 right-6 top-1/2 -translate-y-1/2 h-1 bg-slate-100 -z-0">
                        <div
                            className="h-full bg-blue-600 transition-all duration-500"
                            style={{
                                width: `${(currentStageIndex / (stages.length - 1)) * 100}%`,
                            }}
                        />
                    </div>

                    {stages.map((stage, idx) => {
                        const isDone = idx < currentStageIndex;
                        const isCurrent = idx === currentStageIndex;

                        return (
                            <div key={stage.key} className="relative z-10 flex flex-col items-center">
                                <div
                                    className={`w-11 h-11 rounded-2xl flex items-center justify-center font-bold text-sm transition-all shadow-sm ${
                                        isDone
                                            ? "bg-blue-600 text-white ring-4 ring-blue-50"
                                            : isCurrent
                                            ? "bg-blue-600 text-white ring-4 ring-blue-100 animate-pulse"
                                            : "bg-white text-slate-400 border-2 border-slate-200"
                                    }`}
                                >
                                    {isDone ? <CheckCircle2 className="w-5 h-5" /> : <stage.icon className="w-5 h-5" />}
                                </div>
                                <span
                                    className={`mt-2.5 text-xs font-semibold ${
                                        isCurrent ? "text-blue-600 font-bold" : isDone ? "text-slate-800" : "text-slate-400"
                                    }`}
                                >
                                    {stage.label}
                                </span>
                                <span className="text-[11px] text-slate-400 mt-0.5">{stage.sub}</span>
                            </div>
                        );
                    })}
                </div>
            </div>

            {/* Dues Summary Box if reported */}
            {transfer.duesAmount > 0 && (
                <div className="mb-6 p-4 rounded-xl bg-orange-50/70 border border-orange-200/80 flex items-start justify-between">
                    <div>
                        <div className="flex items-center gap-2">
                            <AlertTriangle className="w-4 h-4 text-orange-600" />
                            <span className="text-sm font-semibold text-orange-900">
                                Outstanding Vendor Dues Reported: ₹{transfer.duesAmount.toLocaleString()}
                            </span>
                        </div>
                        <p className="text-xs text-orange-800/80 mt-1 pl-6">
                            {transfer.duesDescription || "Vendor ledger audit indicates pending transport fees to be cleared by parent."}
                        </p>
                    </div>
                    <span className="text-xs font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-orange-200/80 text-orange-900">
                        {transfer.duesStatus}
                    </span>
                </div>
            )}

            {/* Event Audit Trail */}
            <div className="mt-4 pt-4 border-t border-slate-100">
                <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-3">
                    Notification & Reconciliation Audit Trail ({transfer.events?.length || 0} Events)
                </h4>
                <div className="space-y-3">
                    {transfer.events && transfer.events.length > 0 ? (
                        transfer.events.map((evt) => (
                            <div key={evt.id} className="flex items-start gap-3 text-xs bg-slate-50/80 p-3 rounded-xl border border-slate-100">
                                <div className="w-2 h-2 rounded-full bg-blue-500 mt-1.5 flex-shrink-0" />
                                <div className="flex-1">
                                    <div className="flex items-center justify-between">
                                        <span className="font-semibold text-slate-800">{evt.eventType}</span>
                                        <span className="text-slate-400 text-[11px]">
                                            {new Date(evt.createdAt).toLocaleString()}
                                        </span>
                                    </div>
                                    <p className="text-slate-600 mt-0.5">{evt.notes}</p>
                                    <span className="text-slate-400 text-[11px] mt-1 block">
                                        Action by: <span className="font-medium text-slate-700">{evt.performedBy}</span>
                                    </span>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p className="text-xs text-slate-400 italic">No events logged yet.</p>
                    )}
                </div>
            </div>
        </div>
    );
}
