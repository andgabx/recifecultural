import { api } from "@/lib/api";
import type {
  BffCriado,
  BffSemConteudo,
  StatusProdutor,
  UUID,
} from "@/types/dominio";

export type ProdutorResumo = {
  id: UUID;
  nomeFantasia: string;
  cnpj?: string;
  email: string;
  telefone?: string;
  status: StatusProdutor;
};

export type CriarProdutorRequisicao = {
  nomeFantasia: string;
  cnpj: string;
  email: string;
  telefone?: string;
};

export const produtoresService = {
  listar: () => api.get<ProdutorResumo[]>("/produtores").then((r) => r.data),

  cadastrar: (payload: CriarProdutorRequisicao) =>
    api.post<BffCriado>("/produtores", payload).then((r) => r.data),

  suspender: (id: UUID) =>
    api
      .post<BffSemConteudo>(`/produtores/${id}/suspender`)
      .then((r) => r.data),

  reativar: (id: UUID) =>
    api.post<BffSemConteudo>(`/produtores/${id}/reativar`).then((r) => r.data),

  inativar: (id: UUID) =>
    api.post<BffSemConteudo>(`/produtores/${id}/inativar`).then((r) => r.data),
};

export type ProdutoresService = typeof produtoresService;
