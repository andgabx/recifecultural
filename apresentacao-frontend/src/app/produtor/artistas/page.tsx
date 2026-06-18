'use client';

import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { useMutation } from '@tanstack/react-query';
import { 
  BrainCircuit, 
  TrendingUp, 
  UserX, 
  AlertCircle, 
  CheckCircle2, 
  Loader2, 
  BarChart3, 
  Users, 
  CloudRain, 
  Sun, 
  Cloud,
  DollarSign,
  ArrowUpRight,
  ShieldAlert
} from 'lucide-react';

// ==========================================
// 1. TIPAGENS (Baseado no Backend Java)
// ==========================================
interface PrevisaoReceitaReq {
  orcamentoMarketing: number;
  patrocinio: number;
}

interface PrevisaoReceitaRes {
  investimentoTotal: number;
  receitaEstimada: number;
}

interface PrevisaoNoShowReq {
  ingressoId: string;
  antecedenciaCompraDias: number;
  previsaoClima: string;
}

interface PrevisaoNoShowRes {
  ingressoId: string;
  probabilidadeFalta: number;
  alertaAltoRisco: boolean;
}

// ==========================================
// 2. CONFIGURAÇÃO DE API
// ==========================================
// Aponta nativamente para o seu backend na porta 8080
const API_BASE_URL = 'http://localhost:8080';

const api = {
  // Alterado para 'async function' para evitar conflito com o parser do JSX
  post: async function <T>(endpoint: string, body: any): Promise<T> {
    const res = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(body),
    });

    if (!res.ok) {
      const errorData = await res.json().catch(() => ({ message: 'Erro inesperado no servidor' }));
      throw { status: res.status, data: errorData };
    }

    return res.json();
  }
};

// ==========================================
// 3. COMPONENTES AUXILIARES
// ==========================================
const ErrorDisplay = ({ error }: { error: any }) => {
  if (!error) return null;
  const msg = error.data?.message || error.data?.erro || error.message || "Erro de conexão com o servidor.";
  
  return (
    <div className="mt-4 p-4 bg-destructive/10 border border-destructive/20 text-destructive rounded-lg flex items-start gap-3 text-sm animate-in fade-in">
      <AlertCircle className="w-5 h-5 shrink-0 mt-0.5" />
      <div>
        <p className="font-semibold">Falha na Análise (Status: {error.status || 'Offline'})</p>
        <p className="opacity-90 mt-1">{msg}</p>
        <p className="text-xs opacity-70 mt-2 font-mono">Verifique se o backend (porta 8080) está em execução.</p>
      </div>
    </div>
  );
};

// ==========================================
// 4. PÁGINA PRINCIPAL
// ==========================================
export default function InteligenciaDashboardPage() {
  const [activeTab, setActiveTab] = useState<'simuladores' | 'relatorios'>('simuladores');

  // Estados com valores default úteis para UX
  const [receitaForm, setReceitaForm] = useState<PrevisaoReceitaReq>({ orcamentoMarketing: 0, patrocinio: 0 });
  const [noShowForm, setNoShowForm] = useState<PrevisaoNoShowReq>({ 
    ingressoId: `ING-${Math.floor(Math.random() * 10000)}`, 
    antecedenciaCompraDias: 15,
    previsaoClima: 'ensolarado'
  });

  // Mutações React Query
  const preverReceita = useMutation({
    mutationFn: (dados: PrevisaoReceitaReq) => api.post<PrevisaoReceitaRes>('/api/inteligencia/prever-receita', dados)
  });

  const preverNoShow = useMutation({
    mutationFn: (dados: PrevisaoNoShowReq) => api.post<PrevisaoNoShowRes>('/api/inteligencia/prever-noshow', dados)
  });

  // Renderizadores de Resultados Avançados
  const renderReceitaResult = () => {
    if (!preverReceita.isSuccess || !preverReceita.data) return null;
    const { investimentoTotal, receitaEstimada } = preverReceita.data;
    const lucroEstimado = receitaEstimada - investimentoTotal;
    const roi = investimentoTotal > 0 ? ((lucroEstimado / investimentoTotal) * 100).toFixed(1) : 0;

    return (
      <div className="mt-6 p-5 rounded-xl bg-gradient-to-br from-emerald-500/10 to-emerald-600/5 border border-emerald-500/20 text-emerald-800 dark:text-emerald-300 animate-in slide-in-from-bottom-2">
        <div className="flex items-center gap-2 font-medium mb-3">
          <CheckCircle2 className="w-5 h-5" /> Projeção de Faturamento Concluída
        </div>
        
        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm opacity-80 mb-1">Receita Total Estimada</p>
            <p className="text-3xl font-bold tracking-tight text-emerald-600 dark:text-emerald-400">
              R$ {receitaEstimada.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
            </p>
          </div>
          <div>
            <p className="text-sm opacity-80 mb-1">Lucro Projetado (ROI)</p>
            <div className="flex items-center gap-2">
              <p className="text-xl font-semibold">
                R$ {lucroEstimado.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
              </p>
              <span className="flex items-center text-xs bg-emerald-500/20 px-2 py-1 rounded-full font-bold">
                <ArrowUpRight className="w-3 h-3 mr-1" /> {roi}%
              </span>
            </div>
          </div>
        </div>
      </div>
    );
  };

  const renderNoShowResult = () => {
    if (!preverNoShow.isSuccess || !preverNoShow.data) return null;
    const { probabilidadeFalta, alertaAltoRisco, ingressoId } = preverNoShow.data;
    
    // Converte o decimal (0.15) em percentual (15%)
    const riscoPercentual = Math.round(probabilidadeFalta * 100);
    const presencaPercentual = 100 - riscoPercentual;

    // Determina cores baseadas no risco
    let riskColor = 'bg-emerald-500';
    let textColor = 'text-emerald-700 dark:text-emerald-400';
    let bgColor = 'bg-emerald-50 dark:bg-emerald-950/20';
    let borderColor = 'border-emerald-200 dark:border-emerald-800';

    if (riscoPercentual >= 40) {
      riskColor = 'bg-amber-500'; textColor = 'text-amber-700 dark:text-amber-400'; bgColor = 'bg-amber-50 dark:bg-amber-950/20'; borderColor = 'border-amber-200 dark:border-amber-800';
    }
    if (alertaAltoRisco) {
      riskColor = 'bg-red-500'; textColor = 'text-red-700 dark:text-red-400'; bgColor = 'bg-red-50 dark:bg-red-950/20'; borderColor = 'border-red-200 dark:border-red-800';
    }

    return (
      <div className={`mt-6 p-5 rounded-xl border ${bgColor} ${borderColor} ${textColor} animate-in slide-in-from-bottom-2`}>
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2 font-medium">
            {alertaAltoRisco ? <ShieldAlert className="w-5 h-5" /> : <CheckCircle2 className="w-5 h-5" />}
            Previsão de Comparecimento
          </div>
          {alertaAltoRisco && (
             <span className="bg-red-500 text-white text-xs px-2 py-1 rounded-md font-bold uppercase tracking-wider">
               Alto Risco
             </span>
          )}
        </div>
        
        <div className="mb-4">
          <div className="flex justify-between text-sm mb-1 font-semibold">
            <span>Chance de Presença ({presencaPercentual}%)</span>
            <span>Risco de Falta ({riscoPercentual}%)</span>
          </div>
          <div className="h-3 w-full bg-secondary rounded-full overflow-hidden flex">
            <div className="h-full bg-emerald-500 transition-all duration-1000" style={{ width: `${presencaPercentual}%` }} />
            <div className={`h-full ${riskColor} transition-all duration-1000`} style={{ width: `${riscoPercentual}%` }} />
          </div>
        </div>

        <p className="text-xs opacity-75 font-mono">Análise referencial para o ticket: {ingressoId}</p>
      </div>
    );
  };

  return (
    <div className="p-4 md:p-8 max-w-7xl mx-auto space-y-8 bg-background min-h-screen">
      {/* Header */}
      <div className="flex flex-col gap-2">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-primary/10 rounded-xl">
            <BrainCircuit className="w-8 h-8 text-primary" />
          </div>
          <div>
            <h1 className="text-3xl font-bold tracking-tight">Inteligência & Analytics</h1>
            <p className="text-muted-foreground text-sm md:text-base">
              Painel de predição alimentado por Machine Learning (Regressão Linear e Random Forest).
            </p>
          </div>
        </div>
      </div>

      {/* Navegação de Abas (Estilo Moderno) */}
      <div className="flex gap-1 bg-muted/50 p-1 rounded-lg w-fit border border-border/50">
        <button
          onClick={() => setActiveTab('simuladores')}
          className={`flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium transition-all ${
            activeTab === 'simuladores' ? 'bg-background shadow-sm text-primary border border-border/50' : 'text-muted-foreground hover:text-foreground'
          }`}
        >
          <TrendingUp className="w-4 h-4" /> Simuladores
        </button>
        <button
          onClick={() => setActiveTab('relatorios')}
          className={`flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium transition-all ${
            activeTab === 'relatorios' ? 'bg-background shadow-sm text-primary border border-border/50' : 'text-muted-foreground hover:text-foreground'
          }`}
        >
          <BarChart3 className="w-4 h-4" /> Relatórios Consolidados
        </button>
      </div>

      <div className="mt-4">
        {/* ABA: SIMULADORES */}
        {activeTab === 'simuladores' && (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 animate-in fade-in duration-500">

            {/* CARD: PREVISÃO DE RECEITA */}
            <Card className="border-border/50 shadow-md flex flex-col hover:border-primary/20 transition-colors">
              <CardHeader className="pb-4">
                <CardTitle className="flex items-center gap-2 text-xl">
                  <DollarSign className="w-5 h-5 text-emerald-500" />
                  Regressão de Receita
                </CardTitle>
                <CardDescription>
                  Simule o retorno financeiro com base no orçamento e patrocínios.
                </CardDescription>
              </CardHeader>
              <CardContent className="flex-1 flex flex-col">
                <form 
                  onSubmit={(e) => { e.preventDefault(); preverReceita.mutate(receitaForm); }} 
                  className="space-y-5 flex-1"
                >
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="marketing">Marketing (R$)</Label>
                      <Input
                        id="marketing"
                        type="number"
                        min="0"
                        step="0.01"
                        placeholder="0.00"
                        value={receitaForm.orcamentoMarketing || ''}
                        onChange={(e) => setReceitaForm({ ...receitaForm, orcamentoMarketing: Number(e.target.value) })}
                        required
                        className="bg-muted/50"
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="patrocinio">Patrocínio (R$)</Label>
                      <Input
                        id="patrocinio"
                        type="number"
                        min="0"
                        step="0.01"
                        placeholder="0.00"
                        value={receitaForm.patrocinio || ''}
                        onChange={(e) => setReceitaForm({ ...receitaForm, patrocinio: Number(e.target.value) })}
                        required
                        className="bg-muted/50"
                      />
                    </div>
                  </div>
                  
                  <Button 
                    type="submit" 
                    className="w-full mt-auto" 
                    disabled={preverReceita.isPending || (receitaForm.orcamentoMarketing === 0 && receitaForm.patrocinio === 0)}
                  >
                    {preverReceita.isPending ? <Loader2 className="w-5 h-5 mr-2 animate-spin" /> : 'Processar Simulação Financeira'}
                  </Button>
                </form>

                <ErrorDisplay error={preverReceita.error} />
                {renderReceitaResult()}
              </CardContent>
            </Card>

            {/* CARD: PREVISÃO DE NO-SHOW */}
            <Card className="border-border/50 shadow-md flex flex-col hover:border-primary/20 transition-colors">
              <CardHeader className="pb-4">
                <CardTitle className="flex items-center gap-2 text-xl">
                  <Users className="w-5 h-5 text-blue-500" />
                  Predição de No-Show
                </CardTitle>
                <CardDescription>
                  Avalie o risco de ausência de um ingresso usando dados climáticos e tempo de compra.
                </CardDescription>
              </CardHeader>
              <CardContent className="flex-1 flex flex-col">
                <form 
                  onSubmit={(e) => { e.preventDefault(); preverNoShow.mutate(noShowForm); }} 
                  className="space-y-5 flex-1"
                >
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="ingressoId">ID do Ingresso</Label>
                      <Input
                        id="ingressoId"
                        type="text"
                        placeholder="Ex: ING-1234"
                        value={noShowForm.ingressoId}
                        onChange={(e) => setNoShowForm({ ...noShowForm, ingressoId: e.target.value })}
                        required
                        className="bg-muted/50 uppercase"
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="dias">Comprado há (Dias)</Label>
                      <Input
                        id="dias"
                        type="number"
                        min="0"
                        value={noShowForm.antecedenciaCompraDias}
                        onChange={(e) => setNoShowForm({ ...noShowForm, antecedenciaCompraDias: Number(e.target.value) })}
                        required
                        className="bg-muted/50"
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label>Previsão do Clima no Evento</Label>
                    <div className="grid grid-cols-3 gap-3 mt-1">
                      {[
                        { id: 'ensolarado', icon: Sun, label: 'Sol' },
                        { id: 'nublado', icon: Cloud, label: 'Nublado' },
                        { id: 'chuvoso', icon: CloudRain, label: 'Chuva' }
                      ].map((clima) => (
                        <button
                          key={clima.id}
                          type="button"
                          onClick={() => setNoShowForm({ ...noShowForm, previsaoClima: clima.id })}
                          className={`flex flex-col items-center justify-center gap-2 p-3 rounded-lg border-2 transition-all ${
                            noShowForm.previsaoClima === clima.id 
                              ? 'border-primary bg-primary/10 text-primary' 
                              : 'border-border/50 bg-muted/30 text-muted-foreground hover:bg-muted/80 hover:border-border'
                          }`}
                        >
                          <clima.icon className="w-5 h-5" />
                          <span className="text-xs font-semibold">{clima.label}</span>
                        </button>
                      ))}
                    </div>
                  </div>

                  <Button 
                    type="submit" 
                    className="w-full mt-auto" 
                    variant="secondary"
                    disabled={preverNoShow.isPending || !noShowForm.ingressoId}
                  >
                    {preverNoShow.isPending ? <Loader2 className="w-5 h-5 mr-2 animate-spin" /> : 'Calcular Risco de Ausência'}
                  </Button>
                </form>

                <ErrorDisplay error={preverNoShow.error} />
                {renderNoShowResult()}
              </CardContent>
            </Card>
          </div>
        )}

        {/* ABA: RELATÓRIOS (Em breve) */}
        {activeTab === 'relatorios' && (
          <div className="animate-in fade-in slide-in-from-bottom-4 duration-500">
            <Card className="border-border/50 shadow-sm border-dashed bg-muted/20">
              <CardContent className="flex flex-col items-center justify-center p-12 text-center">
                 <div className="w-16 h-16 bg-primary/10 text-primary rounded-full flex items-center justify-center mb-4">
                    <BrainCircuit className="w-8 h-8 opacity-80" />
                 </div>
                 <h3 className="text-xl font-bold mb-2">Treinamento de Modelos em Andamento</h3>
                 <p className="text-muted-foreground max-w-md">
                   O módulo de relatórios analíticos globais está processando os dados históricos. Estará disponível após o deploy da nova versão do ONNX Runtime no backend.
                 </p>
              </CardContent>
            </Card>
          </div>
        )}
      </div>
    </div>
  );
}