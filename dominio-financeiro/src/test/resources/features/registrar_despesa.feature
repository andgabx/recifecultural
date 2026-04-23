Feature: Registrar Despesa

  Scenario: Registrar despesa dentro do período com orçamento disponível
    Given que existe um orçamento de 10000.00 para o período atual
    When registro uma despesa de 500.00 na categoria PESSOAL com descrição "Salário técnico"
    Then a despesa é registrada com sucesso
    And não há alerta de orçamento

  Scenario: Registrar despesa fora do período orçamentário
    Given que existe um orçamento de 10000.00 para um período passado
    When registro uma despesa de 500.00 na categoria PESSOAL com descrição "Salário técnico"
    Then o sistema rejeita a operação com a mensagem "Despesa fora do período orçamentário."

  Scenario: Registrar despesa que ultrapassa 80% do orçamento
    Given que existe um orçamento de 1000.00 para o período atual
    And já foram registradas despesas no valor de 750.00
    When registro uma despesa de 100.00 na categoria MARKETING com descrição "Campanha"
    Then a despesa é registrada com sucesso
    And há alerta de orçamento

  Scenario: Registrar despesa com orçamento encerrado
    Given que existe um orçamento encerrado de 10000.00 para o período atual
    When registro uma despesa de 500.00 na categoria OUTROS com descrição "Manutenção"
    Then o sistema rejeita a operação com a mensagem "Orçamento encerrado."

  Scenario: Reduzir orçamento abaixo das despesas registradas
    Given que existe um orçamento de 10000.00 para o período atual
    And já foram registradas despesas no valor de 6000.00
    When reduzo o orçamento para 5000.00
    Then o sistema rejeita a operação com a mensagem "Orçamento não pode ser menor que as despesas já registradas."
