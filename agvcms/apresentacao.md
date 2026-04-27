# Roteiro de Apresentação — Recife Cultural

---

## 1. Domínio

O **Recife Cultural** é uma plataforma de gestão de eventos culturais da cidade do Recife.
Ela conecta espectadores a espetáculos teatrais e permite que gestores de teatro administrem sua operação — da bilheteria ao orçamento.

---

## 2. Jornada 1 — Persona: Espectador

**Objetivo:** *"Quero assistir a um espetáculo e, se precisar, cancelar meu ingresso sem perder tudo."*

### Decomposição em subproblemas

| # | Subproblema | Funcionalidade |
|---|---|---|
| 2.1 | Escolher e pagar o ingresso | **Comprar Ingresso** (PIX ou cartão, com QR code gerado) |
| 2.2 | Precisar cancelar com antecedência | **Solicitar Reembolso Escalonado** |

### Protótipos de baixa fidelidade

**Tela: Comprar Ingresso**
```
┌─────────────────────────────────┐
│  🎭 Espetáculo: Dom Casmurro    │
│  Data: 10/05 — 20h              │
│  Local: Teatro do Parque        │
├─────────────────────────────────┤
│  Tipo de ingresso:              │
│  ○ Inteira    R$ 80,00          │
│  ○ Meia       R$ 40,00          │
│  ○ Social     R$ 20,00          │
├─────────────────────────────────┤
│  Pagamento:                     │
│  ○ PIX   ○ Cartão de crédito    │
├─────────────────────────────────┤
│       [ Comprar ]               │
└─────────────────────────────────┘
```

**Tela: Ingresso Confirmado (QR Code)**
```
┌─────────────────────────────────┐
│  ✅ Compra confirmada!          │
│                                 │
│       ▄▄▄▄▄▄▄▄▄▄▄              │
│       █ QR CODE █               │
│       ▀▀▀▀▀▀▀▀▀▀▀              │
│                                 │
│  Código: a1b2-c3d4-...          │
│  Transação: PAY-9x83k           │
├─────────────────────────────────┤
│  [ Solicitar Reembolso ]        │
└─────────────────────────────────┘
```

**Tela: Solicitar Reembolso**
```
┌─────────────────────────────────┐
│  Reembolso — Dom Casmurro       │
│  Apresentação: 10/05 — 20h      │
├─────────────────────────────────┤
│  Antecedência: 12 dias          │
│  Valor pago:   R$ 80,00         │
│  Reembolso:    R$ 80,00 (100%)  │
│  Prazo:        imediato (PIX)   │
├─────────────────────────────────┤
│  Política de reembolso:         │
│  > 7 dias  → 100%               │
│  2 a 7 dias → 50%               │
│  < 2 dias  → sem reembolso      │
├─────────────────────────────────┤
│  [ Confirmar Reembolso ]        │
└─────────────────────────────────┘
```

---

## 3. Jornada 2 — Persona: Gestor do Teatro

**Objetivo:** *"Quero controlar o orçamento da temporada e entender se os espetáculos estão sendo rentáveis."*

### Decomposição em subproblemas

| # | Subproblema | Funcionalidade |
|---|---|---|
| 3.1 | Registrar e controlar despesas do período | **Registrar Despesa + Alerta de Orçamento** |
| 3.2 | Saber se o teatro está lucrando | **Calcular Indicadores de Desempenho** |
| 3.3 | Comparar com períodos anteriores | **Comparar Períodos** |

### Protótipos de baixa fidelidade

**Tela: Registrar Despesa**
```
┌─────────────────────────────────┐
│  Orçamento: Mai/2025            │
│  Valor total: R$ 20.000,00      │
│  Utilizado:   R$ 14.500,00 (72%)│
│  ██████████████░░░░  72%        │
├─────────────────────────────────┤
│  Nova despesa:                  │
│  Descrição: [____________]      │
│  Valor:     [____________]      │
│  Categoria: [ Pessoal      ▼ ]  │
├─────────────────────────────────┤
│       [ Registrar ]             │
└─────────────────────────────────┘
```

**Tela: Alerta de Orçamento (>= 80%)**
```
┌─────────────────────────────────┐
│  ALERTA DE ORCAMENTO            │
│                                 │
│  Você atingiu 83% do orçamento  │
│  do período Mai/2025.           │
│                                 │
│  Utilizado: R$ 16.600,00        │
│  Limite:    R$ 20.000,00        │
│                                 │
│       [ Ok, entendido ]         │
└─────────────────────────────────┘
```

**Tela: Indicadores de Desempenho**
```
┌─────────────────────────────────┐
│  Desempenho — Mai/2025          │
├──────────────┬──────────────────┤
│ Taxa ocupação│ 78%              │
│ Receita bruta│ R$ 24.800,00     │
│ Despesas     │ R$ 14.500,00     │
│ Reembolsos   │ R$  1.200,00     │
│ Receita líq. │ R$  9.100,00     │
│ Cresc. público│ +12% vs abr/25 │
└──────────────┴──────────────────┘
│  [ Comparar com período anterior]
└─────────────────────────────────┘
```

---

## 4. Especificações de Teste e Automação

Funcionalidade escolhida: **Reembolso Escalonado** — regra de negócio com três faixas distintas.

### Cenários BDD (especificação)

```gherkin
Feature: Reembolsar Ingresso

  Scenario: Reembolso PIX com mais de 7 dias de antecedência
    Given que possuo um ingresso ATIVO via PIX com valor 100.00 para daqui a 10 dias
    When solicito o reembolso
    Then o ingresso passa para o status "REEMBOLSADO"
    And o valor reembolsado é 100.00
    And o prazo de processamento é "imediato"

  Scenario: Reembolso com 3 dias de antecedência
    Given que possuo um ingresso ATIVO via PIX com valor 100.00 para daqui a 3 dias
    When solicito o reembolso
    Then o ingresso passa para o status "REEMBOLSADO"
    And o valor reembolsado é 50.00

  Scenario: Reembolso com 1 dia de antecedência
    Given que possuo um ingresso ATIVO via PIX com valor 100.00 para daqui a 1 dias
    When solicito o reembolso
    Then o sistema rejeita o reembolso com a mensagem
         "Reembolso não permitido com menos de 2 dias de antecedência."
```

### Lógica de domínio automatizada

```java
// Ingresso.java
public BigDecimal calcularReembolso(LocalDateTime agora) {
    long diasRestantes = ChronoUnit.DAYS.between(
        agora.toLocalDate(), dataHoraApresentacao.toLocalDate());

    if (diasRestantes > 7)       return valorPago;                          // 100%
    else if (diasRestantes >= 2) return valorPago.multiply(new BigDecimal("0.5")); // 50%
    else                         return BigDecimal.ZERO;                    // negado
}
```

### Step definitions (automação Cucumber)

```java
// ReembolsarIngressoFuncionalidade.java
@Given("que possuo um ingresso ATIVO via {word} com valor {bigdecimal} para daqui a {int} dias")
public void que_possuo_um_ingresso_ativo(String metodo, BigDecimal valor, int dias) {
    LocalDateTime dataApresentacao = LocalDateTime.now().plusDays(dias);
    Ingresso ingresso = servico.comprar(EVENTO_ID, dataApresentacao,
        TipoIngresso.INTEIRA, valor, MetodoPagamento.valueOf(metodo), 100);
    ingressoId = ingresso.getId();
}

@When("solicito o reembolso")
public void solicito_o_reembolso() {
    try {
        resultadoReembolso = servico.solicitarReembolso(ingressoId, LocalDateTime.now());
    } catch (RuntimeException e) {
        excecao = e;
    }
}

@Then("o valor reembolsado é {bigdecimal}")
public void o_valor_reembolsado_e(BigDecimal valor) {
    Ingresso ingresso = repositorio.buscarPorId(ingressoId);
    assertEquals(0, valor.compareTo(ingresso.getValorReembolsado()));
}
```

### Resultado da execução

```
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
```
