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

export const driverService = {
    // Get all drivers
    getAll: async () => {
        const response = await axios.get(`${API_BASE_URL}/drivers/all`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get driver by ID
    getById: async (id) => {
        const response = await axios.get(`${API_BASE_URL}/drivers/${id}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get driver by license number
    getByLicenseNumber: async (licenseNumber) => {
        const response = await axios.get(`${API_BASE_URL}/drivers/license/${licenseNumber}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get drivers by experience
    getByExperience: async (minYears) => {
        const response = await axios.get(`${API_BASE_URL}/drivers/experience/${minYears}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Search drivers by name
    search: async (name) => {
        const response = await axios.get(`${API_BASE_URL}/drivers/search?name=${name}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Check if driver exists by license number
    exists: async (licenseNumber) => {
        const response = await axios.get(`${API_BASE_URL}/drivers/exists/${licenseNumber}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Create new driver
    create: async (driverData) => {
        const response = await axios.post(`${API_BASE_URL}/drivers/save`, driverData, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Update driver
    update: async (id, driverData) => {
        const response = await axios.put(`${API_BASE_URL}/drivers/${id}`, driverData, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Delete driver
    delete: async (id) => {
        const response = await axios.delete(`${API_BASE_URL}/drivers/${id}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    },

    // Get driver statistics
    getStatistics: async (driverEmail) => {
        const response = await axios.get(`${API_BASE_URL}/drivers/statistics/${encodeURIComponent(driverEmail)}`, {
            headers: getAuthHeaders()
        });
        return response.data;
    }
};

