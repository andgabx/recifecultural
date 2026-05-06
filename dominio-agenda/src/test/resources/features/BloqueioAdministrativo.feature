Feature: Gerenciamento de Bloqueios Administrativos

  Scenario: Criar bloqueio com sucesso em período sem eventos
    Given que existe um local com ID "123e4567-e89b-12d3-a456-426614174000"
    And não existem eventos agendados para este local entre "2024-05-01" e "2024-05-15"
    When eu solicitar a criação de um bloqueio administrativo para este local neste período com o motivo "Reforma geral do telhado"
    Then o bloqueio deve ser salvo com sucesso
    And nenhum evento deve ser cancelado

  Scenario: Criar bloqueio que cancela eventos conflitantes
    Given que existe um local com ID "123e4567-e89b-12d3-a456-426614174000"
    And existem eventos agendados para este local entre "2024-06-01" e "2024-06-10"
    When eu solicitar a criação de um bloqueio administrativo para este local neste período com o motivo "Interdição da Defesa Civil"
    Then os eventos conflitantes devem ser cancelados
    And o bloqueio deve ser salvo com sucesso
    And os promotores dos eventos cancelados devem receber uma notificação com o contexto "EVENTO_CANCELADO"

  Scenario: Falha ao tentar criar bloqueio com data de fim anterior a data de início
    Given que existe um local com ID "123e4567-e89b-12d3-a456-426614174000"
    When eu solicitar a criação de um bloqueio administrativo para este local de "2024-07-20" até "2024-07-10" com o motivo "Erro de digitação grave"
    Then o sistema deve retornar um erro de validação informando que a data de fim é antes do início

  Scenario: Falha ao tentar criar bloqueio sem informar o motivo
    Given que existe um local com ID "123e4567-e89b-12d3-a456-426614174000"
    When eu solicitar a criação de um bloqueio administrativo para este local de "2024-08-01" até "2024-08-05" sem informar o motivo
    Then o sistema deve retornar um erro de validação informando que o motivo é obrigatório

  Scenario: Falha ao tentar criar bloqueio com motivo muito curto
    Given que existe um local com ID "123e4567-e89b-12d3-a456-426614174000"
    When eu solicitar a criação de um bloqueio administrativo para este local de "2024-08-01" até "2024-08-05" com o motivo "Curto"
    Then o sistema deve retornar um erro de validação informando que a justificativa deve conter no mínimo 10 caracteres

  Scenario: Obter bloqueio por ID existente
    Given que existe um bloqueio salvo no repositório com ID "11111111-e89b-12d3-a456-426614174011" e motivo "Reforma do palco principal"
    When eu solicitar a busca deste bloqueio por ID
    Then o sistema deve retornar o bloqueio com motivo "Reforma do palco principal"

  Scenario: Atualizar bloqueio existente com sucesso
    Given que existe um bloqueio salvo no repositório com ID "22222222-e89b-12d3-a456-426614174022"
    When eu solicitar a atualização deste bloqueio para o motivo "Manutenção elétrica" de "2024-09-01" até "2024-09-05"
    Then o bloqueio deve ser atualizado com sucesso
    And possíveis eventos conflitantes no novo período devem ser verificados para cancelamento

  Scenario: Deletar bloqueio existente
    Given que existe um bloqueio salvo no repositório com ID "33333333-e89b-12d3-a456-426614174033"
    When eu solicitar a exclusão deste bloqueio
    Then o bloqueio deve ser removido do repositório

  Scenario: Desativar bloqueio existente antecipadamente
    Given que existe um bloqueio salvo no repositório com ID "44444444-e89b-12d3-a456-426614174044"
    When eu solicitar a desativação deste bloqueio
    Then o bloqueio deve constar como inativo