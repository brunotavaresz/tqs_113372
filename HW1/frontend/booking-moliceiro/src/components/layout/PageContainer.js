// src/components/layout/PageContainer.js
import React from 'react';
import { Box, Container } from '@mui/material';

const PageContainer = ({ children }) => {
  return (
    <Box
      component="main"
      sx={{
        flexGrow: 1,
        py: 4,
        minHeight: 'calc(100vh - 128px)', // Altura total - (navbar + footer)
      }}
    >
      <Container maxWidth="xl">{children}</Container>
    </Box>
  );
};

export default PageContainer;
