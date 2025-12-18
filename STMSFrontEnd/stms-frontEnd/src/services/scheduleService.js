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

export const scheduleService = {
    // Get all schedules
    getAll: async () => {
        const response = await axios.get(`${API_BASE_URL}/schedules/all`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get schedules by bus
    getByBus: async (busId) => {
        const response = await axios.get(`${API_BASE_URL}/schedules/bus/${busId}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get schedules by driver email
    getByDriverEmail: async (driverEmail) => {
        const response = await axios.get(`${API_BASE_URL}/schedules/driver/${encodeURIComponent(driverEmail)}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Create new schedule
    create: async (scheduleData) => {
        const response = await axios.post(`${API_BASE_URL}/schedules/save`, scheduleData, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Update schedule
    update: async (id, scheduleData) => {
        const response = await axios.put(`${API_BASE_URL}/schedules/${id}`, scheduleData, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Delete schedule
    delete: async (id) => {
        const response = await axios.delete(`${API_BASE_URL}/schedules/${id}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    }
};

