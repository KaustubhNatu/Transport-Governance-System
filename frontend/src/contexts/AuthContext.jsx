import { createContext, useContext, useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import authService from "../services/authService";
import { DEMO_USERS } from "../utils/mockData";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [isDemoMode, setIsDemoMode] = useState(() => {
        return localStorage.getItem("tgs_demo_mode") === "true";
    });
    const [loading, setLoading] = useState(true);

    const toggleDemoMode = (enabled) => {
        const val = typeof enabled === "boolean" ? enabled : !isDemoMode;
        setIsDemoMode(val);
        localStorage.setItem("tgs_demo_mode", String(val));
    };

    const extractRoleFromToken = (decoded) => {
        if (!decoded) return "PARENT";
        if (typeof decoded.role === "string") return decoded.role;
        if (Array.isArray(decoded.role) && decoded.role.length > 0) {
            const first = decoded.role[0];
            return typeof first === "object" ? first.name : first;
        }
        if (decoded.roles && Array.isArray(decoded.roles)) {
            return decoded.roles[0];
        }
        return "PARENT";
    };

    const login = async (email, password) => {
        // Check if demo user match first or if forced demo mode
        const demoUser = DEMO_USERS.find(
            (u) => u.email.toLowerCase() === email.toLowerCase().trim()
        );

        if (isDemoMode && demoUser) {
            const loggedInUser = {
                email: demoUser.email,
                role: demoUser.role,
                firstName: demoUser.firstName,
                lastName: demoUser.lastName,
                schoolId: demoUser.schoolId,
                vendorId: demoUser.vendorId,
                title: demoUser.title,
                token: "demo-jwt-token-" + demoUser.role.toLowerCase(),
                isDemo: true,
            };
            localStorage.setItem("token", loggedInUser.token);
            localStorage.setItem("tgs_current_user", JSON.stringify(loggedInUser));
            setUser(loggedInUser);
            return loggedInUser;
        }

        try {
            // Attempt live backend login
            const response = await authService.login({ email, password });
            const token = response.token;
            localStorage.setItem("token", token);

            const decoded = jwtDecode(token);
            const role = extractRoleFromToken(decoded);

            const loggedInUser = {
                email: decoded.sub || email,
                role: role.replace("ROLE_", ""),
                token,
                isDemo: false,
            };

            localStorage.setItem("tgs_current_user", JSON.stringify(loggedInUser));
            setUser(loggedInUser);
            return loggedInUser;
        } catch (apiError) {
            // Graceful fallback to demo mode if backend is offline and credentials match demo accounts
            if (demoUser && (!password || password === "Password123!" || password === demoUser.password)) {
                console.warn("Backend offline or failed. Falling back to Demo Mode for user:", demoUser.email);
                const loggedInUser = {
                    email: demoUser.email,
                    role: demoUser.role,
                    firstName: demoUser.firstName,
                    lastName: demoUser.lastName,
                    schoolId: demoUser.schoolId,
                    vendorId: demoUser.vendorId,
                    title: demoUser.title,
                    token: "demo-jwt-token-" + demoUser.role.toLowerCase(),
                    isDemo: true,
                };
                localStorage.setItem("token", loggedInUser.token);
                localStorage.setItem("tgs_current_user", JSON.stringify(loggedInUser));
                localStorage.setItem("tgs_demo_mode", "true");
                setIsDemoMode(true);
                setUser(loggedInUser);
                return loggedInUser;
            }
            throw apiError;
        }
    };

    const loginAsDemoRole = (roleName) => {
        const demoUser = DEMO_USERS.find((u) => u.role === roleName) || DEMO_USERS[0];
        const loggedInUser = {
            email: demoUser.email,
            role: demoUser.role,
            firstName: demoUser.firstName,
            lastName: demoUser.lastName,
            schoolId: demoUser.schoolId,
            vendorId: demoUser.vendorId,
            title: demoUser.title,
            token: "demo-jwt-token-" + demoUser.role.toLowerCase(),
            isDemo: true,
        };
        localStorage.setItem("token", loggedInUser.token);
        localStorage.setItem("tgs_current_user", JSON.stringify(loggedInUser));
        localStorage.setItem("tgs_demo_mode", "true");
        setIsDemoMode(true);
        setUser(loggedInUser);
        return loggedInUser;
    };

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("tgs_current_user");
        setUser(null);
    };

    useEffect(() => {
        const token = localStorage.getItem("token");
        const cachedUser = localStorage.getItem("tgs_current_user");

        if (!token) {
            setUser(null);
            setLoading(false);
            return;
        }

        if (token.startsWith("demo-jwt-token-") && cachedUser) {
            try {
                setUser(JSON.parse(cachedUser));
            } catch {
                setUser(null);
            }
            setLoading(false);
            return;
        }

        try {
            const decoded = jwtDecode(token);
            const role = extractRoleFromToken(decoded);
            const savedData = cachedUser ? JSON.parse(cachedUser) : {};

            setUser({
                email: decoded.sub,
                role: role.replace("ROLE_", ""),
                token,
                ...savedData,
            });
        } catch {
            localStorage.removeItem("token");
            localStorage.removeItem("tgs_current_user");
            setUser(null);
        } finally {
            setLoading(false);
        }
    }, []);

    return (
        <AuthContext.Provider
            value={{
                user,
                login,
                loginAsDemoRole,
                logout,
                isDemoMode,
                toggleDemoMode,
                loading,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}