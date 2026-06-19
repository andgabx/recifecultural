# Recife Cultural

Exemplo de DDD (Domain-Driven Design)

Na raiz do repositório:

```bash
docker compose up --build
```

Para rodar os testes:

```bash
./mvnw test
```

Requisito: Docker (inclui Docker Compose). API em http://localhost:8080

---

## 📄 Relatório Final — Análise e Visualização de Dados

[**📥 RelatorioFinalAVD-Projetos5-G2.pdf**](RelatorioFinalAVD-Projetos5-G2.pdf)

Relatório completo cobrindo EDA, modelo de regressão (R² = 0,761), classificador de no-show (AUC = 0,795) e dashboard interativo.

**Notebook Colab:** [Análise Completa — EDA + Regressão + Classificador](https://colab.research.google.com/drive/1Jrbrqh22-oAtNh0A3owDQqqaH_Jqh6W_?usp=sharing)

---

## Deploy

Aplicação publicada em: https://recifecultural-frontend.onrender.com/

> ⚠️ **Plano gratuito do Render:** o backend pode demorar até 2 minutos para responder na primeira requisição após um período de inatividade (*cold start*). Aguarde o carregamento completo antes de navegar.

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
|---|---|
| `regressao.py` | `receita_model.onnx` |
| `classificador.py` | `noshow_model.onnx` + métricas em `metricas_classificador.json` |
| `eda.py` | Análise exploratória dos datasets |
| `exportar_resultados.py` | 4 JSONs copiados para `apresentacao-backend/src/main/resources/inteligencia/` |

---

## Padrões de Projeto

O projeto distribui os 6 padrões GoF entre 7 pares de features (um padrão é reutilizado). Cada par é de responsabilidade de um integrante.

---

### Par 1 — Compra/Reembolso · Patrocínio
**Padrão:** Strategy · **Responsável:** Anderson Gabriel

| Arquivo | Papel |
|---|---|
| [`EstrategiaProcessamentoReembolso.java`](dominio-ingressos/src/main/java/recifecultural/dominio/ingressos/EstrategiaProcessamentoReembolso.java) | Interface (Strategy) |
| [`EstrategiaReembolsoImediato.java`](dominio-ingressos/src/main/java/recifecultural/dominio/ingressos/EstrategiaReembolsoImediato.java) | ConcreteStrategy — PIX: reembolso imediato |
| [`EstrategiaReembolsoBancario.java`](dominio-ingressos/src/main/java/recifecultural/dominio/ingressos/EstrategiaReembolsoBancario.java) | ConcreteStrategy — Cartão: até 2 dias úteis |
| [`SeletorEstrategiaReembolso.java`](dominio-ingressos/src/main/java/recifecultural/dominio/ingressos/SeletorEstrategiaReembolso.java) | Context — seleciona estratégia por `MetodoPagamento` |
| [`EstrategiaCancelamentoPatrocinio.java`](dominio-patrocinio/src/main/java/recifecultural/dominio/patrocinio/EstrategiaCancelamentoPatrocinio.java) | Interface (Strategy) |
| [`EstrategiaCancelamentoPorEvento.java`](dominio-patrocinio/src/main/java/recifecultural/dominio/patrocinio/EstrategiaCancelamentoPorEvento.java) | ConcreteStrategy — >7d: 100% · 2-7d: 50% · <2d: 0% |
| [`EstrategiaCancelamentoPorPatrocinador.java`](dominio-patrocinio/src/main/java/recifecultural/dominio/patrocinio/EstrategiaCancelamentoPorPatrocinador.java) | ConcreteStrategy — >15d: 100% · ≤15d: 80% + 20% multa |
| [`Patrocinio.java`](dominio-patrocinio/src/main/java/recifecultural/dominio/patrocinio/Patrocinio.java) | Context — delega cálculo de cancelamento à estratégia |

**Testes:** [`reembolsar_ingresso.feature`](dominio-ingressos/src/test/resources/features/reembolsar_ingresso.feature) · [`cancelar_patrocinio.feature`](dominio-patrocinio/src/test/resources/features/cancelar_patrocinio.feature) · [`PatrocinioTest.java`](dominio-patrocinio/src/test/java/recifecultural/dominio/patrocinio/PatrocinioTest.java) · [`AtivarPatrocinioFuncionalidade.java`](dominio-patrocinio/src/test/java/recifecultural/dominio/patrocinio/AtivarPatrocinioFuncionalidade.java) · [`EstrategiaReembolsoTest.java`](dominio-ingressos/src/test/java/recifecultural/dominio/ingressos/EstrategiaReembolsoTest.java)

---

### Par 2 — Espaços · Equipamento
**Padrão:** Proxy · **Responsável:** Debora Souza

| Arquivo | Papel |
|---|---|
| [`IEspacoRepositorio.java`](dominio-espaco/src/main/java/recifecultural/dominio/espaco/espaco/IEspacoRepositorio.java) | Subject (interface) |
| [`EspacoRepositorioProxyCache.java`](infraestrutura/src/main/java/recifecultural/infraestrutura/padroes/EspacoRepositorioProxyCache.java) | Proxy — cache em memória sobre `IEspacoRepositorio` |
| [`IEquipamentoRepositorio.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/equipamento/IEquipamentoRepositorio.java) | Subject (interface) |
| [`EquipamentoRepositorioProxyCache.java`](infraestrutura/src/main/java/recifecultural/infraestrutura/padroes/EquipamentoRepositorioProxyCache.java) | Proxy — cache em memória sobre `IEquipamentoRepositorio` |

**Testes:** [`EspacoRepositorioProxyCacheTest.java`](infraestrutura/src/test/java/recifecultural/infraestrutura/padroes/EspacoRepositorioProxyCacheTest.java) · [`EquipamentoRepositorioProxyCacheTest.java`](infraestrutura/src/test/java/recifecultural/infraestrutura/padroes/EquipamentoRepositorioProxyCacheTest.java)

---

### Par 3 — Bloqueios · Notificações
**Padrão:** Observer · **Responsável:** Filipe Macedo

| Arquivo | Papel |
|---|---|
| [`EventoBarramento.java`](dominio-compartilhado/src/main/java/recifecultural/dominio/compartilhado/evento/EventoBarramento.java) | Subject (interface do barramento) |
| [`EventoBarramentoImpl.java`](infraestrutura/src/main/java/recifecultural/infraestrutura/evento/EventoBarramentoImpl.java) | ConcreteSubject — Spring `ApplicationEventPublisher` |
| [`BloqueioAdministrativoServico.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/bloqueioadministrativo/BloqueioAdministrativoServico.java) | Publicador de eventos de domínio |
| [`BloqueioNotificacaoObservador.java`](aplicacao/src/main/java/recifecultural/aplicacao/agenda/bloqueioadministrativo/BloqueioNotificacaoObservador.java) | ConcreteObserver — dispara notificações ao produtor |
| [`IngressoNotificacaoObservador.java`](aplicacao/src/main/java/recifecultural/aplicacao/agenda/bloqueioadministrativo/IngressoNotificacaoObservador.java) | ConcreteObserver — invalida ingressos ao reembolsar |

**Testes:** [`BloqueioAdministrativo.feature`](dominio-agenda/src/test/resources/features/BloqueioAdministrativo.feature) · [`ObserverBloqueioTest.java`](aplicacao/src/test/java/recifecultural/aplicacao/agenda/bloqueioadministrativo/ObserverBloqueioTest.java)

---

### Par 4 — Aprovação · Comentários
**Padrão:** Decorator · **Responsável:** Pedro Gusmao

| Arquivo | Papel |
|---|---|
| [`IEventoRepositorio.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/evento/IEventoRepositorio.java) | Componente (interface decorada) |
| [`EventoRepositorioComAuditoria.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/evento/EventoRepositorioComAuditoria.java) | Decorator — registra trilha de aprovação/reprovação |
| [`ComentarioRepositorio.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/comentario/ComentarioRepositorio.java) | Componente (interface decorada) |
| [`ComentarioRepositorioComModeracao.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/comentario/ComentarioRepositorioComModeracao.java) | Decorator — filtra palavras vetadas antes de persistir |

**Testes:** [`aprovar_reprovar_evento.feature`](dominio-agenda/src/test/resources/features/aprovar_reprovar_evento.feature) · [`discutir_eventos.feature`](dominio-agenda/src/test/resources/features/discutir_eventos.feature) · [`EventoRepositorioComAuditoriaTest.java`](dominio-agenda/src/test/java/recifecultural/dominio/agenda/evento/EventoRepositorioComAuditoriaTest.java) · [`ComentarioRepositorioComModeracaoTest.java`](dominio-agenda/src/test/java/recifecultural/dominio/agenda/comentario/ComentarioRepositorioComModeracaoTest.java)

---

### Par 5 — Artistas
**Padrão:** Iterator · **Responsável:** Rafael Peixoto

| Arquivo | Papel |
|---|---|
| [`Iterador.java`](dominio-artista/src/main/java/recifecultural/dominio/artista/artista/Iterador.java) | Interface do Iterator (`temProximo()` + `proximo()`) |
| [`IArtistaRepositorio.java`](dominio-artista/src/main/java/recifecultural/dominio/artista/artista/IArtistaRepositorio.java) | Aggregate — expõe `iterarTodos()` como fábrica de iteradores |
| [`IteradorPaginadoArtistas.java`](infraestrutura/src/main/java/recifecultural/infraestrutura/persistencia/artista/artista/IteradorPaginadoArtistas.java) | ConcreteIterator — percorre artistas em páginas via JPA sem materializar a tabela inteira |

**Testes:** [`IteradorPaginadoArtistasTest.java`](infraestrutura/src/test/java/recifecultural/infraestrutura/persistencia/artista/artista/IteradorPaginadoArtistasTest.java) · [`cadastro_artista.feature`](dominio-artista/src/test/resources/features/cadastro_artista.feature)

---

### Par 6 — Sorteio · Acessibilidade
**Padrão:** Template Method · **Responsável:** Yuri Cavalcanti

| Arquivo | Papel |
|---|---|
| [`OperacaoDominioTemplate.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/comum/OperacaoDominioTemplate.java) | AbstractClass genérica — esqueleto: `buscar → aplicarRegra → persistir → notificar` |
| [`OperacaoSorteioTemplate.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/sorteio/OperacaoSorteioTemplate.java) | AbstractClass — especializa buscar/persistir para Sorteio |
| [`ApurarOperacao.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/sorteio/ApurarOperacao.java) | ConcreteClass — apura ganhadores e suplentes |
| [`InscreverOperacao.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/sorteio/InscreverOperacao.java) | ConcreteClass — inscreve espectador |
| [`DesistirOperacao.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/sorteio/DesistirOperacao.java) | ConcreteClass — desistência + promoção de suplente |
| [`CancelarOperacao.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/sorteio/CancelarOperacao.java) | ConcreteClass — cancelamento + broadcast |
| [`OperacaoRecursoAcessibilidadeTemplate.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/acessibilidade/OperacaoRecursoAcessibilidadeTemplate.java) | AbstractClass — especializa para RecursoAcessibilidade |
| [`RemocaoRecursoAcessibilidadeOperacao.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/acessibilidade/RemocaoRecursoAcessibilidadeOperacao.java) | ConcreteClass — remove recurso + broadcast |

**Testes:** [`OperacaoSorteioTemplateTest.java`](dominio-agenda/src/test/java/recifecultural/dominio/agenda/sorteio/OperacaoSorteioTemplateTest.java) · [`sorteio.feature`](dominio-agenda/src/test/resources/features/sorteio.feature) · [`acessibilidade.feature`](dominio-agenda/src/test/resources/features/acessibilidade.feature)

---

### Par 7 — Cupom · Catraca
**Padrão:** Decorator *(reutilizado)* · **Responsável:** Raphael Vilela

| Arquivo | Papel |
|---|---|
| [`ValidadorCupom.java`](dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidadorCupom.java) | Componente (interface do pipeline) |
| [`ValidadorCupomBase.java`](dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidadorCupomBase.java) | Componente concreto — base vazia |
| [`ValidadorCupomDecorator.java`](dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidadorCupomDecorator.java) | Decorator abstrato |
| [`ValidarVigenciaDecorator.java`](dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidarVigenciaDecorator.java) | Camada 1 — validade do cupom |
| [`ValidarMinimoDecorator.java`](dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidarMinimoDecorator.java) | Camada 2 — valor mínimo do pedido |
| [`ValidarCategoriaDecorator.java`](dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidarCategoriaDecorator.java) | Camada 3 — categoria do evento |
| [`ValidarEscassezGlobalDecorator.java`](dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidarEscassezGlobalDecorator.java) | Camada 4 — limite global de usos |
| [`ValidarLimiteCpfDecorator.java`](dominio-ingressos/src/main/java/recifecultural/dominio/cupom/validacoes/ValidarLimiteCpfDecorator.java) | Camada 5 — limite por CPF |
| [`AplicarCupomServico.java`](dominio-ingressos/src/main/java/recifecultural/dominio/cupom/AplicarCupomServico.java) | Monta o pipeline e executa |
| [`ValidadorAcesso.java`](dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidadorAcesso.java) | Componente (interface do pipeline de acesso) |
| [`ValidarEstornoDecorator.java`](dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidarEstornoDecorator.java) | Camada 1 — rejeita se reembolsado |
| [`ValidarDuplaEntradaDecorator.java`](dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidarDuplaEntradaDecorator.java) | Camada 2 — rejeita se já utilizado |
| [`ValidarPortaoDecorator.java`](dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidarPortaoDecorator.java) | Camada 3 — rejeita portão incorreto |
| [`ValidarToleranciaAtrasoDecorator.java`](dominio-ingressos/src/main/java/recifecultural/dominio/catraca/validacoes/ValidarToleranciaAtrasoDecorator.java) | Camada 4 — janela de horário |
| [`CatracaServico.java`](dominio-ingressos/src/main/java/recifecultural/dominio/catraca/CatracaServico.java) | Monta e executa o pipeline de acesso |

**Testes:** [`CatracaDecoratorTest.java`](dominio-ingressos/src/test/java/recifecultural/dominio/catraca/CatracaDecoratorTest.java) · [`validacao_catraca.feature`](dominio-ingressos/src/test/resources/features/validacao_catraca.feature) · [`aplicar_cupom.feature`](dominio-ingressos/src/test/resources/features/aplicar_cupom.feature)

---

### Pré-Reserva — Mapeamento de Assentos
**Padrão:** Template Method *(aplicação interna — viabiliza isolamento do mapa de assentos por evento)*

| Arquivo | Papel |
|---|---|
| [`OperacaoPreReservaTemplate.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/prereserva/OperacaoPreReservaTemplate.java) | AbstractClass — esqueleto: `buscar → aplicarTransicao → persistir → notificar` |
| [`ConfirmarPreReservaOperacao.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/prereserva/ConfirmarPreReservaOperacao.java) | ConcreteClass — `preReserva.confirmar()` |
| [`CancelarPreReservaOperacao.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/prereserva/CancelarPreReservaOperacao.java) | ConcreteClass — `preReserva.cancelar()` |
| [`ExpirarPreReservaOperacao.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/prereserva/ExpirarPreReservaOperacao.java) | ConcreteClass — `preReserva.expirar(agora)` |
| [`PreReservaServico.java`](dominio-agenda/src/main/java/recifecultural/dominio/agenda/prereserva/PreReservaServico.java) | Orquestra as operações |

**Testes:** [`OperacaoPreReservaTemplateTest.java`](dominio-agenda/src/test/java/recifecultural/dominio/agenda/prereserva/OperacaoPreReservaTemplateTest.java) · [`mapeamento_assentos.feature`](dominio-agenda/src/test/resources/features/mapeamento_assentos.feature)
