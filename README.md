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

## Inteligência & Analytics (Sprint CC 6/8/9)

Dashboard `/gestor/inteligencia` com simuladores de IA (inferência ONNX em tempo real) e visualizações analíticas. O pipeline de treino vive em repositório separado (`ml-recife-cultural`) e produz os modelos `.onnx` e os JSONs consumidos aqui.

### Simuladores (CC 6) — inferência ONNX

Modelos treinados em scikit-learn, exportados via `skl2onnx`, carregados em `@PostConstruct` e servidos via Spring.

| Arquivo | Papel |
|---|---|
| `aplicacao/src/main/java/recifecultural/aplicacao/inteligencia/InteligenciaServicoAplicacao.java` | Carrega `receita_model.onnx` e `noshow_model.onnx` em memória (via `byte[]` p/ compatibilidade fat-jar) e executa inferência via `OrtSession.run()` |
| `aplicacao/src/main/resources/models/receita_model.onnx` | Regressão linear: `(orcamentoMarketing, patrocinio) → receitaEstimada` |
| `aplicacao/src/main/resources/models/noshow_model.onnx` | GradientBoostingClassifier: `(precoInteira, diaSemana, isFimDeSemana) → probabilidadeFalta` |
| `apresentacao-backend/src/main/java/recifecultural/apresentacao/bff/inteligencia/InteligenciaBffControlador.java` | BFF — `/api/bff/inteligencia/{prever-receita,prever-noshow,analisar-evento}` |
| `apresentacao-frontend/src/app/gestor/inteligencia/page.tsx` | Dashboard React (Recharts) com abas Simuladores, Análise de Evento e Visualizações |

### Visualizações Analíticas (CC 8/9)

Quatro seções Recharts alimentadas por endpoints BFF dedicados.

| Endpoint | Fonte | Conteúdo |
|---|---|---|
| `/api/bff/inteligencia/visitacao` | DB (real) com fallback em `visitacao.json` | Demanda Real — visitantes por teatro × mês |
| `/api/bff/inteligencia/noshow-por-grupo` | `noshow_grupos.json` | Perfil de no-show por tipo, faixa de preço e categoria |
| `/api/bff/inteligencia/metricas-classificador` | `metricas_classificador.json` | Acurácia 73,5% · Recall 59,0% · AUC 79,5% · matriz de confusão · ROC · PR · feature importance |
| `/api/bff/inteligencia/receita-scatter` | `receita_scatter.json` | Preço efetivo × receita real, segmentado por categoria (escala = capacidade) |

A consulta de visitação real (`infraestrutura/src/main/java/recifecultural/infraestrutura/persistencia/inteligencia/VisitacaoConsultaImpl.java`) agrega `ingresso JOIN evento JOIN espaco` por mês — soma todos os anos para capturar sazonalidade histórica. Quando o banco está vazio, o controlador serve o JSON estático com dados públicos de 2023.

### Pipeline de treino (CC 4/5 — `ml-recife-cultural`)

Scripts Python notebook-style (células `# %%`) que produzem os artefatos consumidos por este projeto.

| Script | Sprint | Saída |
|---|---|---|
| `regressao.py` | CC 4 | `receita_model.onnx` |
| `classificador.py` | CC 5 | `noshow_model.onnx` + métricas em `metricas_classificador.json` |
| `eda.py` | — | Análise exploratória dos datasets |
| `exportar_resultados.py` | — | 4 JSONs copiados para `apresentacao-backend/src/main/resources/inteligencia/` |

---

## Padrões de Projeto

O projeto distribui os 6 padrões GoF entre 7 pares de features (um padrão é reutilizado). Cada par é de responsabilidade de um integrante.

---

### Par 1 — F1.1 Compra/Reembolso · F1.2 Patrocínio
**Padrão:** Strategy
**Responsável:** Anderson Gabriel

| Arquivo | Papel |
|---|---|
| `dominio-ingressos/src/main/java/recifecultural/dominio/ingressos/EstrategiaProcessamentoReembolso.java` | Interface (Strategy) |
| `dominio-ingressos/src/main/java/recifecultural/dominio/ingressos/EstrategiaReembolsoImediato.java` | ConcreteStrategy — PIX: reembolso imediato |
| `dominio-ingressos/src/main/java/recifecultural/dominio/ingressos/EstrategiaReembolsoBancario.java` | ConcreteStrategy — Cartão: até 2 dias úteis |
| `dominio-ingressos/src/main/java/recifecultural/dominio/ingressos/SeletorEstrategiaReembolso.java` | Context — seleciona estratégia por `MetodoPagamento` |
| `dominio-patrocinio/src/main/java/recifecultural/dominio/patrocinio/EstrategiaCancelamentoPatrocinio.java` | Interface (Strategy) |
| `dominio-patrocinio/src/main/java/recifecultural/dominio/patrocinio/EstrategiaCancelamentoPorEvento.java` | ConcreteStrategy — >7d: 100% · 2-7d: 50% · <2d: 0% |
| `dominio-patrocinio/src/main/java/recifecultural/dominio/patrocinio/EstrategiaCancelamentoPorPatrocinador.java` | ConcreteStrategy — >15d: 100% · ≤15d: 80% + 20% multa |
| `dominio-patrocinio/src/main/java/recifecultural/dominio/patrocinio/Patrocinio.java` | Context — delega cálculo de cancelamento à estratégia |

---

### Par 2 — F2.1 Espaços · F2.2 Equipamento
**Padrão:** Proxy
**Responsável:** Debora Souza

| Arquivo | Papel |
|---|---|
| `dominio-espaco/src/main/java/recifecultural/dominio/espaco/espaco/IEspacoRepositorio.java` | Subject (interface) |
| `infraestrutura/src/main/java/recifecultural/infraestrutura/padroes/EspacoRepositorioProxyCache.java` | Proxy — cache em memória sobre `IEspacoRepositorio` |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/equipamento/IEquipamentoRepositorio.java` | Subject (interface) |
| `infraestrutura/src/main/java/recifecultural/infraestrutura/padroes/EquipamentoRepositorioProxyCache.java` | Proxy — cache em memória sobre `IEquipamentoRepositorio` |

---

### Par 3 — F3.1 Bloqueios · F3.2 Notificações
**Padrão:** Observer
**Responsável:** Filipe Macedo

| Arquivo | Papel |
|---|---|
| `dominio-compartilhado/src/main/java/recifecultural/dominio/compartilhado/evento/EventoBarramento.java` | Subject (interface do barramento) |
| `dominio-compartilhado/src/main/java/recifecultural/dominio/compartilhado/evento/EventoObservador.java` | Observer (interface) |
| `dominio-compartilhado/src/main/java/recifecultural/dominio/compartilhado/evento/EventoCanceladoPorBloqueioEvento.java` | Evento de domínio publicado pelo Bloqueio |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/bloqueioadministrativo/BloqueioAdministrativoServico.java` | Publicador — posta `EventoCanceladoPorBloqueioEvento` no barramento |
| `aplicacao/src/main/java/recifecultural/aplicacao/agenda/bloqueioadministrativo/BloqueioNotificacaoObservador.java` | ConcreteObserver — reage ao evento e dispara notificações |
| `infraestrutura/src/main/java/recifecultural/infraestrutura/evento/EventoBarramentoImpl.java` | ConcreteSubject — implementação com Spring `ApplicationEventPublisher` |

---

### Par 4 — F4.1 Aprovação · F4.2 Comentários
**Padrão:** Decorator
**Responsável:** Pedro Gusmao

| Arquivo | Papel |
|---|---|
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/evento/IEventoRepositorio.java` | Componente (interface decorada) |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/evento/EventoRepositorioComAuditoria.java` | Decorator — registra trilha de criação, transição de status e remoção |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/comentario/ComentarioRepositorio.java` | Componente (interface decorada) |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/comentario/ComentarioRepositorioComModeracao.java` | Decorator — substitui palavras vetadas antes de persistir |

---

### Par 5 — F5.1 Artistas/Produtores · F5.2 Setores/Suporte
**Padrão:** Iterator
**Responsável:** Rafael Peixoto

| Arquivo | Papel |
|---|---|
| `dominio-artista/src/main/java/recifecultural/dominio/artista/artista/Iterador.java` | Interface do Iterator (`temProximo()` + `proximo()`) |
| `dominio-artista/src/main/java/recifecultural/dominio/artista/artista/IArtistaRepositorio.java` | Aggregate — fábrica de iteradores (`iterarTodos()`) |
| `infraestrutura/src/main/java/recifecultural/infraestrutura/persistencia/artista/artista/IteradorPaginadoArtistas.java` | ConcreteIterator — percorre o repositório em páginas via JPA (`findAll(Pageable)`), sem materializar a tabela inteira em memória |

---

### Par 6 — F6.1 Sorteio · F6.2 Acessibilidade
**Padrão:** Template Method
**Responsável:** Yuri Cavalcanti

| Arquivo | Papel |
|---|---|
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/comum/OperacaoDominioTemplate.java` | AbstractClass genérica — esqueleto fixo: `buscar → aplicarRegra → persistir → notificar` |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/sorteio/OperacaoSorteioTemplate.java` | AbstractClass — especializa `buscar()` e `persistir()` para `Sorteio` |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/sorteio/ApurarOperacao.java` | ConcreteClass — `sorteio.apurar()` + notifica ganhadores |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/sorteio/InscreverOperacao.java` | ConcreteClass — `sorteio.inscrever(espectadorId)` |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/sorteio/DesistirOperacao.java` | ConcreteClass — `sorteio.desistir(espectadorId)` + notifica suplente promovido |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/sorteio/CancelarOperacao.java` | ConcreteClass — `sorteio.cancelar()` + broadcast de cancelamento |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/acessibilidade/OperacaoRecursoAcessibilidadeTemplate.java` | AbstractClass — especializa `buscar()` e `persistir()` para `RecursoAcessibilidade` |
| `dominio-agenda/src/main/java/recifecultural/dominio/agenda/acessibilidade/RemocaoRecursoAcessibilidadeOperacao.java` | ConcreteClass — `recurso.remover(justificativa)` + broadcast ACESSIBILIDADE_REMOVIDA |

---

### Par 7 — F7.1 Cupom · F7.2 Catraca
**Padrão:** Decorator *(reutilizado — um padrão repete entre 7 pares e 6 padrões)*
**Responsável:** Raphael Vilela

| Arquivo | Papel |
|---|---|
| `dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidadorCupom.java` | Componente (interface do pipeline) |
| `dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidadorCupomBase.java` | Componente concreto — base vazia da cadeia |
| `dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidadorCupomDecorator.java` | Decorator abstrato — encapsula `validadorInterno` e delega |
| `dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidarVigenciaDecorator.java` | Camada 1 — `dataInicio ≤ agora ≤ dataFim` |
| `dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidarMinimoDecorator.java` | Camada 2 — `valorPedido ≥ valorMinimoPedido` |
| `dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidarCategoriaDecorator.java` | Camada 3 — categoria permitida |
| `dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidarEscassezGlobalDecorator.java` | Camada 4 — limite global de usos |
| `dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidarLimiteCpfDecorator.java` | Camada 5 — limite por CPF |
| `dominio-ingressos/src/main/java/recifecultural/dominio/cupom/AplicarCupomServico.java` | Monta o pipeline no construtor e executa |
| `dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidadorAcesso.java` | Componente (interface do pipeline de acesso) |
| `dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidadorAcessoBase.java` | Componente concreto — base vazia da cadeia |
| `dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidadorAcessoDecorator.java` | Decorator abstrato de acesso |
| `dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidarEstornoDecorator.java` | Camada 1 — rejeita se reembolsado |
| `dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidarDuplaEntradaDecorator.java` | Camada 2 — rejeita se já utilizado |
| `dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidarPortaoDecorator.java` | Camada 3 — rejeita se portão errado |
| `dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidarToleranciaAtrasoDecorator.java` | Camada 4 — rejeita se fora da janela de horário |
| `dominio-ingressos/src/main/java/recifecultural/dominio/catraca/CatracaServico.java` | Monta o pipeline no construtor e executa `validarAcesso()` |
