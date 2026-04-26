# language: pt
Funcionalidade: Discutir sobre eventos

  Espectadores podem comentar, curtir, responder e avaliar eventos culturais.

  # HU-1: Postar comentários de eventos
  # Espectador registra sua opinião sobre um evento
  Regra: Comentário deve ter entre 10 e 500 caracteres

    Cenário: Espectador posta comentário com texto curto demais
      Dado um espectador cadastrado
      E um evento existente
      Quando o espectador tentar postar um comentário com texto "Curto"
      Então o sistema deve lançar um erro de texto inválido

    Cenário: Espectador posta comentário com texto longo demais
      Dado um espectador cadastrado
      E um evento existente
      Quando o espectador tentar postar um comentário com mais de 500 caracteres
      Então o sistema deve lançar um erro de texto inválido

    Cenário: Espectador posta comentário válido
      Dado um espectador cadastrado
      E um evento existente
      Quando o espectador postar o comentário "O espetáculo foi incrível, recomendo a todos os amantes de teatro!"
      Então o comentário deve estar registrado no sistema

  # HU-2: Curtir comentários de eventos
  # Espectador demonstra aprovação a comentários de outros espectadores
  Regra: Espectador não pode curtir comentário próprio nem curtir duas vezes

    Cenário: Espectador tenta curtir o próprio comentário
      Dado um espectador cadastrado
      E um comentário postado pelo próprio espectador
      Quando o espectador tentar curtir o próprio comentário
      Então o sistema deve lançar um erro de curtida inválida

    Cenário: Espectador tenta curtir o mesmo comentário duas vezes
      Dado um espectador cadastrado
      E um comentário postado por outro espectador
      Quando o espectador curtir o comentário
      E o espectador tentar curtir o mesmo comentário novamente
      Então o sistema deve lançar um erro de curtida duplicada

    Cenário: Espectador curte comentário de outro espectador
      Dado um espectador cadastrado
      E um comentário postado por outro espectador
      Quando o espectador curtir o comentário
      Então a curtida deve estar registrada no comentário

  # HU-3: Responder comentários em eventos
  # Espectador interage com comentários de outros, gerando discussões aninhadas
  Regra: Resposta deve ter texto válido e referenciar comentário existente

    Cenário: Espectador tenta responder com texto inválido
      Dado um espectador cadastrado
      E um comentário existente no sistema
      Quando o espectador tentar responder com texto "Ok"
      Então o sistema deve lançar um erro de texto inválido

    Cenário: Espectador tenta responder comentário inexistente
      Dado um espectador cadastrado
      Quando o espectador tentar responder a um comentário que não existe
      Então o sistema deve lançar um erro de comentário não encontrado

    Cenário: Espectador responde comentário válido
      Dado um espectador cadastrado
      E um comentário existente no sistema
      Quando o espectador responder com "Concordo totalmente, o elenco estava excepcional naquela noite!"
      Então a resposta deve estar vinculada ao comentário pai

  # HU-4: Visualizar comentários em eventos
  # Espectador acessa a lista de comentários de um evento
  Regra: Comentários deletados não aparecem na listagem

    Cenário: Comentário deletado não aparece ao listar
      Dado um comentário existente no sistema
      Quando o autor deletar o comentário
      E os comentários do evento forem listados
      Então o comentário deletado não deve aparecer na listagem

    Cenário: Evento sem comentários retorna lista vazia
      Dado um evento existente
      Quando os comentários do evento forem listados
      Então a listagem deve estar vazia

  # HU-5: Postar comentário com nota para o evento
  # Espectador que presenciou o evento pode registrar uma avaliação com nota
  Regra: Apenas espectadores presentes podem avaliar; nota deve ser entre 1 e 5; uma nota por evento

    Cenário: Espectador sem presença tenta postar nota
      Dado um espectador cadastrado
      E um evento existente
      E o espectador não esteve presente no evento
      Quando o espectador tentar postar nota 4 para o evento
      Então o sistema deve lançar um erro de presença não confirmada

    Cenário: Espectador posta nota com valor inválido
      Dado um espectador cadastrado
      E um evento existente
      E o espectador esteve presente no evento
      Quando o espectador tentar postar nota 6 para o evento
      Então o sistema deve lançar um erro de nota inválida

    Cenário: Espectador tenta dar segunda nota ao mesmo evento
      Dado um espectador cadastrado
      E um evento existente
      E o espectador esteve presente no evento
      E o espectador já postou nota 5 para o evento
      Quando o espectador tentar postar nota 3 para o evento
      Então o sistema deve lançar um erro de avaliação duplicada

    Cenário: Espectador presente posta nota válida
      Dado um espectador cadastrado
      E um evento existente
      E o espectador esteve presente no evento
      Quando o espectador postar nota 4 para o evento com comentário "Excelente apresentação, vale cada centavo do ingresso!"
      Então o comentário com nota deve estar registrado no sistema
