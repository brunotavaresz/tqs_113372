import http from "k6/http";
import { check } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:3333";

export const options = {
  stages: [
    // Ramp-up: de 0 para 20 VUs nos próximos 5 segundos
    { duration: "5s", target: 20 },
    // Manter 20 VUs por 10 segundos
    { duration: "10s", target: 20 },
    // Ramp-down: de 20 para 0 VUs nos próximos 5 segundos
    { duration: "5s", target: 0 },
  ],
};

export default function () {
  let restrictions = {
    maxCaloriesPerSlice: 500,
    mustBeVegetarian: false,
    excludedIngredients: ["pepperoni"],
    excludedTools: ["knife"],
    maxNumberOfToppings: 6,
    minNumberOfToppings: 2,
  };

  let res = http.post(`${BASE_URL}/api/pizza`, JSON.stringify(restrictions), {
    headers: {
      "Content-Type": "application/json",
      "Authorization": "token abcdef0123456789",
      "X-User-ID": 23423,
    },
  });

  check(res, {
    "Status é 200": (r) => r.status === 200,
  });

  console.log(`Status: ${res.status}`);
  console.log(`Duração da requisição: ${res.timings.duration}ms`);
}
