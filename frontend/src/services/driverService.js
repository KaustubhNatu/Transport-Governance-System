import axiosInstance from "./api/axiosInstance";
import { MOCK_DRIVERS } from "../utils/mockData";

let localDrivers = [...MOCK_DRIVERS];

export const driverService = {
    async getAll() {
        try {
            const res = await axiosInstance.get("/drivers");
            return res.data;
        } catch {
            return localDrivers;
        }
    },

    async getById(id) {
        try {
            const res = await axiosInstance.get(`/drivers/${id}`);
            return res.data;
        } catch {
            return localDrivers.find((d) => d.id === Number(id)) || localDrivers[0];
        }
    },

    async create(data) {
        try {
            const res = await axiosInstance.post("/drivers", data);
            return res.data;
        } catch {
            const newDriver = {
                id: localDrivers.length + 1,
                ...data,
                policeVerification: "VERIFIED",
                status: "ACTIVE",
            };
            localDrivers = [newDriver, ...localDrivers];
            return newDriver;
        }
    },
};

export default driverService;
