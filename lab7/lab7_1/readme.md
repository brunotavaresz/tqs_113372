# k6 Test Results


    execution: local
        script: test.js
        output: -

     scenarios: (100.00%) 1 scenario, 2 max VUs, 10m30s max duration (incl. graceful stop):
              * default: 10 iterations shared among 2 VUs (maxDuration: 10m0s, gracefulStop: 30s)

    INFO[0000] Status: 200                                   source=console
    INFO[0000] Duração da requisição: 46.108274ms            source=console
    INFO[0000] Status: 200                                   source=console
    INFO[0000] Duração da requisição: 77.758799ms            source=console
    INFO[0000] Status: 200                                   source=console
    INFO[0000] Duração da requisição: 84.905835ms            source=console
    INFO[0000] Status: 200                                   source=console
    INFO[0000] Duração da requisição: 117.190236ms           source=console
    INFO[0000] Status: 200                                   source=console
    INFO[0000] Duração da requisição: 88.030864ms            source=console
    INFO[0000] Status: 200                                   source=console
    INFO[0000] Duração da requisição: 32.142778ms            source=console
    INFO[0000] Status: 200                                   source=console
    INFO[0000] Duração da requisição: 50.527261ms            source=console
    INFO[0000] Status: 200                                   source=console
    INFO[0000] Duração da requisição: 131.331113ms           source=console
    INFO[0000] Status: 200                                   source=console
    INFO[0000] Duração da requisição: 145.788085ms           source=console
    INFO[0000] Status: 200                                   source=console
    INFO[0000] Duração da requisição: 212.317788ms           source=console


    █ TOTAL RESULTS 

    checks_total.......................: 10      17.610252/s
    checks_succeeded...................: 100.00% 10 out of 10
    checks_failed......................: 0.00%   0 out of 10

    ✓ Status é 200

    HTTP
    http_req_duration.......................................................: avg=98.61ms min=32.14ms med=86.46ms max=212.31ms p(90)=152.44ms p(95)=182.37ms
      { expected_response:true }............................................: avg=98.61ms min=32.14ms med=86.46ms max=212.31ms p(90)=152.44ms p(95)=182.37ms
    http_req_failed.........................................................: 0.00%  0 out of 10
    http_reqs...............................................................: 10     17.610252/s

    EXECUTION
    iteration_duration......................................................: avg=99.54ms min=32.91ms med=87.18ms max=212.85ms p(90)=153.07ms p(95)=182.96ms
    iterations..............................................................: 10     17.610252/s

    NETWORK
    data_received...........................................................: 7.1 kB 13 kB/s
    data_sent...............................................................: 3.7 kB 6.4 kB/s




    running (00m00.6s), 0/2 VUs, 10 complete and 0 interrupted iterations
    default ✓ [======================================] 2 VUs  00m00.6s/10m0s  10/10 shared iters


# Summary of Results

 ✅ **How long did the API call take?**  

    - The average request duration was 98.61ms. The response time varied from 32.14ms to 212.31ms.

    - The fastest response was 32.14ms, and the slowest was 212.31ms.  

 ✅ **How many requests were made?** 

    - A total of 10 requests were made, distributed among the 2 virtual users. Each user made 5 requests (10 requests in total).

 ✅ **How many requests failed?**  

    - No requests failed. All requests returned a 200 status with success.

    - The failure percentage was 0.00%.