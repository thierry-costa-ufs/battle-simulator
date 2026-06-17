# ⚔️ Battle Simulator

Um simulador de batalhas em turnos RPG desenvolvido em **Java** e **JavaFX**. O projeto simula o combate de um herói contra hordas dinâmicas de goblins que escalam de dificuldade a cada round concluído. 

Este projeto foi desenvolvido com foco em boas práticas de programação, separação clara de responsabilidades (MVC), imutabilidade de dados na camada de apresentação e implementação de estruturas de dados lineares customizadas.

---

## 🚀 Funcionalidades

* **Combate em Turnos Dinâmico:** Sistema de iniciativa controlado por uma fila de turnos compartilhada entre jogadores e inimigos.
* **Hordas Aleatórias:** A cada round, uma nova horda contendo entre 2 e 5 inimigos é gerada com atributos (vida e dano) escaláveis.
* **Sistema de Progressão (Level Up):** Ao derrotar uma horda, o herói sobe de nível, recupera sua vida e aumenta seus atributos de combate para o próximo round.
* **Interface Gráfica Responsiva (JavaFX):** Painel de ações que se adapta dinamicamente à quantidade de inimigos vivos, distribuindo os botões de alvos de forma proporcional e fluida na tela.
* **Log de Batalha em Tempo Real:** Histórico textual detalhando cada ação realizada, ataques desferidos, danos causados e baixas em combate.

---

## 🧠 Arquitetura e Engenharia de Software

O projeto foi estruturado seguindo padrões de projeto de mercado para garantir manutenibilidade e baixo acoplamento:

### 1. Estrutura de Dados Customizada (`CircularQueue`)
Diferente de coleções prontas do Java, o motor do jogo utiliza uma **Fila Circular Encabeçada (com nó cabeça)** desenvolvida do zero para gerenciar:
* A ordem de turnos ativa (`turnQueue`).
* A lista de jogadores vivos (`playersList`).
* A lista de inimigos ativos no round (`enemiesList`).

A rotação de turnos ocorre eficientemente através do reaproveitamento de ponteiros da própria estrutura encadeada.

### 2. Camada de Apresentação Imutável (Records / DTOs)
Para evitar que a interface gráfica altere diretamente o estado das entidades do motor de jogo, a comunicação entre a `BattleEngine` e o `BattleController` é feita estritamente através de **Java Records** (`CombatantRecord` e `BattleStatusRecord`), garantindo a imutabilidade dos dados trafegados.

### 3. Padrões de Projeto Utilizados
* **Model-View-Controller (MVC):** Separação estrita entre as entidades de domínio (`Combatant`, `Player`, `Enemy`), o controlador da interface (`BattleController`) e a renderização visual (`battle-view.fxml` / `battle.css`).
* **Factory Pattern:** Isolamento da lógica de criação de componentes visuais complexos (`BattleMenuFactory`, `CombatantStatusFactory`, `TurnOrderFactory`) e geração aleatória de combatentes (`PlayerFactory`, `EnemyFactory`).

---

## 🛠️ Tecnologias Utilizadas

* **Java 17** (ou superior)
* **JavaFX 8** (ou superior) para a interface gráfica
* **FXML & CSS** para estruturação e estilização visual baseada em pixel-art/retro

---

## 📁 Estrutura do Projeto

```text
src/main/java/com/game/battlesimulator/
│
├── datastructure/
│   └── CircularQueue.java         # Fila Circular customizada com Nó Cabeça
│
├── controller/
│   └── BattleController.java      # Controlador principal da interface e fluxo visual
│
├── model/
│   ├── domain/
│   │   ├── Combatant.java         # Classe abstrata base de combatentes
│   │   ├── Player.java            # Entidade do herói com lógica de Level Up
│   │   └── Enemy.java             # Entidade dos monstros da horda
│   │
│   ├── engine/
│   │   └── BattleEngine.java      # Motor principal das regras de negócio do combate
│   │
│   ├── factory/
│   │   ├── BattleMenuFactory.java # Construtor dinâmico do Grid de alvos
│   │   ├── CombatantStatusFactory.java # Construtor das barras de vida e status
│   │   ├── TurnOrderFactory.java  # Construtor do carrossel visual de turnos
│   │   ├── PlayerFactory.java     # Gerador de atributos do herói por round
│   │   └── EnemyFactory.java      # Gerador de hordas aleatórias e escaláveis
│   │
│   └── payload/
│       ├── CombatantRecord.java   # Snapshot imutável de um combatente
│       └── BattleStatusRecord.java# Snapshot imutável do resultado de uma ação
│
└── view/
    └── BattleApplication.java     # Classe de inicialização do JavaFX (Main)
```

## 🔧 Como Executar o Projeto

### Pré-requisitos
1. Possuir o **JDK 17** ou superior instalado.
2. Certifique-se de ter o **JavaFX SDK** configurado em seu ambiente/IDE (caso não esteja usando um gerenciador de dependências como Maven/Gradle).

### Passos para Execução
1. Clone o repositório em sua máquina:
   ```bash
   git clone [https://github.com/thierry-costa-ufs/battle-simulator]

2. Abra o projeto na sua IDE de preferência (IntelliJ IDEA, Eclipse ou VS Code).

3. Certifique-se de que os arquivos de recursos (battle-view.fxml, battle.css e as imagens hero.png / enemy.png) estão configurados corretamente nas pastas de resources.

4. Execute a classe BattleApplication.java.

### 🕹️ Como Jogar
Interface Inicial: Ao iniciar, a interface exibirá a fila de turnos no topo, o herói no canto inferior direito e a horda de inimigos no topo esquerdo.

Turno do Herói: O botão principal se transformará em uma grade de alvos dinâmica contendo os nomes dos inimigos vivos. Clique no botão correspondente ao inimigo que deseja atacar.

Turno do Inimigo: O botão se transformará em "Próximo Turno". A IA escolherá o jogador como alvo de forma automática e aleatória para desferir o contra-ataque.

Vitória e Progressão: O round é vencido quando todos os goblins da horda atual forem derrotados. O herói subirá de nível e uma nova horda mais forte surgirá.

Fim de Jogo: Caso o herói fique sem vida, o jogo exibirá a opção de reiniciar a jornada de volta do Round 1.
