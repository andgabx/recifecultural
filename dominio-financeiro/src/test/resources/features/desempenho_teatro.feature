Feature: Desempenho do Teatro

  Scenario: Calcular indicadores com vendas e despesas registradas
    Given que existe um orçamento de 5000.00 para análise de desempenho
    And foram vendidos 80 ingressos com receita bruta de 8000.00 no período
    And foram registradas despesas de 3000.00 no orçamento
    When calculo os indicadores de desempenho com capacidade total de 100
    Then a taxa de ocupação é 0.8000
    And a receita bruta é 8000.00
    And a receita líquida é 5000.00

  Scenario: Comparar dois períodos distintos
    Given que existem dados para dois períodos distintos sem sobreposição
    When comparo os dois períodos com capacidade total de 100
    Then o comparativo retorna indicadores para ambos os períodos

  Scenario: Comparar períodos sobrepostos
    Given que existem dois períodos que se sobrepõem
    When tento comparar os períodos sobrepostos com capacidade total de 100
    Then o sistema rejeita a operação com a mensagem "Períodos não podem se sobrepor."
