# Convenções de Commit e Gitflow

Guia para manter histórico limpo e fluxo de trabalho uniforme entre os integrantes do projeto.

## Workflow (GitHub Flow)

- Trabalho direto em **`main`** para mudanças triviais; mudanças reais em **branch de curta duração**.
- Toda branch sai de `main` atualizada e tem **um único objetivo**.
- Fluxo por tarefa:

```text
git checkout -b fix/engine-crash        # branch de uma preocupação
git commit -m "fix: guarda lista de jogadores vazia"   # commits convencionais
git push origin fix/engine-crash
# abrir PR -> review -> squash-merge -> deletar branch
```

- **Squash-merge** no PR: `main` recebe 1 commit por tarefa.
- Branch deletada logo após o merge. Branches mortas no remoto são removidas (`git push origin --delete <branch>`).
- Nunca commitar direto em `main` sem revisão quando houver >1 pessoa mexendo na mesma área.

## Conventional Commits

Formato: `<tipo>: <descrição>`

| Tipo | Uso |
|---|---|
| `feat:` | Nova funcionalidade visível |
| `fix:` | Correção de bug |
| `refactor:` | Mudança sem alterar comportamento |
| `test:` | Testes novos ou ajustes de teste |
| `docs:` | Documentação apenas |
| `chore:` | Tarefas de manutenção, build, config |

Regras:

- Descrição no imperativo, minúscula, sem ponto final: `feat: adiciona tela de vitória`.
- **Um commit = uma preocupação.** Separar mudanças não relacionadas em commits/branches distintos.
- Tema único: um bug ou um refactor por branch. Não misturar `fix` com `refactor` na mesma branch.
- Merge (squash) via UI com mensagem convencional final.

## Gate de Validação (obrigatório antes do push)

- `mvn -q -DskipTests compile` — compila sem erros.
- `mvn -q test` — suíte verde.
- `mvn -q javafx:run` — smoke test manual (UI) quando a mudança tocar em view/fxml/css.

## Sempre

- Não commitar segredos, `.env`, `target/`, `.idea/`.
- Histórico `main` linear via squash.
- Branches curtas (horas/dias, não semanas).
