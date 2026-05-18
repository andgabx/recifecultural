-- V001: Schema inicial do Recife Cultural
-- Criado para a 2ª Entrega — Camada de Persistência com JPA/Hibernate

CREATE TABLE IF NOT EXISTS evento (
    id                      UUID PRIMARY KEY,
    promotor_id             UUID,
    local_id                UUID,
    titulo                  VARCHAR(255),
    descricao_curta         VARCHAR(500),
    descricao_longa         TEXT,
    periodo_inicio          TIMESTAMP,
    periodo_fim             TIMESTAMP,
    categoria               VARCHAR(100),
    status                  VARCHAR(50),
    preco_inteira           NUMERIC(10,2),
    preco_meia              NUMERIC(10,2),
    data_aprovacao          TIMESTAMP,
    data_reprovacao         TIMESTAMP,
    requer_revisao_adicional BOOLEAN DEFAULT FALSE,
    motivo_cancelamento     TEXT
);

CREATE TABLE IF NOT EXISTS evento_apresentacao (
    evento_id   UUID NOT NULL REFERENCES evento(id),
    data_hora   TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS ingresso (
    id                      UUID PRIMARY KEY,
    evento_id               UUID,
    data_hora_apresentacao  TIMESTAMP,
    tipo                    VARCHAR(50),
    status                  VARCHAR(50),
    valor_pago              NUMERIC(10,2),
    codigo_qr               VARCHAR(255),
    codigo_transacao        VARCHAR(255),
    metodo_pagamento        VARCHAR(50),
    data_compra             TIMESTAMP,
    valor_reembolsado       NUMERIC(10,2)
);

CREATE TABLE IF NOT EXISTS patrocinio (
    id                      UUID PRIMARY KEY,
    evento_id               UUID,
    patrocinador_nome       VARCHAR(255),
    categoria_patrocinio    VARCHAR(100),
    tipo                    VARCHAR(50),
    modalidade              VARCHAR(50),
    valor_contribuicao      NUMERIC(12,2),
    data_evento             TIMESTAMP,
    status                  VARCHAR(50),
    valor_reembolsado       NUMERIC(12,2),
    multa_aplicada          NUMERIC(12,2)
);

CREATE TABLE IF NOT EXISTS sorteio (
    id                  UUID PRIMARY KEY,
    apresentacao_id     UUID,
    evento_id           UUID,
    vagas               INTEGER NOT NULL,
    prazo_inscricao     TIMESTAMP,
    data_apresentacao   TIMESTAMP,
    status              VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS sorteio_inscricao (
    sorteio_id          UUID NOT NULL REFERENCES sorteio(id),
    espectador_id       UUID,
    momento_inscricao   TIMESTAMP,
    status              VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS espaco (
    id                  UUID PRIMARY KEY,
    nome                VARCHAR(255),
    capacidade_maxima   INTEGER,
    status              VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS espaco_rider_tecnico (
    espaco_id   UUID NOT NULL REFERENCES espaco(id),
    item        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS espaco_ocupacao (
    espaco_id           UUID NOT NULL REFERENCES espaco(id),
    inicio              TIMESTAMP,
    fim                 TIMESTAMP,
    minutos_montagem    INTEGER,
    minutos_desmontagem INTEGER,
    buffer_extra        INTEGER
);

CREATE TABLE IF NOT EXISTS setor (
    id                              UUID PRIMARY KEY,
    espaco_id                       UUID REFERENCES espaco(id),
    nome                            VARCHAR(255),
    tipo_setor                      VARCHAR(50),
    fileiras_horizontais            INTEGER,
    assentos_por_fileira_vertical   INTEGER,
    versao                          INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS setor_assento (
    setor_id                UUID NOT NULL REFERENCES setor(id),
    id                      UUID,
    codigo                  VARCHAR(20),
    fileira                 VARCHAR(5),
    numero                  INTEGER,
    status                  VARCHAR(50),
    motivo_indisponibilidade VARCHAR(100),
    versao                  INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bloqueio_administrativo (
    id              UUID PRIMARY KEY,
    espaco_id       UUID REFERENCES espaco(id),
    data_inicio     DATE,
    data_fim        DATE,
    justificativa   TEXT,
    ativo           BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS cupom (
    id                  VARCHAR(100) PRIMARY KEY,
    codigo              VARCHAR(100) UNIQUE NOT NULL,
    tipo_desconto       VARCHAR(50),
    valor_desconto      NUMERIC(10,2),
    valor_minimo_pedido NUMERIC(10,2),
    limite_global       INTEGER,
    usos_globais        INTEGER DEFAULT 0,
    limite_por_cpf      INTEGER,
    data_inicio         TIMESTAMP,
    data_fim            TIMESTAMP,
    categoria_permitida VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS cupom_cpf_usado (
    cupom_id    VARCHAR(100) NOT NULL REFERENCES cupom(id),
    cpf         VARCHAR(14)
);

CREATE TABLE IF NOT EXISTS ingresso_catraca (
    id                      VARCHAR(255) PRIMARY KEY,
    id_evento               VARCHAR(255),
    status                  VARCHAR(50),
    horario_inicio_evento   TIMESTAMP,
    tipo_ingresso           VARCHAR(50),
    portao_acesso           VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS orcamento_periodo (
    id              UUID PRIMARY KEY,
    periodo_inicio  TIMESTAMP,
    periodo_fim     TIMESTAMP,
    valor_total     NUMERIC(14,2),
    status          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS despesa (
    id              UUID PRIMARY KEY,
    orcamento_id    UUID REFERENCES orcamento_periodo(id),
    descricao       VARCHAR(500),
    valor           NUMERIC(12,2),
    categoria       VARCHAR(100),
    data_registro   TIMESTAMP
);

CREATE TABLE IF NOT EXISTS artista (
    id          UUID PRIMARY KEY,
    produtor_id UUID,
    nome        VARCHAR(255),
    status      VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS produtor (
    id              UUID PRIMARY KEY,
    nome_fantasia   VARCHAR(255),
    cnpj            VARCHAR(14) UNIQUE,
    email           VARCHAR(255),
    telefone        VARCHAR(20),
    status          VARCHAR(50)
);
