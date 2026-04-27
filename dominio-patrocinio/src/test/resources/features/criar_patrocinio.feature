Feature: Criar Patrocínio

  Scenario: Criar patrocínio MASTER em evento APROVADO
    Given que existe um evento aprovado
    When crio um patrocínio MASTER da categoria "Bebidas" com valor 5000.00
    Then o patrocínio é criado com status "PROPOSTA"
    And o tipo do patrocínio é "MASTER"

  Scenario: Criar patrocínio ASSOCIADO com MASTER já existente
    Given que existe um evento aprovado
    And já existe um patrocínio MASTER da categoria "Bebidas"
    When crio um patrocínio ASSOCIADO da categoria "Tecnologia" com valor 2000.00
    Then o patrocínio é criado com status "PROPOSTA"

  Scenario: Tentar criar segundo patrocínio MASTER no mesmo evento
    Given que existe um evento aprovado
    And já existe um patrocínio MASTER da categoria "Bebidas"
    When tento criar um segundo patrocínio MASTER da categoria "Bancos" com valor 3000.00
    Then o sistema rejeita com a mensagem "Já existe um patrocinador MASTER para este evento."

  Scenario: Criar dois patrocinadores da mesma categoria
    Given que existe um evento aprovado
    And já existe um patrocínio ASSOCIADO da categoria "Tecnologia"
    When tento criar um patrocínio ASSOCIADO da categoria "Tecnologia" com valor 1500.00
    Then o sistema rejeita com a mensagem "Já existe um patrocinador da categoria 'Tecnologia' para este evento."

  Scenario: Criar patrocínio em evento não APROVADO
    Given que existe um evento não aprovado
    When tento criar um patrocínio MASTER da categoria "Bebidas" com valor 5000.00 no evento não aprovado
    Then o sistema rejeita com a mensagem "O patrocínio só pode ser criado para eventos com status APROVADO."

  Scenario: Criar patrocínio com valor zero
    Given que existe um evento aprovado
    When tento criar um patrocínio MASTER da categoria "Bebidas" com valor 0.00
    Then o sistema rejeita com a mensagem "O valor da contribuição deve ser maior que zero."
