Feature: Reservar e fazer check-in em um restaurante
  Como um usuário
  Quero fazer uma reserva em um restaurante
  E realizar o check-in 
  Para garantir minha mesa

  Scenario: Fazer uma reserva e realizar check-in
    Given o usuário acessa a aplicação em "http://localhost:3000/"
    When o usuário navega até a página de reserva
    And clica na imagem do restaurante
    And seleciona o primeiro horário disponível
    And preenche "Bruno" como nome do cliente
    And pressiona Enter
    And confirma a reserva
    And navega para consultar reserva
    And clica na reserva
    And clica no botão de confirmar
    And clica no botão de sucesso
    Then o status deve ser "Checked In"