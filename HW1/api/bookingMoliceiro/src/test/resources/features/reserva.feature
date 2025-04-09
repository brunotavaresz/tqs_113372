Feature: Reserva de Moliceiro

    Scenario: Fazer uma reserva e verificar check-in
    Given o usuário acessa a aplicação em "http://localhost:3000/"
    When o usuário navega até a página de reserva
    And seleciona um restaurante da lista
    And preenche o nome do cliente como "Bruno"
    Then a reserva é efetuada com a mensagem "Reserva Confirmada!"
    And navega para consultar reserva
    And clica no botão de tamanho pequeno
    And clica no botão contido
    And confirma com o botão de sucesso
    Then verifica se o status é "Checked In"