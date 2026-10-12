import axiosInstance from "./api/axiosInstance";
import { MOCK_VENDORS } from "../utils/mockData";

let localVendors = [...MOCK_VENDORS];

export const vendorService = {
    async getAll() {
        try {
            const res = await axiosInstance.get("/vendors");
            return res.data;
        } catch {
            return localVendors;
        }
    },

    async getById(id) {
        try {
            const res = await axiosInstance.get(`/vendors/${id}`);
            return res.data;
        } catch {
            return localVendors.find((v) => v.id === Number(id)) || localVendors[0];
        }
    },

    async create(vendorData) {
        try {
            const res = await axiosInstance.post("/vendors", vendorData);
            return res.data;
        } catch {
            const newVendor = {
                id: localVendors.length + 1,
                name: vendorData.name,
                registrationNumber: vendorData.registrationNumber || `REG-KA-2026-${Math.floor(1000 + Math.random() * 9000)}`,
                contactPerson: vendorData.contactPerson || "Operations Head",
                email: vendorData.email || "dispatch@vendor.com",
                phone: vendorData.phone || "+91 98000 11223",
                vehicleCount: 0,
                driverCount: 0,
                complianceStatus: "COMPLIANT",
                rating: 5.0,
            };
            localVendors = [newVendor, ...localVendors];
            return newVendor;
        }
    },
};

export default vendorService;
