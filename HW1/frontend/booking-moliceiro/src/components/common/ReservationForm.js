import React, { useState, useEffect } from 'react';
import { TextField, Button, FormControl, InputLabel, Select, MenuItem, Typography } from '@mui/material';

const ReservationForm = ({ restaurants, onReserve }) => {
  const [selectedRestaurant, setSelectedRestaurant] = useState('');
  const [menu, setMenu] = useState([]);
  const [reservationCode, setReservationCode] = useState('');

  useEffect(() => {
    if (selectedRestaurant) {
      // Chamada API para pegar o menu do restaurante
      // Exemplo de API: /api/menus/{selectedRestaurant}
      // Aqui você precisará integrar a API para pegar o menu do restaurante selecionado.
      // setMenu(response.data);
    }
  }, [selectedRestaurant]);

  const handleReservationSubmit = () => {
    if (!selectedRestaurant || !menu.length) {
      // Verificar se há dados válidos
      return;
    }

    // Chamada API para fazer a reserva
    // Exemplo de API: /api/reservations
    // Aqui você precisará integrar a API para fazer a reserva e retornar o código.
    // setReservationCode(response.data.code);
    onReserve(); // Passar o código de reserva para o componente pai
  };

  return (
    <div>
      <Typography variant="h5">Faça sua Reserva</Typography>

      <FormControl fullWidth margin="normal">
        <InputLabel id="restaurant-label">Restaurante</InputLabel>
        <Select
          labelId="restaurant-label"
          value={selectedRestaurant}
          onChange={(e) => setSelectedRestaurant(e.target.value)}
        >
          {restaurants.map((restaurant) => (
            <MenuItem key={restaurant.id} value={restaurant.id}>
              {restaurant.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <Typography variant="h6">Menu:</Typography>
      {menu.map((item) => (
        <Typography key={item.id}>{item.name} - {item.price}</Typography>
      ))}

      <Button variant="contained" color="primary" onClick={handleReservationSubmit}>
        Reservar
      </Button>

      {reservationCode && (
        <Typography variant="body1" color="green" marginTop={2}>
          Código da reserva: {reservationCode}
        </Typography>
      )}
    </div>
  );
};

export default ReservationForm;
