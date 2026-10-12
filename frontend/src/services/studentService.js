import axiosInstance from "./api/axiosInstance";
import { MOCK_STUDENTS } from "../utils/mockData";

let localStudents = [...MOCK_STUDENTS];

export const studentService = {
    async getAll() {
        try {
            const res = await axiosInstance.get("/students");
            return res.data;
        } catch {
            return localStudents;
        }
    },

    async getById(id) {
        try {
            const res = await axiosInstance.get(`/students/${id}`);
            return res.data;
        } catch {
            return localStudents.find((s) => s.id === Number(id)) || localStudents[0];
        }
    },

    async create(studentData) {
        try {
            const res = await axiosInstance.post("/students", studentData);
            return res.data;
        } catch {
            const newStudent = {
                id: localStudents.length + 1,
                ...studentData,
                status: "ACTIVE",
            };
            localStudents = [newStudent, ...localStudents];
            return newStudent;
        }
    },
};

export default studentService;
