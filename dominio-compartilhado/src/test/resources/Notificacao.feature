Feature: Gerenciamento de Notificações

  Scenario: Enviar notificação direta para um usuário com sucesso
    Given que existe um usuário alvo com ID "123e4567-e89b-12d3-a456-426614174011"
    When eu solicitar o envio de uma notificação com a mensagem "Seu evento foi aprovado para o Marco Zero"
    Then a notificação direta deve ser salva com sucesso no repositório para o usuário

  Scenario: Enviar aviso global (broadcast) gerando notificações individuais
    Given que o contexto de broadcast retornará os usuários "123e4567-e89b-12d3-a456-426614174022" e "123e4567-e89b-12d3-a456-426614174033"
    When eu solicitar o envio de um broadcast com a mensagem "Manutenção agendada para meia-noite"
    Then o sistema deve gerar e salvar notificações individuais para cada usuário retornado

  Scenario: Marcar notificação como lida
    Given que o usuário "123e4567-e89b-12d3-a456-426614174011" possui uma notificação pendente com a mensagem "Atenção ao prazo"
    When o usuário solicitar a marcação desta notificação como lida
    Then a notificação deve ser atualizada e constar como lida pelo sistema

  Scenario: Marcar notificação como não lida
    Given que o usuário "123e4567-e89b-12d3-a456-426614174011" possui uma notificação lida com a mensagem "Atenção ao prazo"
    When o usuário solicitar a marcação desta notificação como não lida
    Then a notificação deve ser atualizada e constar como não lida pelo sistema

  Scenario: Enviar notificação com contexto e referência
    Given que existe um usuário alvo com ID "123e4567-e89b-12d3-a456-426614174011"
    And um evento de referência com ID "999e4567-e89b-12d3-a456-426614174099"
    When eu solicitar o envio de uma notificação com a mensagem "Evento atualizado", contexto "ATUALIZACAO_EVENTO" e referência do evento
    Then a notificação deve ser salva contendo o contexto "ATUALIZACAO_EVENTO" e a referência correta