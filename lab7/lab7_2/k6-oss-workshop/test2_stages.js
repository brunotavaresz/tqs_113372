import http from "k6/http";
import { check, group } from "k6";
import { Rate } from "k6/metrics";

// Use environment variable for base URL or default to localhost
const BASE_URL = __ENV.BASE_URL || "http://localhost:3333";

// Define export options for load test stages and thresholds
export const options = {
  stages: [
    // Ramp-up from 0 to 20 VUs over 30 seconds
    { duration: "30s", target: 20 },
    // Maintain 20 VUs for 30 seconds
    { duration: "30s", target: 20 },
    // Ramp-down from 20 to 0 VUs over 30 seconds
    { duration: "30s", target: 0 },
  ],

  // Service Level Objectives (SLOs) as thresholds
  thresholds: {
    // 95% of requests must be under 1.1 seconds
    'http_req_duration': ['p(95)<1100'],
    
    // Error rate must be less than 2%
    'http_req_failed': ['rate<0.02'],
    
    // Additional custom metric for tracking errors
    'http_errors': ['rate<0.02']
  }
};

// Custom error rate metric
const errorRate = new Rate('http_errors');

export default function () {
  group('Pizza API Request Group', () => {
    // Prepare pizza restrictions
    let restrictions = {
      maxCaloriesPerSlice: 500,
      mustBeVegetarian: false,
      excludedIngredients: ["pepperoni"],
      excludedTools: ["knife"],
      maxNumberOfToppings: 6,
      minNumberOfToppings: 2,
    };

    // Make POST request to Pizza API
    const res = http.post(`${BASE_URL}/api/pizza`, JSON.stringify(restrictions), {
      headers: {
        "Content-Type": "application/json",
        "Authorization": "token abcdef0123456789",
        "X-User-ID": 23423,
      },
    });

    // Perform comprehensive checks on the response
    const checkResult = check(res, {
      // Check for successful HTTP status code (200)
      'Status is 200': (r) => r.status === 200,
      
      // Check response body size is less than 1KB
      'body size < 1KB': (r) => r.body && r.body.length < 1024,
      
      // Optional: Check for specific content in response
      'response contains expected properties': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.hasOwnProperty('pizza') || body.hasOwnProperty('id');
        } catch {
          return false;
        }
      }
    });

    // Log request details
    console.log(`Status: ${res.status}`);
    console.log(`Request Duration: ${res.timings.duration}ms`);

    // Track error rate for custom metric
    errorRate.add(!checkResult);
  });
}