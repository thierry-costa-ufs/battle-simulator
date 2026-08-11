# ⚔️ Battle Simulator

Um simulador de batalhas em turnos RPG desenvolvido em **Java 25** e **JavaFX 21**. Você comanda um grupo de 1 a 4 heróis contra hordas dinâmicas de goblins que escalam de dificuldade a cada round concluído, em um visual **pixel-art/retro** estilo Game Boy com fonte Press Start 2P.

O projeto foi construído com foco em engenharia de software: arquitetura **MVC** com camada de domínio pura (sem dependência de JavaFX), comunicação com a interface exclusivamente via **Records imutáveis (DTOs)** e uma **estrutura de dados linear customizada** (`CircularQueue`) no lugar de coleções prontas do Java.

---

## 📋 Índice

- [Funcionalidades](#-funcionalidades)
- [Mecânicas do Combate](#-mecânicas-do-combate)
- [Arquitetura e Engenharia](#-arquitetura-e-engenharia-de-software)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Testes](#-testes)
- [Como Executar](#-como-executar-o-projeto)
- [Como Jogar](#-como-jogar)
- [Fluxo de Desenvolvimento](#-fluxo-de-desenvolvimento)

---

## 🚀 Funcionalidades

* **Tela de Menu:** seleção do tamanho do grupo (1 a 4 heróis), troca de paleta de cores e botão de iniciar batalha.
* **Paletas de Tema:** três visualizações trocáveis em tempo real — Amarelo (estilo Pokémon indie), Game Boy e Monocromático.
* **Combate em Turnos Dinâmico:** uma fila de iniciativa decide quem ataca, com carrossel visual mostrando a ordem dos turnos.
* **Hordas Aleatórias:** 2 a 5 goblins por round, com vida e dano escalando a cada round vencido.
* **Sistema de Progressão (Level Up):** ao vencer um round, cada herói ganha `+20` de vida, `+4` de dano e recupera toda a vida.
* **Seleção de Alvos Dinâmica:** o painel de ações se adapta à quantidade de inimigos vivos, distribuindo os botões de alvo em uma grade proporcional.
* **IA Inimiga:** a cada turno inimigo, a horda escolhe um herói vivo de forma aleatória para atacar.
* **Log de Batalha em Tempo Real:** histórico textual de ataques, danos causados e baixas em combate.
* **Overlay de Vitória/Derrota:** ações de continuar (próximo round), jogar novamente (Round 1) ou voltar ao menu.
* **Feedback Visual:** sprites individuais para cada herói e inimigo, barra de vida tricolor (verde/amarelo/vermelho), animação de dano (número flutuante + tremor do sprite) e opacidade nos derrotados.

---

## ⚔️ Mecânicas do Combate

### Turnos

* Uma fila de iniciativa reúne todos os combatentes da batalha: primeiro os heróis, depois os inimigos.
* O combatente no **início da fila** é quem ataca no turno.
* Herói ataca → escolhe um alvo entre os inimigos vivos na grade.
* Inimigo ataca → escolhe um herói vivo de forma aleatória.
* Após cada ataque, o atacante vai para o fim da fila e o próximo combatente assume a vez.

### Dano e Morte

* Cada ataque reduz a vida do alvo, que **nunca fica abaixo de zero**.
* Quem chega a 0 de vida é derrotado na hora e sai da batalha.

### Atributos Base

| Combatente | Vida | Dano |
|---|---|---|
| Herói (Round 1) | 50 | 10 |
| Goblin (Round 1) | 10–20 | 2–5 |

### Progressão

* **Herói:** a cada round vencido, todos os heróis sobem de nível — ganham `+20` de vida, `+4` de dano e recuperam toda a vida.
* **Inimigo:** a cada round, a horda fica mais forte — os intervalos de vida e dano dos goblins aumentam (ex.: Round 2 → vida 25–35, dano 4–7).

### Condições de Término

| Condição | Resultado |
|---|---|
| Todos os inimigos derrotados | **Vitória** → opção de continuar para o próximo round |
| Todos os heróis derrotados | **Derrota** → opções de jogar novamente ou voltar ao menu |

---

## 🧠 Arquitetura e Engenharia de Software

### 1. Estrutura de Dados Customizada (`CircularQueue`)

Uma **Fila Circular Encadeada com nó cabeça (sentinela)** implementada do zero, sem usar coleções prontas do Java. Ela gerencia as três estruturas do motor:

* `turnQueue` — ordem de turnos ativa;
* `playersList` — heróis vivos;
* `enemiesList` — inimigos vivos do round.

Operações suportadas:

| Operação | Descrição |
|---|---|
| `enqueue` | Insere no fim da fila |
| `dequeue` | Remove e retorna o início (ou `null` se vazia) |
| `remove` | Remove um combatente específico pelo objeto (usado nas mortes) |
| `rotateTurn` | Move o início para o fim (rotação de turno, `O(1)`) |
| `getCombatantOnIndex` | Acesso posicional (usado em buscas e snapshots) |
| `clear` | Esvazia a fila (início de round) |
| `isEmpty` / `getSize` | Estado e tamanho |

### 2. Camada de Apresentação Imutável (Records / DTOs)

A comunicação entre `BattleEngine` e os controladores é feita estritamente por **Java Records**, impedindo que a interface altere o estado do motor:

* `CombatantRecord` — snapshot imutável de um combatente: `id`, `name`, `currentHealth`, `maxHealth`, `isPlayer`, `spriteIndex`; helpers `getHealthPercentage()` e `isDead()`.
* `BattleStatusRecord` — resultado de uma ação: listas atualizadas, `actionLog`, `isGameOver`, `isVictory`, alvo derrotado (`killedTarget`), alvo atingido (`hitTargetId`) e dano (`hitDamage`).

### 3. Separação de Camadas

```
┌──────────────────────────────────────────────────────────────┐
│                          view (JavaFX)                        │
│   BattleApplication · SceneManager · view.factory (FXML/CSS)  │
└───────────────▲───────────────────────────────▲──────────────┘
                │ Records imutáveis             │ Records imutáveis
                │ (CombatantRecord /            │ (BattleStatusRecord)
                │  BattleStatusRecord)          │
┌───────────────┴───────────────────────────────┴──────────────┐
│                        controller                             │
│            BattleController · MenuController                  │
└───────────────────────────────▲──────────────────────────────┘
                                │
┌───────────────────────────────┴──────────────────────────────┐
│                        model (puro, sem JavaFX)               │
│   domain (Combatant/Player/Enemy) · engine · factory · payload │
└──────────────────────────────────────────────────────────────┘
```

* **`model`** — camada **pura**, sem nenhuma importação de JavaFX. Contém as entidades de domínio, o motor de regras (`BattleEngine`), as fábricas de geração e os DTOs.
* **`controller`** — orquestra a interação entre UI e motor (lê o estado via records, dispara ações).
* **`view`** — tudo que é JavaFX: aplicação, navegação de telas e fábricas de componentes visuais.

### 4. Padrões de Projeto

* **Model-View-Controller (MVC):** separação estrita entre domínio, controladores e renderização (FXML/CSS).
* **Factory Pattern:** isolamento da criação de componentes visuais (`BattleMenuFactory`, `CombatantStatusFactory`, `TurnOrderFactory`, `OverlayViewFactory`, `SpriteView`) e da geração de combatentes (`PlayerFactory`, `EnemyFactory`).
* **Identificador único (`id`):** cada combatente carrega um `id` (`Hero-N` / `Enemy-N`), usado para alvos e buscas — elimina comparações textuais manuais.
* **Caminho único de mutação:** o estado de um combatente só muda via `takeDamage()` e `levelUp()`.

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão |
|---|---|
| Java | 25 |
| JavaFX | 21.0.6 |
| JUnit (Jupiter) | 5.12.1 |
| Maven (wrapper `mvnw`) | 3.8.5 |
| maven-surefire-plugin | 3.5.3 |
| javafx-maven-plugin | 0.0.8 |

Complementos: **FXML & CSS** para estruturação e estilização (estilo pixel-art/retro), **JPMS** (`module-info.java`) e fonte **Press Start 2P**.

---

## 📁 Estrutura do Projeto

```text
src/main/java/com/game/battlesimulator/
│
├── datastructure/
│   └── CircularQueue.java                # Fila Circular customizada (nó cabeça)
│
├── controller/
│   ├── BattleController.java             # Controlador da tela de batalha
│   └── MenuController.java               # Controlador do menu (party size e temas)
│
├── model/                                # Camada pura (sem JavaFX)
│   ├── domain/
│   │   ├── Combatant.java                # Base abstrata dos combatentes
│   │   ├── Player.java                   # Herói: levelUp (+20 HP, +4 dano, cura)
│   │   └── Enemy.java                    # Inimigo da horda
│   ├── engine/
│   │   └── BattleEngine.java             # Motor das regras de combate
│   ├── factory/
│   │   ├── PlayerFactory.java            # Heróis base (50 HP / 10 dano)
│   │   └── EnemyFactory.java             # Hordas aleatórias e escaláveis
│   └── payload/
│       ├── CombatantRecord.java          # Snapshot imutável de um combatente
│       └── BattleStatusRecord.java       # Snapshot imutável de uma ação
│
└── view/
    ├── BattleApplication.java            # Ponto de entrada JavaFX (Main)
    ├── SceneManager.java                 # Navegação entre telas + tema (800×600)
    └── factory/
        ├── BattleMenuFactory.java        # Grade dinâmica de alvos
        ├── CombatantStatusFactory.java   # Caixas de status e barras de HP
        ├── TurnOrderFactory.java         # Carrossel da ordem de turnos
        ├── OverlayViewFactory.java       # Overlay de vitória/derrota
        └── SpriteView.java               # Sprite com animação de dano
```

```text
src/main/resources/
│
├── css/battle.css                        # Estilos e temas (yellow / gb / mono)
├── fxml/
│   ├── menu-view.fxml                    # Tela de menu
│   └── battle-view.fxml                  # Tela de batalha
├── fonts/
│   └── PressStart2P-Regular.ttf          # Fonte pixel-art
└── images/
    ├── hero-1.png … hero-4.png           # Sprites dos heróis (por índice)
    └── enemy-1.png … enemy-5.png         # Sprites dos inimigos (por índice)

src/test/java/com/game/battlesimulator/
├── datastructure/CircularQueueTest.java
├── model/engine/BattleEngineTest.java
├── model/factory/FactoryTest.java
└── model/payload/RecordTest.java
```

---

## 🧪 Testes

Suíte **JUnit 5** totalmente **desacoplada do JavaFX** (roda sem display gráfico), com **30 testes** distribuídos em 4 classes:

| Classe | Qtd. | Cobre |
|---|---|---|
| `CircularQueueTest` | 10 | Ordem FIFO, `dequeue` em fila vazia, `isEmpty`, rotação de turno (1 e N elementos), remoções (primeiro/meio/último), limites de índice, `clear` |
| `BattleEngineTest` | 12 | Remoção em caso de morte, vitória/derrota, `prepareNextRound`, ataque por `id`, id inválido lançando exceção, `restartEngine` (round e party size), fila de turnos, rotação pós-ataque, exposição dos deltas de level-up |
| `FactoryTest` | 5 | Tamanho da horda (2–5), faixas de atributos por round, `levelUp`, atributos base do herói, `generatePlayers(count)` |
| `RecordTest` | 3 | Porcentagem de HP, divisor zero (`maxHealth` 0), flag `isDead` |

---

## 🔧 Como Executar o Projeto

### Pré-requisitos

1. **JDK 25** instalado e `JAVA_HOME` configurado no ambiente.
2. O Maven não precisa ser instalado — o projeto usa o **wrapper** (`mvnw` / `mvnw.cmd`).

### Comandos

| Comando | Ação |
|---|---|
| `./mvnw javafx:run` (Linux/macOS) ou `.\mvnw.cmd javafx:run` (Windows) | Executa a aplicação |
| `./mvnw test` / `.\mvnw.cmd test` | Roda a suíte de testes |
| `./mvnw -DskipTests compile` / `.\mvnw.cmd -DskipTests compile` | Apenas compila |

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/thierry-costa-ufs/battle-simulator
   ```
2. Abra na sua IDE de preferência (IntelliJ IDEA, Eclipse ou VS Code).
3. Execute a classe `BattleApplication.java` (ou o comando `javafx:run`).

---

## 🕹️ Como Jogar

1. **Menu:** escolha o número de heróis (1 a 4) e a paleta de cores. Clique em **Iniciar Batalha**.
2. **Batalha:** o topo mostra a fila de turnos (carrossel); inimigos no topo; heróis embaixo; log de batalha e painel de ações na base.
3. **Turno do Herói:** o painel de ações vira uma grade com os inimigos vivos — clique no alvo para atacar.
4. **Turno do Inimigo:** o botão vira **Próximo Turno** — a IA escolhe um herói aleatório e contra-ataca.
5. **Vitória:** derrote toda a horda → overlay **Continuar** → heróis sobem de nível e uma horda mais forte se aproxima.
6. **Derrota:** se todos os heróis caírem → overlay **Jogar Novamente** (volta ao Round 1 mantendo o tamanho do grupo) ou **Voltar ao Menu**.
7. Durante a batalha, **Voltar ao Menu** descarta a batalha atual e retorna à tela inicial.

---

## 🤝 Fluxo de Desenvolvimento

Padrões de commit, fluxo de branches (GitHub Flow), squash-merge e gate de validação obrigatório (`compile` / `test` / `javafx:run`) estão documentados em [`docs/COMMIT_CONVENTIONS.md`](docs/COMMIT_CONVENTIONS.md). A **documentação técnica profissional** (arquitetura, motor de regras, estrutura de dados e guia de retomada do desenvolvimento) está em [`docs/DOCUMENTACAO_TECNICA.md`](docs/DOCUMENTACAO_TECNICA.md). A documentação acadêmica (diagrama de classes, documentação técnica e slides) fica em [`docs/`](docs/).
