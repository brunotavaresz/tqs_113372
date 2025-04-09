import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Restaurants endpoints
export const getRestaurants = async () => {
  const response = await api.get('/restaurants');
  return response.data;
};

export const getRestaurantById = async (id) => {
  const response = await api.get(`/restaurants/${id}`);
  return response.data;
};

// Meals endpoints
export const getMeals = async (restaurantId, date) => {
  const formattedDate = date.toISOString().split('T')[0]; // Format: YYYY-MM-DD
  const response = await api.get(`/restaurants/${restaurantId}/meals`, {
    params: { date: formattedDate }
  });
  return response.data;
};

export const getMealById = async (id) => {
  const response = await api.get(`/meals/${id}`);
  return response.data;
};

// Reservations endpoints
export const createReservation = async (reservationData) => {
  const response = await api.post('/reservations', reservationData);
  return response.data;
};

export const getReservationByCode = async (code) => {
  const response = await api.get(`/reservations/code/${code}`);
  return response.data;
};

export const cancelReservation = async (reservationId) => {
  const response = await api.put(`/reservations/${reservationId}/cancel`);
  return response.data;
};

export const verifyReservation = async (code) => {
  const response = await api.get(`/reservations/verify/${code}`);
  return response.data;
};

export const checkInReservation = async (code) => {
  const response = await api.put(`/reservations/checkin/${code}`);
  return response.data;
};

// Cache monitoring endpoint
export const getCacheStats = async () => {
  const response = await api.get('/monitor/cache');
  return response.data;
};

export default api;