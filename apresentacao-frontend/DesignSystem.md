# Fase 6 — UI React + Tailwind

> Planejamento de telas, design system e componentes reutilizáveis com base nos 15 controllers BFF (51 endpoints).
> Stack: **Next + Tailwind CSS v3 · shadcn/ui · motion.dev · react-hook-form · TanStack Query**

---

## 0. Design System

### 0.1 Identidade Visual

O Recife Cultural é o portal centralizado de eventos culturais da Prefeitura do Recife (PCR). A identidade visual segue o **Manual de Marca 2025 da ID PCR**, unindo dois universos:

- **Identidade institucional da PCR** — azul profundo institucional, traços modernos e tipografia sólida da família BR Omny
- **Cultura popular pernambucana** — a vibração do frevo, o verde dos manguezais do Capibaribe, o laranja festivo das manifestações culturais

O resultado é uma interface **viva e acessível com presença institucional**: fundos escuros em azul-noite nas áreas de destaque (hero, sidebar, catraca), superfícies claras com leve toque azulado para conteúdo denso, e o laranja cultural e o verde lima como únicos elementos de cor intensa.

---

### 0.2 Paleta de Cores

```css
/* tailwind.config.ts — extend.colors */
/* Cores oficiais ID PCR / Manual de Marca 2025 */

azul: {
  DEFAULT: '#173DB7',   /* azul institucional PCR — primary brand */
  light:   '#008AF4',   /* azul claro — hover states */
  dark:    '#0D2D8A',   /* azul escuro — pressed / active */
  50:      '#EFF4FF',   /* fundos sutis */
},

verde: {
  DEFAULT: '#006E2B',   /* verde manguezal — secondary brand */
  light:   '#42DA2E',   /* verde lima — badges, indicadores festivos */
  dark:    '#004D1E',   /* verde profundo — pressed */
  50:      '#EDFFF0',   /* fundos sutis */
},

laranja: {
  DEFAULT: '#E94E1B',   /* laranja cultural — CTAs, destaques */
  light:   '#FF6B3D',   /* hover */
  dark:    '#C03A10',   /* pressed */
},

violeta: {
  DEFAULT: '#9346FC',   /* violeta frevo — badges, destaques festivos */
  dark:    '#6D25D4',
},

noite: {
  DEFAULT: '#0A1628',   /* azul-noite profundo — fundos escuros */
  surface: '#122040',   /* sidebar, topbar escura */
},

nevoa: {
  DEFAULT: '#F5F7FF',   /* névoa azulada — page background claro */
  card:    '#FFFFFF',
  muted:   '#EBF0FF',   /* azul muito sutil — card background alternativo */
},
```

**Mapeamento semântico (CSS variables via shadcn/ui):**

| Token | Valor | Uso |
|---|---|---|
| `--primary` | `#173DB7` (azul PCR) | Botões primários, links ativos, ring de foco |
| `--primary-foreground` | `#FFFFFF` | Texto em fundo primary |
| `--secondary` | `#EBF0FF` | Botões secundários, chips |
| `--accent` | `#E94E1B` | Destaques, ícones de ação |
| `--destructive` | `#B91C1C` | Ações destrutivas (reprovar, cancelar) |
| `--background` | `#F5F7FF` | Fundo geral (modo claro) |
| `--card` | `#FFFFFF` | Superfície de cards |
| `--border` | `#D0D8F0` | Bordas, divisores |
| `--muted-foreground` | `#5C6B8A` | Labels, placeholders, texto secundário |

---

### 0.3 Tipografia

```css
/* Fonte principal via licença ou CDN corporativo */

font-display: 'BR Omny', system-ui, sans-serif;
/* Títulos de páginas, nome do evento, cabeçalhos de seção
   Fonte oficial da marca ID PCR — BR Omny Black para hero, BR Omny Bold para seções */

font-sans: 'Inter', system-ui, sans-serif;
/* Corpo, labels, tabelas, formulários
   Leitura limpa em qualquer tamanho */

font-mono: 'JetBrains Mono', monospace;
/* Código de ingresso, QR, IDs */
```

**Escala tipográfica:**

| Classe Tailwind | Uso |
|---|---|
| `font-display text-4xl font-black` | Título da Home (hero) |
| `font-display text-2xl font-bold` | Título de página (`PageLayout`) |
| `font-display text-xl font-bold` | Nome do evento em cards |
| `text-base font-sans` | Corpo de texto |
| `text-sm font-sans text-muted-foreground` | Labels, metadados |
| `text-xs font-mono tracking-widest` | Código de ingresso |

---

### 0.4 Superfícies e Elevação

Inspiradas na **profundidade da paisagem do Recife**: do fundo escuro do Capibaribe à noite até a luz clara do dia sobre a cidade.

| Nível | Cor | Tailwind | Uso |
|---|---|---|---|
| Noite (deep) | `#0A1628` | `bg-noite` | Sidebar, TopBar, hero |
| Sombra | `#122040` | `bg-noite-surface` | Sidebar hover, drawer mobile |
| Névoa muted | `#EBF0FF` | `bg-nevoa-muted` | Fundo de seções alternadas |
| Névoa | `#F5F7FF` | `bg-nevoa` | Background geral de páginas |
| Branco | `#FFFFFF` | `bg-white` | Cards, modais, formulários |

**Sombras:**

```css
shadow-card:   '0 2px 8px rgba(10,22,40,0.08)'    /* cards em repouso */
shadow-raised: '0 8px 24px rgba(10,22,40,0.14)'   /* cards hover, modais */
shadow-stage:  '0 0 40px rgba(23,61,183,0.22)'    /* destaque azul PCR (hero CTA) */
```

---

### 0.5 motion.dev — Padrões Globais de Animação

Instalação: `npm install motion`

#### Transição de Página
Toda `<Page>` é envolvida em `<motion.div>` com variante `pageVariants`:

```ts
const pageVariants = {
  initial: { opacity: 0, y: 16 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.28, ease: 'easeOut' } },
  exit:    { opacity: 0, y: -8, transition: { duration: 0.18, ease: 'easeIn' } },
}
// Usado com <AnimatePresence mode="wait"> no router outlet
```

#### Stagger de Listas
Cards e itens de lista entram em cascata:

```ts
const containerVariants = {
  animate: { transition: { staggerChildren: 0.07 } },
}
const itemVariants = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.3, ease: 'easeOut' } },
}
```

#### Spring Base
Todos os `whileHover` / `whileTap` usam spring com feel ágil e institucional:

```ts
const springConfig = { type: 'spring', stiffness: 380, damping: 28 }
```

---

### 0.6 shadcn/ui — Componentes Base

Inicialização: `npx shadcn@latest init` com tema customizado (azul PCR como primary).

| shadcn | Usado por |
|---|---|
| `Button` | `Button`, `CouponInput`, `ConfirmDialog` |
| `Badge` | `Badge`, `NotificationBell` |
| `Card` | `EventCard`, `StatCard`, `SorteioStatusCard` |
| `Dialog` | `Modal`, `ConfirmDialog` |
| `AlertDialog` | `ConfirmDialog` (ações destrutivas) |
| `Table` | `DataTable` |
| `Input` | `Input`, `CouponInput` |
| `Textarea` | `Textarea` |
| `Select` | `Select` |
| `Tabs` | `Tabs` |
| `Sheet` | Sidebar mobile (drawer) |
| `Popover` | `NotificationBell` dropdown |
| `Command` | Busca na Home |
| `Calendar` | `DateRangePicker` |
| `Sonner` | `Toast` (via `sonner`) |
| `Skeleton` | Loading states em tabelas e cards |
| `Avatar` | Perfil de usuário na TopBar |
| `Separator` | Divisores em formulários e modais |
| `Progress` | Barra de capacidade de espaço |
| `Tooltip` | Ícones de ação sem label |

---

## 1. Papéis e Fluxos de Acesso

| Papel | Acesso Principal |
|---|---|
| **Espectador** | Home → Detalhe → Checkout → Meus Ingressos → Sorteio |
| **Produtor** | Dashboard → Eventos → Financeiro → Patrocínios → Sorteios |
| **Admin / Gestor** | Aprovações → Espaços → Setores → Bloqueios → Artistas → Produtores |
| **Operador de Catraca** | Catraca (leitura de QR) |
| **Todos** | Notificações · Perfil · Acessibilidade |

---

## 2. Páginas por Fluxo

### 2.1 Fluxo Público / Espectador

#### `/` — Home (Explorar Eventos)
**Estilo:** Hero com fundo `bg-noite` e degradê diagonal para `bg-azul-dark`. Título em `font-display text-4xl font-black text-white` com subtítulo em `text-laranja`. Grade de `EventCard` em `bg-nevoa` abaixo do hero. Barra de busca flutuante no topo do hero usando `shadcn Command`.
**Animação:** Hero text com `initial={{ opacity:0, y:32 }}` e stagger nos parágrafos (delay 0.1s por linha). Cards entram com `staggerChildren 0.06s` ao rolar para a grade (`whileInView`).
- BFF: `GET /api/bff/eventos` → `pesquisarEventos()`

#### `/eventos/:id` — Detalhe do Evento
**Estilo:** Imagem de capa em `aspect-[21/9]` com `object-cover` e overlay gradiente `from-noite/80 to-transparent`. Título sobreposto na imagem em `font-display text-3xl font-bold text-white`. Conteúdo abaixo em card `bg-white rounded-2xl -mt-8 relative z-10`. Seção de sorteios em `bg-nevoa-muted rounded-xl`. Preços em pill de `laranja` com ícone de bilhete.
**Animação:** Imagem com `initial={{ scale:1.06 }} animate={{ scale:1 }} transition={{ duration:0.6 }}` (zoom-out de entrada). Seções de conteúdo com `pageVariants`. `EventoStatusTimeline` com linha animada via `scaleX`.
- BFF: `GET /api/bff/eventos/:id` · `GET /api/bff/sorteios/evento/:eventoId`

#### `/checkout` — Checkout
**Estilo:** Layout de duas colunas (`lg:grid-cols-[1fr_380px]`): formulário à esquerda, resumo do pedido à direita em card `bg-nevoa-muted border border-azul/20`. Botão "Finalizar Compra" em `bg-azul hover:bg-azul-light` com sombra `shadow-stage`. Seção de cupom com borda tracejada `border-dashed border-laranja/40`.
**Animação:** Coluna do resumo com `sticky top-6`. Valor total anima com `motion.span` usando `useSpring` para transição numérica suave ao aplicar desconto.
- BFF: `POST /api/bff/checkout/calcular` · `POST /api/bff/checkout/confirmar` · `POST /api/bff/cupom/validar`

#### `/meus-ingressos` — Meus Ingressos
**Estilo:** Cards de ingresso com borda esquerda `border-l-4 border-azul`. QR expandido em modal com fundo `bg-noite` e QR branco centralizado. Status via `Badge` com variante de cor.
**Animação:** Expand/collapse do QR com `AnimatePresence` + `motion.div initial={{ height:0, opacity:0 }} animate={{ height:'auto', opacity:1 }}`.
- BFF: `GET /api/bff/meus-ingressos` · `GET /api/bff/meus-ingressos/:id`

#### `/sorteios` — Meus Sorteios
**Estilo:** Cards `SorteioStatusCard` com indicador de prazo — barra `Progress` do shadcn mostrando tempo restante em azul. Prazo encerrado em cinza. Status pill colorido.
**Animação:** Cards com stagger. Barra de progresso anima de 0 ao valor real com `transition={{ duration:0.8, ease:'easeOut' }}`.
- BFF: `GET /api/bff/sorteio/inscricoes` · `DELETE` cancelar · `POST` inscrever

#### `/acessibilidade` — Acessibilidade
**Estilo:** Grid de ícones de recursos em `bg-white rounded-xl` com ícone grande `text-laranja` e label abaixo. Seção de mapa de acessibilidade do espaço com indicadores visuais.
- BFF: `GET /api/bff/acessibilidade/:eventoId`

---

### 2.2 Fluxo Produtor

#### `/produtor/dashboard` — Dashboard do Produtor
**Estilo:** Grid 2×2 de `StatCard` em destaque. Abaixo, lista dos últimos eventos com status. Topo com saudação personalizada em `font-display font-bold`. Fundo `bg-nevoa`.
**Animação:** `StatCard` entram com `staggerChildren 0.09s`. Número dos indicadores anima de 0 ao valor real usando `useSpring` (motion.dev).
- BFF: `GET /api/bff/financeiro/indicadores` · `GET /api/bff/eventos/produtor`

#### `/produtor/eventos` — Gestão de Eventos
**Estilo:** `DataTable` (shadcn `Table`) com linha de status colorida à esquerda. Chip de status como `Badge`. Linha de evento reprovado com fundo `bg-destructive/5` e ícone de alerta `text-destructive`.
**Animação:** Nova linha inserida com `initial={{ backgroundColor:'rgba(23,61,183,0.12)' }}` passando para transparente (highlight de insert).
- BFF: `GET /api/bff/eventos/produtor` · `GET /api/bff/eventos/produtor/reprovados`

#### `/produtor/eventos/novo` · `/produtor/eventos/:id/editar` — Formulário de Evento
**Estilo:** Formulário em `steps` (wizard de 3 etapas via `StepIndicator`): Informações Básicas → Datas e Espaço → Preços e Revisão. Cada step em card branco. Barra de progresso de steps em `azul`. Textarea de descrição longa com contador de caracteres.
**Animação:** Transição entre steps com `AnimatePresence` + `motion.div` deslizando lateralmente (`x: ±32`). Botão "Próximo" com `whileTap={{ scale:0.97 }}`.
- BFF: `POST /api/bff/eventos` · `PUT /api/bff/eventos/:id`

#### `/produtor/financeiro` — Financeiro
**Estilo:** Tabs shadcn ("Receita" / "Despesas"). Gráfico de linha em `recharts` com cor `azul` e fundo `azul/10`. Tabela de despesas com coluna de valor em `font-mono`. Totalizadores em card `bg-azul text-white` no topo.
**Animação:** Linha do gráfico desenhada com `pathLength` animation (motion SVG). Cards de indicador com stagger.
- BFF: `GET /api/bff/financeiro/indicadores` · `GET /api/bff/financeiro/despesas` · `POST` despesa

#### `/produtor/patrocinios` — Patrocínios
**Estilo:** Cards de patrocinador com logo + nome + valor de patrocínio em pill `laranja`. Botão "Adicionar" em outline `border-laranja text-laranja hover:bg-laranja hover:text-white`.
- BFF: `GET /api/bff/patrocinio/produtor` · `POST /api/bff/patrocinio`

#### `/produtor/sorteios` — Sorteios dos Eventos
**Estilo:** Lista de `SorteioStatusCard`. Botão "Realizar Sorteio" em destaque `bg-verde-light text-noite font-semibold` quando disponível — referência visual ao verde festivo.
**Animação:** Ao clicar "Realizar Sorteio", modal de confirmação entra com animação de confete (partículas CSS simples) após sucesso.
- BFF: `GET /api/bff/sorteio/evento/:eventoId` · `POST /api/bff/sorteio/:sorteioId/realizar`

---

### 2.3 Fluxo Admin / Gestor

#### `/admin/aprovacoes` — Fila de Aprovação
**Estilo:** Kanban-light: coluna de pendentes com card por evento. Card com informações do produtor, categoria, período. Botões "Aprovar" (verde) / "Reprovar" (azul-noite) por card. Evento com `requerRevisaoAdicional=true` recebe badge pulsante `verde-light`.
**Animação:** Card aprovado/reprovado sai da lista com `exit={{ x:160, opacity:0 }}` e a lista reajusta com `layout` prop do motion.
- BFF: `GET /api/bff/eventos/pendentes` · `POST` aprovar/reprovar

#### `/admin/espacos` — Gestão de Espaços
**Estilo:** Cards de espaço com indicador de ocupação via `Progress` (shadcn) em `azul`. Status `ATIVO/INATIVO` como Badge. Botão "Ver Setores" leva ao sub-route.
- BFF: `GET /api/bff/espacos` · `POST /api/bff/espacos` · `GET /api/bff/espacos/:id/ocupacoes`

#### `/admin/espacos/:id/setores` — Setores do Espaço
**Estilo:** `AssentoGrid` com fundo escuro `bg-noite-surface` simulando uma planta de teatro — assentos como quadrados 16×16px, fileiras com label lateral em `font-mono text-xs text-muted`. Palco representado como retângulo `bg-azul/30 rounded-t-lg` no topo da grade.
**Animação:** Grid de assentos renderiza com `staggerChildren 0.005s` — efeito visual de "acender as luzes" da plateia.
- BFF: `GET /api/bff/setores/espaco/:espacoId` · `POST /api/bff/setores` · `GET /api/bff/setores/:id/assentos`

#### `/admin/bloqueios` — Bloqueios de Agenda
**Estilo:** Calendário mensal usando `shadcn Calendar` customizado — dias bloqueados em `bg-azul/20 text-azul font-semibold`. Lista de bloqueios ativos abaixo com chip de espaço e período.
- BFF: `GET /api/bff/bloqueios` · `POST /api/bff/bloqueios` · `DELETE /api/bff/bloqueios/:id`

#### `/admin/artistas` — Gestão de Artistas
**Estilo:** Tabela com avatar do artista (shadcn `Avatar` com iniciais em `bg-laranja`), nome, gênero musical como Badge em `azul/20 text-azul-dark`.
- BFF: `GET /api/bff/artistas` · `POST /api/bff/artistas`

#### `/admin/produtores` — Gestão de Produtores
**Estilo:** Tabela com status de conta. Produtor bloqueado com linha `opacity-60`. Ação de aprovar/bloquear via `ActionMenu` (dropdown de ações por linha).
- BFF: `GET /api/bff/produtores` · `POST /api/bff/produtores/:id/aprovar`

---

### 2.4 Fluxo Operador de Catraca

#### `/catraca` — Leitura de QR
**Estilo:** Tela em modo **fullscreen dark** — fundo `bg-noite`, sem sidebar, sem topbar padrão. Área central com viewfinder de câmera em borda `border-2 border-azul-light rounded-xl`. Input manual abaixo em `font-mono text-xl text-center`. Feedback ocupa a tela inteira: fundo verde `bg-emerald-900` (válido) ou vermelho `bg-red-900` (inválido).
**Animação:** `CatracaFeedback` entra com `initial={{ scale:0.85, opacity:0 }} animate={{ scale:1, opacity:1 }} transition={{ type:'spring', stiffness:500, damping:25 }}`. Reset automático após 3s com `exit={{ scale:0.9, opacity:0 }}`.
- BFF: `POST /api/bff/catraca/validar` · `GET /api/bff/catraca/historico`

---

### 2.5 Páginas Compartilhadas

#### `/notificacoes` — Central de Notificações
**Estilo:** Lista com separador por data (hoje, ontem, mais antigas). Notificação não-lida com fundo `bg-azul/5 border-l-2 border-azul`. Ícone por tipo: aprovação (verde), reprovação (azul-noite), sorteio (verde-light), ingresso (laranja).
**Animação:** Notificação marcada como lida com `animate={{ opacity:0.5, x:8 }}` enquanto a borda esquerda desaparece.
- BFF: `GET /api/bff/notificacoes` · `PATCH` marcar lida

#### `/perfil` — Perfil do Usuário
**Estilo:** Avatar grande central com borda `ring-4 ring-azul/30`. Formulário em card branco. Para produtor: seção "Dados Bancários" separada com ícone de cadeado.

---

## 3. Componentes Reutilizáveis

### 3.1 Layout

#### `AppShell`
- Wrapper raiz da aplicação autenticada: `Sidebar` + `TopBar` + `<main>`
- Props: `role: 'espectador' | 'produtor' | 'admin' | 'catraca'`
- **Estilo:** `bg-nevoa min-h-screen flex`

#### `Sidebar`
- Navegação lateral colapsável. Fundo `bg-noite`, texto `text-white/80`, item ativo `bg-azul text-white rounded-lg`
- Logo no topo com fonte `font-display font-black text-azul-light`
- Estado collapsed persistido em `localStorage`
- **shadcn:** `Sheet` para versão mobile (drawer)
- **motion.dev:** `motion.div animate={{ width: collapsed ? 64 : 256 }} transition={{ type:'spring', stiffness:300, damping:30 }}`. Labels dos itens com `AnimatePresence` e `initial={{ opacity:0, width:0 }} animate={{ opacity:1, width:'auto' }}`

#### `TopBar`
- Fundo `bg-noite`, borda inferior `border-b border-noite-surface`
- Logo à esquerda, `NotificationBell` + `Avatar` + `DropdownMenu` à direita
- **shadcn:** `DropdownMenu` para o menu de usuário
- **motion.dev:** Dropdown com `initial={{ opacity:0, y:-8 }} animate={{ opacity:1, y:0 }}`

#### `PageLayout`
- `p-6 space-y-6 max-w-7xl mx-auto`
- Título em `font-display font-bold text-2xl text-noite`, subtítulo em `text-muted-foreground`
- **motion.dev:** Wrapper com `pageVariants` para transição de rota

#### `PublicLayout`
- Header `bg-noite` com logo `font-display font-black text-azul-light` e links em `text-white/70 hover:text-white`
- Footer com faixa `bg-noite-surface text-white/60`

---

### 3.2 Navegação e Contexto

#### `Breadcrumb`
- `text-sm text-muted-foreground`, separador `›` em `text-laranja/60`
- **shadcn:** baseado no `Breadcrumb` do shadcn

#### `Tabs`
- Fundo de indicador ativo em `bg-azul`, texto ativo `text-azul` ou branco conforme contexto
- **shadcn:** `Tabs` + `TabsList` + `TabsTrigger` customizados
- **motion.dev:** Indicador underline como `motion.div layoutId="tab-indicator"` deslizando entre tabs com `transition={{ type:'spring', stiffness:400, damping:30 }}`

#### `StepIndicator`
- Ícone numerado com `bg-azul text-white` (ativo), `bg-verde/20 text-verde` (completo), `bg-border text-muted` (futuro)
- Linha conectora entre steps com `motion.div scaleX` animado

---

### 3.3 Exibição de Dados

#### `StatCard`
- `shadcn Card` com `bg-white shadow-card`
- Ícone em pill `bg-azul/10 text-azul rounded-lg p-2`
- Valor em `font-display font-bold text-3xl text-noite`
- Trend positivo `text-emerald-600`, negativo `text-destructive`
- **motion.dev:** `motion.div variants={itemVariants}` (stagger do dashboard). Número usa `useSpring` + `useTransform` para animação de contagem.

#### `EventCard`
- `shadcn Card` com `overflow-hidden rounded-2xl`
- Imagem com `aspect-[4/3]` e overlay gradiente `from-noite/60`
- Categoria como `Badge` no canto superior esquerdo da imagem
- Preço em `font-mono text-laranja font-semibold` no rodapé
- **motion.dev:** `whileHover={{ y:-6 }} transition={{ type:'spring', stiffness:400, damping:20 }}`. Imagem interna com `scale:1.04` no hover via `whileHover` no `motion.img`.

#### `DataTable`
- `shadcn Table` com `divide-y divide-border`
- Header `bg-nevoa-muted font-semibold text-sm text-muted-foreground`
- Loading state com `shadcn Skeleton` (3 linhas animadas)
- **motion.dev:** Linhas entram com `stagger 0.04s` no mount inicial

#### `Badge`
- `shadcn Badge` customizado
- Variantes: `aprovado` (`bg-emerald-100 text-emerald-800`), `reprovado` (`bg-azul/15 text-azul`), `em-analise` (`bg-verde-light/20 text-green-800`), `rascunho` (`bg-border text-muted-foreground`), `cancelado` (`bg-red-100 text-red-700`)
- **motion.dev:** `em-analise` com `animate={{ opacity:[1,0.55,1] }} transition={{ repeat:Infinity, duration:2 }}` (pulse sutil)

#### `EmptyState`
- Ícone SVG centralizado em `text-laranja/40` (grande, 64px)
- Título `font-display font-bold text-xl text-noite`, subtítulo `text-muted-foreground`
- CTA opcional em `Button variant="outline"` com cor `azul`
- **motion.dev:** `initial={{ opacity:0, scale:0.92 }} animate={{ opacity:1, scale:1 }} transition={{ duration:0.4 }}`

---

### 3.4 Formulários

#### `FormField`
- Wrapper: `flex flex-col gap-1.5`
- Label em `text-sm font-medium text-noite`
- Mensagem de erro em `text-xs text-destructive` com ícone
- **shadcn:** integra com `Form`, `FormLabel`, `FormMessage` do shadcn/ui

#### `Input`
- `shadcn Input` customizado: `border-border focus:ring-2 focus:ring-azul/30 focus:border-azul`
- Estado error: `border-destructive focus:ring-destructive/30`

#### `Select`
- `shadcn Select` com ícone chevron em `text-laranja`

#### `Textarea`
- `shadcn Textarea` com contador `text-xs text-muted-foreground text-right`

#### `CouponInput`
- `shadcn Input` + `shadcn Button` inline em `flex gap-2`
- Botão "Aplicar" em `bg-laranja text-white hover:bg-laranja-dark`
- Estado success: borda `border-emerald-500` + label do desconto em `text-emerald-700 font-semibold`
- **motion.dev:** Tag de desconto aparece com `initial={{ scale:0.8, opacity:0 }} animate={{ scale:1, opacity:1 }} transition={{ type:'spring' }}`

#### `DateRangePicker`
- `shadcn Calendar` em `Popover` com dois meses lado a lado
- Intervalo selecionado destacado em `bg-azul/15`, datas de início/fim em `bg-azul text-white rounded-full`

---

### 3.5 Feedback e Overlays

#### `Button`
- `shadcn Button` como base
- `primary`: `bg-azul text-white hover:bg-azul-light shadow-sm`
- `secondary`: `bg-nevoa-muted text-noite border border-border hover:bg-border`
- `danger`: `bg-destructive text-white hover:bg-destructive/90`
- `ghost`: `text-azul hover:bg-azul/10`
- `accent`: `bg-laranja text-white hover:bg-laranja-dark` (CTAs de destaque cultural)
- **motion.dev:** `whileTap={{ scale:0.97 }}` em todas as variantes. Loading state com spinner `motion.div animate={{ rotate:360 }} transition={{ repeat:Infinity, duration:0.8, ease:'linear' }}`

#### `Modal`
- `shadcn Dialog` com overlay `bg-noite/70 backdrop-blur-sm`
- Panel `bg-white rounded-2xl shadow-raised`
- Header com borda `border-b border-border pb-4 mb-4`
- **motion.dev:** Overlay `initial={{ opacity:0 }} animate={{ opacity:1 }}`. Panel `initial={{ opacity:0, scale:0.96, y:12 }} animate={{ opacity:1, scale:1, y:0 }} transition={{ type:'spring', stiffness:400, damping:32 }}`

#### `ConfirmDialog`
- `shadcn AlertDialog` para ações destrutivas
- Título em `text-destructive` quando `dangerous=true`
- Campo de motivo (Textarea) quando ação é reprovação
- **motion.dev:** mesma animação do `Modal`

#### `Toast`
- `shadcn Sonner` com posição `top-right`
- Customização: sucesso `border-l-4 border-emerald-500 bg-white`, erro `border-l-4 border-destructive`, info `border-l-4 border-azul`
- **motion.dev:** Sonner já gerencia animação; customizar via `toastOptions` com `transition`

#### `LoadingSpinner`
- SVG circular em `stroke-azul`
- **motion.dev:** `motion.circle animate={{ pathLength:[0,1,0], rotate:360 }} transition={{ repeat:Infinity, duration:1.2, ease:'easeInOut' }}`

#### `PageLoader`
- Fundo `bg-noite` fullscreen
- Logo central com `font-display font-black text-3xl text-azul-light`
- **motion.dev:** Logo com `animate={{ opacity:[0.4,1,0.4] }} transition={{ repeat:Infinity, duration:1.8 }}`

---

### 3.6 Domínio Específico

#### `QRCodeDisplay`
- Fundo `bg-noite rounded-xl p-6` com QR branco centralizado
- Código abaixo em `font-mono text-xs tracking-widest text-white/70`
- Props: `codigo: string, eventoNome: string`
- **motion.dev:** Entrada com `initial={{ rotateY:90 }} animate={{ rotateY:0 }} transition={{ type:'spring', stiffness:200 }}` (flip de entrada)

#### `CatracaFeedback`
- Ocupa viewport inteiro com `fixed inset-0 z-50`
- `valido`: `bg-emerald-950` + ícone ✓ em `text-emerald-400 text-8xl`
- `invalido`: `bg-red-950` + ícone ✗ em `text-red-400 text-8xl`
- `ja-usado`: `bg-yellow-950` + ícone ⚠ em `text-verde-light text-8xl`
- Nome em `font-display font-bold text-2xl text-white`, evento em `text-white/70`
- **motion.dev:** `initial={{ scale:0.8, opacity:0 }} animate={{ scale:1, opacity:1 }} transition={{ type:'spring', stiffness:500, damping:25 }}`. Ícone com `initial={{ scale:0 }} animate={{ scale:1 }} transition={{ delay:0.12, type:'spring', stiffness:600 }}`

#### `AssentoGrid`
- Container `bg-noite-surface rounded-xl p-4`
- "PALCO" em `bg-azul/40 text-white/50 text-xs text-center rounded-t-lg py-2 mb-4`
- Assento `w-5 h-5 rounded-sm cursor-pointer`: disponível `bg-white/20`, reservado `bg-azul/60`, bloqueado `bg-border/30`, selecionado `bg-laranja`
- **motion.dev:** Cada assento com `variants={itemVariants}` e container com `staggerChildren:0.005` — ilumina a plateia gradualmente. `whileHover={{ scale:1.2 }}` e `whileTap={{ scale:0.9 }}` nos disponíveis.

#### `SorteioStatusCard`
- `shadcn Card` com borda top `border-t-4 border-verde-light` (aberto) ou `border-azul` (realizado)
- Barra de progresso de prazo em azul
- Botão de ação contextual (inscrever/cancelar/realizar)
- **motion.dev:** `whileHover={{ y:-3 }}` com spring

#### `NotificationBell`
- Ícone `Bell` com badge numérico `bg-laranja text-white text-xs` pulsante
- Dropdown via `shadcn Popover` com `bg-white shadow-raised rounded-xl`
- Notificações não-lidas com `bg-azul/5`
- **motion.dev:** Badge com notificações novas: `animate={{ scale:[1,1.3,1] }} transition={{ repeat:2, duration:0.3 }}` (shake de atenção). Popover `initial={{ opacity:0, y:-8, scale:0.97 }} animate={{ opacity:1, y:0, scale:1 }}`

#### `EventoStatusTimeline`
- Linha horizontal com 4 nós: Rascunho → Em Análise → (Aprovado | Reprovado)
- Nó ativo: `bg-azul ring-2 ring-azul/30 scale-125`
- Nó completo: `bg-verde`
- **motion.dev:** Linha de progresso como `motion.div initial={{ scaleX:0 }} animate={{ scaleX:1 }} transition={{ duration:0.6, ease:'easeInOut' }} style={{ originX:0 }}`

#### `ActionMenu`
- Dropdown de ações por linha de tabela (⋮ kebab)
- `shadcn DropdownMenu`
- Item destrutivo em `text-destructive focus:bg-destructive/10`

---

## 4. Estrutura de Pastas Sugerida

```
src/
├── lib/
│   ├── utils.ts            # cn() helper (shadcn)
│   ├── motion.ts           # pageVariants, stagger, springConfig exportados
│   └── theme.ts            # mapeamento StatusEvento → Badge variant
│
├── components/
│   ├── layout/
│   │   ├── AppShell.tsx
│   │   ├── Sidebar.tsx
│   │   ├── TopBar.tsx
│   │   ├── PageLayout.tsx
│   │   └── PublicLayout.tsx
│   ├── ui/                 # shadcn/ui (gerados via CLI) + customizações
│   │   ├── badge.tsx
│   │   ├── button.tsx
│   │   ├── card.tsx
│   │   ├── dialog.tsx
│   │   ├── table.tsx
│   │   └── ...
│   ├── shared/             # compostos reutilizáveis
│   │   ├── Badge.tsx       # wrapper do shadcn Badge com variantes de domínio
│   │   ├── Button.tsx      # wrapper com whileTap e variantes azul/laranja
│   │   ├── DataTable.tsx
│   │   ├── EmptyState.tsx
│   │   ├── LoadingSpinner.tsx
│   │   ├── Modal.tsx
│   │   ├── ConfirmDialog.tsx
│   │   ├── StatCard.tsx
│   │   ├── StepIndicator.tsx
│   │   ├── Tabs.tsx
│   │   └── Breadcrumb.tsx
│   ├── form/
│   │   ├── FormField.tsx
│   │   ├── Input.tsx
│   │   ├── Select.tsx
│   │   ├── Textarea.tsx
│   │   ├── CouponInput.tsx
│   │   └── DateRangePicker.tsx
│   └── domain/
│       ├── EventCard.tsx
│       ├── QRCodeDisplay.tsx
│       ├── CatracaFeedback.tsx
│       ├── AssentoGrid.tsx
│       ├── SorteioStatusCard.tsx
│       ├── NotificationBell.tsx
│       ├── EventoStatusTimeline.tsx
│       └── ActionMenu.tsx
│
├── pages/
│   ├── public/
│   │   ├── HomePage.tsx
│   │   ├── EventoDetalhePage.tsx
│   │   └── CheckoutPage.tsx
│   ├── espectador/
│   │   ├── MeusIngressosPage.tsx
│   │   ├── SorteiosPage.tsx
│   │   └── AcessibilidadePage.tsx
│   ├── produtor/
│   │   ├── DashboardPage.tsx
│   │   ├── EventosPage.tsx
│   │   ├── EventoFormPage.tsx
│   │   ├── FinanceiroPage.tsx
│   │   ├── PatrociniosPage.tsx
│   │   └── SorteiosPage.tsx
│   ├── admin/
│   │   ├── AprovacoesPage.tsx
│   │   ├── EspacosPage.tsx
│   │   ├── SetoresPage.tsx
│   │   ├── BloqueiosPage.tsx
│   │   ├── ArtistasPage.tsx
│   │   └── ProdutoresPage.tsx
│   └── catraca/
│       └── CatracaPage.tsx
│
├── hooks/
│   ├── useToast.ts
│   ├── useAuth.ts
│   └── usePermission.ts
│
└── services/
    └── bff/
        ├── eventos.ts
        ├── checkout.ts
        ├── ingressos.ts
        ├── sorteios.ts
        ├── financeiro.ts
        ├── espacos.ts
        ├── catraca.ts
        └── notificacoes.ts
```

---

## 5. Dependências

```json
{
  "dependencies": {
    "react": "^19",
    "react-router-dom": "^7",
    "motion": "^12",
    "@tanstack/react-query": "^5",
    "react-hook-form": "^7",
    "zod": "^3",
    "@hookform/resolvers": "^3",
    "recharts": "^2",
    "sonner": "^2",
    "lucide-react": "^0.400",
    "qrcode.react": "^4",
    "clsx": "^2",
    "tailwind-merge": "^2"
  },
  "devDependencies": {
    "tailwindcss": "^3",
    "autoprefixer": "^10",
    "@types/react": "^19"
  }
}
```

**shadcn/ui** inicializado separadamente via `npx shadcn@latest init` com `baseColor: slate` e primary token sobrescrito para `azul` PCR (`#173DB7`).

---

## 6. Mapa de Rotas

```
/                               → HomePage (público)
/eventos/:id                    → EventoDetalhePage (público)
/checkout                       → CheckoutPage (espectador)
/meus-ingressos                 → MeusIngressosPage (espectador)
/sorteios                       → SorteiosPage (espectador)
/acessibilidade/:eventoId       → AcessibilidadePage (público)
/notificacoes                   → NotificacoesPage (autenticado)
/perfil                         → PerfilPage (autenticado)

/produtor/dashboard             → DashboardPage
/produtor/eventos               → EventosPage
/produtor/eventos/novo          → EventoFormPage
/produtor/eventos/:id/editar    → EventoFormPage (edit mode)
/produtor/financeiro            → FinanceiroPage
/produtor/patrocinios           → PatrociniosPage
/produtor/sorteios              → SorteiosPage (produtor)

/admin/aprovacoes               → AprovacoesPage
/admin/espacos                  → EspacosPage
/admin/espacos/:id/setores      → SetoresPage
/admin/bloqueios                → BloqueiosPage
/admin/artistas                 → ArtistasPage
/admin/produtores               → ProdutoresPage

/catraca                        → CatracaPage (fullscreen dark, sem shell)
```
