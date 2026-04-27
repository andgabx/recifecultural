Feature: Cancelar Patrocínio

  Scenario: Cancelamento pelo evento com mais de 7 dias de antecedência
    Given que existe um patrocínio ATIVO para um evento daqui a 10 dias com valor 5000.00
    When o evento cancela o patrocínio
    Then o resultado do cancelamento tem reembolso de 5000.00
    And o status do patrocínio é "CANCELADO_EVENTO"

  Scenario: Cancelamento pelo evento com 4 dias de antecedência
    Given que existe um patrocínio ATIVO para um evento daqui a 4 dias com valor 5000.00
    When o evento cancela o patrocínio
    Then o resultado do cancelamento tem reembolso de 2500.00
    And o status do patrocínio é "CANCELADO_EVENTO"

  Scenario: Cancelamento pelo evento com 1 dia de antecedência
    Given que existe um patrocínio ATIVO para um evento daqui a 1 dias com valor 5000.00
    When o evento cancela o patrocínio
    Then o resultado do cancelamento tem reembolso de 0.00
    And o status do patrocínio é "CANCELADO_EVENTO"

  Scenario: Cancelamento pelo patrocinador com mais de 15 dias de antecedência
    Given que existe um patrocínio ATIVO para um evento daqui a 20 dias com valor 4000.00
    When o patrocinador cancela o patrocínio
    Then o resultado do cancelamento tem reembolso de 4000.00
    And a multa aplicada é 0.00
    And o status do patrocínio é "CANCELADO_PATROCINADOR"

  Scenario: Cancelamento pelo patrocinador com 10 dias de antecedência
    Given que existe um patrocínio ATIVO para um evento daqui a 10 dias com valor 4000.00
    When o patrocinador cancela o patrocínio
    Then o resultado do cancelamento tem reembolso de 3200.00
    And a multa aplicada é 800.00
    And o status do patrocínio é "CANCELADO_PATROCINADOR"

  Scenario: Cancelar patrocínio já encerrado
    Given que existe um patrocínio ENCERRADO
    When o evento tenta cancelar o patrocínio encerrado
    Then o sistema rejeita com a mensagem "Apenas patrocínios com status ATIVO podem ser cancelados por evento."
