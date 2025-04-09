import React, { useState } from 'react';
import { TextField, Button, Typography } from '@mui/material';

const ReservationCode = ({ onCancel }) => {
  const [reservationCode, setReservationCode] = useState('');
  const [reservationDetails, setReservationDetails] = useState(null);

  const handleCheckReservation = () => {
    if (!reservationCode) {
      // Verificar se o código da reserva foi fornecido
      return;
    }

    // Chamada API para buscar os detalhes da reserva
    // Exemplo de API: /api/reservations/{reservationCode}
    // Aqui você precisará integrar a API para buscar os detalhes da reserva.
    // setReservationDetails(response.data);
  };

  const handleCancelReservation = () => {
    // Chamada API para cancelar a reserva
    // Exemplo de API: /api/reservations/{reservationCode}/cancel
    // Aqui você precisará integrar a API para cancelar a reserva.
    onCancel(); // Passar os dados de cancelamento para o componente pai
  };

  return (
    <div>
      <Typography variant="h5">Verifique sua Reserva</Typography>

      <TextField
        label="Código da Reserva"
        value={reservationCode}
        onChange={(e) => setReservationCode(e.target.value)}
        fullWidth
        margin="normal"
      />

      <Button variant="contained" color="primary" onClick={handleCheckReservation}>
        Verificar Reserva
      </Button>

      {reservationDetails && (
        <div>
          <Typography variant="h6" gutterBottom>
            Detalhes da reserva:
          </Typography>
          <Typography>Restaurante: {reservationDetails.restaurant}</Typography>
          <Typography>Menu: {reservationDetails.menu}</Typography>
          <Typography>Status: {reservationDetails.status}</Typography>

          <Button variant="outlined" color="secondary" onClick={handleCancelReservation}>
            Cancelar Reserva
          </Button>
        </div>
      )}
    </div>
  );
};

export default ReservationCode;
