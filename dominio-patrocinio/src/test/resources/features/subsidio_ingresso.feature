Feature: Subsídio de Ingresso Social

  Scenario: Subsídio reduz preço social acima do piso
    Given que existe um patrocínio ATIVO de SUBSIDIO_INGRESSO_SOCIAL com valor 10.00
    When calculo o subsídio para um ingresso social com preço 30.00
    Then o novo preço social é 20.00
    And o piso não foi aplicado

  Scenario: Subsídio que levaria preço abaixo de R$ 1,00
    Given que existe um patrocínio ATIVO de SUBSIDIO_INGRESSO_SOCIAL com valor 25.00
    When calculo o subsídio para um ingresso social com preço 20.00
    Then o novo preço social é 1.00
    And o piso foi aplicado

  Scenario: Subsídio em patrocínio de modalidade FINANCEIRO
    Given que existe um patrocínio ATIVO de FINANCEIRO com valor 5000.00
    When tento calcular o subsídio para um ingresso social com preço 30.00
    Then o sistema rejeita com a mensagem "Apenas patrocínios com modalidade SUBSIDIO_INGRESSO_SOCIAL podem calcular subsídio."

  Scenario: Subsídio em patrocínio cancelado
    Given que existe um patrocínio CANCELADO de SUBSIDIO_INGRESSO_SOCIAL com valor 10.00
    When tento calcular o subsídio para um ingresso social com preço 30.00
    Then o sistema rejeita com a mensagem "Apenas patrocínios com status ATIVO podem calcular subsídio."
