import axiosInstance from "./api/axiosInstance";
import { MOCK_VEHICLES } from "../utils/mockData";

let localVehicles = [...MOCK_VEHICLES];

export const vehicleService = {
    async getAll() {
        try {
            const res = await axiosInstance.get("/vehicles");
            return res.data;
        } catch {
            return localVehicles;
        }
    },

    async getById(id) {
        try {
            const res = await axiosInstance.get(`/vehicles/${id}`);
            return res.data;
        } catch {
            return localVehicles.find((v) => v.id === Number(id)) || localVehicles[0];
        }
    },

    async create(data) {
        try {
            const res = await axiosInstance.post("/vehicles", data);
            return res.data;
        } catch {
            const newVehicle = {
                id: localVehicles.length + 1,
                ...data,
                status: "ACTIVE",
                gpsStatus: "ONLINE",
            };
            localVehicles = [newVehicle, ...localVehicles];
            return newVehicle;
        }
    },
};

export default vehicleService;
