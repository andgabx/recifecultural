import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';

export interface Produtor {
  id: string;
  nome: string;
  cnpj: string;
  email: string;
  status: 'ATIVO' | 'INATIVO' | 'PENDENTE' | 'BLOQUEADO';
}

export function useProdutores() {
  const queryClient = useQueryClient();

  const { data: produtores, isLoading } = useQuery({
    queryKey: ['produtores'],
    queryFn: async () => {
      const { data } = await api.get('/produtores');
      return data as Produtor[];
    }
  });

  const createMutation = useMutation({
    mutationFn: async (novoProdutor: Partial<Produtor>) => {
      const { data } = await api.post('/produtores', novoProdutor);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produtores'] })
  });

  const updateMutation = useMutation({
    mutationFn: async ({ id, ...dados }: Partial<Produtor> & { id: string }) => {
      const { data } = await api.put(`/produtores/${id}`, dados);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produtores'] })
  });

  // Alterado para utilizar o método PUT enviando o objeto completo
  const toggleStatusMutation = useMutation({
    mutationFn: async ({ produtor, status }: { produtor: Produtor, status: string }) => {
      const { data } = await api.put(`/produtores/${produtor.id}`, { ...produtor, status });
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produtores'] })
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/produtores/${id}`);
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produtores'] })
  });

  return {
    produtores,
    isLoading,
    createProdutor: createMutation.mutateAsync,
    updateProdutor: updateMutation.mutateAsync,
    toggleStatus: toggleStatusMutation.mutateAsync,
    deleteProdutor: deleteMutation.mutateAsync
  };
}