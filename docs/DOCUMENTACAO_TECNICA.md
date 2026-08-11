# Battle Simulator — Documentação Técnica

> **Propósito:** documentação profissional de engenharia para desenvolvimento e manutenção do projeto.
> **Público-alvo:** desenvolvedores que precisam entender, dar manutenção ou evoluir o código.
> **Versão documentada:** `1.0-SNAPSHOT` — compatível com o estado atual de `main`.
> **Documentos relacionados:** [`README.md`](../README.md) (visão de usuário e apresentação), [`COMMIT_CONVENTIONS.md`](COMMIT_CONVENTIONS.md) (fluxo git), `documentacao-tecnica.pdf` / `diagrama.pdf` / `slides.pdf` (material acadêmico).

---

## Índice

1. [Visão Geral](#1-visão-geral)
2. [Stack Tecnológica e Ambiente](#2-stack-tecnológica-e-ambiente)
3. [Visão Arquitetural](#3-visão-arquitetural)
4. [Estrutura de Diretórios](#4-estrutura-de-diretórios)
5. [Estrutura de Dados: `CircularQueue`](#5-estrutura-de-dados-circularqueue)
6. [Camada de Domínio](#6-camada-de-domínio)
7. [Fábricas de Combatentes](#7-fábricas-de-combatentes)
8. [Motor de Regras: `BattleEngine`](#8-motor-de-regras-battleengine)
9. [DTOs (Camada de Payload)](#9-dtos-camada-de-payload)
10. [Camada de Apresentação (FXML/CSS/Temas)](#10-camada-de-apresentação-fxmlcsstemas)
11. [Fábricas de View](#11-fábricas-de-view)
12. [Controladores](#12-controladores)
13. [Navegação e Estado Global](#13-navegação-e-estado-global)
14. [Sequências de Jogo](#14-sequências-de-jogo)
15. [Regras de Jogo Formalizadas](#15-regras-de-jogo-formalizadas)
16. [Testes](#16-testes)
17. [Build, Execução e Validação](#17-build-execução-e-validação)
18. [Convenções de Desenvolvimento](#18-convenções-de-desenvolvimento)
19. [Pontos de Atenção e Limitações](#19-pontos-de-atenção-e-limitações)
20. [Guia de Retomada do Desenvolvimento](#20-guia-de-retomada-do-desenvolvimento)
21. [Glossário](#21-glossário)

---

## 1. Visão Geral

**Battle Simulator** é um jogo de **batalha em turnos** estilo RPG, desktop, desenvolvido em **Java 25 + JavaFX 21**, com visual pixel-art/retro (fonte *Press Start 2P*). O jogador comanda um grupo de 1 a 4 heróis contra hordas aleatórias de goblins que escalam de dificuldade a cada round vencido.

O projeto foi construído como exercício de **engenharia de software** e carrega quatro decisões estruturais que definem a arquitetura:

1. **Arquitetura MVC** com separação estrita entre domínio, controladores e interface.
2. **Camada de domínio pura** — `model` e `datastructure` **não importam JavaFX** (a suíte de testes roda sem display gráfico).
3. **Comunicação via Records imutáveis (DTOs)** — a UI nunca recebe/muta objetos do motor diretamente; recebe *snapshots*.
4. **Estrutura de dados linear implementada do zero** (`CircularQueue`) no lugar de coleções prontas do Java, usada para todas as listas do motor (turnos, heróis, inimigos).

---

## 2. Stack Tecnológica e Ambiente

Versões declaradas no [`pom.xml`](../pom.xml):

| Tecnologia | Versão | Uso |
|---|---|---|
| Java (JDK) | 25 | Compilação (`<source>/<target> = 25`) |
| JavaFX | 21.0.6 | `javafx-controls` e `javafx-fxml` |
| JUnit Jupiter | 5.12.1 | Testes (`junit-jupiter-api` + `engine`, escopo `test`) |
| Maven (wrapper) | 3.8.5 | Build sem instalação global do Maven |
| maven-surefire-plugin | 3.5.3 | Execução dos testes |
| javafx-maven-plugin | 0.0.8 | `mvn javafx:run` |

Identificação do artefato: `groupId=com.game`, `artifactId=battle-simulator`, `version=1.0-SNAPSHOT`.

### Módulo JPMS (`module-info.java`)

O projeto é um **módulo Java** (`com.game.battlesimulator`). O `module-info` é deliberadamente restritivo:

```java
module com.game.battlesimulator {
    requires javafx.controls;
    requires javafx.fxml;
    exports com.game.battlesimulator.view;
    opens com.game.battlesimulator.controller to javafx.fxml;
    opens com.game.battlesimulator.view to javafx.fxml;
}
```

- **`exports` apenas `view`** — `model`, `datastructure` e `controller` ficam internos ao módulo, reforçando o encapsulamento da camada de domínio.
- **`opens` de `controller` e `view`** para `javafx.fxml` — necessário porque o FXML carrega controladores e raízes via reflexão.
- Classe principal (usada pelo plugin): `com.game.battlesimulator/com.game.battlesimulator.view.BattleApplication`.

> **Nota:** como os testes residem no mesmo módulo (em `src/test`), eles acessam normalmente os pacotes internos e o código package-private.

---

## 3. Visão Arquitetural

### Princípios de design

| Princípio | Implementação |
|---|---|
| MVC | `model` (regras) · `controller` (orquestração) · `view` (JavaFX/FXML/CSS) |
| Domínio puro | Nenhuma importação de JavaFX fora de `view`/`controller` |
| Imutabilidade na fronteira | A UI conversa com o motor **somente** através de Records |
| Identificador único | Todo combatente tem `id` (`Hero-N` / `Enemy-N`); alvos e buscas são feitos por `id` |
| Caminho único de mutação | Estado de um combatente só muda via `takeDamage()` e `levelUp()`; **não há setters** |
| Factory Pattern | Geração de combatentes (`PlayerFactory`, `EnemyFactory`) e de componentes visuais (`*.Factory` em `view.factory`) |
| Estado global de UI isolado | `SceneManager` centraliza tema, `partySize` e troca de telas |

### Diagrama de camadas

```
┌────────────────────────────────────────────────────────────────┐
│                         view (JavaFX)                           │
│   BattleApplication · SceneManager                              │
│   view.factory: BattleMenuFactory · CombatantStatusFactory      │
│                 TurnOrderFactory · OverlayViewFactory ·         │
│                 SpriteView                                      │
│   recursos: FXML (menu/battle) · CSS · fontes · imagens         │
└───────────────────▲──────────────────────────▲──────────────────┘
                    │ Records                  │ Records
                    │ CombatantRecord[]        │ BattleStatusRecord
┌───────────────────┴──────────────────────────┴──────────────────┐
│                         controller                              │
│   MenuController (menu) · BattleController (batalha)            │
└──────────────────────────────────▲──────────────────────────────┘
                                   │ chamadas de ação
                                   │ (executeAttack / prepareNextRound / ...)
┌──────────────────────────────────┴──────────────────────────────┐
│                         model (puro)                            │
│   domain:    Combatant (abstrato) · Player · Enemy              │
│   engine:    BattleEngine                                       │
│   factory:   PlayerFactory · EnemyFactory                       │
│   payload:   CombatantRecord · BattleStatusRecord (DTOs)        │
└──────────────────────────────────▲──────────────────────────────┘
                                   │ instâncias
┌──────────────────────────────────┴──────────────────────────────┐
│                     datastructure (puro)                        │
│   CircularQueue · Node (fila circular com nó sentinela)         │
└─────────────────────────────────────────────────────────────────┘
```

### Fluxo de dados típico (turno do herói)

1. **FXML** (`battle-view.fxml`) instancia `BattleController`.
2. `BattleController.initialize()` cria o `BattleEngine` e chama `startBattle(partySize)`.
3. O usuário clica em um alvo → o controller chama `engine.executeAttack(id)`.
4. O motor executa a regra e retorna um **`BattleStatusRecord`** (snapshot + log + flags de fim de jogo).
5. O controller lê os Records, reconstrói a tela (`updateAllUI`) e dispara feedback visual (`SpriteView.showHit`).

> **Regra de ouro:** o controller **nunca** altera o estado do jogo diretamente; ele apenas lê snapshots e invoca ações do motor.

---

## 4. Estrutura de Diretórios

```
src/main/java/com/game/battlesimulator/
│
├── datastructure/
│   └── CircularQueue.java          # Fila circular encadeada com nó sentinela (head/tail/next)
│
├── controller/                     # Orquestração UI ↔ motor (usado via FXML)
│   ├── BattleController.java       # Tela de batalha: renderização, alvos, overlay, log
│   └── MenuController.java         # Menu: party size, temas, iniciar batalha
│
├── model/                          # CAMADA PURA — sem nenhuma dependência de JavaFX
│   ├── domain/
│   │   ├── Combatant.java          # Classe abstrata base: id, spriteIndex, stats, takeDamage
│   │   ├── Player.java             # Herói: levelUp() (+20 HP, +4 dano, cura total)
│   │   └── Enemy.java              # Inimigo da horda (sem comportamento extra)
│   ├── engine/
│   │   └── BattleEngine.java       # Motor de regras: turnos, dano, rounds, game over
│   ├── factory/
│   │   ├── PlayerFactory.java      # Heróis base fixa (50 HP / 10 dano)
│   │   └── EnemyFactory.java       # Hordas aleatórias com stats escalando por round
│   └── payload/
│       ├── CombatantRecord.java    # DTO imutável: snapshot de um combatente
│       └── BattleStatusRecord.java # DTO imutável: resultado de uma ação
│
└── view/                           # CAMADA JavaFX
    ├── BattleApplication.java      # Ponto de entrada (Application.main)
    ├── SceneManager.java           # Navegação, tema e partySize globais (estático)
    └── factory/
        ├── BattleMenuFactory.java      # Grade dinâmica de seleção de alvos
        ├── CombatantStatusFactory.java # Caixas de status (nome + barra de HP)
        ├── TurnOrderFactory.java       # Carrossel da ordem de turnos
        ├── OverlayViewFactory.java     # Overlay de vitória/derrota
        └── SpriteView.java             # Componente sprite + animação de dano
```

```
src/main/resources/
│
├── css/battle.css                  # Único stylesheet: componentes + temas (yellow/gb/mono)
├── fxml/
│   ├── menu-view.fxml              # Tela de menu
│   └── battle-view.fxml            # Tela de batalha
├── fonts/PressStart2P-Regular.ttf  # Fonte pixel-art (registrada via @font-face)
└── images/
    ├── hero-1.png … hero-4.png     # Sprites dos heróis (indexados por classe CSS)
    └── enemy-1.png … enemy-5.png   # Sprites dos inimigos (indexados por classe CSS)

src/test/java/com/game/battlesimulator/
├── datastructure/CircularQueueTest.java   # 10 testes
├── model/engine/BattleEngineTest.java     # 12 testes
├── model/factory/FactoryTest.java         # 5 testes
└── model/payload/RecordTest.java          # 3 testes
```

---

## 5. Estrutura de Dados: `CircularQueue`

**Arquivo:** `src/main/java/com/game/battlesimulator/datastructure/CircularQueue.java`

Fila circular **encadeada simples** com **nó cabeça (sentinela)**, implementada do zero — não usa nenhuma coleção da JCL (`List`, `LinkedList`, etc.). É a única estrutura que armazena combatentes no projeto.

### Modelo da estrutura

- **`Node`** (package-private): contém `info` (um `Combatant`) e `next`.
- **`head`**: nó sentinela — `head.info` é sempre `null`; `head.next` aponta para o **primeiro elemento** (ou para `head` quando vazia).
- **`tail`**: aponta para o **último elemento** (ou para `head` quando vazia).
- **Invariante circular:** `tail.next` sempre aponta para `head.next` (o primeiro elemento) — exceto quando vazia, caso em que `head.next == head` e `tail == head`.
- **`size`**: contador mantido em todas as operações (`getSize()` é O(1)).

### Operações e complexidade

| Operação | Comportamento | Complexidade |
|---|---|---|
| `enqueue(info)` | Insere no fim (atrás de `tail`) | O(1) |
| `dequeue()` | Remove e retorna o início; `null` se vazia | O(1) |
| `remove(info)` | Remove um combatente específico (por igualdade de referência); `false` se ausente | O(n) |
| `rotateTurn()` | Move o início para o fim (dequeue + enqueue); **no-op** se `size <= 1` | O(1) |
| `getCombatantOnIndex(i)` | Acesso posicional; `null` se `i` fora de `[0, size)` | O(n) |
| `clear()` | Esvazia a fila (reaponta `head`/`tail` e zera `size`) | O(1) |
| `isEmpty()` / `getSize()` | Estado e tamanho | O(1) |

### Detalhes de implementação relevantes

- **Primeira inserção** (`isEmpty`): `head.next = novo`, `tail = novo`, `tail.next = head.next` (fecha o círculo).
- **`dequeue` com 1 elemento:** reaponta `head.next = head` e `tail = head` (restaura o estado vazio).
- **`remove`:** percorre a partir de `head.next`; ao remover o elemento que é `tail`, atualiza `tail`; ao esvaziar a fila, restaura `head.next = head`. A comparação usa `equals` (que, como `Combatant` não sobrescreve `equals`, é **igualdade por referência**).
- **`getCombatantOnIndex`:** caminha `index` vezes a partir de `head.next`; sem verificação de laço porque o intervalo é validado antes.
- **Não é thread-safe** — o projeto é single-threaded (UI JavaFX + motor no mesmo thread de eventos).

> A `CircularQueue` é a estrutura que gerencia as três listas do motor: `turnQueue`, `playersList` e `enemiesList`.

---

## 6. Camada de Domínio

### `Combatant` (abstrata) — `model/domain/Combatant.java`

Classe base de todo ser da batalha.

| Membro | Descrição |
|---|---|
| `String id` (final) | Identificador único (`Hero-N` / `Enemy-N`) |
| `int spriteIndex` (final) | Índice 1-based do sprite (mapeia para classe CSS/imagem) |
| `name` / `currentHealth` / `maxHealth` / `attackDamage` (protected) | Estado mutável |

Contrato:

- `isAlive()` → `currentHealth > 0`.
- `takeDamage(int damage)` → `currentHealth = max(0, currentHealth - damage)`.
  - **O dano nunca leva a vida abaixo de zero** (dano excedente é descartado).
- **Sem setters** — a única forma de alterar o estado é `takeDamage()` (e `levelUp()` no `Player`).

### `Player` — `model/domain/Player.java`

```java
public static final int HEALTH_GROWTH = 20;
public static final int ATTACK_GROWTH = 4;
```

- `levelUp()`: `maxHealth += 20`, `attackDamage += 4` e **cura total** (`currentHealth = maxHealth`).
- As constantes são a **fonte única** dos deltas de level-up (usadas também pela UI via `BattleEngine.getPlayerHealthGrowth()`/`getPlayerAttackGrowth()`).

### `Enemy` — `model/domain/Enemy.java`

Herda `Combatant` sem comportamento extra — a variação de stats vem da `EnemyFactory`.

---

## 7. Fábricas de Combatentes

### `PlayerFactory` — `model/factory/PlayerFactory.java`

- Base fixa: **`baseHealth = 50`**, **`baseAttack = 10`**.
- `generatePlayers(count)` → `Player["Hero-N", "HeroN", 50, 10, spriteIndex = N]` (N de 1 a `count`).
- `generatePlayer()` → `generatePlayers(1)`.
- O crescimento **não** está aqui: vive exclusivamente em `Player.levelUp()`.

### `EnemyFactory` — `model/factory/EnemyFactory.java`

Gera hordas com **tamanho e atributos aleatórios**, escalando por round.

| Parâmetro | Valor |
|---|---|
| Tamanho da horda | aleatório entre **2 e 5** |
| Vida base | min 10, max 20 |
| Crescimento de vida/round | +15 |
| Dano base | min 2, max 5 |
| Crescimento de dano/round | +2 |

Fórmulas (`multiplier = currentRound - 1`):

```
vidaMax  = sorteio(10 + 15·(round-1) , 20 + 15·(round-1))
dano     = sorteio( 2 +  2·(round-1) ,  5 +  2·(round-1))
```

Exemplos: Round 1 → vida 10–20, dano 2–5 · Round 2 → vida 25–35, dano 4–7 · Round 3 → vida 40–50, dano 6–9.

Cada inimigo: `Enemy["Enemy-N", "InimigoN", vidaMax, dano, spriteIndex = N]`.

> **Nota:** o contador de sprite indexado (`Enemy-N` e `spriteIndex N`) depende da horda ter no máximo 5 inimigos para casar com as imagens `enemy-1..5.png`.

---

## 8. Motor de Regras: `BattleEngine`

**Arquivo:** `src/main/java/com/game/battlesimulator/model/engine/BattleEngine.java`

É o coração do jogo: mantém o estado da batalha e executa as regras. **Não depende de JavaFX** e é instanciado pelo `BattleController` (e diretamente nos testes).

### Estado interno

| Campo | Papel |
|---|---|
| `int currentRound` | Round atual (inicia em 1) |
| `int partySize` | Tamanho do grupo (default 1) |
| `CircularQueue turnQueue` | Ordem de turnos (heróis primeiro, depois inimigos) |
| `CircularQueue playersList` | Heróis vivos |
| `CircularQueue enemiesList` | Inimigos vivos do round |
| `PlayerFactory` / `EnemyFactory` | Fábricas (campos `final`) |
| `Random` | Sorteio do alvo inimigo |

### Ciclo de vida

```
startBattle(partySize)
        │  (limpa filas → gera heróis → gera horda do round 1)
        ▼
  ┌─── round ativo: herói ataca → inimigo ataca → ... ───┐
  │                                                       │
  │   todos os inimigos derrotados            todos os heróis derrotados
  │        │ vitória                                     │ derrota
  │        ▼                                             ▼
  │   prepareNextRound()                          restartEngine()
  │   (round++, levelUp dos heróis,        (round=1, refaz batalha
  │    nova horda mais forte)               mantendo o partySize)
  └────────────────────────────────────────────────────────┘
```

### API pública

| Método | Retorno / Efeito |
|---|---|
| `startBattle()` | Inicia com `partySize` atual |
| `startBattle(int playersQty)` | Define `partySize`, limpa as filas, gera heróis e horda do round atual |
| `executeAttack(String targetId)` | Atacante = `turnQueue[0]`; aplica dano no alvo; remove mortos; rotaciona turno; retorna `BattleStatusRecord` |
| `executeEnemyTurn()` | Sorteia um herói vivo e ataca via `executeAttack`; guarda caso não haja heróis |
| `prepareNextRound()` | `round++`, level-up dos heróis, nova horda (heróis ficam no início da fila) |
| `restartEngine()` | `round = 1`, recria as filas e reinicia a batalha (mantém `partySize`) |
| `getCurrentAttackerRecord()` | `CombatantRecord` do combatente na vez |
| `getPlayersRecords()` / `getEnemiesRecords()` / `getTurnOrderRecords()` | Arrays de snapshots imutáveis |
| `getPlayerHealthGrowth()` / `getPlayerAttackGrowth()` | Expõe as constantes de level-up para a UI |
| `getEnemiesQuantity()` / `getPlayersQuantity()` / `getCurrentRound()` | Estado básico |

### Regras implementadas em `executeAttack`

1. Valida atacante e alvo (`IllegalArgumentException` se inválidos/inexistentes).
2. Aplica `attacker.attackDamage` sobre o alvo (clamping em 0).
3. Se o alvo morreu: remove de **todas as filas** (`turnQueue`, `enemiesList`, `playersList`) e registra `killedTarget`.
4. **Rotação de turno:** só ocorre se ainda houver **os dois lados com combatentes** e se o **atacante ainda for o primeiro** da fila (o atacante não foi o morto). Isso garante que quem eliminou o alvo continue jogando na sequência correta, e que a batalha pare de rotacionar no momento do game over.
5. **Fim de jogo:** `enemiesList` vazia → vitória; `playersList` vazia → derrota.
6. Retorna um `BattleStatusRecord` com o estado completo pós-ação.

### Casos de borda cobertos

- Ataque com `id` desconhecido → `IllegalArgumentException` (a UI nunca dispara isso, pois só mostra alvos vivos; protegido por contrato/teste).
- `executeEnemyTurn()` com `playersList` vazia → retorna status de derrota imediato (não lança exceção).
- Rotação de turno com fila de 1 elemento → `CircularQueue.rotateTurn()` é no-op.
- Dano excedente descartado (vida nunca negativa).
- `prepareNextRound` mantém `partySize`; `restartEngine` também.

---

## 9. DTOs (Camada de Payload)

### `CombatantRecord` — `model/payload/CombatantRecord.java`

Snapshot imutável de um combatente para a interface:

```java
record CombatantRecord(
    String id,            // identificador único (usado para mirar no ataque)
    String name,          // nome exibido
    int currentHealth,    // vida atual
    int maxHealth,        // vida máxima
    boolean isPlayer,     // lado para renderização (herói vs inimigo)
    int spriteIndex       // índice 1-based do sprite (classe CSS)
)
```

- `getHealthPercentage()` → `currentHealth / maxHealth` (double); **retorna `0.0` se `maxHealth <= 0`** (evita divisão por zero).
- `isDead()` → `currentHealth <= 0`.

### `BattleStatusRecord` — `model/payload/BattleStatusRecord.java`

Resultado de uma ação do motor:

```java
record BattleStatusRecord(
    CombatantRecord[] players,      // heróis pós-ação
    CombatantRecord[] enemies,      // inimigos pós-ação
    String actionLog,               // mensagem para o log de batalha
    boolean isGameOver,             // batalha terminou?
    boolean isVictory,              // se terminou, vitória? (false = derrota)
    CombatantRecord killedTarget,   // quem morreu nesta ação (ou null)
    String hitTargetId,             // id do alvo atingido (feedback visual)
    int hitDamage                   // dano causado (feedback visual)
)
```

> A UI consome exclusivamente estes dois Records — nunca recebe os objetos `Combatant` mutáveis.

---

## 10. Camada de Apresentação (FXML/CSS/Temas)

### Telas (FXML)

**`menu-view.fxml`** — `VBox` central com: título, texto de contexto, seleção de `partySize` (botões 1–4), rótulo do grupo, seletor de paleta (swatches) e botão **Iniciar Batalha**. Controlador: `MenuController`.

**`battle-view.fxml`** — `StackPane` raiz com `VBox`:

```
┌────────────────────────────────────────────────────────────┐
│ [ carrossel de turnos (ScrollPane)          ] [Voltar Menu] │
├────────────────────────────────────────────────────────────┤
│  inimigos (sprites + caixas de status) — HBox              │
├────────────────────────────────────────────────────────────┤
│  heróis (sprites + caixas de status) — HBox                │
├────────────────────────────────────────────────────────────┤
│ [ battleLogView (ListView)      ] [ controlsContainer      │
│                                 ]  └ attackButton → alvos  │
└────────────────────────────────────────────────────────────┘
   overlayRoot (StackPane, invisible) → camada de vitória/derrota
```

Controlador: `BattleController`.

> Observação: a raiz do FXML já nasce com `theme-yellow`; o `SceneManager.switchScene` substitui o tema real no momento da carga.

### Temas e estilos (`css/battle.css`)

- **Sistema de tema por *looked-up colors*:** cada `.theme-*` (`.theme-yellow`, `.theme-gb`, `.theme-mono`) define variáveis `-fx-*` (canvas, panel, ink, accent, etc.), consumidas pelos seletores de componente.
- **Semânticos constantes (não tematizados):** barra de HP tricolor (verde `>50%`, amarelo `>20%`, vermelho ≤20%) e o vermelho de derrota.
- **Sprites por índice:** classes `player-sprite-1..4` e `enemy-sprite-1..5` referenciam as imagens via `-fx-background-image`.
- Fonte *Press Start 2P* registrada com `@font-face` e aplicada globalmente no `.root`.

---

## 11. Fábricas de View

Pacote `view.factory` — constroem componentes JavaFX a partir de Records/parâmetros, mantendo os controladores enxutos.

### `SpriteView` (estende `StackPane`)

Componente de sprite reutilizável:

- Construtor `(boolean isPlayer, int spriteIndex)` → `Region` com classe `player-sprite-N`/`enemy-sprite-N` + label de dano flutuante (oculto).
- `showHit(int damage)`: animação em paralelo de **subida+fade** do número (`-N`) por 700 ms e **tremor** horizontal do sprite (3 ciclos, 120 ms). Ignora `damage <= 0`.
- `markDead()`: aplica a classe `sprite-dead` (opacidade reduzida).

### `BattleMenuFactory`

`createTargetGrid(CombatantRecord[] enemies, Consumer<CombatantRecord> onTargetSelected)` → grade dinâmica de alvos vivos:

- Calcula `aliveCount`; se 0, retorna grade vazia.
- Layout: `maxColumns = min(aliveCount, 3)`, `rows = ceil(aliveCount / columns)`, colunas com largura proporcional.
- Cada alvo vira um botão com sprite + nome; `onAction` chama o `Consumer` passado (no controller, `executePlayerAttack`).

### `CombatantStatusFactory`

`createStatusNode(CombatantRecord c, Pos alignment, boolean textFirst)` → `VBox` com nome, barra `ProgressBar` e texto `[atual/máximo]`.

- Cor da barra por faixa de vida (`hp-bar-green/yellow/red`).
- Morto → opacidade 0.4 e prefixo `[DERROTADO]`.

### `TurnOrderFactory`

`createCarouselNodes(CombatantRecord[] turnOrder)` → `Label[]` com o título `TURNOS` + um rótulo por combatente; índice 0 recebe `carousel-item-active`, demais `carousel-item-queue`, mortos `carousel-item-dead`.

### `OverlayViewFactory`

- Enum `OverlayAction { CONTINUE, RESTART, MENU }`.
- `createOverlay(boolean victory, int round, int healthGrowth, int attackGrowth, Consumer<OverlayAction> onAction)`:
  - Vitória → título `VITÓRIA!`, textos de round/deltas, botão **Continuar** (`CONTINUE`).
  - Derrota → título `DERROTA...`, botão **Jogar Novamente** (`RESTART`) e **Voltar ao Menu** (`MENU`).

---

## 12. Controladores

### `MenuController`

- `selectedPartySize` (default 1); botões 1–4 atualizam o rótulo.
- `handleStartBattle()` → `SceneManager.setPartySize(...)` + `switchScene("/fxml/battle-view.fxml")`.
- Temas: `setTheme("theme-yellow|gb|mono")` + `SceneManager.refreshTheme()` (aplicação imediata na tela atual).

### `BattleController`

É o controlador mais denso. Responsabilidades:

| Responsabilidade | Métodos |
|---|---|
| Criar e iniciar o motor | `initialize()` → `startBattle(SceneManager.getPartySize())` + log de abertura + `updateAllUI()` |
| Renderizar estado | `updateAllUI()` → `updateEnemyRows()`, `updatePlayerRows()`, `updateTurnOrder()` |
| Turno do herói | `handleButtonClick` (atacante é player) → `showTargetSelectionGrid()` → `executePlayerAttack(target)` |
| Turno do inimigo | `handleButtonClick` (atacante é inimigo) → `executeEnemyAttack()` |
| Fim de round | `startNextRound()` → `prepareNextRound()` + reset de UI |
| Reinício | `handleGameRestart()` → `restartEngine()` + limpeza do log |
| Fim de jogo | `showOverlay(victory)` + `handleOverlayAction(OverlayAction)` |
| Feedback visual | `showHitOn(id, dano)` → localiza `SpriteView` nos mapas e anima |
| Navegação | `handleBackToMenu()` → volta ao menu |

Pontos-chave da implementação:

- **`initialize()`** roda uma vez por tela (o FXML recria o controller a cada `switchScene`), então cada nova tela de batalha tem um `BattleEngine` novo.
- O painel de controles alterna entre dois modos: **grade de alvos** (herói) e **botão "Próximo Turno"** (inimigo), trocados via `controlsContainer.getChildren().clear()` + reconstrução.
- Overlay: `overlayRoot` é um `StackPane` inicialmente `visible=false`; o overlay é adicionado e ocultado sob demanda.
- `showHitOn` ignora `damage <= 0` e ids ausentes (defensivo).

---

## 13. Navegação e Estado Global

### `BattleApplication`

Ponto de entrada JavaFX:

1. `SceneManager.init(stage)`
2. `switchScene("/fxml/menu-view.fxml")`
3. `stage.show()`

### `SceneManager` (final, utilitário estático)

| Membro | Papel |
|---|---|
| `partySize` (static, default 1) | Persistido entre telas; definido no menu, lido no início da batalha |
| `theme` (static, default `theme-yellow`) | Tema ativo, aplicado a cada troca de tela |
| `init(Stage)` / `switchScene(String)` | Troca de tela (recria `Scene` 800×600 na primeira vez; depois só `setRoot`) |
| `applyTheme(root)` | Remove classes `theme-*` e aplica a atual |
| `refreshTheme()` | Reaplica o tema na tela corrente (troca de paleta no menu) |

> **Convenção de navegação:** a troca de tela recarrega o FXML, criando um novo controller. Tudo que precisa sobreviver entre telas (partySize, tema) vive nos estáticos do `SceneManager`.

---

## 14. Sequências de Jogo

### S1. Início da batalha

```
Usuário define partySize e tema → MenuController.handleStartBattle()
  → SceneManager.setPartySize(n); switchScene(battle-view.fxml)
  → BattleController.initialize(): engine.startBattle(n)
      → limpa filas → gera n heróis (50/10) → gera horda do round 1 (2–5 goblins)
      → turnQueue = [Hero-1..Hero-n, Enemy-1..Enemy-k]
      → renderiza UI + log de abertura
```

### S2. Turno do herói (ataque com alvo)

```
currentAttacker.isPlayer() → showTargetSelectionGrid() (grade com inimigos vivos)
  → click no alvo → executePlayerAttack(targetRecord)
    → engine.executeAttack(target.id())
      → turnQueue[0] ataca → takeDamage(dano)
      → se morreu: remove das filas, actionLog = "X Foi derrotado!!"
      → se os dois lados seguem vivos e atacante ainda é o head: rotateTurn()
      → determina gameOver/vitória
    → BattleStatusRecord retornado
  → log + updateAllUI() + showHitOn(id, dano) (ou overlay de vitória)
```

### S3. Turno do inimigo

```
currentAttacker não é player → executeEnemyAttack()
  → engine.executeEnemyTurn(): sorteia índice aleatório em playersList → executeAttack(id do herói)
  → mesmo fluxo de dano/rotação do S2 (overlay de derrota se playersList vazia)
```

### S4. Vitória (último inimigo morre)

```
enemiesList vazia → status.isGameOver && isVictory
  → log "Vitória!..." → overlay VITÓRIA! (round, +20 HP, +4 dano)
  → CONTINUE → startNextRound() → engine.prepareNextRound()
      → round++ → levelUp() de todos os heróis (cura total) → nova horda mais forte
      → turnQueue = [heróis..., inimigos...] (heróis jogam primeiro)
```

### S5. Derrota (último herói morre)

```
playersList vazia → status.isGameOver && !isVictory
  → log de derrota → overlay DERROTA...
  → RESTART → engine.restartEngine() (round 1, mesmo partySize, log limpo)
  → MENU → switchScene(menu)
```

### S6. Voltar ao menu durante a batalha

```
handleBackToMenu → switchScene(menu-view.fxml) → batalha atual é descartada
  (novo controller/engine quando iniciar de novo)
```

---

## 15. Regras de Jogo Formalizadas

### Atributos base (Round 1)

| Combatente | Vida | Dano | Progressão |
|---|---|---|---|
| Herói | 50 | 10 | `levelUp()`: +20 HP, +4 dano, cura total por round vencido |
| Goblin | 10–20 | 2–5 | a cada round: +15 na faixa de vida, +2 na faixa de dano |

### Escalonamento (por round `r`)

- Herói após vencer `k` rounds: `maxHealth = 50 + 20k`, `attackDamage = 10 + 4k`.
- Goblin no round `r`: `vida ∈ [10 + 15(r−1), 20 + 15(r−1)]`, `dano ∈ [2 + 2(r−1), 5 + 2(r−1)]`.

### Condições de término

| Condição | Resultado |
|---|---|
| `enemiesList` vazia | Vitória → CONTINUE (próximo round) |
| `playersList` vazia | Derrota → RESTART ou MENU |

### Regras transversais

- Vida nunca abaixo de 0 (dano excedente é perdido).
- Ao morrer, o combatente sai de **todas** as listas na mesma ação.
- Ordem de turnos: heróis sempre iniciam o round (fila começa com os heróis).
- IA inimiga: ataque a um herói vivo **aleatório**.

---

## 16. Testes

Suíte **JUnit 5** (30 testes) **100% desacoplada do JavaFX** — roda com `mvn test` sem display gráfico.

| Classe | Qtd. | Cobertura |
|---|---|---|
| `CircularQueueTest` | 10 | Ordem FIFO, `dequeue` em fila vazia (`null`), transições de `isEmpty`, rotação de turno (1 e N elementos), remoção (primeiro/meio/último), remoção que esvazia, remoção de ausente (`false`), `clear`, limites de `getCombatantOnIndex` |
| `BattleEngineTest` | 12 | Morte remove o combatente de todas as filas, vitória ao matar o último inimigo, derrota ao matar o último herói (e chamada após a derrota), `prepareNextRound` (round 2, 70/70 de HP, herói começa), ataque por `id`, party de 3, `restartEngine` (round 1 + party mantido), id inválido lança exceção, round inicial 1, fila de turnos completa começando com player, rotação pós-ataque, exposição das constantes de level-up |
| `FactoryTest` | 5 | Tamanho da horda 2–5, stats por round (1 e 2), `levelUp` (deltas + cura total), base fixa do `PlayerFactory`, `generatePlayers(count)` |
| `RecordTest` | 3 | `getHealthPercentage`, guarda de `maxHealth = 0`, `isDead` |

Comando: `.\mvnw.cmd test` (Windows) / `./mvnw test` (Linux/macOS).

---

## 17. Build, Execução e Validação

Pré-requisito: **JDK 25** com `JAVA_HOME` configurado. O Maven não precisa ser instalado (wrapper).

| Comando | Ação |
|---|---|
| `.\mvnw.cmd javafx:run` (Windows) / `./mvnw javafx:run` | Executa o jogo |
| `.\mvnw.cmd test` / `./mvnw test` | Roda a suíte de testes |
| `.\mvnw.cmd -DskipTests compile` / `./mvnw -DskipTests compile` | Apenas compila |

Gate de qualidade definido nas convenções: `compile` + `test` sempre; `javafx:run` (smoke manual) quando a mudança tocar em `view`/FXML/CSS.

---

## 18. Convenções de Desenvolvimento

Resumo do fluxo (detalhes em [`docs/COMMIT_CONVENTIONS.md`](COMMIT_CONVENTIONS.md)):

- **GitHub Flow:** branch curta por tarefa → PR → review → **squash-merge** → deletar branch.
- **Conventional Commits:** `feat:` `fix:` `refactor:` `test:` `docs:` `chore:` — descrição no imperativo, minúscula, sem ponto final. Um commit = uma preocupação.
- **Gate obrigatório antes do push:** `mvn -q -DskipTests compile` + `mvn -q test` (+ `javafx:run` quando UI).
- Nunca commitar segredos, `.env`, `target/`, `.idea/`.

---

## 19. Pontos de Atenção e Limitações

Lista honesta de comportamentos conhecidos e armadilhas ao mexer no código:

1. **Identidade de combatente por referência:** `CircularQueue.remove` e `getCombatantOnIndex` não dependem de `equals/hashCode` customizados — se algum dia houver cópias/equivalências de objetos, a remoção pode falhar.
2. **Dano excedente é perdido:** `takeDamage` limita em 0; um hit de 20 em vida 5 "desperdiça" 15.
3. **IDs regenerados por batalha/round:** não há persistência; `restartEngine` recria tudo com os mesmos padrões de nome.
4. **Sem save/load** e sem configuração externa de balanceamento (valores hardcoded nas fábricas e em `Player`).
5. **IA inimiga ingênua:** alvo 100% aleatório, sem foco, sem defesa/cura.
6. **`playersLevelUp` faz cast para `Player`:** é seguro porque `playersList` só contém `Player`, mas quebrará se a lista admitir outro subtipo.
7. **Motor não é thread-safe** e `SceneManager` guarda estado estático (single-threaded por projeto).
8. **Raiz do FXML com `theme-yellow` fixo:** o tema real é aplicado em `switchScene`; editar o FXML sem passar pelo `SceneManager` pode deixar o tema errado.
9. **Log de batalha ilimitado** durante um round longo (ListView sem limite de itens).
10. **Janela fixa 800×600** e suporte a um único stylesheet (`battle.css`).

---

## 20. Guia de Retomada do Desenvolvimento

"Onde mexer para mudar X":

| Quero mudar… | Arquivo(s)-alvo |
|---|---|
| Regras de atributos/scaling de inimigos | `EnemyFactory.java`, `FactoryTest.java` |
| Level-up / cura dos heróis | `Player.java` (`HEALTH_GROWTH`, `ATTACK_GROWTH`, `levelUp`) |
| Regras de dano/morte/rotação | `Combatant.takeDamage`, `BattleEngine.executeAttack` |
| Ordem de turnos / novo round | `BattleEngine.prepareNextRound`, `CircularQueue.rotateTurn` |
| Comportamento da fila | `CircularQueue.java` (+ `CircularQueueTest`) |
| DTOs da fronteira | `model/payload/*.java` |
| Layout da tela de batalha | `battle-view.fxml`, `BattleController` |
| Layout do menu | `menu-view.fxml`, `MenuController` |
| Visual/temas/cores | `css/battle.css` (temas `-fx-*`, swatches) |
| Sprites | `resources/images/*.png` + classes `*-sprite-N` no CSS |
| Animações | `SpriteView.showHit` |
| Navegação entre telas / estado global | `SceneManager`, `BattleApplication` |
| Novas ações/status de combate | `Combatant`/`BattleEngine` + novo campo em `BattleStatusRecord` + render no `BattleController` |

Checklist ao retomar:

1. `git pull` em `main` e rode `.\mvnw.cmd test` (30 testes verdes).
2. Leia o `README.md` (regras do jogo) e este documento (como o código funciona).
3. Para mudanças em regras, escreva/ajuste testes em `model/` antes de tocar a UI.
4. Siga o gate de validação (compile + test + smoke de UI) antes do push.

Ideias de evolução (não priorizadas): ações extras por turno (itens/magia), IA inimiga estratégica, efeitos de status, save/load, configuração externa de balanceamento (JSON), testes de UI com TestFX, internacionalização do log, injeção de dependência no `BattleController` para testabilidade.

---

## 21. Glossário

| Termo | Definição |
|---|---|
| **Round** | Ciclo completo até todos os inimigos (ou heróis) serem derrotados; a dificuldade escala a cada round vencido |
| **Horda** | Grupo de inimigos gerados no início de cada round (2–5 goblins) |
| **turnQueue** | `CircularQueue` com a ordem de turnos (herói no índice 0 ataca) |
| **CombatantRecord** | DTO imutável que representa um combatente para a UI |
| **BattleStatusRecord** | DTO imutável com o resultado de uma ação do motor |
| **Sentinela (nó cabeça)** | Nó dummy no início da `CircularQueue` que simplifica as operações de borda |
| **Snapshots** | Visões imutáveis (Records) geradas pelo motor — a UI nunca muta o estado real |
| **levelUp()** | Progressão do herói: +20 HP, +4 dano e cura total ao vencer um round |
| **JPMS** | Sistema de módulos do Java; usado aqui para encapsular a camada de domínio |
