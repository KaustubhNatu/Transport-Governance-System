import axiosInstance from "./api/axiosInstance";
import { MOCK_COMPLIANCE_DOCS } from "../utils/mockData";

let localDocs = [...MOCK_COMPLIANCE_DOCS];

export const complianceService = {
    async getVehicleDocuments() {
        try {
            const res = await axiosInstance.get("/vehicle-documents");
            return res.data;
        } catch {
            return localDocs.filter((d) => d.type === "VEHICLE");
        }
    },

    async getDriverDocuments() {
        try {
            const res = await axiosInstance.get("/driver-documents");
            return res.data;
        } catch {
            return localDocs.filter((d) => d.type === "DRIVER");
        }
    },

    async getAllDocuments() {
        try {
            const [vRes, dRes] = await Promise.all([
                axiosInstance.get("/vehicle-documents"),
                axiosInstance.get("/driver-documents"),
            ]);
            return [...vRes.data, ...dRes.data];
        } catch {
            return localDocs;
        }
    },

    async createVehicleDoc(data) {
        try {
            const res = await axiosInstance.post("/vehicle-documents", data);
            return res.data;
        } catch {
            const newDoc = {
                id: localDocs.length + 1,
                type: "VEHICLE",
                ...data,
                status: "VALID",
                verifiedByRTO: true,
            };
            localDocs = [newDoc, ...localDocs];
            return newDoc;
        }
    },

    async createDriverDoc(data) {
        try {
            const res = await axiosInstance.post("/driver-documents", data);
            return res.data;
        } catch {
            const newDoc = {
                id: localDocs.length + 1,
                type: "DRIVER",
                ...data,
                status: "VALID",
                verifiedByRTO: true,
            };
            localDocs = [newDoc, ...localDocs];
            return newDoc;
        }
    },
};

export default complianceService;
