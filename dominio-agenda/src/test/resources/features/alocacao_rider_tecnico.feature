# language: pt

Funcionalidade: Alocação de Rider Técnico (Inventário)
  Como Gestor de Patrimônio
  Eu quero que o inventário reaja automaticamente à agenda
  Para evitar eventos sem infraestrutura ou conflitos de equipamentos

  Cenario: Sucesso na reserva de equipamentos ao aprovar evento
    Dado que o teatro possui 2 unidades de "Mesa de Som" disponíveis
    E um evento foi aprovado necessitando de 1 "Mesa de Som"
    Quando o sistema processar a alocacao do rider tecnico
    Entao o status do equipamento deve mudar para "ALOCADO"

  Cenario: Quebra de equipamento aciona alerta critico para o promotor do evento
      Dado que uma "Mesa de Som" esta com status "ALOCADO" para o "Show de Rock"
      Quando o zelador registrar que a "Mesa de Som" foi para manutencao
      Entao o status do equipamento deve mudar para "EM_MANUTENCAO"
      E o sistema deve enviar uma notificacao de alerta para o promotor do "Show de Rock"

  Cenario: Desmobilização automatica ao cancelar evento
    Dado que uma "Mesa de Som" esta com status "ALOCADO" para o "Show de Jazz"
    Quando o "Show de Jazz" for cancelado
    Entao a "Mesa de Som" deve ser devolvida ao pool com status "DISPONIVEL"

  Cenario: Registro de nova aquisição de equipamento para o teatro
    Dado que o teatro adquiriu um novo "Projetor 4K"
    Quando eu confirmar o registro no sistema
    Entao o "Projetor 4K" deve aparecer no inventario como "DISPONIVEL"

  Cenario: Remocao definitiva por perda ou quebra total
    Dado que o equipamento "Microfone Sem Fio" foi extraviado
    Quando eu solicitar a exclusao definitiva do equipamento
    Entao o equipamento nao deve mais existir no repositorio

  Cenario: Proibicao de deletar equipamento alocado
    Dado que uma "Mesa de Som" esta com status "ALOCADO" para o "Show de Rock"
    Quando eu tentar excluir definitivamente a "Mesa de Som"
    Entao o sistema deve impedir a exclusao informando que o item esta em uso