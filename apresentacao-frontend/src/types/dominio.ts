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
  | "CANCELADO"
  | "FINALIZADO";

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

export type TipoPatrocinio = "MASTER" | "OFICIAL" | "APOIADOR";
export type ModalidadeContribuicao =
  | "VALOR_MONETARIO"
  | "SUBSIDIO_INGRESSO_SOCIAL"
  | "PERMUTA";

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
