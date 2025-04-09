// src/components/layout/Footer.js
import React from 'react';
import { Box, Container, Typography, Grid, Link, Divider } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import PhoneIcon from '@mui/icons-material/Phone';
import EmailIcon from '@mui/icons-material/Email';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';

const Footer = () => {
  return (
    <Box
      component="footer"
      sx={{
        py: 4,
        bgcolor: 'background.paper',
        borderTop: '1px solid',
        borderColor: 'divider',
      }}
    >
      <Container maxWidth="xl">
        <Grid container spacing={4}>
          <Grid item xs={12} sm={6} md={3}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <CalendarTodayIcon sx={{ mr: 1, color: 'primary.main' }} />
              <Typography variant="h6" component="div" fontWeight="bold">
                CampusEmentas
              </Typography>
            </Box>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Sistema de reserva de refeições do campus universitário Moliceiro.
            </Typography>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Typography variant="h6" sx={{ mb: 2 }}>Links Rápidos</Typography>
            <Link component={RouterLink} to="/" color="inherit" display="block" sx={{ mb: 1 }}>
              Início
            </Link>
            <Link component={RouterLink} to="/book" color="inherit" display="block" sx={{ mb: 1 }}>
              Reservar Refeição
            </Link>
            <Link component={RouterLink} to="/check-reservation" color="inherit" display="block">
              Consultar Reserva
            </Link>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Typography variant="h6" sx={{ mb: 2 }}>Restaurantes</Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <RestaurantIcon sx={{ mr: 1, fontSize: 'small', color: 'primary.main' }} />
              <Typography variant="body2">Cantina Central</Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <RestaurantIcon sx={{ mr: 1, fontSize: 'small', color: 'primary.main' }} />
              <Typography variant="body2">Restaurante Santiago</Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <RestaurantIcon sx={{ mr: 1, fontSize: 'small', color: 'primary.main' }} />
              <Typography variant="body2">Cafeteria DETI</Typography>
            </Box>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Typography variant="h6" sx={{ mb: 2 }}>Contato</Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <LocationOnIcon sx={{ mr: 1, fontSize: 'small', color: 'primary.main' }} />
              <Typography variant="body2">Campus Universitário de Aveiro</Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <PhoneIcon sx={{ mr: 1, fontSize: 'small', color: 'primary.main' }} />
              <Typography variant="body2">+351 234 370 200</Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <EmailIcon sx={{ mr: 1, fontSize: 'small', color: 'primary.main' }} />
              <Typography variant="body2">refeicoes@moliceiro.ua.pt</Typography>
            </Box>
          </Grid>
        </Grid>
        
        <Divider sx={{ my: 3 }} />
        
        <Typography variant="body2" color="text.secondary" align="center">
          © {new Date().getFullYear()} CampusEmentas | Universidade Moliceiro | Desenvolvido para TQS HW1
        </Typography>
      </Container>
    </Box>
  );
};

export default Footer;