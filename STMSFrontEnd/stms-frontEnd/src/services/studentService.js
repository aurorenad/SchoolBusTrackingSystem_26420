import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

const getAuthHeaders = () => {
    const token = localStorage.getItem('token');
    if (!token) {
        console.warn('No token found in localStorage');
        return {
            'Content-Type': 'application/json'
        };
    }
    // Clean token - remove quotes if present
    const cleanToken = token.replace(/^"|"$/g, '');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${cleanToken}`
    };
};

export const studentService = {
    // Get all students
    getAll: async () => {
        const response = await axios.get(`${API_BASE_URL}/students/all`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get student by ID
    getById: async (id) => {
        const response = await axios.get(`${API_BASE_URL}/students/${id}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get students by class
    getByClass: async (className) => {
        const response = await axios.get(`${API_BASE_URL}/students/class/${className}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get students by status
    getByStatus: async (status) => {
        const response = await axios.get(`${API_BASE_URL}/students/status/${status}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get students by parent
    getByParent: async (parentId) => {
        const response = await axios.get(`${API_BASE_URL}/students/parent/${parentId}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get students by bus
    getByBus: async (busId) => {
        const response = await axios.get(`${API_BASE_URL}/students/bus/${busId}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get students by pickup point
    getByPickupPoint: async (pickUpPoint) => {
        const response = await axios.get(`${API_BASE_URL}/students/pickup/${pickUpPoint}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get students by dropoff point
    getByDropoffPoint: async (dropOffPoint) => {
        const response = await axios.get(`${API_BASE_URL}/students/dropoff/${dropOffPoint}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Search students by name
    search: async (name) => {
        const response = await axios.get(`${API_BASE_URL}/students/search?name=${name}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Create new student
    create: async (studentData) => {
        const response = await axios.post(`${API_BASE_URL}/students/save`, studentData, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Update student
    update: async (id, studentData) => {
        const response = await axios.put(`${API_BASE_URL}/students/${id}`, studentData, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Delete student
    delete: async (id) => {
        const response = await axios.delete(`${API_BASE_URL}/students/${id}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Assign parent to student
    assignParent: async (studentId, parentId) => {
        const response = await axios.put(`${API_BASE_URL}/students/assignParent?studentId=${studentId}&parentId=${parentId}`, {}, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Assign bus to student
    assignBus: async (studentId, busId) => {
        const response = await axios.put(`${API_BASE_URL}/students/assignBus?studentId=${studentId}&busId=${busId}`, {}, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Update student status
    updateStatus: async (studentId, status) => {
        const response = await axios.put(`${API_BASE_URL}/students/updateStatus?studentId=${studentId}&status=${status}`, {}, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get students by driver email
    getByDriverEmail: async (driverEmail) => {
        const response = await axios.get(`${API_BASE_URL}/students/driver/${encodeURIComponent(driverEmail)}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Update student status by driver
    updateStatusByDriver: async (studentId, status) => {
        const response = await axios.put(`${API_BASE_URL}/students/driver/updateStatus?studentId=${studentId}&status=${status}`, {}, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get student statistics
    getStatistics: async (studentEmail) => {
        const response = await axios.get(`${API_BASE_URL}/students/statistics/${encodeURIComponent(studentEmail)}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    }
};

