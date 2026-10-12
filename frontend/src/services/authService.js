import axiosInstance from "./api/axiosInstance";

const authService = {
    async login(credentials) {
        const response = await axiosInstance.post("/auth/login", credentials);
        return response.data;
    },

    async register(userData) {
        const response = await axiosInstance.post("/auth/register", userData);
        return response.data;
    },
};

export default authService;