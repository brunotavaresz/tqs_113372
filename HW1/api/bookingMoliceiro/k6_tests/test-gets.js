import http from 'k6/http';
import { check, sleep } from 'k6';

export default function () {
  // Teste para o endpoint /restaurants
  const urlRestaurants = 'http://localhost:8080/restaurants';  // Altere para o endpoint correto
  let resRestaurants = http.get(urlRestaurants);
  
  // Verifica o status e o tempo de resposta para /restaurants
  check(resRestaurants, {
    'status é 200 para /restaurants': (r) => r.status === 200,
    'duração da resposta < 500ms para /restaurants': (r) => r.timings.duration < 500,
  });

  // Teste para o endpoint /reservations
  const urlReservations = 'http://localhost:8080/reservations';  // Altere para o endpoint correto
  let resReservations = http.get(urlReservations);
  
  // Verifica o status e o tempo de resposta para /reservations
  check(resReservations, {
    'status é 200 para /reservations': (r) => r.status === 200,
    'duração da resposta < 500ms para /reservations': (r) => r.timings.duration < 500,
  });

  // Teste para o endpoint /meals
  const urlMeals = 'http://localhost:8080/meals';  // Altere para o endpoint correto
  let resMeals = http.get(urlMeals);

  // Verifica o status e o tempo de resposta para /meals
  check(resMeals, {
    'status é 200 para /meals': (r) => r.status === 200,
    'duração da resposta < 500ms para /meals': (r) => r.timings.duration < 500,
  });

  // Aguarda 1 segundo antes de enviar a próxima requisição
  sleep(1);
}
