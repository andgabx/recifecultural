# language: pt
Funcionalidade: Aplicação de Cupom de Desconto Dinâmico
  Como um espectador do Portal Cultural do Recife
  Quero aplicar cupons de desconto na minha compra
  Para incentivar minha participação em eventos culturais

  Contexto:
    Dado que o sistema possui o cupom "CULTURA20" com 20% de desconto
    E o cupom exige valor mínimo de 100 reais e categoria "TEATRO"

  Cenário: Sucesso ao aplicar cupom válido
    Quando o espectador com CPF "11122233344" tenta aplicar o cupom "CULTURA20" em um ingresso de "TEATRO" no valor de 150 reais
    Então o sistema aplica o desconto
    E o valor final da compra deve ser 120 reais

  Cenário: Falha ao aplicar cupom em pedido abaixo do valor mínimo
    Quando o espectador com CPF "11122233344" tenta aplicar o cupom "CULTURA20" em um ingresso de "TEATRO" no valor de 80 reais
    Então o sistema deve negar a aplicação com o erro "Pedido abaixo do valor mínimo de R$ 100.0"

  Cenário: Falha ao aplicar cupom de outra categoria
    Quando o espectador com CPF "11122233344" tenta aplicar o cupom "CULTURA20" em um ingresso de "SHOW" no valor de 150 reais
    Então o sistema deve negar a aplicação com o erro "Cupom inválido para a categoria SHOW"

  Cenário: Falha por limite de uso por CPF
    Dado que o espectador com CPF "99988877766" já utilizou o cupom "CULTURA20" 1 vez
    Quando o espectador com CPF "99988877766" tenta aplicar o cupom "CULTURA20" em um ingresso de "TEATRO" no valor de 150.0 reais na data "15/06/2026"
    Então o sistema deve negar a aplicação com o erro "Limite por CPF atingido."

  Cenário: Falha por limite global de usos esgotado (Escassez)
    Dado que o cupom "CULTURA20" já foi utilizado 5 vezes no total
    Quando o espectador com CPF "99988877766" tenta aplicar o cupom "CULTURA20" em um ingresso de "TEATRO" no valor de 150 reais
    Então o sistema deve negar a aplicação com o erro "Limite global atingido."

  Cenário: Falha ao usar cupom com data de validade expirada (Time Travel)
    Quando o espectador com CPF "11122233344" tenta aplicar o cupom "CULTURA20" em um ingresso de "TEATRO" no valor de 150 reais na data "01/01/2027"
    Então o sistema deve negar a aplicação com o erro "Cupom expirado ou ainda não iniciado."