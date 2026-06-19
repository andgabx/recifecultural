# Recife Cultural

Exemplo de DDD (Domain-Driven Design)

Na raiz do repositório:

```bash
docker compose up --build
```

Requisito: Docker (inclui Docker Compose). API em http://localhost:8080

---

## Deploy

Aplicação publicada em: https://recifecultural-frontend.onrender.com/

Configuração declarativa em `render.yaml`: PostgreSQL gerenciado + backend Spring Boot (Docker) + frontend Next.js. Healthcheck via `/actuator/health`.

---

## Inteligência & Analytics

Dashboard `/gestor/inteligencia` com simuladores de IA (inferência ONNX em tempo real) e visualizações analíticas. O pipeline de treino vive em repositório separado (`ml-recife-cultural`) e produz os modelos `.onnx` e os JSONs consumidos aqui.

### Simuladores — inferência ONNX

Modelos treinados em scikit-learn, exportados via `skl2onnx`, carregados em `@PostConstruct` e servidos via Spring.

| Arquivo | Papel |
|---|---|
| `InteligenciaServicoAplicacao.java` | Carrega `receita_model.onnx` e `noshow_model.onnx` em memória (via `byte[]` p/ compatibilidade fat-jar) e executa inferência via `OrtSession.run()` |
| `receita_model.onnx` | Regressão linear: `(orcamentoMarketing, patrocinio) → receitaEstimada` |
| `noshow_model.onnx` | GradientBoostingClassifier: `(precoInteira, diaSemana, isFimDeSemana) → probabilidadeFalta` |
| `InteligenciaBffControlador.java` | BFF — `/api/bff/inteligencia/{prever-receita,prever-noshow,analisar-evento}` |
| `page.tsx` | Dashboard React (Recharts) com abas Simuladores, Análise de Evento e Visualizações |

### Visualizações Analíticas

Quatro seções Recharts alimentadas por endpoints BFF dedicados.

| Endpoint | Fonte | Conteúdo |
|---|---|---|
| `/api/bff/inteligencia/visitacao` | DB (real) com fallback em `visitacao.json` | Demanda Real — visitantes por teatro × mês |
| `/api/bff/inteligencia/noshow-por-grupo` | `noshow_grupos.json` | Perfil de no-show por tipo, faixa de preço e categoria |
| `/api/bff/inteligencia/metricas-classificador` | `metricas_classificador.json` | Acurácia 73,5% · Recall 59,0% · AUC 79,5% · matriz de confusão · ROC · PR · feature importance |
| `/api/bff/inteligencia/receita-scatter` | `receita_scatter.json` | Preço efetivo × receita real, segmentado por categoria (escala = capacidade) |

A consulta de visitação real (`VisitacaoConsultaImpl.java`) agrega `ingresso JOIN evento JOIN espaco` por mês — soma todos os anos para capturar sazonalidade histórica. Quando o banco está vazio, o controlador serve o JSON estático com dados públicos de 2023.

### Pipeline de treino (`ml-recife-cultural`)

Scripts Python notebook-style (células `# %%`) que produzem os artefatos consumidos por este projeto.

| Script | Saída |
|---|---|---|
| `regressao.py` | `receita_model.onnx` |
| `classificador.py` | `noshow_model.onnx` + métricas em `metricas_classificador.json` |
| `eda.py` | Análise exploratória dos datasets |
| `exportar_resultados.py` | 4 JSONs copiados para `apresentacao-backend/src/main/resources/inteligencia/` |

---

## Padrões de Projeto

O projeto distribui os 6 padrões GoF entre 7 pares de features (um padrão é reutilizado). Cada par é de responsabilidade de um integrante.

---

### Par 1 — Compra/Reembolso · Patrocínio
**Padrão:** Strategy
**Responsável:** Anderson Gabriel

| Arquivo | Papel |
|---|---|
| `EstrategiaProcessamentoReembolso.java` | Interface (Strategy) |
| `EstrategiaReembolsoImediato.java` | ConcreteStrategy — PIX: reembolso imediato |
| `EstrategiaReembolsoBancario.java` | ConcreteStrategy — Cartão: até 2 dias úteis |
| `SeletorEstrategiaReembolso.java` | Context — seleciona estratégia por `MetodoPagamento` |
| `EstrategiaCancelamentoPatrocinio.java` | Interface (Strategy) |
| `EstrategiaCancelamentoPorEvento.java` | ConcreteStrategy — >7d: 100% · 2-7d: 50% · <2d: 0% |
| `EstrategiaCancelamentoPorPatrocinador.java` | ConcreteStrategy — >15d: 100% · ≤15d: 80% + 20% multa |
| `Patrocinio.java` | Context — delega cálculo de cancelamento à estratégia |

---

### Par 2 — Espaços · Equipamento
**Padrão:** Proxy
**Responsável:** Debora Souza

| Arquivo | Papel |
|---|---|
| `IEspacoRepositorio.java` | Subject (interface) |
| `EspacoRepositorioProxyCache.java` | Proxy — cache em memória sobre `IEspacoRepositorio` |
| `IEquipamentoRepositorio.java` | Subject (interface) |
| `EquipamentoRepositorioProxyCache.java` | Proxy — cache em memória sobre `IEquipamentoRepositorio` |

---

### Par 3 — Bloqueios · Notificações
**Padrão:** Observer
**Responsável:** Filipe Macedo

| Arquivo | Papel |
|---|---|
| `EventoBarramento.java` | Subject (interface do barramento) |
| `EventoObservador.java` | Observer (interface) |
| `EventoCanceladoPorBloqueioEvento.java` | Evento de domínio publicado pelo Bloqueio |
| `BloqueioAdministrativoServico.java` | Publicador — posta `EventoCanceladoPorBloqueioEvento` no barramento |
| `BloqueioNotificacaoObservador.java` | ConcreteObserver — reage ao evento e dispara notificações |
| `EventoBarramentoImpl.java` | ConcreteSubject — implementação com Spring `ApplicationEventPublisher` |

---

### Par 4 — Aprovação · Comentários
**Padrão:** Decorator
**Responsável:** Pedro Gusmao

| Arquivo | Papel |
|---|---|
| `IEventoRepositorio.java` | Componente (interface decorada) |
| `EventoRepositorioComAuditoria.java` | Decorator — registra trilha de criação, transição de status e remoção |
| `ComentarioRepositorio.java` | Componente (interface decorada) |
| `ComentarioRepositorioComModeracao.java` | Decorator — substitui palavras vetadas antes de persistir |

---

### Par 5 — Artistas/Produtores · Setores/Suporte
**Padrão:** Iterator
**Responsável:** Rafael Peixoto

| Arquivo | Papel |
|---|---|
| `Iterador.java` | Interface do Iterator (`temProximo()` + `proximo()`) |
| `IArtistaRepositorio.java` | Aggregate — fábrica de iteradores (`iterarTodos()`) |
| `IteradorPaginadoArtistas.java` | ConcreteIterator — percorre o repositório em páginas via JPA (`findAll(Pageable)`), sem materializar a tabela inteira em memória |

---

### Par 6 — Sorteio · Acessibilidade
**Padrão:** Template Method
**Responsável:** Yuri Cavalcanti

| Arquivo | Papel |
|---|---|
| `OperacaoDominioTemplate.java` | AbstractClass genérica — esqueleto fixo: `buscar → aplicarRegra → persistir → notificar` |
| `OperacaoSorteioTemplate.java` | AbstractClass — especializa `buscar()` e `persistir()` para `Sorteio` |
| `ApurarOperacao.java` | ConcreteClass — `sorteio.apurar()` + notifica ganhadores |
| `InscreverOperacao.java` | ConcreteClass — `sorteio.inscrever(espectadorId)` |
| `DesistirOperacao.java` | ConcreteClass — `sorteio.desistir(espectadorId)` + notifica suplente promovido |
| `CancelarOperacao.java` | ConcreteClass — `sorteio.cancelar()` + broadcast de cancelamento |
| `OperacaoRecursoAcessibilidadeTemplate.java` | AbstractClass — especializa `buscar()` e `persistir()` para `RecursoAcessibilidade` |
| `RemocaoRecursoAcessibilidadeOperacao.java` | ConcreteClass — `recurso.remover(justificativa)` + broadcast ACESSIBILIDADE_REMOVIDA |

---

### Par 7 — Cupom · Catraca
**Padrão:** Decorator *(reutilizado — um padrão repete entre 7 pares e 6 padrões)*
**Responsável:** Raphael Vilela

| Arquivo | Papel |
|---|---|
| `ValidadorCupom.java` | Componente (interface do pipeline) |
| `ValidadorCupomBase.java` | Componente concreto — base vazia da cadeia |
| `ValidadorCupomDecorator.java` | Decorator abstrato — encapsula `validadorInterno` e delega |
| `ValidarVigenciaDecorator.java` | Camada 1 — `dataInicio ≤ agora ≤ dataFim` |
| `ValidarMinimoDecorator.java` | Camada 2 — `valorPedido ≥ valorMinimoPedido` |
| `ValidarCategoriaDecorator.java` | Camada 3 — categoria permitida |
| `ValidarEscassezGlobalDecorator.java` | Camada 4 — limite global de usos |
| `ValidarLimiteCpfDecorator.java` | Camada 5 — limite por CPF |
| `AplicarCupomServico.java` | Monta o pipeline no construtor e executa |
| `ValidadorAcesso.java` | Componente (interface do pipeline de acesso) |
| `ValidadorAcessoBase.java` | Componente concreto — base vazia da cadeia |
| `ValidadorAcessoDecorator.java` | Decorator abstrato de acesso |
| `ValidarEstornoDecorator.java` | Camada 1 — rejeita se reembolsado |
| `ValidarDuplaEntradaDecorator.java` | Camada 2 — rejeita se já utilizado |
| `ValidarPortaoDecorator.java` | Camada 3 — rejeita se portão errado |
| `ValidarToleranciaAtrasoDecorator.java` | Camada 4 — rejeita se fora da janela de horário |
| `CatracaServico.java` | Monta o pipeline no construtor e executa `validarAcesso()` |
