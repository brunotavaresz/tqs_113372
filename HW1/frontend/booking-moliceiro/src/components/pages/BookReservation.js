import React, { useState, useEffect } from 'react';
import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Container, 
  Grid, 
  Card, 
  CardContent, 
  CardMedia, 
  CardActionArea, 
  Button, 
  Dialog, 
  DialogTitle, 
  DialogContent, 
  DialogActions, 
  Stepper, 
  Step, 
  StepLabel, 
  Paper, 
  List, 
  ListItem, 
  ListItemText, 
  Divider,
  TextField,
  InputAdornment,
  Box,
  Tabs,
  Tab,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  IconButton,
  CircularProgress,
  Alert,
  Snackbar,
  Tooltip,
  Chip
} from '@mui/material';

// Ícones simulados com componentes de texto (substituir por ícones reais em implementação)
const SearchIcon = () => <span>🔍</span>;
const NextWeekIcon = () => <span>➡️</span>;
const PrevWeekIcon = () => <span>⬅️</span>;
const SunIcon = () => <span>☀️</span>;
const RainIcon = () => <span>🌧️</span>;
const CloudIcon = () => <span>☁️</span>;
const SnowIcon = () => <span>❄️</span>;
const ThunderIcon = () => <span>⚡</span>;
const MistIcon = () => <span>🌫️</span>;
const LunchIcon = () => <span>🍽️</span>;
const DinnerIcon = () => <span>🍷</span>;

// API base URL
const API_BASE_URL = 'http://localhost:8080';

// Função para obter o ícone do clima baseado no código da API
const getWeatherIcon = (weatherMain) => {
  switch (weatherMain?.toLowerCase()) {
    case 'clear': return <SunIcon />;
    case 'rain': return <RainIcon />;
    case 'clouds': return <CloudIcon />;
    case 'snow': return <SnowIcon />;
    case 'thunderstorm': return <ThunderIcon />;
    case 'mist': case 'fog': case 'haze': return <MistIcon />;
    default: return <CloudIcon />;
  }
};

// Função para determinar se é horário de almoço ou jantar
const getMealPeriod = (timeString) => {
  if (!timeString) return 'Não especificado';
  
  const hour = parseInt(timeString.split(':')[0], 10);
  
  if (hour >= 11 && hour <= 15) {
    return 'Almoço';
  } else if (hour >= 18 && hour <= 23) {
    return 'Jantar';
  } else {
    return 'Refeição';
  }
};

// Função para obter o ícone do período da refeição
const getMealPeriodIcon = (timeString) => {
  const hour = parseInt(timeString.split(':')[0], 10);
  
  if (hour >= 11 && hour <= 15) {
    return <LunchIcon />;
  } else if (hour >= 18 && hour <= 23) {
    return <DinnerIcon />;
  } else {
    return null;
  }
};

// Função para formatar o horário
const formatMealTime = (timeString) => {
  if (!timeString) return '';
  
  // Formato do timeString: "12:30:00"
  return timeString.substr(0, 5); // Retorna "12:30"
};

// Aplicação principal
const App = () => {
  // State for data from API
  const [restaurants, setRestaurants] = useState([]);
  const [meals, setMeals] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [reservationCode, setReservationCode] = useState("");
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");
  const [weatherData, setWeatherData] = useState({});

  // UI state
  const [activeStep, setActiveStep] = useState(0);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [selectedDay, setSelectedDay] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedMeal, setSelectedMeal] = useState(null);
  const [openReservationDialog, setOpenReservationDialog] = useState(false);
  const [reservationConfirmed, setReservationConfirmed] = useState(false);
  const [currentWeek, setCurrentWeek] = useState("current");
  const [searchTerm, setSearchTerm] = useState("");
  const [cuisineFilter, setCuisineFilter] = useState("");
  const [ratingFilter, setRatingFilter] = useState("");
  const [mealPeriodFilter, setMealPeriodFilter] = useState(""); // novo filtro para período de refeição

  const [customerName, setCustomerName] = useState('');


  // Fetch data from API
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        // Fetch restaurants, meals, and reservations in parallel
        const [restaurantsRes, mealsRes, reservationsRes] = await Promise.all([
          fetch(`${API_BASE_URL}/restaurants`),
          fetch(`${API_BASE_URL}/meals`),
          fetch(`${API_BASE_URL}/reservations`)
        ]);

        // Check if responses are ok
        if (!restaurantsRes.ok) throw new Error('Failed to fetch restaurants');
        if (!mealsRes.ok) throw new Error('Failed to fetch meals');
        if (!reservationsRes.ok) throw new Error('Failed to fetch reservations');

        // Parse JSON responses
        const restaurantsData = await restaurantsRes.json();
        const mealsData = await mealsRes.json();
        const reservationsData = await reservationsRes.json();

        // Update state with fetched data
        setRestaurants(restaurantsData);
        setMeals(mealsData);
        setReservations(reservationsData);
        
        // Fetch weather data for all meals
        const weatherPromises = mealsData.map(meal => 
          fetchWeatherForMeal(meal.id)
        );
        
        // Wait for all weather data to be fetched
        const weatherResults = await Promise.allSettled(weatherPromises);
        
        // Process successful weather fetches
        const weatherObject = {};
        weatherResults.forEach((result, index) => {
          if (result.status === 'fulfilled' && result.value) {
            weatherObject[mealsData[index].id] = result.value;
          }
        });
        
        setWeatherData(weatherObject);
      } catch (err) {
        setError(err.message);
        console.error('Error fetching data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // Fetch weather data for a specific meal
  const fetchWeatherForMeal = async (mealId) => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/meals/${mealId}/weather`);
      if (!response.ok) {
        console.warn(`Weather data not available for meal ${mealId}`);
        return null;
      }
      return await response.json();
    } catch (err) {
      console.error(`Error fetching weather for meal ${mealId}:`, err);
      return null;
    }
  };

  // Generate a random reservation code
  const generateReservationCode = () => {
    return 'RS' + Math.floor(10000 + Math.random() * 90000);
  };

  // Group meals by restaurant, date, and meal period (lunch/dinner)
  const getRestaurantMeals = (restaurantId) => {
    // Filter meals for this restaurant
    const restaurantMeals = meals.filter(meal => meal.restaurant.id === restaurantId);
    
    // Group by date (current week and next week)
    const today = new Date();
    const oneWeek = 7 * 24 * 60 * 60 * 1000; // 7 days in milliseconds
    
    const groupedMeals = {
      current: {},
      next: {}
    };
    
    // Days of the week in Portuguese
    const daysOfWeek = ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'];
    
    restaurantMeals.forEach(meal => {
      const mealDate = new Date(meal.date);
      const dayDiff = Math.floor((mealDate - today) / (24 * 60 * 60 * 1000));
      
      // Determine if meal is in current week (0-6 days from now) or next week (7-13 days)
      const weekKey = dayDiff < 7 ? 'current' : 'next';
      const dayName = daysOfWeek[mealDate.getDay()];
      
      // Determine if it's lunch or dinner
      const mealPeriod = getMealPeriod(meal.time);
      
      if (dayDiff >= 0 && dayDiff < 14) { // Only include meals within the next two weeks
        // Initialize day structure if it doesn't exist
        if (!groupedMeals[weekKey][dayName]) {
          groupedMeals[weekKey][dayName] = {
            date: meal.date,
            meals: {
              'Almoço': [],
              'Jantar': []
            }
          };
        }
        
        // Add meal to the appropriate period
        groupedMeals[weekKey][dayName].meals[mealPeriod].push({
          id: meal.id,
          name: meal.name,
          price: meal.price,
          time: meal.time,
          description: meal.description || `Preço: €${meal.price.toFixed(2)}`
        });
      }
    });
    
    return groupedMeals;
  };

  // Get unique cuisine types (based on restaurant locations for now)
  const cuisineOptions = [...new Set(restaurants.map(r => r.location))];

  // Filter restaurants based on search terms and filters
  const filteredRestaurants = restaurants.filter(restaurant => {
    const matchesSearch = restaurant.name.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesLocation = cuisineFilter === "" || restaurant.location === cuisineFilter;
    return matchesSearch && matchesLocation;
  });

  // Functions to navigate between steps
  const handleRestaurantSelect = (restaurant) => {
    setSelectedRestaurant(restaurant);
    setActiveStep(1);
  };

  const handleCancelReservation = async (reservationCode) => {
    try {
      const response = await fetch(`${API_BASE_URL}/reservations/${reservationCode}`, {
        method: 'DELETE',
      });
  
      if (!response.ok) {
        throw new Error('Erro ao cancelar a reserva');
      }
  
      // Atualiza as reservas após a exclusão
      const updatedReservationsRes = await fetch(`${API_BASE_URL}/reservations`);
      if (updatedReservationsRes.ok) {
        const updatedReservationsData = await updatedReservationsRes.json();
        setReservations(updatedReservationsData);
      }
  
      setSnackbarMessage("Reserva cancelada com sucesso!");
      setSnackbarSeverity("success");
      setSnackbarOpen(true);
    } catch (err) {
      console.error('Erro ao cancelar reserva:', err);
      setSnackbarMessage("Erro ao cancelar a reserva. Tente novamente.");
      setSnackbarSeverity("error");
      setSnackbarOpen(true);
    }
  };

  const handleMealSelect = (day, date, meal) => {
    setSelectedDay(day);
    setSelectedDate(date);
    setSelectedMeal(meal);
    setReservationCode(generateReservationCode());
    setOpenReservationDialog(true);
    setReservationConfirmed(false);
  };

  const handleReservationClose = () => {
    setOpenReservationDialog(false);
  };

  const handleReservationConfirm = async () => {
    try {
      // Create a reservation using the API
      const reservationData = {
        customerName: customerName.trim(),
        reservationCode: reservationCode,
        checkedIn: false, // Initially not checked in
        restaurant: {
          id: selectedRestaurant.id
        },
        meal: {
          id: selectedMeal.id,
          name: selectedMeal.name,
          price: selectedMeal.price,
          date: selectedDate,
          time: selectedMeal.time,
          restaurant: {
            id: selectedRestaurant.id
          }
        }
      };
      
      const response = await fetch(`${API_BASE_URL}/reservations`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'accept': '*/*'
        },
        body: JSON.stringify(reservationData)
      });

      if (!response.ok) {
        throw new Error('Failed to create reservation');
      }

      // Fetch updated reservations
      const updatedReservationsRes = await fetch(`${API_BASE_URL}/reservations`);
      if (updatedReservationsRes.ok) {
        const updatedReservationsData = await updatedReservationsRes.json();
        setReservations(updatedReservationsData);
      }

      setReservationConfirmed(true);
      setOpenReservationDialog(false);
      setSnackbarMessage("Reserva confirmada com sucesso!");
      setSnackbarSeverity("success");
      setSnackbarOpen(true);
    } catch (err) {
      console.error('Error making reservation:', err);
      setSnackbarMessage("Restaurante cheio. Tente outra data.");
      setSnackbarSeverity("error");
      setSnackbarOpen(true);
    }
  };

  const resetSelection = () => {
    setActiveStep(0);
    setSelectedRestaurant(null);
    setSelectedDay(null);
    setSelectedDate(null);
    setSelectedMeal(null);
    setReservationConfirmed(false);
    setCurrentWeek("current");
    setMealPeriodFilter("");
    setCustomerName('');
  };

  const toggleWeek = () => {
    setCurrentWeek(currentWeek === "current" ? "next" : "current");
  };

  // Clear filters
  const clearFilters = () => {
    setSearchTerm("");
    setCuisineFilter("");
    setRatingFilter("");
    setMealPeriodFilter("");
  };

  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
  };

  // Renderiza o componente de previsão do tempo
  const WeatherDisplay = ({ mealId }) => {
    const weather = weatherData[mealId];
    
    if (!weather) {
      return (
        <Box sx={{ display: 'flex', alignItems: 'center', mr: 2 }}>
          <Typography variant="body2" color="text.secondary">
            Clima: N/A
          </Typography>
        </Box>
      );
    }
    
    const weatherIcon = getWeatherIcon(weather.weather[0]?.main);
    const temp = Math.round(weather.main.temp);
    
    return (
      <Tooltip title={weather.weather[0]?.description || 'Previsão do tempo'}>
        <Box sx={{
          display: 'flex',
          alignItems: 'center',
          backgroundColor: '#e3f2fd',
          borderRadius: '8px',
          padding: '6px 12px',
          mr: 2,
          border: '1px solid #90caf9',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          height: '40px',
        }}>
          <Box sx={{ 
            mr: 1, 
            fontSize: '1.5rem',
            color: '#0277bd',
            filter: 'drop-shadow(1px 1px 1px rgba(0,0,0,0.2))',
            display: 'flex', 
            alignItems: 'center'
          }}>
            {weatherIcon}
          </Box>
          <Typography variant="body1" fontWeight="medium" color="#0277bd">
            {temp}°C
          </Typography>
        </Box>
      </Tooltip>
    );
  };

  // Display loading state
  if (loading) {
    return (
      <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
        <Typography variant="h6" sx={{ ml: 2 }}>Carregando dados...</Typography>
      </Container>
    );
  }

  // Display error state
  if (error) {
    return (
      <Container sx={{ mt: 4 }}>
        <Paper sx={{ p: 3, backgroundColor: '#ffebee' }}>
          <Typography variant="h5" color="error" gutterBottom>Erro ao carregar dados</Typography>
          <Typography>{error}</Typography>
          <Button variant="contained" color="primary" sx={{ mt: 2 }} onClick={() => window.location.reload()}>
            Tentar novamente
          </Button>
        </Paper>
      </Container>
    );
  }

  return (
    <div>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        {/* Stepper para acompanhar o progresso */}
        <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
          <Step>
            <StepLabel>Escolha o Restaurante</StepLabel>
          </Step>
          <Step>
            <StepLabel>Selecione o Dia e Refeição</StepLabel>
          </Step>
        </Stepper>

        {/* Snackbar para notificações */}
        <Snackbar 
          open={snackbarOpen} 
          autoHideDuration={6000} 
          onClose={handleSnackbarClose}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        >
          <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
            {snackbarMessage}
          </Alert>
        </Snackbar>

        {/* Tela 1: Seleção de Restaurante com Filtros */}
        {activeStep === 0 && (
          <>
            <Typography variant="h4" gutterBottom>
              Escolha um Restaurante
            </Typography>
            
            {/* Filtros e pesquisa */}
            <Paper sx={{ p: 3, mb: 4 }}>
              <Grid container spacing={2} alignItems="center">
                <Grid item xs={12} md={4}>
                  <TextField
                    fullWidth
                    label="Pesquisar restaurante"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <SearchIcon />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>
                
                <Grid item xs={12} md={4}>
                  <FormControl fullWidth>
                    <InputLabel>Localização</InputLabel>
                    <Select
                      value={cuisineFilter}
                      onChange={(e) => setCuisineFilter(e.target.value)}
                      label="Localização"
                      sx={{ minWidth: '250px' }}
                    >
                      <MenuItem value="">Todos</MenuItem>
                      {cuisineOptions.map((location) => (
                        <MenuItem key={location} value={location}>{location}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
                
                <Grid item xs={12} md={2}>
                  <Button 
                    variant="outlined" 
                    color="primary" 
                    fullWidth
                    onClick={clearFilters}
                  >
                    Limpar Filtros
                  </Button>
                </Grid>
              </Grid>
            </Paper>
            
            {/* Lista de restaurantes */}
            <Grid container spacing={4}>
              {filteredRestaurants.length > 0 ? (
                filteredRestaurants.map((restaurant) => (
                  <Grid item xs={12} sm={6} md={4} key={restaurant.id}>
                    <Card sx={{ height: '100%' }}>
                      <CardActionArea onClick={() => handleRestaurantSelect(restaurant)}>
                      <CardMedia
                        component="img"
                        height="140"
                        image={restaurant.photoUrl}
                        alt={restaurant.name}
                      />
                        <CardContent>
                          <Typography gutterBottom variant="h5" component="div">
                            {restaurant.name}
                          </Typography>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <Typography variant="body2" color="text.secondary">
                              Localização: {restaurant.location}
                            </Typography>
                          </Box>
                        </CardContent>
                      </CardActionArea>
                    </Card>
                  </Grid>
                ))
              ) : (
                <Grid item xs={12}>
                  <Paper sx={{ p: 3, textAlign: 'center' }}>
                    <Typography variant="h6">
                      Nenhum restaurante encontrado com os filtros selecionados.
                    </Typography>
                    <Button 
                      variant="contained" 
                      color="primary" 
                      sx={{ mt: 2 }}
                      onClick={clearFilters}
                    >
                      Limpar Filtros
                    </Button>
                  </Paper>
                </Grid>
              )}
            </Grid>
            
            {/* Seção de reservas existentes */}
            {reservations.length > 0 && (
              <Paper sx={{ p: 3, mt: 4 }}>
                <Typography variant="h5" gutterBottom>
                  Minhas Reservas
                </Typography>
                <List>
                  {reservations.map((reservation) => (
                    <ListItem 
                      key={reservation.id}
                      sx={{ 
                        borderLeft: '4px solid #4caf50',
                        m: 1,
                        borderRadius: 1,
                        justifyContent: 'space-between'
                      }}
                    >
                      <Box sx={{ flex: 1 }}>
                        <ListItemText 
                          primary={
                            <Typography variant="h6">
                              {reservation.restaurant.name} - {reservation.meal.name}
                            </Typography>
                          }
                          secondary={
                            <>
                              <Typography variant="body2">
                                Código de Reserva: {reservation.reservationCode}
                              </Typography>
                              <Typography variant="body2">
                                Cliente: {reservation.customerName}
                              </Typography>
                              <Typography variant="body2">
                                Data: {new Date(reservation.meal.date).toLocaleDateString('pt-PT')}
                              </Typography>
                              <Typography variant="body2">
                                Horário: {formatMealTime(reservation.meal.time)} - {getMealPeriod(reservation.meal.time)}
                              </Typography>
                              <Typography variant="body2">
                                Local: {reservation.restaurant.location}
                              </Typography>
                              <Typography variant="body2" color="text.secondary">
                                {reservation.checkedIn ? "Check-in realizado" : "Check-in pendente"}
                              </Typography>
                            </>
                          }
                        />
                      </Box>
                      <Button 
                        variant="outlined" 
                        color="error" 
                        onClick={() => handleCancelReservation(reservation.reservationCode)}
                        sx={{ ml: 2 }}
                      >
                        Cancelar
                      </Button>
                    </ListItem>
                  ))}
                </List>
              </Paper>
            )}
          </>
        )}

        {/* Tela 2: Menu da Semana com Almoço e Jantar */}
        {activeStep === 1 && selectedRestaurant && (
          <>
            <Paper sx={{ p: 3, mb: 4 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h4" color="text.primary" gutterBottom>
                  {selectedRestaurant.name}
                </Typography>
                
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Button 
                    variant="contained"
                    color={currentWeek === "current" ? "primary" : "secondary"}
                    onClick={toggleWeek}
                    startIcon={currentWeek === "next" ? <PrevWeekIcon /> : null}
                    endIcon={currentWeek === "current" ? <NextWeekIcon /> : null}
                    sx={{ mr: 2 }}
                  >
                    {currentWeek === "current" ? "Ver Próxima Semana" : "Ver Semana Atual"}
                  </Button>
                  
                  <Button 
                    variant="outlined" 
                    color="primary"
                    onClick={resetSelection}
                  >
                    Voltar para Restaurantes
                  </Button>
                </Box>
              </Box>

              <Typography variant="body1" sx={{ mb: 2 }}>
                Localização: {selectedRestaurant.location}
              </Typography>

              {/* Filtro para Almoço/Jantar */}
              <Box sx={{ mb: 3, display: 'flex', justifyContent: 'flex-start' }}>
                <FormControl sx={{ minWidth: 200 }}>
                  <InputLabel>Período</InputLabel>
                  <Select
                    value={mealPeriodFilter}
                    onChange={(e) => setMealPeriodFilter(e.target.value)}
                    label="Período"
                  >
                    <MenuItem value="">Todos</MenuItem>
                    <MenuItem value="Almoço">Almoço</MenuItem>
                    <MenuItem value="Jantar">Jantar</MenuItem>
                  </Select>
                </FormControl>
              </Box>

              {/* Show meals for the selected restaurant */}
              {(() => {
                const restaurantMeals = getRestaurantMeals(selectedRestaurant.id);
                const weekMeals = restaurantMeals[currentWeek];
                
                if (Object.keys(weekMeals).length === 0) {
                  return (
                    <Paper sx={{ p: 3, textAlign: 'center', backgroundColor: '#f5f5f5' }}>
                      <Typography variant="h6">
                        Não há refeições disponíveis para {currentWeek === "current" ? "esta semana" : "a próxima semana"}.
                      </Typography>
                    </Paper>
                  );
                }
                
                // Sort days by date
                const sortedDays = Object.entries(weekMeals)
                  .sort(([, a], [, b]) => new Date(a.date) - new Date(b.date));
                
                return (
                  <List>
                    {sortedDays.map(([day, dayData], dayIndex) => {
                      // Filter meal periods based on selection
                      const periodsToShow = mealPeriodFilter ? [mealPeriodFilter] : ['Almoço', 'Jantar'];
                      
                      // Check if there are meals in the selected periods
                      const hasVisibleMeals = periodsToShow.some(period => 
                        dayData.meals[period] && dayData.meals[period].length > 0
                      );
                      
                      if (!hasVisibleMeals) return null;
                      
                      return (
                        <React.Fragment key={day}>
                          <Paper 
                            elevation={3} 
                            sx={{ mb: 3, overflow: 'hidden', borderRadius: 2 }}
                          >
                            <Box sx={{ 
                              p: 2, 
                              backgroundColor: '#1976d2', 
                              color: 'white',
                              display: 'flex',
                              justifyContent: 'space-between',
                              alignItems: 'center'
                            }}>
                              <Typography variant="h6">
                                {day}{currentWeek === "next" ? " (Próxima Semana)" : ""}
                              </Typography>
                              <Typography variant="body1">
                                {new Date(dayData.date).toLocaleDateString('pt-PT')}
                              </Typography>
                            </Box>
                            
                            {periodsToShow.map(period => {
                              const periodMeals = dayData.meals[period];
                              
                              if (!periodMeals || periodMeals.length === 0) return null;
                              
                              return (
                                <Box key={period} sx={{ p: 2, borderTop: '1px solid #e0e0e0' }}>
                                  <Box sx={{ 
                                    display: 'flex', 
                                    alignItems: 'center', 
                                    mb: 2 
                                  }}>
                                    <Chip 
                                      icon={period === 'Almoço' ? <LunchIcon /> : <DinnerIcon />}
                                      label={period} 
                                      color={period === 'Almoço' ? 'success' : 'primary'}
                                      sx={{ fontWeight: 'bold', mr: 2 }}
                                    />
                                    <Typography variant="body2" color="text.secondary">
                                      {period === 'Almoço' ? '11:00 às 15:00' : '18:00 às 23:00'}
                                    </Typography>
                                  </Box>
                                  
                                  <List>
                                    {periodMeals.map((meal, mealIndex) => (
                                      <ListItem 
                                        key={meal.id}
                                        sx={{ 
                                          borderLeft: '4px solid',
                                          borderColor: period === 'Almoço' ? '#4caf50' : '#1976d2',
                                          m: 1,
                                          p: 2,
                                          borderRadius: 1,
                                          backgroundColor: '#fafafa',
                                          flexDirection: 'column',
                                        '&:hover': { 
                                          backgroundColor: '#f0f7ff'
                                        }
                                      }}
                                    >
                                      <Box sx={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'flex-start' }}>
                                        <Box sx={{ flex: 1 }}>
                                          <Typography variant="h6">
                                            {meal.name}
                                          </Typography>
                                          <Typography variant="body1" color="primary" fontWeight="bold">
                                            €{meal.price.toFixed(2)}
                                          </Typography>
                                          <Typography variant="body2" color="text.secondary">
                                            Horário: {formatMealTime(meal.time)}
                                          </Typography>
                                        </Box>
                                        
                                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                          <WeatherDisplay mealId={meal.id} />
                                          <Button
                                            variant="contained"
                                            color={period === 'Almoço' ? 'success' : 'primary'}
                                            onClick={() => handleMealSelect(day, dayData.date, meal)}
                                          >
                                            Reservar
                                          </Button>
                                        </Box>
                                      </Box>
                                    </ListItem>
                                  ))}
                                </List>
                              </Box>
                            );
                          })}
                        </Paper>
                      </React.Fragment>
                    );
                  })}
                </List>
              );
            })()}
          </Paper>
          
          {/* Diálogo de Reserva */}
          <Dialog open={openReservationDialog} onClose={handleReservationClose}>
            <DialogTitle>Confirmar Reserva</DialogTitle>
            <DialogContent>
              {selectedRestaurant && selectedDay && selectedMeal && selectedDate && (
                <>
                  <TextField
                    autoFocus
                    margin="dense"
                    id="customerName"
                    label="Nome do Cliente"
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={customerName}
                    onChange={(e) => setCustomerName(e.target.value)}
                    required
                    sx={{ mb: 2 }}
                  />
                  <Typography variant="h5"  gutterBottom>
                    <strong>{selectedRestaurant.name}</strong>
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    Localização: {selectedRestaurant.location}
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    Data: {new Date(selectedDate).toLocaleDateString('pt-PT')}
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    Horário: {formatMealTime(selectedMeal.time)} - {getMealPeriod(selectedMeal.time)}
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    Prato: {selectedMeal.name}
                  </Typography>

                  <Typography variant="body1" gutterBottom>
                    Preço: €{selectedMeal.price.toFixed(2)}
                  </Typography>
                  <Typography variant="body1" sx={{ mt: 2, fontWeight: 'bold' }}>
                    Código de Reserva: {reservationCode}
                  </Typography>
                </>
              )}
            </DialogContent>
            <DialogActions>
              <Button onClick={handleReservationClose} color="primary">
                Cancelar
              </Button>
              <Button 
                onClick={handleReservationConfirm} 
                color="primary" 
                variant="contained"
                disabled={!customerName.trim()}
              >
                Confirmar Reserva
              </Button>
            </DialogActions>
          </Dialog>

          {/* Confirmação de Reserva */}
          {reservationConfirmed && selectedRestaurant && selectedDay && selectedMeal && selectedDate && (
            <Paper sx={{ p: 3, mb: 4, mt: 4, backgroundColor: '#e8f5e9' }}>
              <Typography variant="h5" gutterBottom color="success.main">
                Reserva Confirmada!
              </Typography>
              <Typography variant="body1">
                Cliente: {customerName}
              </Typography>
              <Typography variant="body1">
                Restaurante: {selectedRestaurant.name}
              </Typography>
              <Typography variant="body1">
                Localização: {selectedRestaurant.location}
              </Typography>
              <Typography variant="body1">
                Data: {new Date(selectedDate).toLocaleDateString('pt-PT')}
              </Typography>
              <Typography variant="body1">
                Horário: {formatMealTime(selectedMeal.time)} - {getMealPeriod(selectedMeal.time)}
              </Typography>
              <Typography variant="body1">
                Prato: {selectedMeal.name}
              </Typography>
              <Typography variant="body1">
                Preço: €{selectedMeal.price.toFixed(2)}
              </Typography>
              <Typography variant="body1" sx={{ fontWeight: 'bold', mt: 1 }}>
                Código de Reserva: {reservationCode}
              </Typography>
              <Button 
                variant="contained" 
                color="primary" 
                sx={{ mt: 2 }}
                onClick={resetSelection}
              >
                Fazer Nova Reserva
              </Button>
            </Paper>
                    )}
                  </>
                )}

      {/* Snackbar para feedbacks */}
      <Snackbar 
        open={snackbarOpen} 
        autoHideDuration={6000} 
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </Container>
  </div>
);
};

export default App;