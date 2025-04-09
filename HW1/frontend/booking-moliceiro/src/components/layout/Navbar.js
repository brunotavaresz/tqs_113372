// src/components/layout/Navbar.js
import React, { useState } from 'react';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import {
  AppBar,
  Box,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
  useMediaQuery,
  Container,
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import MenuIcon from '@mui/icons-material/Menu';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import BookmarkAddIcon from '@mui/icons-material/BookmarkAdd';
import SearchIcon from '@mui/icons-material/Search';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import SpeedIcon from '@mui/icons-material/Speed';
import HomeIcon from '@mui/icons-material/Home';

const navigation = [
  { name: 'Início', path: '/', icon: <HomeIcon /> },
  { name: 'Reservar', path: '/book', icon: <BookmarkAddIcon /> },
  { name: 'Consultar Reserva', path: '/check-reservation', icon: <SearchIcon /> },
  //{ name: 'Verificação (Staff)', path: '/staff', icon: <VerifiedUserIcon /> },
  { name: 'Monitoramento', path: '/monitor', icon: <SpeedIcon /> },
];

const Navbar = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [mobileOpen, setMobileOpen] = useState(false);
  const location = useLocation();

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const drawer = (
    <Box onClick={handleDrawerToggle} sx={{ textAlign: 'center' }}>
      <Box sx={{ display: 'flex', alignItems: 'center', p: 2 }}>
        <CalendarTodayIcon sx={{ mr: 1, color: theme.palette.primary.main }} />
        <Typography variant="h6" component="div" fontWeight="bold">
          CampusEmentas
        </Typography>
      </Box>
      <Divider />
      <List>
        {navigation.map((item) => (
          <ListItem 
            key={item.name} 
            component={RouterLink} 
            to={item.path}
            selected={location.pathname === item.path}
            sx={{ 
              color: location.pathname === item.path ? theme.palette.primary.main : 'inherit',
              '&.Mui-selected': {
                backgroundColor: 'rgba(25, 118, 210, 0.08)'
              }
            }}
          >
            <ListItemIcon sx={{ 
              color: location.pathname === item.path ? theme.palette.primary.main : 'inherit',
              minWidth: '40px'
            }}>
              {item.icon}
            </ListItemIcon>
            <ListItemText primary={item.name} />
          </ListItem>
        ))}
      </List>
    </Box>
  );

  return (
    <>
      <AppBar position="fixed" color="default" elevation={1}>
        <Container maxWidth="xl">
          <Toolbar disableGutters>
            {isMobile ? (
              <>
                <IconButton
                  color="inherit"
                  aria-label="open drawer"
                  edge="start"
                  onClick={handleDrawerToggle}
                  sx={{ mr: 2 }}
                >
                  <MenuIcon />
                </IconButton>
                <Box sx={{ display: 'flex', alignItems: 'center', flexGrow: 1, justifyContent: 'center' }}>
                  <CalendarTodayIcon sx={{ mr: 1, color: theme.palette.primary.main }} />
                  <Typography variant="h6" component="div" fontWeight="bold">
                    CampusEmentas
                  </Typography>
                </Box>
              </>
            ) : (
              <>
                <Box sx={{ display: 'flex', alignItems: 'center', mr: 4 }}>
                  <CalendarTodayIcon sx={{ mr: 1, color: theme.palette.primary.main }} />
                  <Typography variant="h6" component="div" fontWeight="bold">
                    CampusEmentas
                  </Typography>
                </Box>
                <Box sx={{ flexGrow: 1, display: 'flex' }}>
                  {navigation.map((item) => (
                    <Button
                      key={item.name}
                      component={RouterLink}
                      to={item.path}
                      startIcon={item.icon}
                      sx={{
                        mx: 1,
                        py: 1,
                        color: location.pathname === item.path ? theme.palette.primary.main : 'text.primary',
                        fontWeight: location.pathname === item.path ? 'bold' : 'normal',
                        borderBottom: location.pathname === item.path ? `2px solid ${theme.palette.primary.main}` : 'none',
                        borderRadius: 0,
                        '&:hover': {
                          backgroundColor: 'transparent',
                          borderBottom: `2px solid ${theme.palette.primary.light}`,
                        }
                      }}
                    >
                      {item.name}
                    </Button>
                  ))}
                </Box>
              </>
            )}
          </Toolbar>
        </Container>
      </AppBar>
      <Box component="nav">
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true,
          }}
          sx={{
            display: { xs: 'block', md: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: 280 },
          }}
        >
          {drawer}
        </Drawer>
      </Box>
      <Toolbar /> {/* Este Toolbar é para compensar o espaço do AppBar fixo */}
    </>
  );
};

export default Navbar;