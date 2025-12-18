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

export const announcementService = {
    // Get all announcements
    getAll: async () => {
        const response = await axios.get(`${API_BASE_URL}/announcements/all`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get announcements for drivers (includes ALL and DRIVER target audience)
    getForDrivers: async () => {
        const response = await axios.get(`${API_BASE_URL}/announcements/drivers`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get announcements for students (includes ALL and STUDENT target audience)
    getForStudents: async () => {
        const response = await axios.get(`${API_BASE_URL}/announcements/students`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Create new announcement
    create: async (announcementData) => {
        const response = await axios.post(`${API_BASE_URL}/announcements/save`, announcementData, {
            headers: getAuthHeaders()
        });
        return response.data;
    }
};

