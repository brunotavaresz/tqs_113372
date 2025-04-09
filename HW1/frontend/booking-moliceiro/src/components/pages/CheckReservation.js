import React, { useState, useEffect } from 'react';
import { 
  Container, Box, Typography, TextField, Button, 
  Card, CardContent, CardActions, Dialog, DialogTitle, 
  DialogContent, DialogActions, Alert, CircularProgress,
  FormControlLabel, Checkbox, Grid
} from '@mui/material';

const ReservationManagement = () => {
  // State for reservation code input
  const [reservationCode, setReservationCode] = useState('');
  
  // State for current reservation details
  const [reservation, setReservation] = useState(null);
  
  // State for all reservations (for list view)
  const [allReservations, setAllReservations] = useState([]);
  
  // State for loading status
  const [loading, setLoading] = useState(false);
  
  // State for error messages
  const [error, setError] = useState('');
  
  // State for success messages
  const [successMessage, setSuccessMessage] = useState('');
  
  // State for edit dialog
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  
  // State for edit form
  const [editForm, setEditForm] = useState({
    checkedIn: false,
    restaurantId: '',
    mealId: '',
    mealName: '',
    mealPrice: '',
    mealDate: '',
  });

  // State for delete confirmation dialog
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  
  // API Base URL - adjust this to match your API endpoint
  const API_BASE_URL = 'http://localhost:8080';
  
  // Fetch all reservations on component mount
  useEffect(() => {
    fetchAllReservations();
  }, []);

  // Function to fetch all reservations
  const fetchAllReservations = async () => {
    setLoading(true);
    setError('');
    
    try {
      const response = await fetch(`${API_BASE_URL}/reservations`, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        throw new Error(`Error ${response.status}: ${response.statusText}`);
      }
      
      const data = await response.json();
      console.log("Reservations data:", data); // Debug log
      setAllReservations(data);
    } catch (err) {
      console.error("Fetch error:", err);
      setError(`Failed to fetch reservations: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Function to fetch a single reservation by code
  const fetchReservation = async () => {
    if (!reservationCode.trim()) {
      setError('Please enter a reservation code');
      return;
    }
    
    setLoading(true);
    setError('');
    setSuccessMessage('');
    
    try {
      const response = await fetch(`${API_BASE_URL}/reservations/${reservationCode}`, {
        headers: {
          'Accept': 'application/json'
        }
      });
      
      if (!response.ok) {
        throw new Error(`Error ${response.status}: ${response.statusText}`);
      }
      
      const data = await response.json();
      console.log("Single reservation data:", data); // Debug log
      setReservation(data);
      
      // Pre-fill the edit form with current values
      setEditForm({
        checkedIn: data.checkedIn,
        restaurantId: data.restaurant.id,
        mealId: data.meal.id,
        mealName: data.meal.name,
        mealPrice: data.meal.price,
        mealDate: data.meal.date,
      });
    } catch (err) {
      console.error("Fetch error:", err);
      setError(`Failed to fetch reservation: ${err.message}`);
      setReservation(null);
    } finally {
      setLoading(false);
    }
  };

  // Function to check in a reservation
  const handleCheckIn = async () => {
    if (!reservation) return;
    
    setLoading(true);
    setError('');
    setSuccessMessage('');
    
    try {
      const response = await fetch(`${API_BASE_URL}/reservations/${reservation.reservationCode}/check-in`, {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        throw new Error(`Error ${response.status}: ${response.statusText}`);
      }
      
      setSuccessMessage('Check-in successful!');
      
      // Refresh the reservation data
      fetchReservation();
      fetchAllReservations();
    } catch (err) {
      console.error("Check-in error:", err);
      setError(`Failed to check in: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Function to delete a reservation
  const handleDelete = async () => {
    if (!reservation) return;
    
    setLoading(true);
    setError('');
    setSuccessMessage('');
    
    try {
      const response = await fetch(`${API_BASE_URL}/reservations/${reservation.reservationCode}`, {
        method: 'DELETE',
        headers: {
          'Accept': 'application/json'
        }
      });
      
      if (!response.ok) {
        throw new Error(`Error ${response.status}: ${response.statusText}`);
      }
      
      setSuccessMessage('Reservation deleted successfully!');
      setReservation(null);
      setDeleteDialogOpen(false);
      
      // Refresh the reservations list
      fetchAllReservations();
    } catch (err) {
      console.error("Delete error:", err);
      setError(`Failed to delete reservation: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Function to update a reservation
  const handleUpdate = async () => {
    if (!reservation) return;
    
    setLoading(true);
    setError('');
    setSuccessMessage('');
    
    try {
      const updatedReservation = {
        id: reservation.id,
        reservationCode: reservation.reservationCode,
        checkedIn: editForm.checkedIn,
        restaurant: {
          id: editForm.restaurantId,
        },
        meal: {
          id: editForm.mealId,
          name: editForm.mealName,
          price: parseFloat(editForm.mealPrice),
          date: editForm.mealDate,
          restaurant: {
            id: editForm.restaurantId
          }
        }
      };
      
      console.log("Sending update:", updatedReservation); // Debug log
      
      const response = await fetch(`${API_BASE_URL}/reservations/${reservation.reservationCode}`, {
        method: 'PUT',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedReservation),
      });
      
      if (!response.ok) {
        throw new Error(`Error ${response.status}: ${response.statusText}`);
      }
      
      const data = await response.json();
      setReservation(data);
      setSuccessMessage('Reservation updated successfully!');
      setEditDialogOpen(false);
      
      // Refresh the reservations list
      fetchAllReservations();
    } catch (err) {
      console.error("Update error:", err);
      setError(`Failed to update reservation: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Function to handle input changes in the edit form
  const handleEditFormChange = (e) => {
    const { name, value, type, checked } = e.target;
    setEditForm(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  // Function to select a reservation from the list
  const selectReservation = (code) => {
    setReservationCode(code);
  };

  return (
    <Container maxWidth="lg">
      <Typography variant="h4" component="h1" gutterBottom align="center" sx={{ mt: 4 }}>
        Gestão de Reservas
      </Typography>
      
      {/* Search bar */}
      <Box sx={{ display: 'flex', mb: 4, mt: 4 }}>
        <TextField
          label="Reservation Code"
          variant="outlined"
          fullWidth
          value={reservationCode}
          onChange={(e) => setReservationCode(e.target.value)}
        />
        <Button 
          variant="contained" 
          color="primary" 
          sx={{ ml: 2 }}
          onClick={fetchReservation}
          disabled={loading}
        >
          {loading ? <CircularProgress size={24} /> : 'Procurar'}
        </Button>
      </Box>
      
      {/* Error and success messages */}
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>
      )}
      
      {successMessage && (
        <Alert severity="success" sx={{ mb: 2 }}>{successMessage}</Alert>
      )}
      
      {/* Reservation details */}
      {reservation && (
        <Card variant="outlined" sx={{ mb: 4 }}>
          <CardContent>
              <Typography variant="h5" component="h2" gutterBottom>
                Reserva: {reservation.reservationCode}
              </Typography>

              <Grid container spacing={2}>
                {/* Restaurante */}
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" component="div" color="text.secondary">
                    Restaurante:
                  </Typography>
                  <Typography variant="body1" component="div" gutterBottom>
                    {reservation.restaurant.name || 'N/A'} ({reservation.restaurant.location || 'N/A'})
                  </Typography>
                </Grid>

                {/* Cliente */}
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" component="div" color="text.secondary">
                    Cliente:
                  </Typography>
                  <Typography variant="body1" component="div" gutterBottom>
                    {reservation.customerName || 'N/A'}
                  </Typography>
                </Grid>

                {/* Refeição */}
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" component="div" color="text.secondary">
                    Prato:
                  </Typography>
                  <Typography variant="body1" component="div" gutterBottom>
                    {reservation.meal.name} - €{reservation.meal.price.toFixed(2)}
                  </Typography>
                  <Typography variant="body2" component="div" color="text.secondary">
                    Data: {reservation.meal.date}
                  </Typography>
                </Grid>

                {/* Estado */}
                <Grid item xs={12}>
                  <Typography variant="subtitle1" component="div" color="text.secondary">
                    Status:
                  </Typography>
                  <Typography
                    variant="body1"
                    component="div"
                    color={reservation.checkedIn ? "success.main" : "warning.main"}
                    gutterBottom
                  >
                    {reservation.checkedIn ? 'Checked In' : 'Not Checked In'}
                  </Typography>
                </Grid>
              </Grid>
            </CardContent>

          
          <CardActions>
            {!reservation.checkedIn && (
              <Button 
                size="small" 
                color="success" 
                onClick={handleCheckIn}
                disabled={loading}
              >
                Check In
              </Button>
            )}
            
            {/* 
            <Button 
              size="small" 
              color="primary" 
              onClick={() => setEditDialogOpen(true)}
              disabled={loading}
            >
              Edit
            </Button> */}
            
            <Button 
              size="small" 
              color="error" 
              onClick={() => setDeleteDialogOpen(true)}
              disabled={loading}
            >
              Eliminar
            </Button>
          </CardActions>
        </Card>
      )}

      
      {/* All reservations list */}
      <Typography variant="h5" component="h2" gutterBottom>
        Todas as Reservas
      </Typography>
      
      {loading && allReservations.length === 0 ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
          <CircularProgress />
        </Box>
      ) : allReservations.length > 0 ? (
        <Grid container spacing={2}>
          {allReservations.map((res) => (
            <Grid item xs={12} sm={6} md={4} key={res.id}>
              <Card variant="outlined">
                <CardContent>
                  <Typography variant="h6" component="div">
                    {res.reservationCode}
                  </Typography>
                  <Box sx={{ mt: 1 }}>
                    <Typography variant="body2" component="div" color="text.secondary">
                      Restaurante:
                    </Typography>
                    <Typography variant="body2" component="div">
                      {res.restaurant.name}
                    </Typography>
                  </Box>
                  <Box sx={{ mt: 1 }}>
                    <Typography variant="body2" component="div" color="text.secondary">
                      Prato:
                    </Typography>
                    <Typography variant="body2" component="div">
                      {res.meal.name} - {res.meal.date}
                    </Typography>
                  </Box>
                  <Box sx={{ mt: 1 }}>
                    <Typography variant="body2" component="div" color="text.secondary">
                      Status:
                    </Typography>
                    <Typography 
                      variant="body2" 
                      component="div"
                      color={res.checkedIn ? "success.main" : "warning.main"}
                    >
                      {res.checkedIn ? 'Checked In' : 'Not Checked In'}
                    </Typography>
                  </Box>
                </CardContent>
                <CardActions>
                  <Button 
                    size="small" 
                    onClick={() => selectReservation(res.reservationCode)}
                  >
                    Detalhes
                  </Button>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      ) : (
        <Alert severity="info" sx={{ my: 2 }}>Sem reservas.</Alert>
      )}
      
      {/* Edit Reservation Dialog */}
      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Editar Reserva</DialogTitle>
        <DialogContent>
          <Box component="form" sx={{ mt: 2 }}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={editForm.checkedIn}
                  onChange={handleEditFormChange}
                  name="checkedIn"
                />
              }
              label="Checked In"
            />
            
            <TextField
              margin="dense"
              name="mealName"
              label="Meal Name"
              type="text"
              fullWidth
              variant="outlined"
              value={editForm.mealName}
              onChange={handleEditFormChange}
              sx={{ mb: 2 }}
            />
            
            <TextField
              margin="dense"
              name="mealPrice"
              label="Price"
              type="number"
              fullWidth
              variant="outlined"
              value={editForm.mealPrice}
              onChange={handleEditFormChange}
              sx={{ mb: 2 }}
            />
            
            <TextField
              margin="dense"
              name="mealDate"
              label="Date"
              type="date"
              fullWidth
              variant="outlined"
              value={editForm.mealDate}
              onChange={handleEditFormChange}
              InputLabelProps={{
                shrink: true,
              }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>Cancelar</Button>
          <Button onClick={handleUpdate} variant="contained" color="primary" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Save Changes'}
          </Button>
        </DialogActions>
      </Dialog>
      
      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Confirmar exclusão</DialogTitle>
        <DialogContent>
          <Typography variant="body1" component="div">
            Certeza que quer eliminar a reserva? Não dá para voltar atrás.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>Cancelar</Button>
          <Button onClick={handleDelete} color="error" variant="contained" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Eliminar'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ReservationManagement;