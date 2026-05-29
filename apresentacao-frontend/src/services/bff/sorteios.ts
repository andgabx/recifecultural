import { api } from "@/lib/api";
import type {
  BffCriado,
  BffSemConteudo,
  StatusInscricao,
  StatusSorteio,
  UUID,
} from "@/types/dominio";

export type SorteioResumo = {
  id: UUID;
  eventoId: UUID;
  apresentacaoId: UUID;
  vagas: number;
  status: StatusSorteio;
  prazoInscricao: string;
  dataApresentacao: string;
};

export type SorteioInscritoResumo = {
  sorteioId: UUID;
  eventoId: UUID;
  apresentacaoId: UUID;
  vagas: number;
  statusSorteio: StatusSorteio;
  prazoInscricao: string;
  dataApresentacao: string;
  statusInscricao: StatusInscricao;
  posicao?: number;
  totalInscritos: number;
};

export type Inscricao = {
  espectadorId: UUID;
  momentoInscricao: string;
  status: StatusInscricao;
};

export type CriarSorteioRequisicao = {
  eventoId: UUID;
  apresentacaoId: UUID;
  vagas: number;
  prazoInscricao: string;
  dataApresentacao: string;
};

export const sorteiosService = {
  listarPorEvento: (eventoId: UUID) =>
    api.get<SorteioResumo[]>(`/sorteios/evento/${eventoId}`).then((r) => r.data),

  listarPorEspectador: (espectadorId: UUID) =>
    api
      .get<SorteioInscritoResumo[]>(`/sorteios/espectador/${espectadorId}`)
      .then((r) => r.data),

  criar: (payload: CriarSorteioRequisicao) =>
    api.post<BffCriado>("/sorteios", payload).then((r) => r.data),

  inscrever: (sorteioId: UUID, espectadorId: UUID) =>
    api
      .post<BffSemConteudo>(`/sorteios/${sorteioId}/inscrever/${espectadorId}`)
      .then((r) => r.data),

  apurar: (sorteioId: UUID) =>
    api
      .post<BffSemConteudo>(`/sorteios/${sorteioId}/apurar`)
      .then((r) => r.data),

  desistir: (sorteioId: UUID, espectadorId: UUID) =>
    api
      .post<BffSemConteudo>(`/sorteios/${sorteioId}/desistir/${espectadorId}`)
      .then((r) => r.data),

  cancelar: (sorteioId: UUID) =>
    api
      .post<BffSemConteudo>(`/sorteios/${sorteioId}/cancelar`)
      .then((r) => r.data),

  listarInscricoes: (sorteioId: UUID) =>
    api
      .get<Inscricao[]>(`/sorteios/${sorteioId}/inscricoes`)
      .then((r) => r.data),
};

export type SorteiosService = typeof sorteiosService;
