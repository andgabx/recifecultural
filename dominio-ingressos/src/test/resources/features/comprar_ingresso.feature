Feature: Comprar Ingresso

  Scenario: Compra inteira via PIX aprovado
    When submeto a compra de um ingresso INTEIRA via PIX com valor 100.00 e capacidade 100
    Then o ingresso é criado com status "ATIVO"
    And o ingresso possui um QR code único

  Scenario: Compra inteira via cartão de crédito aprovado
    When submeto a compra de um ingresso INTEIRA via CARTAO_CREDITO com valor 100.00 e capacidade 100
    Then o ingresso é criado com status "ATIVO"
    And o ingresso possui um QR code único

  Scenario: Compra meia-entrada via PIX
    When submeto a compra de um ingresso MEIA_ENTRADA via PIX com valor 50.00 e capacidade 100
    Then o ingresso é criado com status "ATIVO"
    And o valor pago é 50.00

  Scenario: Capacidade esgotada
    Given que a apresentação já possui 50 ingressos ativos com capacidade máxima 50
    When tento comprar mais um ingresso INTEIRA via PIX com valor 100.00
    Then o sistema rejeita a compra com a mensagem "Capacidade esgotada para esta apresentação."

  Scenario: Pagamento recusado pelo gateway
    Given que o gateway rejeita pagamentos
    When submeto a compra de um ingresso INTEIRA via PIX com valor 100.00 e capacidade 100
    Then o sistema rejeita a compra com a mensagem "Pagamento recusado pelo gateway."
