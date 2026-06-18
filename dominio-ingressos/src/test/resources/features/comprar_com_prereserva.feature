Feature: Comprar Ingresso com Pré-Reserva

  Scenario: Compra com pré-reserva válida
    Given que existe uma pré-reserva válida com id "RESERVA-001" para o assento "A1"
    When submeto a compra com pré-reserva INTEIRA via PIX com valor 120.00 e capacidade 100
    Then o ingresso com pré-reserva é criado com status "ATIVO"
    And a pré-reserva "RESERVA-001" foi confirmada
    And o ingresso com pré-reserva possui um QR code único

  Scenario: Compra com pré-reserva e cupom de desconto
    Given que existe uma pré-reserva válida com id "RESERVA-002" para o assento "B5"
    And que existe um cupom "DESCONTO20" com 20 por cento de desconto válido para a categoria "TEATRO"
    When submeto a compra com pré-reserva INTEIRA via PIX com valor 150.00 cupom "DESCONTO20" CPF "123.456.789-00" categoria "TEATRO" e capacidade 100
    Then o ingresso com pré-reserva é criado com status "ATIVO"
    And o valor pago com pré-reserva é 120.00
    And a pré-reserva "RESERVA-002" foi confirmada

  Scenario: Pagamento recusado cancela a pré-reserva
    Given que existe uma pré-reserva válida com id "RESERVA-003" para o assento "C3"
    And que o gateway rejeita pagamentos na compra com pré-reserva
    When submeto a compra com pré-reserva INTEIRA via PIX com valor 120.00 e capacidade 100
    Then o sistema rejeita a compra com pré-reserva com a mensagem "Pagamento recusado pelo gateway."
    And a pré-reserva "RESERVA-003" foi cancelada
