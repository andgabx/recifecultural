# language: pt
Funcionalidade: Aprovar/Reprovar evento

  O gestor do teatro avalia os projetos submetidos e decide por aprovar ou reprovar.

  # HU-1: Aprovação e reprovação de eventos
  # O gestor pode aprovar ou reprovar somente eventos que estejam em análise
  Regra: Apenas eventos em análise podem ser aprovados ou reprovados

    Cenário: Gestor aprova evento em análise
      Dado um evento submetido para análise
      Quando o gestor aprovar o evento
      Então o status do evento deve ser "APROVADO"

    Cenário: Gestor tenta aprovar evento já aprovado
      Dado um evento já aprovado
      Quando o gestor tentar aprovar o evento novamente
      Então o sistema deve lançar um erro de transição de status inválida

  # HU-2: Feedback obrigatório na reprovação
  # Ao reprovar um evento, o gestor deve registrar uma justificativa detalhada para o promotor
  Regra: Feedback de reprovação deve ter no mínimo 20 caracteres

    Cenário: Gestor tenta reprovar evento com feedback vazio
      Dado um evento submetido para análise
      Quando o gestor tentar reprovar o evento com feedback vazio
      Então o sistema deve lançar um erro de feedback inválido

    Cenário: Gestor reprova evento com feedback válido
      Dado um evento submetido para análise
      Quando o gestor reprovar o evento com feedback "O projeto não atende aos requisitos de acessibilidade exigidos pelo edital cultural"
      Então o status do evento deve ser "REPROVADO"
      E o feedback de reprovação deve estar registrado no evento

  # HU-2: Prazo para reprovação
  # O gestor deve reprovar o evento dentro de 30 dias após a submissão para análise
  Regra: Evento não pode ser reprovado após 30 dias em análise

    Cenário: Gestor tenta reprovar evento após prazo de 30 dias em análise
      Dado um evento submetido para análise há mais de 30 dias
      Quando o gestor tentar reprovar o evento com feedback "O projeto não atende aos requisitos de acessibilidade exigidos pelo edital cultural"
      Então o sistema deve lançar um erro de prazo de reprovação expirado

  # HU-3: Submissão de eventos para análise
  # O evento precisa estar completo (com datas de apresentação) antes de ser avaliado pelo gestor
  Regra: Evento deve ter pelo menos uma data de apresentação para ser submetido à análise

    Cenário: Promotor tenta submeter evento sem apresentações programadas
      Dado um evento cadastrado sem datas de apresentação
      Quando o promotor tentar submeter o evento para análise
      Então o sistema deve lançar um erro de submissão inválida

    Cenário: Promotor submete evento com apresentação programada
      Dado um evento cadastrado com uma data de apresentação programada
      Quando o promotor submeter o evento para análise
      Então o status do evento deve ser "EM_ANALISE"
