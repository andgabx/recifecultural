# Feature 1 — Comprar Ingresso + Reembolso Escalonado

## Contexto

Feature focada no ciclo de vida completo de um ingresso: compra com pagamento mockado, geração de QR code único e reembolso escalonado por proximidade da data do espetáculo. O gateway de pagamento é uma interface — mockado agora, substituível por implementação real sem tocar no domínio.

---

## Persona

**Espectador** — pessoa que deseja comprar um ingresso para um espetáculo e, se necessário, solicitar reembolso com antecedência.

---

## Módulo Maven

`dominio-ingressos` — package `recifecultural.dominio.ingressos`

---

## Aggregate: `Ingresso`

| Campo | Tipo | Obrigatório |
|---|---|---|
| `id` | `IngressoId` (VO) | sim — gerado |
| `eventoId` | `UUID` | sim |
| `dataHoraApresentacao` | `LocalDateTime` | sim |
| `tipo` | `TipoIngresso` (enum) | sim |
| `status` | `StatusIngresso` (enum) | sim — `ATIVO` ao criar |
| `valorPago` | `BigDecimal` | sim — > 0 |
| `codigoQr` | `String` | sim — único, gerado na compra |
| `codigoTransacao` | `String` | sim — retornado pelo gateway |
| `metodoPagamento` | `MetodoPagamento` (enum) | sim |
| `dataCompra` | `LocalDateTime` | sim — gerado |
| `valorReembolsado` | `BigDecimal` | não — preenchido no reembolso |

### Máquina de estados

```
ATIVO → REEMBOLSADO
      → UTILIZADO (futura feature de validação na entrada)
```

---

## Enums

| Enum | Valores |
|---|---|
| `TipoIngresso` | `INTEIRA`, `MEIA_ENTRADA`, `SOCIAL` |
| `StatusIngresso` | `ATIVO`, `REEMBOLSADO`, `UTILIZADO` |
| `MetodoPagamento` | `CARTAO_CREDITO`, `PIX` |

---

## Regras de Negócio

### Compra
- Verifica capacidade disponível antes de processar pagamento
- Pagamento processado via `IGatewayPagamento` (mockado)
- Se pagamento recusado → ingresso não é criado
- QR code único gerado no momento da compra

### Reembolso Escalonado

| Antecedência | Reembolso |
|---|---|
| > 7 dias | 100% do valor pago |
| 2 a 7 dias | 50% do valor pago |
| < 2 dias | 0% — operação negada |

- Apenas ingressos com status `ATIVO` podem ser reembolsados
- PIX: reembolso imediato
- Cartão de crédito: reembolso em até 2 dias úteis

---

## Cenários BDD

### `comprar_ingresso.feature`

| Cenário | Resultado esperado |
|---|---|
| Compra inteira via PIX aprovado | `ATIVO` com QR code único |
| Compra inteira via cartão de crédito aprovado | `ATIVO` com QR code único |
| Compra meia-entrada via PIX | valor 50% da inteira, `ATIVO` |
| Capacidade esgotada | rejeitado |
| Pagamento recusado pelo gateway | ingresso não criado |

### `reembolsar_ingresso.feature`

| Cenário | Resultado esperado |
|---|---|
| Reembolso PIX com > 7 dias | 100% imediato, `REEMBOLSADO` |
| Reembolso cartão com > 7 dias | 100% em até 2 dias úteis, `REEMBOLSADO` |
| Reembolso com 3 dias de antecedência | 50%, `REEMBOLSADO` |
| Reembolso com 1 dia de antecedência | 0%, operação negada |
| Ingresso já reembolsado | negado |
| Ingresso `UTILIZADO` | negado |

---

## Arquivos principais

```
dominio-ingressos/src/main/java/recifecultural/dominio/ingressos/
├── Ingresso.java               # aggregate
├── IngressoId.java             # value object
├── IngressoServico.java        # domain service
├── IGatewayPagamento.java      # interface do gateway
├── IIngressoRepositorio.java   # interface do repositório
├── TipoIngresso.java
├── StatusIngresso.java
├── MetodoPagamento.java
├── ResultadoPagamento.java
└── ResultadoReembolso.java

dominio-ingressos/src/test/
├── resources/features/
│   ├── comprar_ingresso.feature
│   └── reembolsar_ingresso.feature
└── java/recifecultural/dominio/ingressos/
    ├── GatewayPagamentoMock.java
    ├── IngressoRepositorioEmMemoria.java
    ├── IngressoFuncionalidade.java
    ├── ComprarIngressoFuncionalidade.java
    ├── ReembolsarIngressoFuncionalidade.java
    └── RunCucumberIngressosTest.java
```

---

## Como executar os testes

```bash
./mvnw test -pl dominio-ingressos
```
