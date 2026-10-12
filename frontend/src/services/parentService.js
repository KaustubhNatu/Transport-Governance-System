import { MOCK_STUDENTS, MOCK_INVOICES, MOCK_TRANSFER_NOTIFICATIONS } from "../utils/mockData";

export const parentService = {
    getChildren(parentId = 5) {
        return MOCK_STUDENTS.filter((s) => s.parentId === parentId);
    },

    getInvoices(parentId = 5) {
        return MOCK_INVOICES.filter((i) => i.parentId === parentId);
    },

    getTransferRequests(parentId = 5) {
        const studentIds = MOCK_STUDENTS.filter((s) => s.parentId === parentId).map((s) => s.id);
        return MOCK_TRANSFER_NOTIFICATIONS.filter((t) => studentIds.includes(t.studentId));
    },
};

export default parentService;
