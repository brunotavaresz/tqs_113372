// src/App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import theme from './theme/theme';

// Layout Components
import Navbar from './components/layout/Navbar';
import Footer from './components/layout/Footer';
import PageContainer from './components/layout/PageContainer';

// Pages
import Home from './components/pages/Home';
import MealPlanning from './components/pages/MealPlanning';
import BookReservation from './components/pages/BookReservation';
import CheckReservation from './components/pages/CheckReservation';
import StaffVerification from './components/pages/StaffVerification';
import CacheMonitor from './components/pages/CacheMonitor';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Navbar />
        <PageContainer>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/book" element={<BookReservation />} />
            <Route path="/check-reservation" element={<CheckReservation />} />
            <Route path="/staff" element={<StaffVerification />} />
            <Route path="/monitor" element={<CacheMonitor />} />
          </Routes>
        </PageContainer>
        <Footer />
      </Router>
    </ThemeProvider>
  );
}

export default App;
