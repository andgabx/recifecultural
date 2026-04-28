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

  # HU-4: Bloqueio por histórico de reprovações
  # Um promotor com muitas reprovações recentes fica temporariamente impedido de submeter novos eventos
  Regra: Promotor com 3 ou mais reprovações nos últimos 90 dias fica bloqueado por 30 dias

    Cenário: Promotor tenta submeter evento após acumular 3 reprovações recentes
      Dado um evento pronto para submissão de um promotor com 3 reprovações nos últimos 90 dias
      Quando o promotor tentar submeter o evento para análise
      Então o sistema deve lançar um erro de bloqueio por excesso de reprovações

    Cenário: Promotor com reprovações antigas não é bloqueado
      Dado um evento pronto para submissão de um promotor com 3 reprovações há mais de 90 dias
      Quando o promotor submeter o evento para análise
      Então o status do evento deve ser "EM_ANALISE"

  # HU-5: Lista de artistas obrigatória na submissão
  # O promotor deve vincular ao menos um artista antes de submeter o evento para avaliação
  Regra: Evento deve ter pelo menos um artista incluído para ser submetido à análise

    Cenário: Promotor tenta submeter evento sem artistas
      Dado um evento cadastrado com apresentação e categoria mas sem artistas
      Quando o promotor tentar submeter o evento para análise
      Então o sistema deve lançar um erro de submissão inválida

    Cenário: Promotor submete evento com artista incluído
      Dado um evento completo cadastrado
      Quando o promotor submeter o evento para análise
      Então o status do evento deve ser "EM_ANALISE"

  # HU-6: Categoria obrigatória na submissão
  # O evento precisa ter uma categoria cultural definida para que o gestor possa avaliá-lo
  Regra: Evento deve ter categoria definida para ser submetido à análise

    Cenário: Promotor tenta submeter evento sem categoria
      Dado um evento cadastrado com apresentação e artistas mas sem categoria
      Quando o promotor tentar submeter o evento para análise
      Então o sistema deve lançar um erro de submissão inválida

    Cenário: Promotor submete evento com categoria definida
      Dado um evento completo cadastrado
      Quando o promotor submeter o evento para análise
      Então o status do evento deve ser "EM_ANALISE"

  # HU-7: Salvar como rascunho
  # O promotor pode criar e manter o evento como rascunho enquanto preenche os dados gradualmente,
  # sem precisar completar todos os campos exigidos na submissão
  Regra: Promotor pode salvar evento como rascunho sem preencher todos os campos de submissão

    Cenário: Promotor salva evento com dados incompletos como rascunho
      Dado um evento criado sem artistas, sem categoria e sem datas de apresentação
      Então o status do evento deve ser "RASCUNHO"

    Cenário: Promotor adiciona artista a evento em rascunho
      Dado um evento criado sem artistas, sem categoria e sem datas de apresentação
      Quando o promotor adicionar um artista ao evento
      Então o evento deve ter pelo menos um artista registrado

  # HU-8: Espaço definido obrigatório na submissão
  # O promotor deve selecionar um espaço existente antes de submeter o evento para análise
  Regra: Evento deve ter um espaço definido para ser submetido à análise

    Cenário: Promotor tenta submeter evento sem espaço definido
      Dado um evento completo cadastrado sem espaço definido
      Quando o promotor tentar submeter o evento para análise
      Então o sistema deve lançar um erro de submissão inválida

    Cenário: Promotor submete evento com espaço selecionado
      Dado um evento completo cadastrado
      Quando o promotor submeter o evento para análise
      Então o status do evento deve ser "EM_ANALISE"
