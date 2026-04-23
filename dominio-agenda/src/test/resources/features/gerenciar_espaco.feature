# language: pt

Funcionalidade: Cadastro e Configuração de Local (Teatro)
  Como um Gestor Público
  Eu quero cadastrar, configurar a capacidade e interditar teatros
  Para garantir a segurança do público e a gestão correta das pautas

  Cenario: Cadastro de um novo Teatro
    Dado que eu informo o nome "Teatro de Santa Isabel"
    E informo a capacidade maxima estipulada pelos bombeiros de 600 lugares
    Quando eu confirmo o cadastro do espaco
    Entao o espaco deve ser criado com o status "ATIVO"

  Cenario: Ajuste de capacidade com sucesso (Sem overbooking)
    Dado que o "Teatro Apolo" tem capacidade de 400 lugares
    E o evento futuro com mais vendas possui 350 ingressos vendidos
    Quando eu tento reduzir a capacidade do teatro para 380 lugares
    Entao a nova capacidade do teatro deve ser 380 lugares

  Cenario: Falha ao reduzir capacidade gerando conflito (Overbooking)
    Dado que o "Teatro Parque" tem capacidade de 500 lugares
    E o evento futuro com mais vendas possui 480 ingressos vendidos
    Quando eu tento reduzir a capacidade do teatro para 450 lugares
    Entao o sistema deve bloquear a acao com uma mensagem de "Conflito Crítico"

  Cenario: Interdição Logistica do Espaco (Delete Logico)
    Dado que o "Teatro Hermilo Borba" esta "ATIVO"
    Quando eu solicito a interdicao do espaco
    Entao o status do espaco deve mudar para "INTERDITADO"