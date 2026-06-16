import { api } from "@/lib/api";
import type {
  BffCriado,
  BffSemConteudo,
  DisponibilidadeEquipamento,
  UUID,
} from "@/types/dominio";

import type { StatusEquipamento } from "@/types/dominio";
export type { StatusEquipamento } from "@/types/dominio";

export type EquipamentoResumo = {
  id: UUID;
  espacoId: UUID;
  nome: string;
  status: StatusEquipamento;
  eventoAlocadoId: UUID | null;
  alocacaoInicio: string | null;
  alocacaoFim: string | null;
};

export type AdquirirEquipamentoRequisicao = {
  espacoId: UUID;
  nome: string;
};

export const equipamentosService = {
  listarPorEspaco: (espacoId: UUID) =>
    api
      .get<EquipamentoResumo[]>(`/equipamentos/espaco/${espacoId}`)
      .then((r) => r.data),

  verificarDisponibilidade: (
    espacoId: string,
    nome: string,
    quantidade: number,
    inicio?: string,
    fim?: string,
  ) => {
    let url = `/equipamentos/disponibilidade?espacoId=${espacoId}&nome=${encodeURIComponent(nome)}&quantidade=${quantidade}`;
    if (inicio) url += `&inicio=${inicio.slice(0, 10)}`;
    if (fim) url += `&fim=${fim.slice(0, 10)}`;
    return api.get<DisponibilidadeEquipamento>(url).then((r) => r.data);
  },

  adquirir: (payload: AdquirirEquipamentoRequisicao) =>
    api.post<BffCriado>("/equipamentos", payload).then((r) => r.data),

  marcarManutencao: (id: UUID) =>
    api
      .post<BffSemConteudo>(`/equipamentos/${id}/manutencao`)
      .then((r) => r.data),

  liberar: (id: UUID) =>
    api.post<BffSemConteudo>(`/equipamentos/${id}/liberar`).then((r) => r.data),

  remover: (id: UUID) =>
    api.delete<BffSemConteudo>(`/equipamentos/${id}`).then((r) => r.data),
};

export type EquipamentosService = typeof equipamentosService;
