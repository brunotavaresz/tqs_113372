Rndpoints que tenho a dar:

    POST /restaurants - Criar restaurante.

    GET /restaurants - Listar restaurantes.

    POST /meals - Criar refeição.

    GET /meals - Listar refeições.

    POST /reservations - Criar reserva.

    GET /reservations: Para listar todas as reservas 

    GET /reservations/{reservationCode} - Obter detalhes de reserva.

    DELETE /reservations/{reservationCode} - Cancelar reserva.

    POST /reservations/{reservationCode}/check-in - Realizar check-in de reserva.

    GET /restaurants/{restaurantId}/menus - Listar refeições de um restaurante específico.

    GET /restaurants/{restaurantId}/reservations - Listar reservas de um restaurante específico.

    GET /restaurants/{restaurantId} – Obter detalhes de um restaurante específico.
    Justificativa: Esse endpoint seria útil para visualizar as informações completas de um restaurante específico, como nome, localização e dados gerais.

    PUT /restaurants/{restaurantId} – Atualizar informações de um restaurante específico.
    Justificativa: Pode ser necessário para editar detalhes do restaurante, caso haja mudanças nas informações como nome, localização, etc.

    GET /meals/{mealId} – Obter detalhes de uma refeição específica.
    Justificativa: Esse endpoint seria útil para visualizar informações sobre uma refeição específica, como preço e nome.

    PUT /meals/{mealId} – Atualizar uma refeição específica.
    Justificativa: Seria necessário caso a refeição precise ser atualizada (preço, nome, etc.).

    PUT /reservations/{reservationCode} – Atualizar informações de uma reserva específica.
    Justificativa: Caso o cliente ou o sistema precise modificar uma reserva já feita, este endpoint seria útil. Isso pode incluir mudança de refeição, alteração de data ou hora, etc.


Resumo da Lista de Tarefas:

    Implementar os endpoints faltantes.

    Criar testes unitários e de integração para os serviços e controladores.

    Testar o frontend, garantindo que ele esteja integrando corretamente com a API.

    Adicionar validações e testar cenários de erro.

    Testar performance (opcional).

    Documentação de testes.