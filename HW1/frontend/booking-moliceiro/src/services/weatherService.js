import api from './api';

export const getWeatherForecast = async (date) => {
  try {
    const formattedDate = date.toISOString().split('T')[0]; // Format: YYYY-MM-DD
    const response = await api.get(`/weather`, {
      params: { date: formattedDate }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching weather forecast:', error);
    return null;
  }
};

export const getWeatherForecasts = async (startDate, days = 5) => {
  try {
    const start = new Date(startDate);
    const dates = Array.from({ length: days }, (_, i) => {
      const date = new Date(start);
      date.setDate(date.getDate() + i);
      return date.toISOString().split('T')[0];
    });
    
    const response = await api.get(`/weather/batch`, {
      params: { dates: dates.join(',') }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching weather forecasts:', error);
    return {};
  }
};

export default {
  getWeatherForecast,
  getWeatherForecasts
};