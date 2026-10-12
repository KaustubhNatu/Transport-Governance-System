import axiosInstance from "./api/axiosInstance";
import { MOCK_TRANSFER_NOTIFICATIONS } from "../utils/mockData";

let localTransfers = [...MOCK_TRANSFER_NOTIFICATIONS];

export const transferService = {
    async getAll() {
        try {
            const res = await axiosInstance.get("/transfer-notifications");
            return res.data;
        } catch {
            return localTransfers;
        }
    },

    async getById(id) {
        try {
            const res = await axiosInstance.get(`/transfer-notifications/${id}`);
            return res.data;
        } catch {
            return localTransfers.find((t) => t.id === Number(id)) || localTransfers[0];
        }
    },

    async create(data) {
        try {
            const res = await axiosInstance.post("/transfer-notifications", {
                studentId: data.studentId,
            });
            return res.data;
        } catch {
            const now = new Date().toISOString();
            const newTransfer = {
                id: localTransfers.length + 101,
                studentId: data.studentId,
                studentName: data.studentName || "Student #" + data.studentId,
                grade: "Grade 8-A",
                schoolId: data.schoolId || 1,
                schoolName: "Greenwood High International",
                vendorId: data.vendorId || 1,
                vendorName: "City Transit Fleet Solutions",
                newVendorName: data.newVendorName || "Withdrawing / Transferring",
                newRouteCode: data.newRouteCode || "N/A",
                duesStatus: "UNKNOWN",
                duesAmount: 0.0,
                duesDescription: "Notification broadcast to transport vendor for account audit.",
                reason: data.reason || "Student transferring out of school.",
                createdAt: now,
                vendorNotifiedAt: now,
                parentNotifiedAt: now,
                completedAt: null,
                events: [
                    {
                        id: Date.now(),
                        eventType: "NOTIFICATION_CREATED",
                        notes: data.reason || "Transfer notification created and sent to transport vendor.",
                        performedBy: "School Administration",
                        createdAt: now,
                    },
                    {
                        id: Date.now() + 1,
                        eventType: "VENDOR_NOTIFIED",
                        notes: "Vendor dispatch alerted of upcoming student transfer.",
                        performedBy: "System",
                        createdAt: now,
                    },
                ],
            };
            localTransfers = [newTransfer, ...localTransfers];
            return newTransfer;
        }
    },

    async updateDues(id, duesData) {
        try {
            const res = await axiosInstance.put(`/transfer-notifications/${id}/dues`, {
                duesStatus: duesData.amount > 0 ? "DUES_REPORTED" : "CLEAR",
                duesAmount: Number(duesData.amount || 0),
                notes: duesData.notes || "",
            });
            return res.data;
        } catch {
            const index = localTransfers.findIndex((t) => t.id === Number(id));
            if (index !== -1) {
                const updated = {
                    ...localTransfers[index],
                    duesAmount: Number(duesData.amount || 0),
                    duesDescription: duesData.notes || "Dues audited by vendor.",
                    duesStatus: duesData.amount > 0 ? "DUES_REPORTED" : "CLEAR",
                    events: [
                        ...(localTransfers[index].events || []),
                        {
                            id: Date.now(),
                            eventType: "DUES_UPDATED",
                            notes: duesData.notes || `Vendor reported dues of ₹${duesData.amount || 0}.`,
                            performedBy: "Vendor Transport Dispatch",
                            createdAt: new Date().toISOString(),
                        },
                    ],
                };
                localTransfers[index] = updated;
                return updated;
            }
            return null;
        }
    },

    async completeTransfer(id) {
        try {
            await axiosInstance.put(`/transfer-notifications/${id}/complete`);
            return true;
        } catch {
            const index = localTransfers.findIndex((t) => t.id === Number(id));
            if (index !== -1) {
                const updated = {
                    ...localTransfers[index],
                    duesStatus: "NOTIFICATION_COMPLETED",
                    completedAt: new Date().toISOString(),
                    events: [
                        ...(localTransfers[index].events || []),
                        {
                            id: Date.now(),
                            eventType: "NOTIFICATION_COMPLETED",
                            notes: "Transfer notification completed and records archived.",
                            performedBy: "School Administration",
                            createdAt: new Date().toISOString(),
                        },
                    ],
                };
                localTransfers[index] = updated;
            }
            return true;
        }
    },

    async getEvents(id) {
        try {
            const res = await axiosInstance.get(`/transfer-notifications/${id}/events`);
            return res.data;
        } catch {
            const item = localTransfers.find((t) => t.id === Number(id));
            return item ? item.events : [];
        }
    },
};

export default transferService;
