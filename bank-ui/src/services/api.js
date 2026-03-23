import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const createClient = (client) => api.post('/clients', client);
export const getAllClients = () => api.get('/clients');
export const getClientById = (id) => api.get(`/clients/${id}`);
export const updateEmail = (id, newEmail) => api.put(`/clients/${id}/email`, { newEmail });
export const deleteClient = (id) => api.delete(`/clients/${id}`);
export const searchClientByEmail = (email) => api.get(`/clients/search?email=${email}`);

export const createCard = (clientId, cardType, validityYears) => api.post(`/clients/${clientId}/cards`,
    { cardType, validityYears });
export const getClientCards = (clientId) => api.get(`/clients/${clientId}/cards`);
export const getCardById = (cardId) => api.get(`/cards/${cardId}`);
export const cancelCard = (cardId) => api.delete(`/cards/${cardId}`);
export const getExpiringCards = (days = 30) => api.get(`/cards/expiring?days=${days}`);
export const getActiveClientCards = (clientId) => api.get(`/clients/${clientId}/cards/active`);
export const cardExists = (cardId) => api.get(`/cards/${cardId}/exists`);

export default api;