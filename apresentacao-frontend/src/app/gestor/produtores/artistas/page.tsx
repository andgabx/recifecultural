"use client";

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';

export interface Artista {
  id: string;
  nome: string;
  biografia: string;
  cpfCnpj: string;
  produtorId: string;
  produtorNome?: string;
  status: 'ATIVO' | 'INATIVO';
}

export interface CadastrarArtistaPayload {
  produtorId: string;
  nome: string;
  riderItens: string[];
}

export function useArtistas(produtorIdFiltro?: string) {
  const queryClient = useQueryClient();

  const { data: artistas } = useQuery({
    queryKey: ['artistas', produtorIdFiltro ?? 'todos'],
    queryFn: async () => {
      const url = produtorIdFiltro
        ? `/artistas?produtorId=${produtorIdFiltro}`
        : '/artistas';
      const { data } = await api.get(url);
      return data as Artista[];
    },
  });

  // POST /artistas — cadastra artista vinculado a um produtor
  const createMutation = useMutation({
    mutationFn: async (payload: CadastrarArtistaPayload) => {
      const { data } = await api.post('/artistas', payload);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['artistas'] }),
  });

  // POST /artistas/{id}/inativar
  const inativarMutation = useMutation({
    mutationFn: async (id: string) => {
      const { data } = await api.post(`/artistas/${id}/inativar`);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['artistas'] }),
  });

  // POST /artistas/{id}/reativar
  const reativarMutation = useMutation({
    mutationFn: async (id: string) => {
      const { data } = await api.post(`/artistas/${id}/reativar`);
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['artistas'] }),
  });

  return {
    artistas,
    createArtista: createMutation.mutateAsync,
    inativarArtista: inativarMutation.mutateAsync,
    reativarArtista: reativarMutation.mutateAsync,
  };
}

export default function ArtistasPage() {
  const { artistas } = useArtistas();

  return (
    <div className="p-6 flex flex-col gap-4">
      <h1 className="text-2xl font-bold">Gestão de Artistas</h1>
      
      { (
        <div className="bg-white p-4 rounded-lg shadow border border-gray-200">
          {artistas && artistas.length > 0 ? (
            <ul className="space-y-2">
              {artistas.map((artista) => (
                <li key={artista.id} className="p-3 bg-gray-50 rounded-md border">
                  <p className="font-semibold">{artista.nome}</p>
                  <p className="text-sm text-gray-600">Status: {artista.status}</p>
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-sm text-gray-500">Nenhum artista encontrado.</p>
          )}
        </div>
      )}
    </div>
  );
}