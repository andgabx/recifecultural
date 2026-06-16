/*
 * Tipos compartilhados entre os services BFF.
 * Espelham os enums e value objects do dominio Java (ver dominio-*).
 */

export type UUID = string;

export type MetodoPagamento = "PIX" | "CARTAO_CREDITO" | "CARTAO_DEBITO";
export type TipoIngresso = "INTEIRA" | "MEIA_ENTRADA" | "SOCIAL";
export type StatusIngresso = "ATIVO" | "UTILIZADO" | "REEMBOLSADO";

export type StatusEvento =
  | "RASCUNHO"
  | "EM_ANALISE"
  | "APROVADO"
  | "REPROVADO"
  | "REALIZADO"
  | "CANCELADO";

export type StatusSorteio =
  | "INSCRICOES_ABERTAS"
  | "EM_APURACAO"
  | "CONCLUIDO"
  | "CANCELADO";

export type StatusInscricao =
  | "INSCRITO"
  | "GANHADOR"
  | "SUPLENTE"
  | "DESISTENTE"
  | "CANCELADA";

export type StatusPatrocinio =
  | "PROPOSTA"
  | "ATIVO"
  | "ENCERRADO"
  | "CANCELADO_EVENTO"
  | "CANCELADO_PATROCINADOR";

export type TipoPatrocinio = "MASTER" | "ASSOCIADO";
export type ModalidadeContribuicao =
  | "FINANCEIRO"
  | "SUBSIDIO_INGRESSO_SOCIAL";

export type StatusProdutor = "ATIVO" | "SUSPENSO" | "INATIVO";
export type StatusArtista = "ATIVO" | "INATIVO";
export type StatusEspaco = "ATIVO" | "INTERDITADO";
export type StatusAssento =
  | "LIVRE"
  | "PRE_RESERVADO"
  | "OCUPADO"
  | "BLOQUEADO";

export type TipoRecursoAcessibilidade =
  | "LIBRAS"
  | "AUDIODESCRICAO"
  | "LEGENDA_DESCRITIVA"
  | "ESPACO_PCD"
  | "PROGRAMA_BRAILE";

export type StatusRecursoAcessibilidade = "CONFIRMADO" | "REMOVIDO";

/** Resposta padrao de POST que cria recurso (201). */
export type BffCriado = { id: string; mensagem?: string };

/** Resposta padrao de POST/DELETE sem conteudo (204) — vem como objeto vazio. */
export type BffSemConteudo = Record<string, never>;

// ─── Service enums moved here for co-location ─────────────────────────────────

export type StatusEquipamento = "DISPONIVEL" | "ALOCADO" | "EM_MANUTENCAO";

export type TipoDesconto = "PERCENTUAL" | "VALOR_FIXO";

export type AcaoAuditoria = "CRIADO" | "TRANSICAO_STATUS" | "REMOVIDO";

export type StatusComentario = "ATIVO" | "DELETADO";

export type RiderItem = {
  equipamentoId: UUID;
  quantidade: number;
};

export type DisponibilidadeEquipamento = {
  disponivel: boolean;
  quantidadeDisponivel: number;
};
