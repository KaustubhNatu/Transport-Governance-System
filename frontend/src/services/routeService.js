import axiosInstance from "./api/axiosInstance";
import { MOCK_ROUTES } from "../utils/mockData";

let localRoutes = [...MOCK_ROUTES];

export const routeService = {
    async getAll() {
        try {
            const res = await axiosInstance.get("/routes");
            return res.data;
        } catch {
            return localRoutes;
        }
    },

    async getById(id) {
        try {
            const res = await axiosInstance.get(`/routes/${id}`);
            return res.data;
        } catch {
            return localRoutes.find((r) => r.id === Number(id)) || localRoutes[0];
        }
    },

    async create(data) {
        try {
            const res = await axiosInstance.post("/routes", data);
            return res.data;
        } catch {
            const newRoute = {
                id: localRoutes.length + 1,
                ...data,
                status: "ACTIVE",
            };
            localRoutes = [newRoute, ...localRoutes];
            return newRoute;
        }
    },
};

export default routeService;
