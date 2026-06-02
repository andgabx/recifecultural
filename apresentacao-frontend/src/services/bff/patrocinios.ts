import { api } from "@/lib/api";
import type {
  BffCriado,
  ModalidadeContribuicao,
  StatusPatrocinio,
  TipoPatrocinio,
  UUID,
} from "@/types/dominio";

export type PatrocinioResumo = {
  id: UUID;
  eventoId: UUID;
  patrocinadorNome: string;
  categoriaPatrocinio: string | null;
  tipo: TipoPatrocinio;
  modalidade: ModalidadeContribuicao | null;
  /** Backend retorna como string (BigDecimal.toPlainString) */
  valorContribuicao: string | null;
  dataEvento: string | null;
  status: StatusPatrocinio;
  valorReembolsado: string | null;
  multaAplicada: string | null;
};

export type CriarPatrocinioRequisicao = {
  eventoId: UUID;
  patrocinadorNome: string;
  categoriaPatrocinio: string;
  tipo: TipoPatrocinio;
  modalidade: ModalidadeContribuicao;
  valorContribuicao: number;
  dataEvento: string;
  eventoAprovado: boolean;
};

export type SimulacaoCancelamentoPatrocinio = {
  valorReembolsado: number;
  multaAplicada: number;
  motivo: string | null;
};

export type ResultadoSubsidio = {
  novoPrecoSocial: number;
  pisoAplicado: boolean;
};

export const patrociniosService = {
  listarPorEvento: (eventoId: UUID) =>
    api
      .get<PatrocinioResumo[]>(`/patrocinios/evento/${eventoId}`)
      .then((r) => r.data),

  criar: (payload: CriarPatrocinioRequisicao) =>
    api.post<BffCriado>("/patrocinios", payload).then((r) => r.data),

  ativar: (id: UUID) =>
    api.post<ResultadoSubsidio | null>(`/patrocinios/${id}/ativar`).then((r) => r.data),

  cancelarPorEvento: (id: UUID) =>
    api
      .post<SimulacaoCancelamentoPatrocinio>(`/patrocinios/${id}/cancelar-por-evento`)
      .then((r) => r.data),

  cancelarPorPatrocinador: (id: UUID) =>
    api
      .post<SimulacaoCancelamentoPatrocinio>(`/patrocinios/${id}/cancelar-por-patrocinador`)
      .then((r) => r.data),

  calcularSubsidio: (id: UUID, precoSocial: number) =>
    api
      .get<ResultadoSubsidio>(`/patrocinios/${id}/subsidio`, {
        params: { precoSocial },
      })
      .then((r) => r.data),
};

export type PatrociniosService = typeof patrociniosService;
