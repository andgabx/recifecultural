# language: pt
Funcionalidade: Sorteio de Ingressos

  O gestor abre sorteios para distribuir cortesias de apresentações aprovadas,
  garantindo rotatividade automática quando ganhadores desistem.

  Regra: Sorteios só podem ser criados para eventos aprovados

    Cenário: Tentativa de criar sorteio para evento ainda em análise
      Dado um evento em análise pronto para sorteio
      Quando o gestor tentar abrir um sorteio com 2 vagas
      Então o sistema deve lançar um erro de regra do sorteio

  Regra: Espectadores só podem se inscrever uma vez por sorteio

    Cenário: Espectador se inscreve em sorteio aberto
      Dado um sorteio aberto com 2 vagas
      Quando o espectador "alice" se inscreve no sorteio
      Então o sorteio deve ter 1 inscrição

    Cenário: Espectador tenta se inscrever duas vezes
      Dado um sorteio aberto com 2 vagas
      E o espectador "alice" já está inscrito
      Quando o espectador "alice" tenta se inscrever novamente
      Então o sistema deve lançar um erro de regra do sorteio

  Regra: A apuração distribui ganhadores e suplentes conforme as vagas

    Cenário: Apuração com mais inscritos que vagas
      Dado um sorteio aberto com 2 vagas
      E 5 espectadores se inscreveram no sorteio
      Quando o gestor apurar o sorteio
      Então o sorteio deve estar com status "CONCLUIDO"
      E o sorteio deve ter 2 ganhadores
      E o sorteio deve ter 3 suplentes

  Regra: Desistência de ganhador promove o primeiro suplente

    Cenário: Ganhador desiste e suplente é promovido
      Dado um sorteio apurado com 1 vaga e 3 inscritos
      Quando um ganhador desiste do sorteio
      Então o sorteio deve ter 1 ganhador
      E o sorteio deve ter 1 desistente
