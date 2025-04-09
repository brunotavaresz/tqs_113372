/*
import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Typography,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
  Alert,
  Tabs,
  Tab,
  Paper,
  IconButton
} from '@mui/material';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';

// Generate 2 weeks of mock meal data
const generateMockMeals = () => {
  const restaurants = [
    { id: '1', name: 'Cantina Central', location: 'Edifício A' },
    { id: '2', name: 'Restaurante Norte', location: 'Campus Norte' },
    { id: '3', name: 'Café Moliceiro', location: 'Biblioteca Central' }
  ];

  // Generate dates for next 14 days
  const dates = [];
  const today = new Date('2025-04-01T12:00:00');  // Starting from a fixed date for demo
  
  for (let i = 0; i < 14; i++) {
    const date = new Date(today);
    date.setDate(today.getDate() + i);
    dates.push(date.toISOString().split('T')[0]);
  }

  // Meal templates
  const mealTemplates = [
    {
      name: 'Almoço Tradicional',
      price: 3.95,
      description: 'Uma refeição tradicional portuguesa com carne e acompanhamentos.',
      tags: ['Carne', 'Tradicional'],
      menuItems: ['Sopa de legumes', 'Carne de porco à alentejana', 'Arroz', 'Salada mista', 'Sobremesa do dia']
    },
    {
      name: 'Opção Vegetariana',
      price: 3.50,
      description: 'Uma opção vegetariana nutritiva e saborosa.',
      tags: ['Vegetariano', 'Saudável'],
      menuItems: ['Sopa de legumes', 'Legumes grelhados com tofu', 'Arroz integral', 'Salada mista', 'Fruta fresca']
    },
    {
      name: 'Almoço do Mar',
      price: 4.25,
      description: 'Prato de peixe fresco com acompanhamentos saudáveis.',
      tags: ['Peixe', 'Saudável'],
      menuItems: ['Creme de cenoura', 'Bacalhau com natas', 'Batata cozida', 'Legumes salteados', 'Pudim de caramelo']
    },
    {
      name: 'Prato de Carne',
      price: 4.10,
      description: 'Opção de carne para os amantes de sabores intensos.',
      tags: ['Carne', 'Tradicional'],
      menuItems: ['Sopa da horta', 'Bife de frango grelhado', 'Arroz de legumes', 'Batata frita', 'Mousse de chocolate']
    },
    {
      name: 'Menu Especial',
      price: 5.00,
      description: 'Menu especial com pratos gourmet a preço acessível.',
      tags: ['Especial', 'Gourmet'],
      menuItems: ['Sopa de peixe', 'Risotto de cogumelos', 'Salada de rúcula', 'Pão artesanal', 'Cheesecake']
    }
  ];

  // Generate meals for each restaurant and date
  const mealsByRestaurant = {};
  
  restaurants.forEach(restaurant => {
    mealsByRestaurant[restaurant.id] = [];
    
    dates.forEach((date, dateIndex) => {
      // Skip weekends (Saturday and Sunday)
      const dayOfWeek = new Date(date).getDay();
      if (dayOfWeek === 0 || dayOfWeek === 6) {
        return;
      }
      
      // Add 2-3 meals per day per restaurant
      const numMeals = 2 + (dateIndex % 2);
      for (let i = 0; i < numMeals; i++) {
        const templateIndex = (dateIndex + i) % mealTemplates.length;
        const template = mealTemplates[templateIndex];
        
        mealsByRestaurant[restaurant.id].push({
          id: `${restaurant.id}${dateIndex}${i}`,
          name: template.name,
          date: `${date}T12:00:00`,
          price: template.price + (Math.random() * 0.5 - 0.25), // Add some price variation
          description: template.description,
          tags: template.tags,
          menuItems: template.menuItems,
          restaurant: restaurant
        });
      }
    });
  });
  
  return { restaurants, mealsByRestaurant };
};

const { restaurants: mockRestaurants, mealsByRestaurant: mockMealsByRestaurant } = generateMockMeals();

const MealListPage = () => {
  const [meals, setMeals] = useState([]);
  const [restaurants] = useState(mockRestaurants);
  const [selectedRestaurant, setSelectedRestaurant] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);
  const [tabsStartIndex, setTabsStartIndex] = useState(0);
  const maxVisibleTabs = 5; // Number of tabs visible at once

  // Get unique dates from all meals for tabs
  const uniqueDates = React.useMemo(() => {
    const dates = new Set();
    Object.values(mockMealsByRestaurant).forEach(restaurantMeals => {
      restaurantMeals.forEach(meal => {
        const date = new Date(meal.date).toISOString().split('T')[0];
        dates.add(date);
      });
    });
    return Array.from(dates).sort();
  }, []);

  // Update meals when restaurant or date changes
  useEffect(() => {
    if (selectedRestaurant) {
      setLoading(true);
      
      // Simulate API loading
      setTimeout(() => {
        const restaurantMeals = mockMealsByRestaurant[selectedRestaurant] || [];
        
        // Filter by selected date if any
        const filteredMeals = selectedDate 
          ? restaurantMeals.filter(meal => {
              const mealDate = new Date(meal.date).toISOString().split('T')[0];
              return mealDate === selectedDate;
            })
          : restaurantMeals;
        
        setMeals(filteredMeals);
        setLoading(false);
      }, 300);
    } else {
      setMeals([]);
    }
  }, [selectedRestaurant, selectedDate]);

  // Initialize with first restaurant
  useEffect(() => {
    if (restaurants.length > 0 && !selectedRestaurant) {
      setSelectedRestaurant(restaurants[0].id);
    }
    
    // Set default date to first available date
    if (uniqueDates.length > 0 && !selectedDate) {
      setSelectedDate(uniqueDates[0]);
    }
  }, [restaurants, uniqueDates, selectedRestaurant, selectedDate]);

  const handleRestaurantChange = (event) => {
    setSelectedRestaurant(event.target.value);
  };

  const handleDateChange = (event, newDate) => {
    setSelectedDate(newDate);
  };

  // Format date for display
  const formatTabDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', { weekday: 'short', day: 'numeric', month: 'short' });
  };

  // Navigate tabs
  const handleScrollTabs = (direction) => {
    if (direction === 'left' && tabsStartIndex > 0) {
      setTabsStartIndex(tabsStartIndex - 1);
    } else if (direction === 'right' && tabsStartIndex + maxVisibleTabs < uniqueDates.length) {
      setTabsStartIndex(tabsStartIndex + 1);
    }
  };

  // Get the visible dates for tabs
  const visibleDates = uniqueDates.slice(tabsStartIndex, tabsStartIndex + maxVisibleTabs);

  const renderMealMenu = (meal) => (
    <Box sx={{ mb: 4, p: 3, border: '1px solid #e0e0e0', borderRadius: 2 }}>
      <Typography variant="h6" gutterBottom>{meal.name}</Typography>
      <Typography variant="subtitle1" color="text.secondary" gutterBottom>
        {meal.tags.join(' • ')} • {meal.price.toFixed(2)}€
      </Typography>
      <Typography variant="body2" paragraph>{meal.description}</Typography>
  
      <Typography variant="subtitle2" gutterBottom sx={{ mt: 2 }}>Menu:</Typography>
      <ul style={{ paddingLeft: '20px' }}>
        {meal.menuItems.map((item, index) => (
          <li key={index}>
            <Typography variant="body2">{item}</Typography>
          </li>
        ))}
      </ul>
    </Box>
  );

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Cardápios Disponíveis
      </Typography>
      
      <Box sx={{ mb: 4 }}>
        <FormControl fullWidth>
          <InputLabel id="restaurant-select-label">Selecionar Restaurante</InputLabel>
          <Select
            labelId="restaurant-select-label"
            id="restaurant-select"
            value={selectedRestaurant}
            label="Selecionar Restaurante"
            onChange={handleRestaurantChange}
          >
            {restaurants.map((restaurant) => (
              <MenuItem key={restaurant.id} value={restaurant.id}>
                {restaurant.name} - {restaurant.location}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>
      
      {selectedRestaurant && (
        <Paper sx={{ mb: 4 }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <IconButton 
              onClick={() => handleScrollTabs('left')} 
              disabled={tabsStartIndex === 0}
              aria-label="Dias anteriores"
            >
              <ChevronLeftIcon />
            </IconButton>
            
            <Tabs
              value={selectedDate}
              onChange={handleDateChange}
              variant="scrollable"
              scrollButtons="auto"
              indicatorColor="primary"
              textColor="primary"
              sx={{ flexGrow: 1 }}
            >
              {visibleDates.map(date => (
                <Tab 
                  key={date} 
                  label={formatTabDate(date)} 
                  value={date} 
                />
              ))}
            </Tabs>
            
            <IconButton 
              onClick={() => handleScrollTabs('right')} 
              disabled={tabsStartIndex + maxVisibleTabs >= uniqueDates.length}
              aria-label="Próximos dias"
            >
              <ChevronRightIcon />
            </IconButton>
          </Box>
        </Paper>
      )}
      
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}
      
      {loading ? (
  <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
    <CircularProgress />
  </Box>
) : meals.length === 0 ? (
  <Alert severity="info">
    Não há refeições disponíveis para o restaurante selecionado nesta data.
  </Alert>
) : (
  <Grid container spacing={3}>
    {meals.map((meal) => (
      <Grid item key={meal.id} xs={12} sm={6} md={4} sx={{ display: 'flex', justifyContent: 'center', maxWidth: "31%", minWidth: "30%" }}>
          {renderMealMenu(meal)}
      </Grid>
    ))}
  </Grid>
)}

    </Container>
  );
};

export default MealListPage;
*/