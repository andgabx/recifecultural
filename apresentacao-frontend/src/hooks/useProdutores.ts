import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';

export interface Produtor {
  id: string;
  nomeFantasia: string;
  cnpj: string;
  email: string;
  telefone: string;
  status: 'ATIVO' | 'INATIVO' | 'PENDENTE' | 'BLOQUEADO';
}

export interface CadastrarProdutorPayload {
  nomeFantasia: string;
  cnpj: string;
  email: string;
  telefone: string;
}

export interface AcaoAdministrativaPayload {
  responsavel: string;
  motivo: string;
}

export function useProdutores() {
  const queryClient = useQueryClient();

  const { data: produtores, isLoading } = useQuery({
    queryKey: ['produtores'],
    queryFn: async () => {
      const { data } = await api.get('/produtores');
      return data as Produtor[];
    },
  });

  // POST /produtores
  const createMutation = useMutation({
    mutationFn: async (payload: CadastrarProdutorPayload) => {
      const { data } = await api.post('/produtores', payload);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produtores'] }),
  });

  // POST /produtores/{id}/suspender
  const suspenderMutation = useMutation({
    mutationFn: async ({ id, ...payload }: AcaoAdministrativaPayload & { id: string }) => {
      const { data } = await api.post(`/produtores/${id}/suspender`, payload);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produtores'] }),
  });

  // POST /produtores/{id}/reativar
  const reativarMutation = useMutation({
    mutationFn: async ({ id, ...payload }: AcaoAdministrativaPayload & { id: string }) => {
      const { data } = await api.post(`/produtores/${id}/reativar`, payload);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produtores'] }),
  });

  // POST /produtores/{id}/inativar
  const inativarMutation = useMutation({
    mutationFn: async ({ id, ...payload }: AcaoAdministrativaPayload & { id: string }) => {
      const { data } = await api.post(`/produtores/${id}/inativar`, payload);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produtores'] }),
  });

  return {
    produtores,
    isLoading,
    createProdutor: createMutation.mutateAsync,
    suspenderProdutor: suspenderMutation.mutateAsync,
    reativarProdutor: reativarMutation.mutateAsync,
    inativarProdutor: inativarMutation.mutateAsync,
  };
}