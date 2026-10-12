import axiosInstance from "./api/axiosInstance";
import { MOCK_INVOICES, MOCK_PAYMENTS } from "../utils/mockData";

let localInvoices = [...MOCK_INVOICES];
let localPayments = [...MOCK_PAYMENTS];

export const invoiceService = {
    async getAllInvoices() {
        try {
            const res = await axiosInstance.get("/invoices");
            return res.data;
        } catch {
            return localInvoices;
        }
    },

    async getInvoiceById(id) {
        try {
            const res = await axiosInstance.get(`/invoices/${id}`);
            return res.data;
        } catch {
            return localInvoices.find((i) => i.id === Number(id)) || localInvoices[0];
        }
    },

    async createInvoice(data) {
        try {
            const res = await axiosInstance.post("/invoices", data);
            return res.data;
        } catch {
            const newInvoice = {
                id: localInvoices.length + 1000,
                invoiceNumber: `INV-2026-${Math.floor(1000 + Math.random() * 9000)}`,
                ...data,
                paidAmount: 0.0,
                status: "PENDING",
            };
            localInvoices = [newInvoice, ...localInvoices];
            return newInvoice;
        }
    },

    async getAllPayments() {
        try {
            const res = await axiosInstance.get("/payments");
            return res.data;
        } catch {
            return localPayments;
        }
    },

    async recordPayment(paymentData) {
        try {
            const res = await axiosInstance.post("/payments", paymentData);
            return res.data;
        } catch {
            const newPayment = {
                id: localPayments.length + 500,
                invoiceId: paymentData.invoiceId,
                invoiceNumber: `INV-2026-${paymentData.invoiceId}`,
                studentName: paymentData.studentName || "Student",
                amount: Number(paymentData.amount),
                paymentMethod: paymentData.paymentMethod || "UPI",
                transactionRef: `TXN_${Date.now()}`,
                paymentDate: new Date().toISOString(),
                recordedBy: "Portal User",
                receiptUrl: "#",
                status: "SUCCESS",
            };
            localPayments = [newPayment, ...localPayments];

            // Update matching invoice status
            const invIndex = localInvoices.findIndex((inv) => inv.id === Number(paymentData.invoiceId));
            if (invIndex !== -1) {
                localInvoices[invIndex].paidAmount =
                    (localInvoices[invIndex].paidAmount || 0) + Number(paymentData.amount);
                if (localInvoices[invIndex].paidAmount >= localInvoices[invIndex].amount) {
                    localInvoices[invIndex].status = "PAID";
                }
            }

            return newPayment;
        }
    },
};

export default invoiceService;
