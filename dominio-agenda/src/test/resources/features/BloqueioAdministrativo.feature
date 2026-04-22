Feature: Gerenciamento de Bloqueios Administrativos

Scenario: Criar bloqueio com sucesso em período sem eventos
  Given que existe um local com ID "123e4567-e89b-12d3-a456-426614174000"
  And não existem eventos agendados para este local entre "2024-05-01T08:00:00" e "2024-05-15T18:00:00"
  When eu solicitar a criação de um bloqueio administrativo para este local neste período com o motivo "Reforma geral do telhado"
  Then o bloqueio deve ser salvo com sucesso
  And nenhum evento deve ser cancelado

Scenario: Criar bloqueio que cancela eventos conflitantes
  Given que existe um local com ID "123e4567-e89b-12d3-a456-426614174000"
  And existem eventos agendados para este local entre "2024-06-01T08:00:00" e "2024-06-10T18:00:00"
  When eu solicitar a criação de um bloqueio administrativo para este local neste período com o motivo "Interdição da Defesa Civil"
  Then os eventos conflitantes devem ser cancelados
  And o bloqueio deve ser salvo com sucesso

Scenario: Falha ao tentar criar bloqueio com data de fim anterior a data de início
  Given que existe um local com ID "123e4567-e89b-12d3-a456-426614174000"
  When eu solicitar a criação de um bloqueio administrativo para este local de "2024-07-20T10:00:00" até "2024-07-10T10:00:00" com o motivo "Erro de digitação"
  Then o sistema deve retornar um erro de validação informando que a data de fim é antes do início

Scenario: Falha ao tentar criar bloqueio sem informar o motivo
  Given que existe um local com ID "123e4567-e89b-12d3-a456-426614174000"
  When eu solicitar a criação de um bloqueio administrativo para este local de "2024-08-01T08:00:00" até "2024-08-05T18:00:00" sem informar o motivo
  Then o sistema deve retornar um erro de validação informando que o motivo é obrigatório