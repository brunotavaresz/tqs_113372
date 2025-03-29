import http from "k6/http";
import { check } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:3333";

export let options = {
  vus: 2,
  iterations: 10
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

  // Verificação da resposta
  check(res, {
    "Status é 200": (r) => r.status === 200,
  });

  console.log(`Status: ${res.status}`);
  console.log(`Duração da requisição: ${res.timings.duration}ms`);
}
