export default function MetricCard({ title, value, subtitle, icon: Icon, trend, variant = "blue" }) {
    const variantStyles = {
        blue: {
            bg: "bg-blue-50 text-blue-600 border-blue-100",
            glow: "hover:border-blue-300",
        },
        emerald: {
            bg: "bg-emerald-50 text-emerald-600 border-emerald-100",
            glow: "hover:border-emerald-300",
        },
        amber: {
            bg: "bg-amber-50 text-amber-600 border-amber-100",
            glow: "hover:border-amber-300",
        },
        rose: {
            bg: "bg-rose-50 text-rose-600 border-rose-100",
            glow: "hover:border-rose-300",
        },
        purple: {
            bg: "bg-purple-50 text-purple-600 border-purple-100",
            glow: "hover:border-purple-300",
        },
        indigo: {
            bg: "bg-indigo-50 text-indigo-600 border-indigo-100",
            glow: "hover:border-indigo-300",
        },
    };

    const style = variantStyles[variant] || variantStyles.blue;

    return (
        <div className={`p-5 rounded-2xl bg-white border border-slate-200/80 shadow-sm transition-all duration-200 hover:shadow-md ${style.glow}`}>
            <div className="flex items-center justify-between">
                <span className="text-sm font-medium text-slate-500">{title}</span>
                {Icon && (
                    <div className={`p-2.5 rounded-xl border ${style.bg}`}>
                        <Icon className="w-5 h-5" />
                    </div>
                )}
            </div>

            <div className="mt-4 flex items-baseline justify-between">
                <span className="text-2xl font-bold tracking-tight text-slate-900">{value}</span>
                {trend && (
                    <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${trend.positive ? "bg-emerald-50 text-emerald-600" : "bg-rose-50 text-rose-600"}`}>
                        {trend.text}
                    </span>
                )}
            </div>

            {subtitle && (
                <p className="mt-1 text-xs text-slate-400 font-normal">{subtitle}</p>
            )}
        </div>
    );
}
