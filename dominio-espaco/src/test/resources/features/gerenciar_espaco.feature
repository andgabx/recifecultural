# language: pt

Funcionalidade: Cadastro e Configuração de Local (Teatro)
  Como um Gestor Público
  Eu quero cadastrar, configurar a capacidade, agendar pautas e interditar teatros
  Para garantir a segurança do público e a gestão correta do espaço

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

  Cenario: Interdição Logistica do Espaco
    Dado que o "Teatro Hermilo Borba" esta "ATIVO"
    Quando eu solicito a interdicao do espaco
    Entao o status do espaco deve mudar para "INTERDITADO"

  Cenario: Agendar pauta com sucesso em espaco livre
    Dado que o "Teatro Luiz Mendonça" tem capacidade de 800 lugares
    E não há nenhuma ocupação agendada para o dia "2024-05-10"
    Quando eu agendo uma pauta no dia "2024-05-10" das "19:00" às "21:00" com 60 min de montagem, 60 min de desmontagem e 0 min de buffer
    Então o agendamento deve ser salvo com sucesso

  Cenario: Falha ao agendar pauta devido à sobreposição do tempo de buffer
    Dado que o "Teatro Barreto Júnior" tem capacidade de 400 lugares
    E existe uma ocupação no dia "2024-05-10" das "18:00" às "20:00" com 60 min de montagem, 60 min de desmontagem e 30 min de buffer extra
    Quando eu agendo uma pauta no dia "2024-05-10" das "21:00" às "23:00" com 60 min de montagem, 30 min de desmontagem e 0 min de buffer
    Então o sistema deve bloquear a acao com uma mensagem de "Conflito de Horário"

  Cenario: Falha ao agendar pauta por conflito apenas no tempo de montagem
     Dado que o "Teatro Barreto Júnior" tem capacidade de 400 lugares
     E existe uma ocupação no dia "2024-06-15" das "14:00" às "16:00" com 60 min de montagem, 60 min de desmontagem e 0 min de buffer extra
     Quando eu agendo uma pauta no dia "2024-06-15" das "18:00" às "20:00" com 120 min de montagem, 60 min de desmontagem e 0 min de buffer
     Entao o sistema deve bloquear a acao com uma mensagem de "Conflito de Horário"

  Cenario: Falha ao agendar pauta em um teatro que está interditado
     Dado que o "Teatro Hermilo Borba" esta "INTERDITADO"
     Quando eu agendo uma pauta no dia "2024-07-20" das "19:00" às "21:00" com 60 min de montagem, 60 min de desmontagem e 0 min de buffer
     Entao o sistema deve bloquear a acao com uma mensagem de "Espaço interditado não aceita novas pautas."

  Cenario: Ajuste de capacidade no limite exato dos ingressos já vendidos
       Dado que o "Teatro Apolo" tem capacidade de 400 lugares
       E o evento futuro com mais vendas possui 350 ingressos vendidos
       Quando eu tento reduzir a capacidade do teatro para 350 lugares
       Entao a nova capacidade do teatro deve ser 350 lugares