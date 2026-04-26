# language: pt
Funcionalidade: Controle de Acesso Inteligente na Catraca do Teatro

  Contexto:
    Dado que o evento "Auto da Compadecida" começa às "20:00"

  Cenário: Sucesso ao passar na catraca no portão correto e no horário
    Dado que o espectador possui o ingresso "QR-123" com status "VALIDO"
    E o ingresso é do tipo "COMUM" e pertence ao "Portão A"
    Quando ele tenta passar a catraca do "Portão A" às "19:30"
    Então a catraca deve exibir a mensagem "ACESSO LIBERADO. Catraca destravada com sucesso."
    E o ingresso "QR-123" deve ter o status atualizado para "UTILIZADO"

  Cenário: Falha por tentativa de dupla entrada (Anti-Fraude)
    Dado que o ingresso "QR-999" já possui o status "UTILIZADO"
    E o ingresso é do tipo "COMUM" e pertence ao "Portão A"
    Quando ele tenta passar a catraca do "Portão A" às "19:45"
    Então o sistema deve bloquear com o erro "ALERTA FRAUDE: Este ingresso já foi utilizado."

  Cenário: Falha ao tentar entrar no portão errado
    Dado que o espectador possui o ingresso "QR-123" com status "VALIDO"
    E o ingresso é do tipo "COMUM" e pertence ao "Portão A"
    Quando ele tenta passar a catraca do "Portão B" às "19:30"
    Então o sistema deve bloquear com o erro "Acesso Negado: Este ingresso pertence ao Portão A. Dirija-se ao local correto."

  Cenário: Falha por ingresso cancelado
    Dado que o ingresso "QR-404" foi devolvido e tem status "CANCELADO_OU_REEMBOLSADO"
    E o ingresso é do tipo "COMUM" e pertence ao "Portão A"
    Quando ele tenta passar a catraca do "Portão A" às "19:50"
    Então o sistema deve bloquear com o erro "Entrada Negada: Este ingresso consta como cancelado ou reembolsado."

  Cenário: Falha por atraso excessivo para ingresso COMUM
    Dado que o espectador possui o ingresso "QR-123" com status "VALIDO"
    E o ingresso é do tipo "COMUM" e pertence ao "Portão A"
    Quando ele tenta passar a catraca do "Portão A" às "20:20"
    Então o sistema deve bloquear com o erro "Entrada Negada: O limite de 15 minutos de atraso foi excedido. As portas do teatro estão fechadas."

  Cenário: Sucesso de ingresso VIP chegando muito atrasado
    Dado que o espectador possui o ingresso "QR-VIP" com status "VALIDO"
    E o ingresso é do tipo "VIP" e pertence ao "Portão A"
    Quando ele tenta passar a catraca do "Portão A" às "21:00"
    Então a catraca deve exibir a mensagem "ACESSO LIBERADO. Catraca destravada com sucesso."