# language: pt
Funcionalidade: Recursos de Acessibilidade na Apresentação

  O promotor marca recursos de acessibilidade (Libras, Audiodescrição, etc.) em apresentações
  já aprovadas. A remoção de um recurso anunciado exige justificativa pública para manter
  a transparência com o público PCD.

  Regra: Recursos só podem ser marcados em eventos aprovados

    Cenário: Tentativa de marcar Libras em evento ainda em análise
      Dado uma apresentação de evento em análise
      Quando o promotor tentar marcar o recurso "LIBRAS"
      Então o sistema deve lançar um erro de regra de acessibilidade

  Regra: O mesmo recurso não pode ser marcado duas vezes na mesma apresentação

    Cenário: Marcação bem-sucedida de Libras
      Dado uma apresentação de evento aprovado
      Quando o promotor marcar o recurso "LIBRAS"
      Então o recurso deve estar com status "CONFIRMADO"

    Cenário: Tentativa de marcar Libras duplicado
      Dado uma apresentação de evento aprovado com o recurso "LIBRAS" já marcado
      Quando o promotor tentar marcar o recurso "LIBRAS"
      Então o sistema deve lançar um erro de regra de acessibilidade

  Regra: Remoção de recurso anunciado exige justificativa pública

    Cenário: Tentativa de remover sem justificativa
      Dado uma apresentação com o recurso "LIBRAS" confirmado
      Quando o gestor tentar remover o recurso sem justificativa
      Então o sistema deve lançar um erro de justificativa inválida

    Cenário: Tentativa de remover com justificativa muito curta
      Dado uma apresentação com o recurso "LIBRAS" confirmado
      Quando o gestor tentar remover o recurso com a justificativa "Cancelado"
      Então o sistema deve lançar um erro de justificativa inválida

    Cenário: Remoção válida com justificativa
      Dado uma apresentação com o recurso "LIBRAS" confirmado
      Quando o gestor remover o recurso com a justificativa "Intérprete contratado cancelou por motivo de saúde na véspera"
      Então o recurso deve estar com status "REMOVIDO"
      E a justificativa de remoção deve estar registrada
