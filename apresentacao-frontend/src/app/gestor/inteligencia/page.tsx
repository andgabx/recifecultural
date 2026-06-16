'use client';

import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { BrainCircuit, TrendingUp, UserX, AlertCircle, CheckCircle2, Loader2, CalendarSearch, BarChart3, ChevronRight, Info, Users } from 'lucide-react';
import { useQuery, useMutation } from '@tanstack/react-query';

// Implementação local nativa de fetch (bypassa problemas de compilação do arquivo '@/lib/api' no ambiente de preview,
// mantendo envio seguro de credenciais como cookies/tokens originais da aplicação)
const getBaseUrl = () => {
  if (typeof process !== 'undefined' && process.env.NEXT_PUBLIC_API_URL) {
    return process.env.NEXT_PUBLIC_API_URL;
  }
  return '';
};

const fetchApi = async (endpoint: string, method: string = 'GET', body?: any) => {
  const baseUrl = getBaseUrl();
  const url = endpoint.startsWith('http') ? endpoint : `${baseUrl}${endpoint}`;

  const res = await fetch(url, {
    method,
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    },
    body: body ? JSON.stringify(body) : undefined,
    credentials: 'include'
  });

  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw { response: { status: res.status, data: errorData } };
  }

  return { data: await res.json() };
};

const api = {
  get: (url: string) => fetchApi(url, 'GET'),
  post: (url: string, body: any) => fetchApi(url, 'POST', body),
};

// Helper para desembrulhar respostas API
const extractData = (res: any) => {
  if (res === null || res === undefined) return null;
  if (res.data?.data) return res.data.data;
  if (res.data) return res.data;
  return res;
};

// Componente para exibir o erro
const ErrorDisplay = ({ error }: { error: any }) => {
  if (!error) return null;
  const msg = error.response?.data?.message || error.response?.data?.erro || error.message || "Erro desconhecido ao contactar a API.";
  const details = error.response?.data?.details || error.response?.data?.detalhes || error.response?.data?.error;

  return (
    <div className="mt-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-md flex flex-col gap-1 text-sm">
      <div className="flex items-center gap-2 font-bold">
        <AlertCircle className="w-5 h-5 shrink-0" /> Falha na Requisição ({error.response?.status || 'Erro interno'})
      </div>
      <p className="ml-7 opacity-90">{msg}</p>
      {details && <p className="ml-7 text-xs opacity-75 font-mono bg-red-100/50 p-1 rounded mt-1">{details}</p>}
    </div>
  );
};

export default function InteligenciaDashboardPage() {
  const [activeTab, setActiveTab] = useState<'simuladores' | 'analise-evento'>('simuladores');

  // Estados dos formulários (Receita usa apenas valores, No-Show usa APENAS eventoId)
  const [receitaForm, setReceitaForm] = useState({ orcamentoMarketing: 0, patrocinio: 0 });
  const [noShowForm, setNoShowForm] = useState({ eventoId: '' });
  const [eventoSelecionado, setEventoSelecionado] = useState<string>('');

  // Declaração Inline e super-resiliente dos Hooks
  const preverReceita = useMutation({
    mutationFn: async (dados: any) => {
      const endpoints = ['/api/inteligencia/receita', '/bff/gestor/inteligencia/receita', '/api/inteligencia/prever-receita'];
      let lastErr;
      for (const ep of endpoints) {
        try {
          const res = await api.post(ep, dados);
          return res.data;
        } catch (e: any) {
          lastErr = e;
          if (e.response?.status !== 404 && e.response?.status !== 405) throw e;
        }
      }
      throw lastErr;
    }
  });

  const preverNoShow = useMutation({
    mutationFn: async (dados: any) => {
      const endpoints = ['/api/inteligencia/no-show', '/bff/gestor/inteligencia/no-show', '/api/inteligencia/prever-no-show'];
      let lastErr;
      for (const ep of endpoints) {
        try {
          const res = await api.post(ep, dados);
          return res.data;
        } catch (e: any) {
          lastErr = e;
          if (e.response?.status !== 404 && e.response?.status !== 405) throw e;
        }
      }
      throw lastErr;
    }
  });

  const analisarEvento = useMutation({
    mutationFn: async ({ eventoId }: { eventoId: string }) => {
      const getEndpoints = [`/api/inteligencia/analise/${eventoId}`, `/bff/gestor/inteligencia/analise/${eventoId}`];
      const postEndpoints = ['/api/inteligencia/analise', '/bff/gestor/inteligencia/analise'];
      let lastErr;

      for (const ep of getEndpoints) {
        try {
          const res = await api.get(ep);
          return res.data;
        } catch (e: any) {
          lastErr = e;
          if (e.response?.status !== 404 && e.response?.status !== 405) throw e;
        }
      }
      for (const ep of postEndpoints) {
        try {
          const res = await api.post(ep, { eventoId });
          return res.data;
        } catch (e: any) {
          lastErr = e;
          if (e.response?.status !== 404 && e.response?.status !== 405) throw e;
        }
      }
      throw lastErr;
    }
  });

  const isReceitaLoading = preverReceita.isPending || (preverReceita as any).isLoading;
  const isNoShowLoading = preverNoShow.isPending || (preverNoShow as any).isLoading;
  const isAnaliseLoading = analisarEvento.isPending || (analisarEvento as any).isLoading;

  // Busca dinâmica da lista de Eventos
  const { data: eventosData, isLoading: isLoadingEventos } = useQuery({
    queryKey: ['eventos-inteligencia-dropdown'],
    queryFn: async () => {
      const endpoints = ['/eventos', '/bff/eventos', '/api/eventos', '/bff/gestor/eventos'];
      let lastError;
      for (const endpoint of endpoints) {
        try {
          const res = await api.get(`${endpoint}?size=100&sort=data,desc`);
          if (res.data) return res.data;
        } catch (err) {
          lastError = err;
        }
      }
      throw lastError;
    },
    staleTime: 60000,
  });

  const extrairListaDeEventos = (dados: any): any[] => {
    if (!dados) return [];
    if (Array.isArray(dados)) return dados;
    if (Array.isArray(dados.content)) return dados.content;
    if (Array.isArray(dados.data)) return dados.data;
    if (Array.isArray(dados.items)) return dados.items;
    for (const key in dados) {
      if (Array.isArray(dados[key])) return dados[key];
    }
    return [];
  };

  const eventos = extrairListaDeEventos(eventosData);

  const renderEventOptions = () => (
    <>
      <option value="" disabled>
        {isLoadingEventos ? "A carregar eventos..." : "Selecione o evento"}
      </option>
      {!isLoadingEventos && eventos.length === 0 && (
        <option value="empty" disabled>Nenhum evento encontrado</option>
      )}
      {!isLoadingEventos && eventos.map((evento: any) => (
        <option key={evento.id} value={String(evento.id)}>
          {evento.titulo || evento.nome || `Evento ID: ${evento.id}`}
        </option>
      ))}
    </>
  );

  // Extração segura dos dados retornados pelas predições
  const receitaData = extractData(preverReceita.data);
  const noShowData = extractData(preverNoShow.data);
  const analiseData = extractData(analisarEvento.data);

  // Renderizadores dinâmicos que previnem falhas invisíveis
  const renderReceitaResult = () => {
    if (!preverReceita.isSuccess || !receitaData) return null;

    const receita = receitaData.receitaEstimada ?? receitaData.receita ?? receitaData.valorEstimado ?? receitaData.valor;
    const margem = receitaData.margemErro ?? receitaData.margem_erro ?? receitaData.margem;
    const isKnownFormat = receita !== undefined || typeof receitaData === 'number';
    const displayReceita = typeof receitaData === 'number' ? receitaData : (receita || 0);

    return (
      <div className="mt-6 p-4 rounded-lg bg-emerald-500/10 border border-emerald-500/20 text-emerald-700 dark:text-emerald-400">
        <div className="flex items-center gap-2 font-medium">
          <CheckCircle2 className="w-5 h-5" /> Receita Estimada Projetada
        </div>
        {isKnownFormat ? (
          <>
            <p className="mt-2 text-3xl font-bold tracking-tight">
              R$ {Number(displayReceita).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
            </p>
            {margem !== undefined && margem !== null && (
              <p className="text-sm mt-1 opacity-80">
                Margem de erro: ± R$ {Number(margem).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
              </p>
            )}
          </>
        ) : (
          <div className="mt-3 bg-black/10 p-3 rounded overflow-auto text-xs font-mono">
            <p className="mb-2 opacity-80">Formato de resposta não reconhecido. Dados puros:</p>
            {JSON.stringify(receitaData, null, 2)}
          </div>
        )}
      </div>
    );
  };

  const renderNoShowResult = () => {
    if (!preverNoShow.isSuccess || !noShowData) return null;

    const probNoShow = noShowData.probabilidadeNoShow ?? noShowData.probabilidade_no_show ?? noShowData.probabilidade ?? noShowData.riscoNoShow;
    const risco = noShowData.risco ?? noShowData.nivelRisco ?? noShowData.nivel_risco;
    const isKnownFormat = probNoShow !== undefined || typeof noShowData === 'number';
    const displayProbNoShow = typeof noShowData === 'number' ? noShowData : (probNoShow || 0);
    const probComparecimento = Math.max(0, 100 - Number(displayProbNoShow));

    return (
      <div className="mt-6 p-4 rounded-lg bg-blue-500/10 border border-blue-500/20 text-blue-800 dark:text-blue-300">
        <div className="flex items-center gap-2 font-medium">
          <CheckCircle2 className="w-5 h-5" /> Comparecimento Estimado
        </div>
        {isKnownFormat ? (
          <>
            <p className="mt-2 text-3xl font-bold tracking-tight">
              {probComparecimento.toFixed(1)}%
            </p>
            <div className="flex items-center gap-2 mt-3 text-sm opacity-80 pt-2 border-t border-blue-500/20">
              <UserX className="w-4 h-4" />
              <span>Risco de Não Comparecimento (No-Show): {Number(displayProbNoShow).toFixed(1)}%</span>
              {risco && <span className="ml-auto font-semibold">({risco})</span>}
            </div>
          </>
        ) : (
          <div className="mt-3 bg-black/10 p-3 rounded overflow-auto text-xs font-mono">
            <p className="mb-2 opacity-80">Formato de resposta não reconhecido. Dados puros:</p>
            {JSON.stringify(noShowData, null, 2)}
          </div>
        )}
      </div>
    );
  };

  const renderAnaliseResult = () => {
    if (!analisarEvento.isSuccess || !analiseData) return null;

    const taxaOcupacao = analiseData.taxaOcupacaoEsperada ?? analiseData.taxa_ocupacao_esperada ?? analiseData.ocupacao;
    const riscoCanc = analiseData.riscoCancelamento ?? analiseData.risco_cancelamento ?? analiseData.risco;
    const publico = analiseData.publicoAlvoMaiorAdesao ?? analiseData.publico_alvo_maior_adesao ?? analiseData.publicoAlvo;
    const isKnownFormat = taxaOcupacao !== undefined || riscoCanc !== undefined || publico !== undefined;

    return (
      <div className="pt-6 border-t border-border/50 animate-in fade-in slide-in-from-bottom-4">
        <div className="rounded-xl bg-muted/50 p-6 border border-border/50">
          <h3 className="text-lg font-semibold flex items-center gap-2 mb-4 text-primary">
            <BrainCircuit className="w-5 h-5" /> Insights Estratégicos Extraídos
          </h3>
          {isKnownFormat ? (
            <ul className="space-y-4 text-sm text-foreground/80">
              <li className="flex items-start gap-3 bg-background p-3 rounded-lg border border-border/40 shadow-sm">
                <ChevronRight className="w-5 h-5 mt-0.5 text-primary shrink-0" />
                <span>
                  A ocupação atual projetada é de <strong>{taxaOcupacao ?? 'N/A'}%</strong>. Considerando as análises financeiras e operacionais, isto resulta num risco estimado de nível <strong className="uppercase">{riscoCanc ?? 'N/A'}</strong> para o evento.
                </span>
              </li>
              <li className="flex items-start gap-3 bg-background p-3 rounded-lg border border-border/40 shadow-sm">
                <ChevronRight className="w-5 h-5 mt-0.5 text-primary shrink-0" />
                <span>
                  Foram detetadas predominâncias nos perfis de compra que apontam <strong>{publico || 'um segmento específico'}</strong> como o público de maior adesão, recomendando o direcionamento de campanhas de conversão de ingressos para este grupo.
                </span>
              </li>
            </ul>
          ) : (
            <div className="bg-black/10 p-3 rounded overflow-auto text-xs font-mono">
              <p className="mb-2 opacity-80">Formato de resposta não reconhecido. Dados puros:</p>
              {JSON.stringify(analiseData, null, 2)}
            </div>
          )}
        </div>
      </div>
    );
  };

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-8">
      <div>
        <h1 className="text-3xl font-bold tracking-tight flex items-center gap-2">
          <BrainCircuit className="w-8 h-8 text-primary" />
          Inteligência & Analytics
        </h1>
        <p className="text-muted-foreground mt-2">
          Utilize modelos preditivos de IA para estimar a receita, a probabilidade de comparecimento total e analisar riscos dos eventos.
        </p>
      </div>

      {/* Navegação de Abas */}
      <div className="flex gap-4 border-b border-border/50 pb-px">
        <button
          onClick={() => setActiveTab('simuladores')}
          className={`pb-2 px-1 text-sm font-medium border-b-2 transition-colors ${
            activeTab === 'simuladores' ? 'border-primary text-primary' : 'border-transparent text-muted-foreground hover:text-foreground'
          }`}
        >
          <div className="flex items-center gap-2">
            <TrendingUp className="w-4 h-4" /> Simuladores Preditivos
          </div>
        </button>
        <button
          onClick={() => setActiveTab('analise-evento')}
          className={`pb-2 px-1 text-sm font-medium border-b-2 transition-colors ${
            activeTab === 'analise-evento' ? 'border-primary text-primary' : 'border-transparent text-muted-foreground hover:text-foreground'
          }`}
        >
          <div className="flex items-center gap-2">
            <CalendarSearch className="w-4 h-4" /> Análise de Evento
          </div>
        </button>
      </div>

      <div className="mt-6">
        {/* ABA: SIMULADORES */}
        {activeTab === 'simuladores' && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 animate-in fade-in slide-in-from-bottom-4 duration-500">

            {/* Form de Receita */}
            <Card className="border-border/50 shadow-sm flex flex-col">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <BarChart3 className="w-5 h-5 text-primary" />
                  Previsão de Receita
                </CardTitle>
                <CardDescription>Estime a receita total baseada nos aportes e orçamento de marketing.</CardDescription>
              </CardHeader>
              <CardContent className="flex-1 flex flex-col">
                <form onSubmit={(e) => { e.preventDefault(); preverReceita.mutate(receitaForm); }} className="space-y-4 flex-1">
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label>Marketing (R$)</Label>
                      <Input
                        type="number"
                        value={receitaForm.orcamentoMarketing || ''}
                        onChange={(e) => setReceitaForm({ ...receitaForm, orcamentoMarketing: Number(e.target.value) })}
                        placeholder="Ex: 5000"
                        min="0"
                      />
                    </div>
                    <div className="space-y-2">
                      <Label>Patrocínio (R$)</Label>
                      <Input
                        type="number"
                        value={receitaForm.patrocinio || ''}
                        onChange={(e) => setReceitaForm({ ...receitaForm, patrocinio: Number(e.target.value) })}
                        placeholder="Ex: 10000"
                        min="0"
                      />
                    </div>
                  </div>
                  <Button type="submit" className="w-full mt-4" disabled={isReceitaLoading}>
                    {isReceitaLoading ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : 'Calcular Previsão de Receita'}
                  </Button>
                </form>

                <ErrorDisplay error={preverReceita.error} />
                {renderReceitaResult()}
              </CardContent>
            </Card>

            {/* Form de No-Show (Comparecimento Total) */}
            <Card className="border-border/50 shadow-sm flex flex-col">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Users className="w-5 h-5 text-primary" />
                  Previsão de Comparecimento Total
                </CardTitle>
                <CardDescription>Analise a probabilidade de comparecimento geral com base no histórico do evento.</CardDescription>
              </CardHeader>
              <CardContent className="flex-1 flex flex-col">
                <form onSubmit={(e) => { e.preventDefault(); preverNoShow.mutate(noShowForm); }} className="space-y-4 flex-1">
                  <div className="space-y-2">
                    <Label>Evento Alvo</Label>
                    <select
                      className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:opacity-50"
                      value={noShowForm.eventoId}
                      onChange={(e) => setNoShowForm({ ...noShowForm, eventoId: e.target.value })}
                      required
                    >
                      {renderEventOptions()}
                    </select>
                  </div>
                  <Button type="submit" className="w-full mt-4" disabled={isNoShowLoading || !noShowForm.eventoId}>
                    {isNoShowLoading ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : 'Calcular Probabilidade'}
                  </Button>
                </form>

                <ErrorDisplay error={preverNoShow.error} />
                {renderNoShowResult()}
              </CardContent>
            </Card>
          </div>
        )}

        {/* ABA: ANÁLISE DE EVENTOS */}
        {activeTab === 'analise-evento' && (
          <div className="animate-in fade-in slide-in-from-bottom-4 duration-500">
            <Card className="border-border/50 shadow-sm max-w-3xl">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <BrainCircuit className="w-5 h-5 text-primary" />
                  Análise IA do Evento
                </CardTitle>
                <CardDescription>
                  Selecione um evento para gerar um relatório inteligente de ocupação, riscos e predominância de público.
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-6">
                  <div className="flex flex-col md:flex-row items-end gap-4">
                    <div className="flex-1 w-full space-y-2">
                      <Label>Evento para Análise</Label>
                      <select
                        className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:opacity-50"
                        value={eventoSelecionado}
                        onChange={(e) => setEventoSelecionado(e.target.value)}
                      >
                        {renderEventOptions()}
                      </select>

                      {eventos.length === 0 && !isLoadingEventos && (
                        <p className="text-xs flex items-center gap-1 text-muted-foreground mt-1">
                          <Info className="w-3 h-3" /> Verifique se há eventos registados no sistema.
                        </p>
                      )}
                    </div>
                    <Button
                      onClick={() => { if (eventoSelecionado) analisarEvento.mutate({ eventoId: eventoSelecionado }); }}
                      disabled={!eventoSelecionado || isAnaliseLoading}
                      className="w-full md:w-auto"
                    >
                      {isAnaliseLoading ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : 'Gerar Relatório'}
                    </Button>
                  </div>

                  <ErrorDisplay error={analisarEvento.error} />
                  {renderAnaliseResult()}
                </div>
              </CardContent>
            </Card>
          </div>
        )}
      </div>
    </div>
  );
}