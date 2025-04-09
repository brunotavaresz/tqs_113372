import React from 'react';
import { 
  Card, 
  CardContent, 
  CardActions, 
  Typography, 
  Button, 
  Grid, 
  Chip, 
  Box,
  Divider
} from '@mui/material';
import { formatDate } from '../../utils/dateFormatter';

const MealCard = ({ 
  meal, 
  weather, 
  onBookClick,
  expanded = false 
}) => {
  return (
    <Card elevation={3} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent sx={{ flexGrow: 1 }}>
        <Grid container spacing={2}>
          <Grid item xs={8}>
            <Typography gutterBottom variant="h5" component="div">
              {meal.name}
            </Typography>
            <Typography variant="subtitle1" color="text.secondary" gutterBottom>
              {formatDate(meal.date, 'long')}
            </Typography>
          </Grid>
          
        </Grid>
        
        <Divider sx={{ my: 2 }} />
        
        <Box sx={{ mb: 2 }}>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Available: {meal.availablePortions} / {meal.totalPortions} portions
          </Typography>
          <Typography variant="h6" color="primary" sx={{ fontWeight: 'bold', mb: 1 }}>
            {meal.price.toFixed(2)}€
          </Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, mb: 2 }}>
            {meal.tags && meal.tags.map((tag) => (
              <Chip key={tag} label={tag} size="small" />
            ))}
          </Box>
        </Box>
        
        {expanded && (
          <>
            <Typography variant="body1" paragraph>
              {meal.description}
            </Typography>
            <Typography variant="subtitle2" gutterBottom>
              Menu Items:
            </Typography>
            <ul>
              {meal.menuItems && meal.menuItems.map((item, index) => (
                <li key={index}>
                  <Typography variant="body2">{item}</Typography>
                </li>
              ))}
            </ul>
            <Typography variant="subtitle2" gutterBottom sx={{ mt: 2 }}>
              Restaurant:
            </Typography>
            <Typography variant="body2" paragraph>
              {meal.restaurant?.name} - {meal.restaurant?.location}
            </Typography>
          </>
        )}
      </CardContent>
      <CardActions>
        <Button 
          size="small" 
          variant="contained" 
          color="primary" 
          disabled={meal.availablePortions <= 0}
          fullWidth
          onClick={() => onBookClick && onBookClick(meal)}
        >
          {meal.availablePortions <= 0 ? 'Sold Out' : 'Book Now'}
        </Button>
      </CardActions>
    </Card>
  );
};

export default MealCard;