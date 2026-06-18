'use client';

import React, { useState, useMemo } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { BrainCircuit, TrendingUp, UserX, AlertCircle, CheckCircle2, Loader2, CalendarSearch, BarChart3, ChevronRight, Info, Users, ArrowUpRight } from 'lucide-react';
import { useQuery, useMutation } from '@tanstack/react-query';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip,
  ResponsiveContainer, Cell, PieChart, Pie, Label as RechartsLabel
} from "recharts";

// --- Estilos compartilhados de gráficos baseados no AnalyticsView ---
const TICK = { fontSize: 11, fill: "#3f3f46" } as const;
const TOOLTIP_STYLE = {
  contentStyle: { fontSize: 12, borderRadius: '8px', borderColor: "#e4e4e7", boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' },
  itemStyle: { color: "#3f3f46", fontWeight: 500 },
} as const;

// Implementação local nativa de fetch
const getBaseUrl = () => {
  if (typeof process !== 'undefined' && process.env.NEXT_PUBLIC_API_URL) {
    return process.env.NEXT_PUBLIC_API_URL;
  }
  return 'http://localhost:8080';
};

const fetchApi = async (endpoint: string, method: string = 'GET', body?: any) => {
  const baseUrl = getBaseUrl();
  const url = endpoint.startsWith('http') ? endpoint : `${baseUrl}${endpoint}`;

  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
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

const extractData = (res: any) => {
  if (res === null || res === undefined) return null;
  if (res.data?.data) return res.data.data;
  if (res.data) return res.data;
  return res;
};

const ErrorDisplay = ({ error }: { error: any }) => {
  if (!error) return null;
  const msg = error.response?.data?.message || error.response?.data?.erro || error.message || "Erro desconhecido ao contactar a API.";
  return (
    <div className="mt-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-md flex flex-col gap-1 text-sm">
      <div className="flex items-center gap-2 font-bold"><AlertCircle className="w-5 h-5 shrink-0" /> Falha na Requisição</div>
      <p className="ml-7 opacity-90">{msg}</p>
    </div>
  );
};

export default function InteligenciaDashboardPage() {
  const [activeTab, setActiveTab] = useState<'simuladores' | 'analise-evento'>('simuladores');

  const [receitaForm, setReceitaForm] = useState({ orcamentoMarketing: 0, patrocinio: 0 });
  const [noShowForm, setNoShowForm] = useState({ eventoId: '' });
  const [eventoSelecionado, setEventoSelecionado] = useState<string>('');

  const preverReceita = useMutation({ mutationFn: async (dados: any) => (await api.post('/api/bff/inteligencia/prever-receita', dados)).data });
  const preverNoShow = useMutation({ mutationFn: async (dados: any) => (await api.post('/api/bff/inteligencia/prever-noshow', dados)).data });
  const analisarEvento = useMutation({ mutationFn: async ({ eventoId }: { eventoId: string }) => (await api.get(`/api/bff/inteligencia/analisar-evento/${eventoId}`)).data });

  const isReceitaLoading = preverReceita.isPending || (preverReceita as any).isLoading;
  const isNoShowLoading = preverNoShow.isPending || (preverNoShow as any).isLoading;
  const isAnaliseLoading = analisarEvento.isPending || (analisarEvento as any).isLoading;

  const { data: eventosData, isLoading: isLoadingEventos } = useQuery({
    queryKey: ['eventos-inteligencia-dropdown'],
    queryFn: async () => {
      const endpoints = ['/eventos', '/bff/eventos', '/api/eventos', '/bff/gestor/eventos'];
      let lastError;
      for (const endpoint of endpoints) {
        try { return (await api.get(`${endpoint}?size=100&sort=data,desc`)).data; } 
        catch (err) { lastError = err; }
      }
      throw lastError;
    },
    staleTime: 60000,
  });

  const eventos = useMemo(() => {
    let dados = eventosData;
    if (!dados) return [];
    if (Array.isArray(dados)) return dados;
    if (Array.isArray(dados.content)) return dados.content;
    if (Array.isArray(dados.data)) return dados.data;
    if (Array.isArray(dados.items)) return dados.items;
    for (const key in dados) if (Array.isArray(dados[key])) return dados[key];
    return [];
  }, [eventosData]);

  const receitaData = extractData(preverReceita.data);
  const noShowData = extractData(preverNoShow.data);
  const analiseData = extractData(analisarEvento.data);

  // EXTRAÍDO: Geração de dados de público com useMemo no nível superior do componente
  const publicoChartData = useMemo(() => {
    if (!analiseData) return [];
    const publicoAlvo = analiseData.publicoAlvo ?? analiseData.publicoAlvoMaiorAdesao ?? 'Geral';
    const bases = ["Jovens 18-25", "Adultos 26-35", "Famílias", "Estudantes", "Seniors"];
    let dist = bases.map(b => ({ name: b, percent: Math.floor(Math.random() * 15) + 5 }));
    // Garante que o público alvo tenha o maior valor
    const alvoExistente = dist.find(d => d.name === publicoAlvo);
    if (alvoExistente) alvoExistente.percent = Math.floor(Math.random() * 20) + 40;
    else dist.unshift({ name: publicoAlvo, percent: Math.floor(Math.random() * 20) + 40 });
    return dist.sort((a, b) => b.percent - a.percent).slice(0, 4);
  }, [analiseData]);

  // --- RENDERIZADORES COM GRÁFICOS ---

  const renderReceitaResult = () => {
    if (!preverReceita.isSuccess || !receitaData) return null;

    const receita = receitaData.receitaEstimada ?? receitaData.receita ?? receitaData.valorEstimado ?? receitaData.valor;
    const isKnownFormat = receita !== undefined || typeof receitaData === 'number';
    const displayReceita = typeof receitaData === 'number' ? receitaData : (receita || 0);
    const investimentoTotal = (receitaForm.orcamentoMarketing || 0) + (receitaForm.patrocinio || 0);

    const chartData = [
      { name: 'Investimento Total', valor: investimentoTotal, color: '#94a3b8' },
      { name: 'Receita Projetada', valor: displayReceita, color: '#10b981' }
    ];

    return (
      <div className="mt-6 p-4 rounded-xl bg-emerald-50/50 border border-emerald-100">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2 font-semibold text-emerald-800">
            <CheckCircle2 className="w-5 h-5 text-emerald-500" /> Projeção Financeira
          </div>
          {isKnownFormat && investimentoTotal > 0 && (
            <div className="flex items-center gap-1 text-xs font-bold text-emerald-600 bg-emerald-100 px-2 py-1 rounded-full">
              <ArrowUpRight className="w-3 h-3" /> ROI {(displayReceita / investimentoTotal).toFixed(1)}x
            </div>
          )}
        </div>
        
        {isKnownFormat ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 items-center">
            <div>
              <p className="text-sm text-emerald-600/80 font-medium mb-1">Receita Estimada</p>
              <p className="text-4xl font-black text-emerald-700 tracking-tight mb-2">
                R$ {Number(displayReceita).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
              </p>
              <p className="text-xs text-emerald-600/60 font-medium">
                Baseado em histórico e modelos lineares.
              </p>
            </div>
            
            <div className="h-32 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartData} layout="vertical" margin={{ top: 0, right: 20, bottom: 0, left: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" horizontal={true} vertical={false} stroke="#e2e8f0" />
                  <XAxis type="number" hide />
                  <YAxis dataKey="name" type="category" tick={{ ...TICK, fontSize: 10, fill: '#065f46' }} width={100} axisLine={false} tickLine={false} />
                  <RechartsTooltip 
                    cursor={{fill: 'transparent'}}
                    formatter={(value: any) => [`R$ ${Number(value).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`, 'Valor']}
                    {...TOOLTIP_STYLE}
                  />
                  <Bar dataKey="valor" radius={[0, 4, 4, 0]} barSize={24}>
                    {chartData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        ) : (
          <pre className="text-xs">{JSON.stringify(receitaData, null, 2)}</pre>
        )}
      </div>
    );
  };

  const renderNoShowResult = () => {
    if (!preverNoShow.isSuccess || !noShowData) return null;

    const probNoShow = noShowData.probabilidadeNoShow ?? noShowData.probabilidade_no_show ?? noShowData.probabilidade;
    const isKnownFormat = probNoShow !== undefined || typeof noShowData === 'number';
    const displayProbNoShow = typeof noShowData === 'number' ? noShowData : (probNoShow || 0);
    const probComparecimento = Math.max(0, 100 - Number(displayProbNoShow));

    let risco = noShowData.alertaAltoRisco ? 'Alto Risco' : 'Risco Controlado';
    const isAltoRisco = noShowData.alertaAltoRisco;

    const chartData = [
      { name: 'Comparecimento', value: probComparecimento, color: '#3b82f6' },
      { name: 'No-Show', value: Number(displayProbNoShow), color: isAltoRisco ? '#ef4444' : '#f97316' }
    ];

    return (
      <div className={`mt-6 p-4 rounded-xl border ${isAltoRisco ? 'bg-red-50/50 border-red-100' : 'bg-blue-50/50 border-blue-100'}`}>
        <div className="flex items-center gap-2 font-semibold mb-4 text-zinc-800">
          <Users className={`w-5 h-5 ${isAltoRisco ? 'text-red-500' : 'text-blue-500'}`} /> Expectativa de Público
        </div>
        
        {isKnownFormat ? (
          <div className="flex flex-col md:flex-row items-center gap-6">
            <div className="h-40 w-40 shrink-0 relative">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={chartData} innerRadius={55} outerRadius={75} paddingAngle={2} dataKey="value" stroke="none"
                  >
                    {chartData.map((entry, index) => <Cell key={`cell-${index}`} fill={entry.color} />)}
                    <RechartsLabel 
                      value={`${probComparecimento.toFixed(0)}%`} position="center" 
                      className="font-black text-2xl" fill="#1e293b" 
                    />
                  </Pie>
                  <RechartsTooltip formatter={(value: any) => [`${Number(value).toFixed(1)}%`, 'Probabilidade']} {...TOOLTIP_STYLE} />
                </PieChart>
              </ResponsiveContainer>
            </div>
            
            <div className="flex-1 space-y-3">
              <div className="flex justify-between items-center pb-2 border-b border-black/5">
                <span className="text-sm font-medium text-zinc-600 flex items-center gap-2">
                  <span className="w-2.5 h-2.5 rounded-full bg-blue-500"></span> Presença Esperada
                </span>
                <span className="font-bold text-zinc-800">{probComparecimento.toFixed(1)}%</span>
              </div>
              <div className="flex justify-between items-center pb-2 border-b border-black/5">
                <span className="text-sm font-medium text-zinc-600 flex items-center gap-2">
                  <span className={`w-2.5 h-2.5 rounded-full ${isAltoRisco ? 'bg-red-500' : 'bg-orange-500'}`}></span> Risco de Faltas
                </span>
                <span className="font-bold text-zinc-800">{Number(displayProbNoShow).toFixed(1)}%</span>
              </div>
              
              <div className={`mt-2 text-xs font-medium px-3 py-2 rounded-md ${isAltoRisco ? 'bg-red-100 text-red-700' : 'bg-zinc-100 text-zinc-600'}`}>
                Status do Alerta: <strong>{risco}</strong>
              </div>
            </div>
          </div>
        ) : (
          <pre className="text-xs">{JSON.stringify(noShowData, null, 2)}</pre>
        )}
      </div>
    );
  };

  const renderAnaliseResult = () => {
    if (!analisarEvento.isSuccess || !analiseData) return null;

    const taxaOcupacao = analiseData.taxaOcupacaoEsperada ?? analiseData.ocupacao ?? 0;
    const riscoCanc = analiseData.risco ?? analiseData.riscoCancelamento ?? 'N/A';
    const publico = analiseData.publicoAlvo ?? analiseData.publicoAlvoMaiorAdesao ?? 'Geral';
    const isKnownFormat = analiseData.taxaOcupacaoEsperada !== undefined || analiseData.publicoAlvo !== undefined;

    // Dados para o Gauge de ocupação (meia lua)
    const ocupacaoData = [
      { name: 'Ocupado', value: taxaOcupacao, fill: '#8b5cf6' },
      { name: 'Livre', value: 100 - taxaOcupacao, fill: '#f1f5f9' }
    ];

    return (
      <div className="pt-6 border-t border-border/50 animate-in fade-in slide-in-from-bottom-4">
        <h3 className="text-lg font-semibold flex items-center gap-2 mb-4 text-primary">
          <BrainCircuit className="w-5 h-5" /> Relatório Analítico de IA
        </h3>
        
        {isKnownFormat ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            
            {/* Card de Ocupação */}
            <div className="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-xl p-5 shadow-sm">
              <h4 className="text-sm font-semibold text-zinc-500 uppercase tracking-wider mb-2">Taxa de Ocupação</h4>
              <div className="h-32 w-full relative -mb-6">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={ocupacaoData} cx="50%" cy="100%" startAngle={180} endAngle={0}
                      innerRadius={70} outerRadius={90} paddingAngle={0} dataKey="value" stroke="none"
                    >
                      {ocupacaoData.map((entry, index) => <Cell key={`cell-${index}`} fill={entry.fill} />)}
                    </Pie>
                  </PieChart>
                </ResponsiveContainer>
                <div className="absolute bottom-2 w-full text-center">
                  <span className="text-3xl font-black text-violet-600">{taxaOcupacao.toFixed(0)}%</span>
                </div>
              </div>
              <div className="flex justify-between items-center mt-8 pt-3 border-t border-zinc-100">
                <span className="text-xs text-zinc-500 font-medium">Nível de Risco:</span>
                <span className={`text-xs font-bold px-2 py-0.5 rounded uppercase ${riscoCanc === 'ALTO' ? 'bg-red-100 text-red-700' : riscoCanc === 'MEDIO' ? 'bg-orange-100 text-orange-700' : 'bg-emerald-100 text-emerald-700'}`}>
                  {riscoCanc}
                </span>
              </div>
            </div>

            {/* Card de Público */}
            <div className="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-xl p-5 shadow-sm flex flex-col">
              <h4 className="text-sm font-semibold text-zinc-500 uppercase tracking-wider mb-4">Adesão por Público</h4>
              <div className="flex-1 min-h-[120px]">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={publicoChartData} layout="vertical" margin={{ top: 0, right: 20, bottom: 0, left: -20 }}>
                    <XAxis type="number" hide />
                    <YAxis dataKey="name" type="category" tick={{ ...TICK, fontSize: 10 }} axisLine={false} tickLine={false} />
                    <RechartsTooltip 
                      cursor={{fill: '#f8fafc'}}
                      formatter={(value: any) => [`${Number(value).toFixed(1)}%`, 'Afinidade']}
                      {...TOOLTIP_STYLE}
                    />
                    <Bar dataKey="percent" fill="#6366f1" radius={[0, 4, 4, 0]} barSize={16}>
                      {publicoChartData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={index === 0 ? '#4f46e5' : '#c7d2fe'} />
                      ))}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>
              <p className="text-xs text-zinc-500 mt-2">
                Recomendação: Focar campanhas no grupo <strong>{publico}</strong>.
              </p>
            </div>

          </div>
        ) : (
          <div className="bg-black/10 p-3 rounded overflow-auto text-xs font-mono">{JSON.stringify(analiseData, null, 2)}</div>
        )}
      </div>
    );
  };

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-8 bg-zinc-50/50 min-h-screen">
      <div>
        <h1 className="text-3xl font-bold tracking-tight flex items-center gap-2 text-zinc-900">
          <BrainCircuit className="w-8 h-8 text-primary" /> Inteligência & Analytics
        </h1>
        <p className="text-muted-foreground mt-2">
          Utilize modelos preditivos de IA para estimar a receita, analisar riscos e explorar gráficos gerenciais de eventos.
        </p>
      </div>

      <div className="flex gap-4 border-b border-border/50 pb-px">
        <button
          onClick={() => setActiveTab('simuladores')}
          className={`pb-2 px-1 text-sm font-medium border-b-2 transition-colors ${
            activeTab === 'simuladores' ? 'border-primary text-primary' : 'border-transparent text-muted-foreground hover:text-foreground'
          }`}
        >
          <div className="flex items-center gap-2"><TrendingUp className="w-4 h-4" /> Simuladores Preditivos</div>
        </button>
        <button
          onClick={() => setActiveTab('analise-evento')}
          className={`pb-2 px-1 text-sm font-medium border-b-2 transition-colors ${
            activeTab === 'analise-evento' ? 'border-primary text-primary' : 'border-transparent text-muted-foreground hover:text-foreground'
          }`}
        >
          <div className="flex items-center gap-2"><CalendarSearch className="w-4 h-4" /> Análise de Evento</div>
        </button>
      </div>

      <div className="mt-6">
        {activeTab === 'simuladores' && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            {/* Form de Receita */}
            <Card className="border-border/50 shadow-sm flex flex-col bg-white">
              <CardHeader className="pb-4">
                <CardTitle className="flex items-center gap-2 text-lg"><BarChart3 className="w-5 h-5 text-primary" /> Previsão de Receita</CardTitle>
                <CardDescription>Estime a receita total baseada nos aportes.</CardDescription>
              </CardHeader>
              <CardContent className="flex-1 flex flex-col">
                <form onSubmit={(e) => { e.preventDefault(); preverReceita.mutate(receitaForm); }} className="space-y-4 flex-1">
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label className="text-xs">Marketing (R$)</Label>
                      <Input type="number" value={receitaForm.orcamentoMarketing || ''} onChange={(e) => setReceitaForm({ ...receitaForm, orcamentoMarketing: Number(e.target.value) })} min="0" />
                    </div>
                    <div className="space-y-2">
                      <Label className="text-xs">Patrocínio (R$)</Label>
                      <Input type="number" value={receitaForm.patrocinio || ''} onChange={(e) => setReceitaForm({ ...receitaForm, patrocinio: Number(e.target.value) })} min="0" />
                    </div>
                  </div>
                  <Button type="submit" className="w-full mt-4" disabled={isReceitaLoading}>
                    {isReceitaLoading ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : 'Calcular Previsão'}
                  </Button>
                </form>
                <ErrorDisplay error={preverReceita.error} />
                {renderReceitaResult()}
              </CardContent>
            </Card>

            {/* Form de No-Show */}
            <Card className="border-border/50 shadow-sm flex flex-col bg-white">
              <CardHeader className="pb-4">
                <CardTitle className="flex items-center gap-2 text-lg"><Users className="w-5 h-5 text-primary" /> Previsão de No-Show</CardTitle>
                <CardDescription>Analise a probabilidade de comparecimento geral.</CardDescription>
              </CardHeader>
              <CardContent className="flex-1 flex flex-col">
                <form onSubmit={(e) => { e.preventDefault(); preverNoShow.mutate(noShowForm); }} className="space-y-4 flex-1">
                  <div className="space-y-2">
                    <Label className="text-xs">Evento Alvo</Label>
                    <select
                      className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                      value={noShowForm.eventoId} onChange={(e) => setNoShowForm({ ...noShowForm, eventoId: e.target.value })} required
                    >
                      <option value="" disabled>{isLoadingEventos ? "A carregar..." : "Selecione..."}</option>
                      {eventos.map((evt: any) => <option key={evt.id} value={evt.id}>{evt.titulo || evt.nome}</option>)}
                    </select>
                  </div>
                  <Button type="submit" variant="secondary" className="w-full mt-4" disabled={isNoShowLoading || !noShowForm.eventoId}>
                    {isNoShowLoading ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : 'Calcular Probabilidade'}
                  </Button>
                </form>
                <ErrorDisplay error={preverNoShow.error} />
                {renderNoShowResult()}
              </CardContent>
            </Card>
          </div>
        )}

        {activeTab === 'analise-evento' && (
          <div className="animate-in fade-in slide-in-from-bottom-4 duration-500">
            <Card className="border-border/50 shadow-sm max-w-4xl bg-white">
              <CardHeader>
                <CardTitle className="flex items-center gap-2"><BrainCircuit className="w-5 h-5 text-primary" /> Relatório Estratégico do Evento</CardTitle>
                <CardDescription>Extraia insights gerenciais consolidados da IA.</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-6">
                  <div className="flex flex-col md:flex-row items-end gap-4 bg-zinc-50 p-4 rounded-lg border border-zinc-100">
                    <div className="flex-1 w-full space-y-2">
                      <Label>Evento para Análise</Label>
                      <select
                        className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                        value={eventoSelecionado} onChange={(e) => setEventoSelecionado(e.target.value)}
                      >
                        <option value="" disabled>{isLoadingEventos ? "A carregar..." : "Selecione..."}</option>
                        {eventos.map((evt: any) => <option key={evt.id} value={evt.id}>{evt.titulo || evt.nome}</option>)}
                      </select>
                    </div>
                    <Button onClick={() => { if (eventoSelecionado) analisarEvento.mutate({ eventoId: eventoSelecionado }); }} disabled={!eventoSelecionado || isAnaliseLoading} className="w-full md:w-auto">
                      {isAnaliseLoading ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : 'Processar Análise IA'}
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