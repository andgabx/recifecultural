Feature: Gerenciamento de Notificações

Scenario: Enviar notificação direta para um usuário com sucesso
  Given que existe um usuário alvo com ID "123e4567-e89b-12d3-a456-426614174011"
  When eu solicitar o envio de uma notificação com a mensagem "Seu evento foi aprovado para o Marco Zero"
  Then a notificação direta deve ser salva com sucesso no repositório

Scenario: Enviar aviso global (broadcast) com sucesso
  When eu solicitar o envio de um broadcast com a mensagem "Manutenção agendada para meia-noite"
  Then a notificação de broadcast deve ser salva com sucesso no repositório

Scenario: Marcar notificação direta como lida
  Given que o usuário "123e4567-e89b-12d3-a456-426614174011" possui uma notificação direta pendente com a mensagem "Atenção ao prazo"
  When o usuário solicitar a marcação desta notificação como lida
  Then a notificação deve ser atualizada e constar como lida pelo sistema

Scenario: Usuário marca um broadcast como lido
  Given que existe um broadcast pendente com a mensagem "Novo edital aberto"
  And um usuário leitor com ID "123e4567-e89b-12d3-a456-426614174022"
  When este usuário leitor solicitar a marcação do broadcast como lido
  Then o broadcast deve registrar a leitura exclusivamente para este usuário