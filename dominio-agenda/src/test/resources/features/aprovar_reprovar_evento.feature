# language: pt

Funcionalidade: Aprovar/Reprovar evento

  O gestor do teatro avalia os projetos submetidos e decide por aprovar ou reprovar.

  Regra: Apenas eventos em análise podem ser aprovados ou reprovados

    Cenário: Gestor aprova evento em análise
      Dado um evento submetido para análise
      Quando o gestor aprovar o evento
      Então o status do evento deve ser "APROVADO"

    Cenário: Gestor tenta aprovar evento já aprovado
      Dado um evento já aprovado
      Quando o gestor tentar aprovar o evento novamente
      Então o sistema deve lançar um erro de transição de status inválida

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

  Regra: Evento deve ter pelo menos uma data de apresentação para ser submetido à análise

    Cenário: Promotor tenta submeter evento sem apresentações programadas
      Dado um evento cadastrado sem datas de apresentação
      Quando o promotor tentar submeter o evento para análise
      Então o sistema deve lançar um erro de submissão inválida

    Cenário: Promotor submete evento com apresentação programada
      Dado um evento cadastrado com uma data de apresentação programada
      Quando o promotor submeter o evento para análise
      Então o status do evento deve ser "EM_ANALISE"

  Regra: Promotor com 3 ou mais reprovações nos últimos 90 dias fica bloqueado por 30 dias

    Cenário: Promotor tenta submeter evento após acumular 3 reprovações recentes
      Dado um evento pronto para submissão de um promotor com 3 reprovações nos últimos 90 dias
      Quando o promotor tentar submeter o evento para análise
      Então o sistema deve lançar um erro de bloqueio por excesso de reprovações

    Cenário: Promotor com reprovações antigas não é bloqueado
      Dado um evento pronto para submissão de um promotor com 3 reprovações há mais de 90 dias
      Quando o promotor submeter o evento para análise
      Então o status do evento deve ser "EM_ANALISE"

  Regra: Evento deve ter pelo menos um artista incluído para ser submetido à análise

    Cenário: Promotor tenta submeter evento sem artistas
      Dado um evento cadastrado com apresentação e categoria mas sem artistas
      Quando o promotor tentar submeter o evento para análise
      Então o sistema deve lançar um erro de submissão inválida

    Cenário: Promotor submete evento com artista incluído
      Dado um evento completo cadastrado
      Quando o promotor submeter o evento para análise
      Então o status do evento deve ser "EM_ANALISE"

  Regra: Evento deve ter categoria definida para ser submetido à análise

    Cenário: Promotor tenta submeter evento sem categoria
      Dado um evento cadastrado com apresentação e artistas mas sem categoria
      Quando o promotor tentar submeter o evento para análise
      Então o sistema deve lançar um erro de submissão inválida

    Cenário: Promotor submete evento com categoria definida
      Dado um evento completo cadastrado
      Quando o promotor submeter o evento para análise
      Então o status do evento deve ser "EM_ANALISE"

  Regra: Promotor pode salvar evento como rascunho sem preencher todos os campos de submissão

    Cenário: Promotor salva evento com dados incompletos como rascunho
      Dado um evento criado sem artistas, sem categoria e sem datas de apresentação
      Então o status do evento deve ser "RASCUNHO"

    Cenário: Promotor adiciona artista a evento em rascunho
      Dado um evento criado sem artistas, sem categoria e sem datas de apresentação
      Quando o promotor adicionar um artista ao evento
      Então o evento deve ter pelo menos um artista registrado

  Regra: Evento deve ter um espaço definido para ser submetido à análise

    Cenário: Promotor tenta submeter evento sem espaço definido
      Dado um evento completo cadastrado sem espaço definido
      Quando o promotor tentar submeter o evento para análise
      Então o sistema deve lançar um erro de submissão inválida

    Cenário: Promotor submete evento com espaço selecionado
      Dado um evento completo cadastrado
      Quando o promotor submeter o evento para análise
      Então o status do evento deve ser "EM_ANALISE"

  Regra: Não é permitido aprovar evento quando o espaço já está ocupado no mesmo período

    Cenário: Gestor tenta aprovar evento com conflito de espaço
      Dado um evento submetido para análise em espaço ocupado por evento aprovado no mesmo período
      Quando o gestor tentar aprovar o evento
      Então o sistema deve lançar um erro de conflito de espaço

    Cenário: Gestor aprova evento em espaço disponível no período
      Dado um evento submetido para análise em espaço disponível no período
      Quando o gestor aprovar o evento
      Então o status do evento deve ser "APROVADO"

  Regra: Promotor não pode ter mais de 5 eventos aprovados simultaneamente

    Cenário: Gestor tenta aprovar evento de promotor que atingiu o limite
      Dado um evento submetido para análise de um promotor com 5 eventos já aprovados
      Quando o gestor tentar aprovar o evento
      Então o sistema deve lançar um erro de limite de eventos aprovados atingido

    Cenário: Gestor aprova evento de promotor abaixo do limite
      Dado um evento submetido para análise de um promotor com 4 eventos já aprovados
      Quando o gestor aprovar o evento
      Então o status do evento deve ser "APROVADO"

  Regra: Evento de promotor com taxa histórica de aprovação inferior a 30% é marcado para revisão adicional

    Cenário: Promotor com baixa taxa de aprovação tem evento marcado para revisão
      Dado um evento pronto para submissão de um promotor com taxa de aprovação de 20% nos últimos 12 meses
      Quando o promotor submeter o evento para análise
      Então o evento deve estar marcado como requer revisão adicional

    Cenário: Promotor com boa taxa de aprovação não tem evento marcado para revisão
      Dado um evento pronto para submissão de um promotor com taxa de aprovação de 60% nos últimos 12 meses
      Quando o promotor submeter o evento para análise
      Então o evento não deve estar marcado como requer revisão adicional

    Cenário: Promotor com histórico insuficiente não é penalizado
      Dado um evento pronto para submissão de um promotor com apenas 3 eventos finalizados nos últimos 12 meses
      Quando o promotor submeter o evento para análise
      Então o evento não deve estar marcado como requer revisão adicional
