import React from 'react';
import { 
  Box, 
  Typography, 
  Button, 
  Grid, 
  Card, 
  CardContent, 
  CardMedia,
  Paper,
  Stack
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import SearchIcon from '@mui/icons-material/Search';
import { Link as RouterLink } from 'react-router-dom';

import Cantina1 from '../../assets/cantina1.jpeg';
import Cantina2 from '../../assets/cantina2.jpeg';
import Cafetaria1 from '../../assets/cafetaria1.png';
import Banner3 from '../../assets/banner4.jpeg';

const Home = () => {
  const theme = useTheme();

  // Estilo comum para os cards
  const cardStyle = {
    height: '100%',
    display: 'flex', 
    flexDirection: 'column',
    width: '100%'
  };

  // Estilo para o texto descritivo
  const descriptionStyle = {
    mb: 3,
    wordWrap: 'break-word',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    hyphens: 'auto'
  };

  return (
    <Box>
      {/* Hero Section */}
      <Paper
        sx={{
          position: 'relative',
          color: 'white',
          mb: 4,
          backgroundSize: 'cover',
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center',
          backgroundImage: `url(${Banner3})`,
          borderRadius: 2,
          overflow: 'hidden'
        }}
      >
        <Box
          sx={{
            position: 'absolute',
            top: 0,
            bottom: 0,
            right: 0,
            left: 0,
            backgroundColor: 'rgba(0,0,0,.5)',
          }}
        />
        <Box
          sx={{
            position: 'relative',
            p: { xs: 3, md: 6 },
            height: 300,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
          }}
        >
          <Typography component="h1" variant="h3" gutterBottom fontWeight="bold">
            Refeições do Campus Moliceiro
          </Typography>
          <Typography variant="h5" sx={{ mb: 4, maxWidth: { xs: '100%', md: '60%' } }}>
            Reserve sua refeição com antecedência e evite filas. Consulte cardápios e previsão do tempo.
          </Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <Button 
              variant="contained" 
              size="large" 
              component={RouterLink} 
              to="/book"
              startIcon={<RestaurantIcon />}
            >
              Ver Restaurantes
            </Button>
            <Button 
              variant="outlined" 
              size="large" 
              component={RouterLink} 
              to="/check-reservation"
              startIcon={<EventAvailableIcon />}
              sx={{ backgroundColor: 'rgba(255,255,255,0.9)' }}
            >
              Gerir Reserva
            </Button>
          </Stack>
        </Box>
      </Paper>

      {/* Features Section */}
      <Typography variant="h4" component="h2" sx={{ mb: 4 }}>
        Como Funciona
      </Typography>
      
      <Box sx={{ mb: 6 }}>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', mx: -2 }}>
          {[
            {
              title: 'Visualize os Restaurantes',
              description: 'Consulte os restaurantes de todas as unidades do campus para os próximos dias, incluindo a previsão do tempo.',
              icon: <RestaurantIcon fontSize="large" color="primary" />,
              link: '/book'
            },
            {
              title: 'Faça Sua Reserva',
              description: 'Reserve com antecedência para garantir sua refeição e evitar filas. Você receberá um código de confirmação.',
              icon: <EventAvailableIcon fontSize="large" color="primary" />,
              link: '/book'
            },
            {
              title: 'Consulte Sua Reserva',
              description: 'Verifique os detalhes da sua reserva ou cancele se necessário usando o código da reserva.',
              icon: <SearchIcon fontSize="large" color="primary" />,
              link: '/check-reservation'
            }
          ].map((feature, index) => (
            <Box 
              key={index} 
              sx={{ 
                width: { xs: '100%', sm: '50%', md: '33.33%' }, 
                p: 2,
                boxSizing: 'border-box'
              }}
            >
              <Card sx={cardStyle}>
                <CardContent sx={{ flexGrow: 1, textAlign: 'center', p: 3 }}>
                  <Box sx={{ mb: 2 }}>
                    {feature.icon}
                  </Box>
                  <Typography variant="h5" component="h3" gutterBottom>
                    {feature.title}
                  </Typography>
                  <Typography color="text.secondary" sx={descriptionStyle}>
                    {feature.description}
                  </Typography>
                  <Button 
                    variant="outlined" 
                    component={RouterLink} 
                    to={feature.link}
                  >
                    Acessar
                  </Button>
                </CardContent>
              </Card>
            </Box>
          ))}
        </Box>
      </Box>

      {/* Restaurants Section */}
      <Typography variant="h4" component="h2" sx={{ mb: 4 }}>
        Alguns dos Nossos Restaurantes
      </Typography>
      
      <Box>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', mx: -2 }}>
          {[
            {
              name: 'Cantina Central',
              image: Cantina1,
              description: 'Localizada no coração do campus, oferece grande variedade de pratos a preços acessíveis.'
            },
            {
              name: 'Restaurante Santiago',
              image: Cantina2,
              description: 'Ambiente agradável com opções mais elaboradas e serviço de buffet no almoço.'
            },
            {
              name: 'Cafeteria DETI',
              image: Cafetaria1,
              description: 'Opções rápidas para o dia a dia, com sanduíches, saladas e refeições leves.'
            }
          ].map((restaurant, index) => (
            <Box 
              key={index} 
              sx={{ 
                width: { xs: '100%', sm: '50%', md: '33.33%' }, 
                p: 2,
                boxSizing: 'border-box'
              }}
            >
              <Card sx={cardStyle}>
                <CardMedia
                  component="img"
                  height="200"
                  image={restaurant.image}
                  alt={restaurant.name}
                />
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography variant="h5" component="h3" gutterBottom>
                    {restaurant.name}
                  </Typography>
                  <Typography color="text.secondary" sx={descriptionStyle}>
                    {restaurant.description}
                  </Typography>
                </CardContent>
              </Card>
            </Box>
          ))}
        </Box>
      </Box>
    </Box>
  );
};

export default Home;