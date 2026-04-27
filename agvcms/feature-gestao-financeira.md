# Feature 2 — Gestão Financeira e Desempenho do Teatro

## Contexto

Feature focada no gestor de teatro: define orçamento por período, registra despesas operacionais e acompanha indicadores de desempenho cruzando receita de bilheteria com custos. Permite comparar períodos, identificar espetáculos com baixa demanda e medir crescimento de público e receita.

---

## Persona

**Gestor do Teatro** — responsável pela saúde financeira do teatro, precisa controlar despesas, monitorar receita e comparar o desempenho entre temporadas.

---

## Módulo Maven

`dominio-financeiro` — package `recifecultural.dominio.financeiro`

Depende de `dominio-ingressos` para calcular receita de bilheteria.

---

## Aggregates

### `OrcamentoPeriodo`

| Campo | Tipo | Obrigatório |
|---|---|---|
| `id` | `OrcamentoId` (VO) | sim — gerado |
| `periodo` | `Periodo` (VO) | sim |
| `valorTotal` | `BigDecimal` | sim — > 0 |
| `status` | `StatusOrcamento` (enum) | sim — `ABERTO` ao criar |

**Máquina de estados:**
```
ABERTO → ENCERRADO
```

### `Despesa`

| Campo | Tipo | Obrigatório |
|---|---|---|
| `id` | `DespesaId` (VO) | sim — gerado |
| `orcamentoId` | `OrcamentoId` | sim |
| `descricao` | `String` | sim |
| `valor` | `BigDecimal` | sim — > 0 |
| `categoria` | `CategoriaDespesa` (enum) | sim |
| `dataRegistro` | `LocalDateTime` | sim — gerado |

---

## Enums

| Enum | Valores |
|---|---|
| `StatusOrcamento` | `ABERTO`, `ENCERRADO` |
| `CategoriaDespesa` | `MANUTENCAO`, `PESSOAL`, `MARKETING`, `EQUIPAMENTO`, `OUTROS` |

---

## Regras de Negócio

### Registrar Despesa
- Orçamento deve estar com status `ABERTO`
- Data do registro deve estar dentro do período orçamentário
- Alerta disparado quando total de despesas atinge 80% do orçamento

### Reduzir Orçamento
- Novo valor não pode ser menor que o total de despesas já registradas

### Indicadores de Desempenho
| Indicador | Cálculo |
|---|---|
| Taxa de ocupação | ingressos vendidos ÷ capacidade total |
| Receita bruta | soma dos valores pagos pelos ingressos |
| Receita líquida | receita bruta − despesas − reembolsos |
| Crescimento de público | comparado com período anterior |

### Comparar Períodos
- Períodos sobrepostos são rejeitados

---

## Cenários BDD

### `registrar_despesa.feature`

| Cenário | Resultado esperado |
|---|---|
| Despesa dentro do período com orçamento disponível | despesa registrada |
| Despesa fora do período orçamentário | rejeitada |
| Despesa que ultrapassa 80% do orçamento | registrada com alerta |
| Despesa com orçamento encerrado | rejeitada |
| Reduzir orçamento abaixo das despesas registradas | rejeitado |

### `desempenho_teatro.feature`

| Cenário | Resultado esperado |
|---|---|
| Calcular indicadores com vendas e despesas registradas | retorna taxa de ocupação, receita líquida |
| Comparar dois períodos distintos | retorna comparativo com variações |
| Comparar períodos sobrepostos | rejeitado |

---

## Arquivos principais

```
dominio-financeiro/src/main/java/recifecultural/dominio/financeiro/
├── OrcamentoPeriodo.java           # aggregate
├── OrcamentoId.java                # value object
├── Despesa.java                    # aggregate
├── DespesaId.java                  # value object
├── Periodo.java                    # value object
├── DesempenhoTeatroServico.java    # domain service
├── IOrcamentoRepositorio.java
├── IDespesaRepositorio.java
├── IndicadoresDesempenho.java
├── ComparativoPeriodos.java
├── ResultadoRegistroDespesa.java
├── CategoriaDespesa.java
└── StatusOrcamento.java

dominio-financeiro/src/test/
├── resources/features/
│   ├── registrar_despesa.feature
│   └── desempenho_teatro.feature
└── java/recifecultural/dominio/financeiro/
    ├── FinanceiroContexto.java         # estado compartilhado (PicoContainer)
    ├── OrcamentoRepositorioEmMemoria.java
    ├── DespesaRepositorioEmMemoria.java
    ├── RegistrarDespesaFuncionalidade.java
    ├── DesempenhoTeatroFuncionalidade.java
    └── RunCucumberFinanceiroTest.java
```

---

## Como executar os testes

```bash
./mvnw test -pl dominio-ingressos,dominio-financeiro
```
