Feature: Configuração de Setores e Mapeamento de Assentos

  Scenario: Configurar setor em espaço ativo
    Given que o espaço "Teatro A" possui status "ATIVO"
    When eu configuro um setor com 2 fileiras e 2 assentos por fileira
    Then a grade gerada deve conter os assentos "A1", "A2", "B1", "B2"
    And os códigos dos assentos devem ser únicos no setor

  Scenario: Controle de concorrência na pré-reserva
    Given que o assento "A1" está "LIVRE"
    When o usuário "X" e o usuário "Y" tentam pré-reservar o assento "A1" simultaneamente
    Then o sistema deve processar a primeira reserva com sucesso
    And o sistema deve lançar "ConcorrenciaException" para a segunda tentativa

  Scenario: Expiração automática de pré-reserva
    Given que o assento "B2" tem uma pré-reserva expirada no passado
    When o job de expiração de reservas for executado
    Then o status do assento "B2" deve retornar para "LIVRE"

  Scenario: Cancelar pré-reserva
    Given que o assento "B2" tem uma pré-reserva "ATIVA" para o usuário "X"
    When o usuário "X" cancelar a pré-reserva
    Then o status do assento "B2" deve retornar para "LIVRE" atomicamente
