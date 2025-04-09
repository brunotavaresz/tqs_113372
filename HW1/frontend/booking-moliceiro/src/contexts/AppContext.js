import React, { createContext, useState, useEffect, useContext } from 'react';
import { getRestaurants, getMeals } from '../services/api';

const AppContext = createContext();

export const useAppContext = () => useContext(AppContext);

export const AppProvider = ({ children }) => {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [currentUser, setCurrentUser] = useState({ role: 'student' }); // Default to student role

  const fetchInitialData = async () => {
    try {
      setLoading(true);
      const restaurantsData = await getRestaurants();
      setRestaurants(restaurantsData);
      
      if (restaurantsData.length > 0) {
        setSelectedRestaurant(restaurantsData[0].id);
      }
      
      setLoading(false);
    } catch (err) {
      setError('Failed to load initial data');
      setLoading(false);
      console.error('Error fetching initial data:', err);
    }
  };

  useEffect(() => {
    fetchInitialData();
  }, []);

  // Toggle user role for demo purposes (in a real app this would be handled by auth)
  const toggleUserRole = () => {
    setCurrentUser(prev => ({
      ...prev,
      role: prev.role === 'student' ? 'staff' : 'student'
    }));
  };

  const value = {
    restaurants,
    loading,
    error,
    selectedRestaurant,
    setSelectedRestaurant,
    selectedDate,
    setSelectedDate,
    currentUser,
    toggleUserRole,
    refreshData: fetchInitialData
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
};

export default AppContext;