export default function StatusBadge({ status }) {
    if (!status) return null;

    const normalized = String(status).toUpperCase();

    const getBadgeStyle = () => {
        switch (normalized) {
            case "ACTIVE":
            case "VALID":
            case "PAID":
            case "COMPLIANT":
            case "SUCCESS":
            case "ONLINE":
            case "TRANSFER_COMPLETED":
            case "VERIFIED":
                return "bg-emerald-50 text-emerald-700 border-emerald-200 ring-emerald-500/20";

            case "PENDING":
            case "TRANSFER_INITIATED":
            case "VENDOR_NOTIFIED":
            case "TRANSFER_PENDING":
                return "bg-amber-50 text-amber-700 border-amber-200 ring-amber-500/20";

            case "EXPIRING_SOON":
            case "DUES_REPORTED":
            case "WARNING":
            case "NEEDS_ATTENTION":
                return "bg-orange-50 text-orange-700 border-orange-200 ring-orange-500/20";

            case "EXPIRED":
            case "OVERDUE":
            case "FAILED":
            case "OFFLINE":
            case "IN_MAINTENANCE":
                return "bg-rose-50 text-rose-700 border-rose-200 ring-rose-500/20";

            default:
                return "bg-slate-50 text-slate-700 border-slate-200 ring-slate-500/20";
        }
    };

    const formatText = () => {
        return normalized.replace(/_/g, " ");
    };

    return (
        <span
            className={`inline-flex items-center gap-1.5 px-2.5 py-1 text-xs font-semibold rounded-full border ring-1 ${getBadgeStyle()}`}
        >
            <span className="w-1.5 h-1.5 rounded-full bg-current opacity-75"></span>
            {formatText()}
        </span>
    );
}
