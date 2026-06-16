-- =====================================================================
-- Fixtures de demonstração — Recife Cultural
-- Cobertura completa: todos os padrões de projeto demonstráveis
-- Idempotente via ON CONFLICT (id) DO NOTHING e WHERE NOT EXISTS
-- Arquivo local, não commitar.
-- =====================================================================


-- ── ESPAÇOS ──────────────────────────────────────────────────────────
-- Teatro do Parque: espaço principal das demos
-- Teatro UFPE: INTERDITADO — mostra resultado de bloqueio histórico
INSERT INTO espaco (id, nome, capacidade_maxima, status)
VALUES
    ('10000000-0000-0000-0000-000000000001', 'Teatro do Parque',  500, 'ATIVO'),
    ('10000000-0000-0000-0000-000000000002', 'Teatro UFPE',       150, 'INTERDITADO')
ON CONFLICT (id) DO NOTHING;


-- ── SETORES (Teatro do Parque) ────────────────────────────────────────
INSERT INTO setor (id, espaco_id, nome, tipo_setor, fileiras_horizontais, assentos_por_fileira_vertical, versao)
VALUES
    ('11000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'Plateia',  'PLATEIA',  10, 15, 0),
    ('11000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', 'Camarote', 'CAMAROTE',  3,  4, 0)
ON CONFLICT (id) DO NOTHING;

-- Plateia: 10 fileiras (A-J) × 15 assentos — fileiras A e B com 3 OCUPADOS (dos ingressos existentes)
INSERT INTO setor_assento (setor_id, id, codigo, fileira, numero, status, motivo_indisponibilidade, versao)
SELECT
    '11000000-0000-0000-0000-000000000001',
    gen_random_uuid(),
    chr(64 + f.n) || a.n::text,
    chr(64 + f.n),
    a.n,
    CASE WHEN f.n <= 2 AND a.n <= 3 THEN 'OCUPADO' ELSE 'LIVRE' END,
    NULL,
    0
FROM generate_series(1, 10) AS f(n),
     generate_series(1, 15) AS a(n)
WHERE NOT EXISTS (
    SELECT 1 FROM setor_assento WHERE setor_id = '11000000-0000-0000-0000-000000000001'
);

-- Camarote: 3 fileiras (A-C) × 4 assentos — todos LIVRES
INSERT INTO setor_assento (setor_id, id, codigo, fileira, numero, status, motivo_indisponibilidade, versao)
SELECT
    '11000000-0000-0000-0000-000000000002',
    gen_random_uuid(),
    chr(64 + f.n) || a.n::text,
    chr(64 + f.n),
    a.n,
    'LIVRE',
    NULL,
    0
FROM generate_series(1, 3) AS f(n),
     generate_series(1, 4) AS a(n)
WHERE NOT EXISTS (
    SELECT 1 FROM setor_assento WHERE setor_id = '11000000-0000-0000-0000-000000000002'
);


-- ── PRODUTOR DEMO ─────────────────────────────────────────────────────
INSERT INTO produtor (id, nome_fantasia, cnpj, email, telefone, status)
VALUES ('00000000-0000-0000-0000-000000000002', 'Demo Produções Ltda', '12345678000190', 'demo@producoes.com', '81900000000', 'ATIVO')
ON CONFLICT (id) DO NOTHING;


-- ── ARTISTAS ──────────────────────────────────────────────────────────
INSERT INTO artista (id, produtor_id, nome, status)
VALUES
    ('60000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'Banda Frevo Total',              'ATIVO'),
    ('60000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', 'Orquestra Recife Clássico',       'ATIVO'),
    ('60000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002', 'Grupo Maracatu Nação Pernambuco', 'ATIVO')
ON CONFLICT (id) DO NOTHING;


-- ── CUPONS — 5 camadas do Decorator ──────────────────────────────────
-- FREVO20     : funciona (camada base)
-- CUPOMEXPIRADO: falha na camada Vigência
-- MUSICAONLY  : falha na camada Categoria se evento != MUSICA
-- MINIMO200   : falha na camada Valor Mínimo se pedido < R$200
-- ESGOTADO    : falha na camada Escassez Global (usos == limite)
INSERT INTO cupom (id, codigo, tipo_desconto, valor_desconto, valor_minimo_pedido,
                   limite_global, usos_globais, limite_por_cpf, data_inicio, data_fim, categoria_permitida)
VALUES
    ('cupom-frevo20',   'FREVO20',       'PERCENTUAL', 20,   0, 100,  0, 1, NOW() - INTERVAL '1 day',   NOW() + INTERVAL '30 days', NULL),
    ('cupom-expirado',  'CUPOMEXPIRADO', 'PERCENTUAL', 10,   0, 100,  0, 1, NOW() - INTERVAL '60 days', NOW() - INTERVAL '30 days', NULL),
    ('cupom-musica',    'MUSICAONLY',    'PERCENTUAL', 25,   0, 100,  0, 1, NOW() - INTERVAL '1 day',   NOW() + INTERVAL '30 days', 'MUSICA'),
    ('cupom-minimo200', 'MINIMO200',     'PERCENTUAL', 15, 200, 100,  0, 1, NOW() - INTERVAL '1 day',   NOW() + INTERVAL '30 days', NULL),
    ('cupom-esgotado',  'ESGOTADO',      'PERCENTUAL', 30,   0,  10, 10, 1, NOW() - INTERVAL '1 day',   NOW() + INTERVAL '30 days', NULL)
ON CONFLICT (id) DO NOTHING;


-- ── EVENTOS — 6 status distintos ─────────────────────────────────────
INSERT INTO evento (id, promotor_id, local_id,
                    titulo, descricao_curta, descricao_longa,
                    periodo_inicio, periodo_fim, categoria, status,
                    preco_inteira, preco_meia,
                    data_aprovacao, data_reprovacao,
                    requer_revisao_adicional, motivo_cancelamento)
VALUES
    -- APROVADO 1: base para sorteio / ingressos / patrocínio / acessibilidade
    ('20000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001',
     'Noite do Frevo',
     'Uma noite inesquecível ao ritmo do frevo pernambucano.',
     'Venha celebrar a cultura pernambucana com os melhores grupos de frevo do Recife. Show com desfile, maracatu e batuque.',
     NOW() + INTERVAL '30 days', NOW() + INTERVAL '90 days',
     'MUSICA', 'APROVADO', 80.00, 40.00,
     NOW() - INTERVAL '5 days', NULL, FALSE, NULL),

    -- APROVADO 2: subsídio social (Patrocínio Petrobras) / sorteio CONCLUIDO
    ('20000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001',
     'Forró no Parque',
     'Forró pé-de-serra ao ar livre no coração do parque.',
     'Uma noite de forró com os melhores sanfoneiros de Pernambuco. Entrada com subsídio social disponível graças ao patrocínio.',
     NOW() + INTERVAL '45 days', NOW() + INTERVAL '75 days',
     'MUSICA', 'APROVADO', 60.00, 30.00,
     NOW() - INTERVAL '3 days', NULL, FALSE, NULL),

    -- EM_ANALISE: gestor aprova ao vivo → Decorator registra auditoria
    ('20000000-0000-0000-0000-000000000003',
     '00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001',
     'Jazz na Varanda',
     'Uma tarde de jazz intimista na varanda do teatro.',
     'Espetáculo intimista de jazz com músicos locais em ambiente acolhedor.',
     NOW() + INTERVAL '50 days', NOW() + INTERVAL '80 days',
     'MUSICA', 'EM_ANALISE', 100.00, 50.00,
     NULL, NULL, FALSE, NULL),

    -- REPROVADO: mostra data_reprovacao e fila de histórico do gestor
    ('20000000-0000-0000-0000-000000000004',
     '00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001',
     'Maracatu Nação',
     'Apresentação de maracatu com nação tradicional do Recife.',
     'Espetáculo de maracatu com percussão e dança afro-pernambucana.',
     NOW() + INTERVAL '60 days', NOW() + INTERVAL '90 days',
     'MUSICA', 'REPROVADO', 50.00, 25.00,
     NULL, NOW() - INTERVAL '2 days', FALSE, NULL),

    -- RASCUNHO: produtor edita e submete ao vivo durante a demo
    ('20000000-0000-0000-0000-000000000005',
     '00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001',
     'Circo Pernambucano',
     'Espetáculo circense com artistas locais de Pernambuco.',
     'Um circo cheio de malabares, acrobacias e humor com artistas pernambucanos.',
     NOW() + INTERVAL '70 days', NOW() + INTERVAL '100 days',
     'TEATRO', 'RASCUNHO', 45.00, 22.50,
     NULL, NULL, FALSE, NULL),

    -- CANCELADO: mostra motivo_cancelamento e sorteio CANCELADO
    ('20000000-0000-0000-0000-000000000006',
     '00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001',
     'Axé Nordestino',
     'Festival de axé com atrações nordestinas de renome.',
     'Festival reunindo os maiores nomes do axé e pagode nordestino em uma noite histórica.',
     NOW() + INTERVAL '20 days', NOW() + INTERVAL '50 days',
     'MUSICA', 'CANCELADO', 70.00, 35.00,
     NOW() - INTERVAL '10 days', NULL, FALSE,
     'Cancelado por bloqueio administrativo do espaço.')
ON CONFLICT (id) DO NOTHING;


-- ── APRESENTAÇÕES ─────────────────────────────────────────────────────
INSERT INTO evento_apresentacao (evento_id, data_hora)
SELECT v.evento_id, v.data_hora
FROM (VALUES
    ('20000000-0000-0000-0000-000000000001'::uuid, (NOW() + INTERVAL '30 days')::timestamp),
    ('20000000-0000-0000-0000-000000000001'::uuid, (NOW() + INTERVAL '60 days')::timestamp)
) AS v(evento_id, data_hora)
WHERE NOT EXISTS (
    SELECT 1 FROM evento_apresentacao WHERE evento_id = '20000000-0000-0000-0000-000000000001'
);

INSERT INTO evento_apresentacao (evento_id, data_hora)
SELECT v.evento_id, v.data_hora
FROM (VALUES
    ('20000000-0000-0000-0000-000000000002'::uuid, (NOW() + INTERVAL '45 days')::timestamp),
    ('20000000-0000-0000-0000-000000000002'::uuid, (NOW() + INTERVAL '65 days')::timestamp)
) AS v(evento_id, data_hora)
WHERE NOT EXISTS (
    SELECT 1 FROM evento_apresentacao WHERE evento_id = '20000000-0000-0000-0000-000000000002'
);


-- ── RECURSOS DE ACESSIBILIDADE ────────────────────────────────────────
-- Template Method: remover ao vivo mostra buscar→remover→persistir→broadcast
INSERT INTO recurso_acessibilidade (id, evento_id, apresentacao_id, tipo, status, justificativa_remocao)
VALUES
    ('C0000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', NULL, 'LIBRAS',          'CONFIRMADO', NULL),
    ('C0000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', NULL, 'AUDIODESCRICAO',  'CONFIRMADO', NULL),
    ('C0000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000002', NULL, 'ESPACO_PCD',       'CONFIRMADO', NULL)
ON CONFLICT (id) DO NOTHING;


-- ── SORTEIOS — 3 status distintos ────────────────────────────────────
INSERT INTO sorteio (id, evento_id, apresentacao_id, vagas, prazo_inscricao, data_apresentacao, status)
VALUES
    -- INSCRICOES_ABERTAS: Espectador já inscrito, apurar ao vivo → Template Method
    ('30000000-0000-0000-0000-000000000001',
     '20000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 5,
     NOW() + INTERVAL '7 days', NOW() + INTERVAL '14 days', 'INSCRICOES_ABERTAS'),

    -- CONCLUIDO: Espectador Demo é ganhador — mostra histórico
    ('30000000-0000-0000-0000-000000000002',
     '20000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002', 3,
     NOW() - INTERVAL '5 days', NOW() + INTERVAL '2 days', 'CONCLUIDO'),

    -- CANCELADO: junto com Axé Nordestino cancelado — mostra ciclo completo
    ('30000000-0000-0000-0000-000000000003',
     '20000000-0000-0000-0000-000000000006', '40000000-0000-0000-0000-000000000003', 2,
     NOW() - INTERVAL '15 days', NOW() + INTERVAL '1 day', 'CANCELADO')
ON CONFLICT (id) DO NOTHING;

-- Inscrições sorteio 1 (INSCRICOES_ABERTAS) — Espectador Demo inscrito
INSERT INTO sorteio_inscricao (sorteio_id, espectador_id, momento_inscricao, status)
SELECT '30000000-0000-0000-0000-000000000001',
       '00000000-0000-0000-0000-000000000001',
       NOW() - INTERVAL '1 day',
       'INSCRITO'
WHERE NOT EXISTS (
    SELECT 1 FROM sorteio_inscricao
    WHERE sorteio_id   = '30000000-0000-0000-0000-000000000001'
      AND espectador_id = '00000000-0000-0000-0000-000000000001'
);

-- Inscrições sorteio 2 (CONCLUIDO) — Espectador ganhador + 2 ganhadores + 3 suplentes
INSERT INTO sorteio_inscricao (sorteio_id, espectador_id, momento_inscricao, status)
SELECT v.sorteio_id, v.espectador_id, v.momento, v.status
FROM (VALUES
    ('30000000-0000-0000-0000-000000000002'::uuid, '00000000-0000-0000-0000-000000000001'::uuid, (NOW() - INTERVAL '20 days')::timestamp, 'GANHADOR'),
    ('30000000-0000-0000-0000-000000000002'::uuid, 'F0000000-0000-0000-0000-000000000001'::uuid, (NOW() - INTERVAL '19 days')::timestamp, 'GANHADOR'),
    ('30000000-0000-0000-0000-000000000002'::uuid, 'F0000000-0000-0000-0000-000000000002'::uuid, (NOW() - INTERVAL '18 days')::timestamp, 'GANHADOR'),
    ('30000000-0000-0000-0000-000000000002'::uuid, 'F0000000-0000-0000-0000-000000000003'::uuid, (NOW() - INTERVAL '17 days')::timestamp, 'SUPLENTE'),
    ('30000000-0000-0000-0000-000000000002'::uuid, 'F0000000-0000-0000-0000-000000000004'::uuid, (NOW() - INTERVAL '16 days')::timestamp, 'SUPLENTE'),
    ('30000000-0000-0000-0000-000000000002'::uuid, 'F0000000-0000-0000-0000-000000000005'::uuid, (NOW() - INTERVAL '15 days')::timestamp, 'SUPLENTE')
) AS v(sorteio_id, espectador_id, momento, status)
WHERE NOT EXISTS (
    SELECT 1 FROM sorteio_inscricao WHERE sorteio_id = '30000000-0000-0000-0000-000000000002'
);

-- Inscrições sorteio 3 (CANCELADO) — 2 inscrições canceladas
INSERT INTO sorteio_inscricao (sorteio_id, espectador_id, momento_inscricao, status)
SELECT v.sorteio_id, v.espectador_id, v.momento, v.status
FROM (VALUES
    ('30000000-0000-0000-0000-000000000003'::uuid, '00000000-0000-0000-0000-000000000001'::uuid, (NOW() - INTERVAL '25 days')::timestamp, 'CANCELADA'),
    ('30000000-0000-0000-0000-000000000003'::uuid, 'F0000000-0000-0000-0000-000000000001'::uuid, (NOW() - INTERVAL '24 days')::timestamp, 'CANCELADA')
) AS v(sorteio_id, espectador_id, momento, status)
WHERE NOT EXISTS (
    SELECT 1 FROM sorteio_inscricao WHERE sorteio_id = '30000000-0000-0000-0000-000000000003'
);


-- ── INGRESSO_CATRACA — id = codigo_qr do ingresso (string que o usuário digita) ─
-- horario_inicio_evento = NOW()+1h → portões abertos agora, janela fecha em 75min
INSERT INTO ingresso_catraca (id, id_evento, status, horario_inicio_evento, tipo_ingresso, portao_acesso)
VALUES
    ('QR-ATIVO-NOITEFREVO-001',     '20000000-0000-0000-0000-000000000001', 'VALIDO',                  NOW() + INTERVAL '1 hour', 'COMUM',       NULL),
    ('QR-UTILIZADO-NOITEFREVO-002', '20000000-0000-0000-0000-000000000001', 'UTILIZADO',               NOW() + INTERVAL '1 hour', 'COMUM',       'PORTAO_A'),
    ('QR-REEMB-NOITEFREVO-003',     '20000000-0000-0000-0000-000000000001', 'CANCELADO_OU_REEMBOLSADO', NOW() + INTERVAL '1 hour', 'COMUM',       NULL),
    ('QR-ATIVO-FORROPARQUE-004',    '20000000-0000-0000-0000-000000000002', 'VALIDO',                  NOW() + INTERVAL '1 hour', 'MEIA_ENTRADA', NULL),
    ('QR-SOCIAL-FORROPARQUE-005',   '20000000-0000-0000-0000-000000000002', 'VALIDO',                  NOW() + INTERVAL '1 hour', 'COMUM',       NULL)
ON CONFLICT (id) DO UPDATE SET
    horario_inicio_evento = EXCLUDED.horario_inicio_evento,
    status = EXCLUDED.status,
    portao_acesso = EXCLUDED.portao_acesso;


-- ── INGRESSOS DO ESPECTADOR DEMO ─────────────────────────────────────
INSERT INTO ingresso (id, evento_id, data_hora_apresentacao, tipo, status,
                      valor_pago, codigo_qr, codigo_transacao, metodo_pagamento, data_compra)
VALUES
    -- ATIVO PIX: demo de reembolso Strategy + validação catraca
    ('40000000-0000-0000-0000-000000000001',
     '20000000-0000-0000-0000-000000000001', NOW() + INTERVAL '30 days',
     'INTEIRA', 'ATIVO', 80.00,
     'QR-ATIVO-NOITEFREVO-001', 'TXN-0001', 'PIX', NOW() - INTERVAL '5 days'),

    -- UTILIZADO Cartão: demo de avaliação sem precisar passar pela catraca ao vivo
    ('40000000-0000-0000-0000-000000000002',
     '20000000-0000-0000-0000-000000000001', NOW() + INTERVAL '30 days',
     'INTEIRA', 'UTILIZADO', 80.00,
     'QR-UTILIZADO-NOITEFREVO-002', 'TXN-0002', 'CARTAO_CREDITO', NOW() - INTERVAL '10 days'),

    -- REEMBOLSADO: mostra QR oculto, badge vermelho e histórico Strategy
    ('40000000-0000-0000-0000-000000000003',
     '20000000-0000-0000-0000-000000000001', NOW() + INTERVAL '30 days',
     'INTEIRA', 'REEMBOLSADO', 80.00,
     'QR-REEMB-NOITEFREVO-003', 'TXN-0003', 'PIX', NOW() - INTERVAL '8 days'),

    -- MEIA_ENTRADA Cartão: segundo evento ativo no Forró no Parque
    ('40000000-0000-0000-0000-000000000004',
     '20000000-0000-0000-0000-000000000002', NOW() + INTERVAL '45 days',
     'MEIA_ENTRADA', 'ATIVO', 30.00,
     'QR-ATIVO-FORROPARQUE-004', 'TXN-0004', 'CARTAO_CREDITO', NOW() - INTERVAL '3 days'),

    -- SOCIAL PIX: ingresso subsidiado pelo patrocínio Petrobras
    ('40000000-0000-0000-0000-000000000005',
     '20000000-0000-0000-0000-000000000002', NOW() + INTERVAL '45 days',
     'SOCIAL', 'ATIVO', 1.00,
     'QR-SOCIAL-FORROPARQUE-005', 'TXN-0005', 'PIX', NOW() - INTERVAL '2 days')
ON CONFLICT (id) DO NOTHING;


-- ── PATROCÍNIOS ───────────────────────────────────────────────────────
INSERT INTO patrocinio (id, evento_id, patrocinador_nome, categoria_patrocinio,
                        tipo, modalidade, valor_contribuicao, status,
                        valor_reembolsado, multa_aplicada)
VALUES
    -- PROPOSTA: gestor ativa ao vivo → Strategy de patrocínio
    ('50000000-0000-0000-0000-000000000001',
     '20000000-0000-0000-0000-000000000001',
     'Heineken', 'MASTER', 'MASTER', 'FINANCEIRO', 50000.00, 'PROPOSTA', NULL, NULL),

    -- ATIVO com SUBSIDIO_INGRESSO_SOCIAL: justifica o preço social do Forró no Parque
    ('50000000-0000-0000-0000-000000000002',
     '20000000-0000-0000-0000-000000000002',
     'Petrobras', 'ASSOCIADO', 'ASSOCIADO', 'SUBSIDIO_INGRESSO_SOCIAL', 20000.00, 'ATIVO', NULL, NULL),

    -- CANCELADO_PATROCINADOR: mostra reembolso parcial + multa (Strategy de cancelamento)
    ('50000000-0000-0000-0000-000000000003',
     '20000000-0000-0000-0000-000000000001',
     'Brahma', 'ASSOCIADO', 'ASSOCIADO', 'FINANCEIRO', 15000.00, 'CANCELADO_PATROCINADOR', 10000.00, 5000.00)
ON CONFLICT (id) DO NOTHING;


-- ── BLOQUEIO HISTÓRICO (Teatro UFPE) ─────────────────────────────────
-- Período no passado, ativo=false → mostra resultado do Observer sem cancelar eventos ativos
INSERT INTO bloqueio_administrativo (id, espaco_id, data_inicio, data_fim, justificativa, ativo)
VALUES ('D0000000-0000-0000-0000-000000000001',
        '10000000-0000-0000-0000-000000000002',
        CURRENT_DATE - INTERVAL '30 days',
        CURRENT_DATE - INTERVAL '1 day',
        'Manutenção preventiva da estrutura do palco.', FALSE)
ON CONFLICT (id) DO NOTHING;


-- ── NOTIFICAÇÕES DO ESPECTADOR DEMO (3 não lidas) ────────────────────
INSERT INTO notificacao (id, usuario_alvo, mensagem, contexto, id_referencia, foi_lida, data_criacao)
VALUES
    ('B0000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000001',
     'Você foi promovido de suplente para ganhador no sorteio de Noite do Frevo!',
     'SORTEIO', '20000000-0000-0000-0000-000000000001', FALSE,
     NOW() - INTERVAL '2 hours'),

    ('B0000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0000-000000000001',
     'O sorteio de Axé Nordestino foi cancelado.',
     'SORTEIO', '20000000-0000-0000-0000-000000000006', FALSE,
     NOW() - INTERVAL '1 hour'),

    ('B0000000-0000-0000-0000-000000000003',
     '00000000-0000-0000-0000-000000000001',
     'Seu ingresso para Noite do Frevo foi reembolsado com sucesso.',
     'INGRESSO', '20000000-0000-0000-0000-000000000001', FALSE,
     NOW() - INTERVAL '30 minutes')
ON CONFLICT (id) DO NOTHING;


-- ── AUDITORIA — ciclo de vida dos eventos ────────────────────────────
INSERT INTO auditoria (id, entidade, entidade_id, acao, status_anterior, status_novo, descricao, momento)
VALUES
    -- Noite do Frevo (aprovado)
    ('A0000000-0000-0000-0000-000000000001', 'Evento', '20000000-0000-0000-0000-000000000001',
     'CRIADO', NULL, NULL, 'Noite do Frevo criado pelo produtor.', NOW() - INTERVAL '12 days'),
    ('A0000000-0000-0000-0000-000000000002', 'Evento', '20000000-0000-0000-0000-000000000001',
     'TRANSICAO_STATUS', 'RASCUNHO', 'EM_ANALISE', 'Evento submetido para aprovação.', NOW() - INTERVAL '10 days'),
    ('A0000000-0000-0000-0000-000000000003', 'Evento', '20000000-0000-0000-0000-000000000001',
     'TRANSICAO_STATUS', 'EM_ANALISE', 'APROVADO', 'Evento aprovado pelo gestor.', NOW() - INTERVAL '5 days'),

    -- Forró no Parque (aprovado)
    ('A0000000-0000-0000-0000-000000000004', 'Evento', '20000000-0000-0000-0000-000000000002',
     'CRIADO', NULL, NULL, 'Forró no Parque criado pelo produtor.', NOW() - INTERVAL '11 days'),
    ('A0000000-0000-0000-0000-000000000005', 'Evento', '20000000-0000-0000-0000-000000000002',
     'TRANSICAO_STATUS', 'RASCUNHO', 'EM_ANALISE', 'Evento submetido para aprovação.', NOW() - INTERVAL '9 days'),
    ('A0000000-0000-0000-0000-000000000006', 'Evento', '20000000-0000-0000-0000-000000000002',
     'TRANSICAO_STATUS', 'EM_ANALISE', 'APROVADO', 'Evento aprovado pelo gestor.', NOW() - INTERVAL '3 days'),

    -- Jazz na Varanda (em análise — aguardando aprovação ao vivo)
    ('A0000000-0000-0000-0000-000000000007', 'Evento', '20000000-0000-0000-0000-000000000003',
     'CRIADO', NULL, NULL, 'Jazz na Varanda criado pelo produtor.', NOW() - INTERVAL '8 days'),
    ('A0000000-0000-0000-0000-000000000008', 'Evento', '20000000-0000-0000-0000-000000000003',
     'TRANSICAO_STATUS', 'RASCUNHO', 'EM_ANALISE', 'Evento submetido para aprovação.', NOW() - INTERVAL '6 days'),

    -- Maracatu Nação (reprovado)
    ('A0000000-0000-0000-0000-000000000009', 'Evento', '20000000-0000-0000-0000-000000000004',
     'CRIADO', NULL, NULL, 'Maracatu Nação criado pelo produtor.', NOW() - INTERVAL '14 days'),
    ('A0000000-0000-0000-0000-000000000010', 'Evento', '20000000-0000-0000-0000-000000000004',
     'TRANSICAO_STATUS', 'RASCUNHO', 'EM_ANALISE', 'Evento submetido para aprovação.', NOW() - INTERVAL '12 days'),
    ('A0000000-0000-0000-0000-000000000011', 'Evento', '20000000-0000-0000-0000-000000000004',
     'TRANSICAO_STATUS', 'EM_ANALISE', 'REPROVADO', 'Documentação incompleta. Reenvie o contrato do artista.', NOW() - INTERVAL '2 days'),

    -- Axé Nordestino (cancelado — ciclo completo)
    ('A0000000-0000-0000-0000-000000000012', 'Evento', '20000000-0000-0000-0000-000000000006',
     'CRIADO', NULL, NULL, 'Axé Nordestino criado pelo produtor.', NOW() - INTERVAL '20 days'),
    ('A0000000-0000-0000-0000-000000000013', 'Evento', '20000000-0000-0000-0000-000000000006',
     'TRANSICAO_STATUS', 'RASCUNHO', 'EM_ANALISE', 'Evento submetido para aprovação.', NOW() - INTERVAL '18 days'),
    ('A0000000-0000-0000-0000-000000000014', 'Evento', '20000000-0000-0000-0000-000000000006',
     'TRANSICAO_STATUS', 'EM_ANALISE', 'APROVADO', 'Evento aprovado pelo gestor.', NOW() - INTERVAL '10 days'),
    ('A0000000-0000-0000-0000-000000000015', 'Evento', '20000000-0000-0000-0000-000000000006',
     'TRANSICAO_STATUS', 'APROVADO', 'CANCELADO', 'Cancelado por bloqueio administrativo do espaço.', NOW() - INTERVAL '8 days')
ON CONFLICT (id) DO NOTHING;
