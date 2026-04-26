Feature: Reembolsar Ingresso

  Scenario: Reembolso PIX com mais de 7 dias de antecedência
    Given que possuo um ingresso ATIVO via PIX com valor 100.00 para daqui a 10 dias
    When solicito o reembolso
    Then o ingresso passa para o status "REEMBOLSADO"
    And o valor reembolsado é 100.00
    And o prazo de processamento é "imediato"

  Scenario: Reembolso cartão de crédito com mais de 7 dias de antecedência
    Given que possuo um ingresso ATIVO via CARTAO_CREDITO com valor 100.00 para daqui a 10 dias
    When solicito o reembolso
    Then o ingresso passa para o status "REEMBOLSADO"
    And o valor reembolsado é 100.00
    And o prazo de processamento é "até 2 dias úteis"

  Scenario: Reembolso com 3 dias de antecedência
    Given que possuo um ingresso ATIVO via PIX com valor 100.00 para daqui a 3 dias
    When solicito o reembolso
    Then o ingresso passa para o status "REEMBOLSADO"
    And o valor reembolsado é 50.00

  Scenario: Reembolso com 1 dia de antecedência
    Given que possuo um ingresso ATIVO via PIX com valor 100.00 para daqui a 1 dias
    When solicito o reembolso
    Then o sistema rejeita o reembolso com a mensagem "Reembolso não permitido com menos de 2 dias de antecedência."

  Scenario: Ingresso já reembolsado
    Given que possuo um ingresso já REEMBOLSADO
    When solicito o reembolso
    Then o sistema rejeita o reembolso com a mensagem "Apenas ingressos com status ATIVO podem ser reembolsados."

  Scenario: Ingresso UTILIZADO
    Given que possuo um ingresso com status UTILIZADO
    When solicito o reembolso
    Then o sistema rejeita o reembolso com a mensagem "Apenas ingressos com status ATIVO podem ser reembolsados."
