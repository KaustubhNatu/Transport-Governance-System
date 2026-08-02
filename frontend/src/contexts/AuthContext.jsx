import { createContext, useContext, useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import authService from "../services/authService";

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);

    const login = async (email, password) => {
        const response = await authService.login({ email, password });

        const token = response.token;

        localStorage.setItem("token", token);

        const decoded = jwtDecode(token);

        setUser({
            email: decoded.sub,
            role: decoded.role[0].name,
            token,
        });
    };

    const logout = () => {
        localStorage.removeItem("token");
        setUser(null);
    };

    useEffect(() => {
        const token = localStorage.getItem("token");

        if (!token) return;

        try {
            const decoded = jwtDecode(token);

            setUser({
                email: decoded.sub,
                role: decoded.role[0].name,
                token,
            });
        } catch (error) {
            localStorage.removeItem("token");
            setUser(null);
        }
    }, []);

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}