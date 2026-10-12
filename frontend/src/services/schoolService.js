import axiosInstance from "./api/axiosInstance";
import { MOCK_SCHOOLS } from "../utils/mockData";

let localSchools = [...MOCK_SCHOOLS];

export const schoolService = {
    async getAll() {
        try {
            const res = await axiosInstance.get("/schools");
            return res.data;
        } catch {
            return localSchools;
        }
    },

    async getById(id) {
        try {
            const res = await axiosInstance.get(`/schools/${id}`);
            return res.data;
        } catch {
            return localSchools.find((s) => s.id === Number(id)) || localSchools[0];
        }
    },

    async create(data) {
        try {
            const res = await axiosInstance.post("/schools", data);
            return res.data;
        } catch {
            const newSchool = {
                id: localSchools.length + 1,
                name: data.name,
                address: data.address,
                contactEmail: data.contactEmail,
                contactPhone: data.contactPhone,
                enrolledStudents: 0,
                activeBuses: 0,
                activeVendors: 1,
                complianceScore: 100,
                createdAt: new Date().toISOString(),
            };
            localSchools = [newSchool, ...localSchools];
            return newSchool;
        }
    },
};

export default schoolService;
