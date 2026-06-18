import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';

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

  const toggleStatusMutation = useMutation({
    mutationFn: async ({ id, status }: { id: string, status: string }) => {
      const { data } = await api.patch(`/produtores/${id}/status`, { status });
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
    toggleStatus: toggleStatusMutation.mutateAsync,
    deleteProdutor: deleteMutation.mutateAsync
  };
}