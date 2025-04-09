import http from 'k6/http';
import { check, sleep } from 'k6';

export default function () {
  // Endpoint /restaurants
  const urlRestaurants = 'http://localhost:8080/restaurants';
  const restaurantPayload = JSON.stringify({
    name: 'Restaurant Test',
    location: 'Test Location',
    photoUrl: 'http://testurl.com/photo.jpg',
    maxCapacity: 100,
  });

  const restaurantParams = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  let resRestaurant = http.post(urlRestaurants, restaurantPayload, restaurantParams);

  check(resRestaurant, {
    'status é 201 para POST /restaurants': (r) => r.status === 201,
    'duração da resposta < 500ms para /restaurants': (r) => r.timings.duration < 500,
  });

  const urlMeals = 'http://localhost:8080/meals';
  const mealPayload = JSON.stringify({
    name: 'Meal Test',
    price: 20.5,
    date: '2025-04-11',
    time: '18:00',     
    restaurantId: JSON.parse(resRestaurant.body).id, 
  });

  let resMeal = http.post(urlMeals, mealPayload, restaurantParams);

  check(resMeal, {
    'status é 201 para POST /meals': (r) => r.status === 201,
    'duração da resposta < 500ms para /meals': (r) => r.timings.duration < 500,
  });

  sleep(1);
}
