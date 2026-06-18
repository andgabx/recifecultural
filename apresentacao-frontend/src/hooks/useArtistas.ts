import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';

export interface Artista {
  id: string;
  nome: string;
  biografia: string;
  cpfCnpj: string;
  status: 'ATIVO' | 'INATIVO';
}

export function useArtistas() {
  const queryClient = useQueryClient();

  const { data: artistas, isLoading } = useQuery({
    queryKey: ['artistas'],
    queryFn: async () => {
      const { data } = await api.get('/artistas');
      return data as Artista[];
    }
  });

  const createMutation = useMutation({
    mutationFn: async (novoArtista: Partial<Artista>) => {
      const { data } = await api.post('/artistas', novoArtista);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['artistas'] })
  });

  const updateMutation = useMutation({
    mutationFn: async ({ id, ...dados }: Partial<Artista> & { id: string }) => {
      const { data } = await api.put(`/artistas/${id}`, dados);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['artistas'] })
  });

  // Alterado para utilizar o método PUT enviando o objeto completo
  const toggleStatusMutation = useMutation({
    mutationFn: async ({ artista, status }: { artista: Artista, status: 'ATIVO' | 'INATIVO' }) => {
      const { data } = await api.put(`/artistas/${artista.id}`, { ...artista, status });
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['artistas'] })
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/artistas/${id}`);
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['artistas'] })
  });

  return {
    artistas,
    isLoading,
    createArtista: createMutation.mutateAsync,
    updateArtista: updateMutation.mutateAsync,
    toggleStatus: toggleStatusMutation.mutateAsync,
    deleteArtista: deleteMutation.mutateAsync
  };
}