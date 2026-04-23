# Recife Cultural — CLAUDE.md

## Visão Geral do Projeto

Plataforma web de gestão e descoberta de eventos culturais da cidade do Recife.
Trabalho acadêmico da disciplina de Requisitos — CESAr School (Prof. Saulo Medeiros de Araujo, @profsauloaraujo).

- **Tipo:** Trabalho em grupo
- **Repositório:** deve ser público ou privado com acesso para @profsauloaraujo
- **Stack obrigatória:** Java, Spring Boot, JPA, PostgreSQL, Angular ou Vaadin

---

## Stack e Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 25 |
| Framework | Spring Boot 4.0.5 |
| Persistência | JPA + Hibernate + PostgreSQL 17 |
| Build | Maven (multi-módulo) |
| Containerização | Docker + Docker Compose |
| Testes | JUnit 5, Cucumber (BDD) |

---

## Estrutura de Módulos Maven

O projeto segue Clean Architecture com módulos separados por camada. As dependências apontam sempre para dentro (em direção ao domínio).

```
recifecultural/
├── dominio-compartilhado/   # Value Objects e tipos compartilhados entre bounded contexts
├── dominio-agenda/          # Bounded Context principal (core domain)
├── aplicacao/               # Casos de uso / orquestração (sem Spring, sem JPA)
├── infraestrutura/          # JPA, repositórios concretos, integrações externas
└── apresentacao-backend/    # Controllers REST (Spring Web), entry point da app
```

**Regra de dependência:**
`apresentacao-backend` → `aplicacao` → `dominio-*` ← `infraestrutura`

O módulo `dominio-*` não conhece Spring, JPA, nem nenhum framework.

---

## Arquitetura: DDD + Clean Architecture

### Níveis DDD exigidos pelo professor

| Nível | O que é |
|---|---|
| **Preliminar** | Linguagem Onipresente — vocabulário compartilhado entre time e especialistas de domínio, usado no código, docs e testes |
| **Estratégico** | Subdomínios (Core, Supporting, Generic) e Bounded Contexts |
| **Tático** | Entities, Value Objects, Aggregates, Domain Services, Repositories (interfaces no domínio, implementação na infra) |
| **Operacional** | Casos de uso na camada `aplicacao`, controllers na `apresentacao-backend` |

### Padrões obrigatórios

- **Aggregate Root:** toda modificação de estado passa pela raiz do aggregate
- **Repository:** interface definida no domínio, implementação na infraestrutura
- **Modelo de domínio separado do modelo JPA:** classe de domínio sem anotações `@Entity`; classe JPA na infra faz o mapeamento
- **Objetos criados em estado válido:** validações no construtor, nunca setters públicos para campos de invariante
- **Sem objetos anêmicos:** regras de negócio vivem nas entidades e VOs, não em services utilitários

---

## Subdomínios e Bounded Contexts

| Subdomain | Tipo | Bounded Context |
|---|---|---|
| Agenda de Eventos | **Core** | `AgendaContext` |
| Bilheteria (Ingressos) | Supporting | `BilheteriaContext` |
| Locais Culturais | Supporting | `LocaisContext` |
| Promotores | Supporting | `PromotoresContext` |
| Avaliações | Supporting | `AvaliacoesContext` |

---

## Funcionalidades (14 — 2 por integrante)

Todas são **não triviais**: possuem regras de negócio de complexidade média/alta e não são apenas leitura.

| # | Funcionalidade | Subdomain | Regra central |
|---|---|---|---|
| 1 | Cadastrar Evento | Agenda | Datas obrigatórias; fim ≥ início; preço meia ≤ inteira |
| 2 | Programar Apresentação | Agenda | Data da apresentação deve estar dentro do período do evento |
| 3 | Publicar Evento | Agenda | Evento precisa de ≥1 apresentação; status RASCUNHO → PUBLICADO |
| 4 | Cancelar Apresentação | Agenda | Regras de prazo; ingressos ativos afetados |
| 5 | Cadastrar Local Cultural | Locais | CNPJ/nome únicos; capacidade > 0 |
| 6 | Gerenciar Capacidade por Apresentação | Locais | Ingressos vendidos ≤ capacidade do local |
| 7 | Cadastrar Promotor | Promotores | CNPJ único e válido |
| 8 | Comprar Ingresso | Bilheteria | Verifica capacidade; gera código único; calcula preço por tipo |
| 9 | Solicitar Meia-Entrada | Bilheteria | Valida documento habilitante; meia = 50% da inteira |
| 10 | Cancelar Ingresso | Bilheteria | Reembolso apenas se >48h antes da apresentação |
| 11 | Validar Ingresso na Entrada | Bilheteria | Código único; cada ingresso validado apenas uma vez |
| 12 | Avaliar Evento | Avaliações | Apenas compradores com ingresso UTILIZADO; nota 1–5; uma avaliação por evento/comprador |
| 13 | Destacar Evento na Vitrine | Agenda | Critérios: avaliação média e percentual de vendas |
| 14 | Gerar Relatório de Bilheteria | Bilheteria | Totais por tipo de ingresso; receita calculada |

---

## Entrega 1

### Artefatos exigidos

| Artefato | Localização no repositório | Status |
|---|---|---|
| Descrição do domínio com Linguagem Onipresente | `docs/linguagem-onipresente.md` | ⬜ |
| Mapa de Histórias do Usuário | `docs/mapa-historias.md` | ⬜ |
| Protótipos (baixa ou alta fidelidade) | `docs/prototipos/` | ⬜ |
| Modelo Context Mapper (CML) | `recifecultural.cml` (raiz do projeto) | ⬜ |
| Cenários BDD | `dominio-*/src/test/resources/features/*.feature` | ⬜ |
| Automação Cucumber | Runner + step definitions no módulo de domínio | ⬜ |

---

### CML — Context Mapper

- Arquivo na **raiz do projeto**: `recifecultural.cml`
- Instalar o plugin Context Mapper no IntelliJ para validar e visualizar
- Modelar todos os bounded contexts com aggregates, entities, value objects, services e repositories
- Referência do projeto do professor: `sgb-2025-01/sgb.cml`

---

### BDD — Cenários `.feature`

- Ficam em: `dominio-<contexto>/src/test/resources/features/`
- Keywords em inglês: `Feature`, `Scenario`, `Given`, `When`, `Then`, `And`
- Texto dos cenários em português- Focar em **regras de negócio**, nunca em detalhes de UI
- Estrutura obrigatória:
  - `Given` → estado inicial do sistema
  - `When` → ação do usuário ou sistema
  - `Then` → comportamento esperado refletindo uma regra de negócio
- Cobrir caminho feliz **e** caminhos alternativos (erro, estado inválido)
- Usar a Linguagem Onipresente nos textos dos cenários

---

### Cucumber — Automação

Os testes Cucumber ficam **no módulo de domínio** que contém as features sendo testadas (ex: `dominio-agenda`), não no `apresentacao-backend`.

#### Dependências — adicionar no `pom.xml` do módulo de domínio

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.21.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>7.21.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite-engine</artifactId>
    <scope>test</scope>
</dependency>
```

#### Runner — `RunCucumberTest.java`

```
dominio-agenda/src/test/java/recifecultural/dominio/artistas/RunCucumberTest.java
```

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "recifecultural.dominio.artistas")
public class RunCucumberTest {}
```

#### Step definitions

- Localização: `dominio-<contexto>/src/test/java/recifecultural/dominio/<contexto>/`
- Nomenclatura: `<NomeFuncionalidade>Funcionalidade.java` (ex: `CadastrarArtistaFuncionalidade.java`)
- Referência do projeto do professor: `sgb-2025-01/dominio-acervo/src/test/java/`
- Repositórios: sempre implementar manualmente (`*RepositorioEmMemoria` com HashMap) — sem Mockito
- Gateways e colaboradores com comportamento variável: usar Mockito para o cenário alternativo (ex: gateway que rejeita), mock manual para o caminho feliz

```java
// caminho feliz — mock manual (classe própria)
gateway = new GatewayPagamentoMock();

// cenário alternativo — Mockito
gateway = mock(IGatewayPagamento.class);
when(gateway.processar(any(), any(), any()))
        .thenReturn(new ResultadoPagamento("", false));
servico = new IngressoServico(repositorio, gateway);
```

---

## Convenções de Código

- Pacote raiz: `recifecultural`
- Domínio: `recifecultural.dominio.<contexto>` (sem anotações de framework)
- Aplicação: `recifecultural.aplicacao.<contexto>`
- Infraestrutura: `recifecultural.infraestrutura.<contexto>`
- Apresentação: `recifecultural.apresentacao`
- Lombok permitido em infraestrutura e apresentação; no domínio usar getters explícitos (Lombok não tem annotation processor configurado nos módulos de domínio)
- Validações no construtor e em métodos de domínio via Apache Commons Lang3 `Validate` (static imports: `notNull`, `notBlank`, `isTrue`)
- Interfaces de repositório com prefixo `I` e nome em português (ex: `IArtistaRepositorio`)
- Repositórios retornam o tipo diretamente (nullable); usar `Validate.notNull` para checar no serviço- Listas de VOs retornadas como `Collections.unmodifiableList()`
- Exceções de domínio com nomes descritivos (ex: `ApresentacaoForaDoPeriodoException`)

---

## Como Rodar

```bash
# Subir banco + aplicação
docker compose up --build

# Somente o banco (para desenvolvimento local)
docker compose up db

# Rodar testes
./mvnw test
```

A API sobe em `http://localhost:8080`.
