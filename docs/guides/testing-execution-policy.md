# Política de execução de testes — ambiente, recursos e confiabilidade

Complementa [`local-runtime-authorization.md`](./local-runtime-authorization.md) (gate de owner para infra) e `.cursor/rules/quality-gates.mdc` (CI/Checkstyle/JaCoCo/Sonar). Este guia cobre **como** inspecionar, planejar e executar testes de forma coesa, consciente de recursos e reprodutível — não substitui os gates de autorização, que continuam valendo (`ok` / `ok infra` / commit explícito).

Em caso de conflito, **os gates em `local-runtime-authorization.md` e `quality-gates.mdc` vencem** sobre este guia.

## 1. Objetivo

Toda execução de testes, builds, verificações de segurança e testes de integração deve ser:

- **Coesa** — respeita arquitetura, contratos e dependências reais do projeto.
- **Coerente** — cenários, dados, mocks e critérios de aprovação correspondem ao comportamento esperado.
- **Consciente** — considera ambiente, recursos disponíveis, serviços ativos e restrições da máquina (MacBook compartilhado — ver [`local-runtime-authorization.md`](./local-runtime-authorization.md)).
- **Antecipatória** — riscos de indisponibilidade, conflito de porta, concorrência e falhas previsíveis são identificados antes da execução.
- **Reproduzível** — resultados relevantes podem ser repetidos em condições equivalentes.
- **Segura** — nenhum serviço, processo, arquivo, volume ou dado externo é removido/alterado sem identificação inequívoca e autorização.
- **Eficiente** — executa somente o necessário em cada etapa, ampliando cobertura progressivamente.
- **Confiável** — distingue falha real de código de falha de infraestrutura/ambiente/configuração.

Não declarar execução 100% confiável apenas porque os comandos terminaram com código zero. Demonstrar com evidências, critérios explícitos e limitações conhecidas.

## 2. Reconhecimento prévio do ambiente (antes de iniciar serviços)

Mapeia para os **passos 1–2 (inspect/audit)** do gate de runtime local.

**Máquina:** SO/arquitetura, versão do JDK, Maven/Docker/Compose, memória disponível/pressão/swap, CPU e carga, espaço em disco, estado do Docker Engine (`docker ps`, `docker images`, `docker volume ls`, `docker network ls`), processos conflitantes, limites de recursos de container.

Usar apenas ferramentas nativas do SO identificado (não presumir comandos exclusivos de Linux no macOS). Nunca expor segredos, tokens, senhas ou credenciais durante a inspeção.

**Projeto:** testes já existentes (unitário/integração/contrato/E2E/segurança), dependências externas por categoria, profiles/propriedades/Compose de teste, uso real de Testcontainers/PostgreSQL/Vault/auth, portas (fixas vs. dinâmicas), scripts de build/CI reaproveitáveis, testes com estado compartilhado ou ordem-dependente.

Consultar primeiro os arquivos existentes no repositório (`pom.xml`, `docker-compose.yml`, `application*.yml`, `.github/workflows/`, `scripts/`). Não criar infraestrutura paralela nem substituir mecanismos existentes sem justificar a necessidade.

## 3. Planejamento preventivo de recursos

Antes de cada grupo de testes, plano proporcional ao impacto, informando: recursos necessários vs. disponíveis; containers/serviços/processos a iniciar; portas/redes/volumes/diretórios; riscos de conflito ou saturação; estratégia de isolamento e limpeza; timeout e limites; condições para prosseguir/reduzir paralelismo/interromper; critério objetivo de "ambiente pronto".

Não inventar estimativas de memória/CPU. Quando o consumo não puder ser calculado, executar um piloto de baixo impacto e observar antes de ampliar a carga. Se a capacidade for insuficiente ou desconhecida para uma operação de alto impacto, não iniciá-la sem alternativa segura definida.

## 4. Auditoria de portas e prevenção de conflitos

Antes de iniciar qualquer serviço: identificar portas configuradas e sua origem (`application*.yml`, `docker-compose.yml`); verificar ocupação de portas fixas (`lsof -nP -iTCP -sTCP:LISTEN`); diferenciar porta do host vs. porta interna do container; inspecionar mapeamentos/redes existentes; preferir portas dinâmicas ou isolamento por rede quando a ferramenta suportar; confirmar disponibilidade real após a inicialização (uma consulta prévia não elimina corrida entre processos).

Evitar modificar configuração global ou matar processo de terceiros para liberar porta. Se houver conflito, identificar a causa e escolher a alternativa de menor impacto — nunca `kill`/`pkill` indiscriminado.

## 5. Containers e dependências

Antes de subir um container, confirmar: é realmente necessário; existe instância compatível reutilizável com segurança; o teste exige isolamento/dados limpos/instância exclusiva; imagem disponível localmente ou exige pull; espaço/portas/redes/volumes suficientes; compatibilidade de versão com a config do projeto; readiness check e timeout realistas; como identificar e limpar exclusivamente os recursos criados por esta execução.

Não reaproveitar automaticamente um banco/serviço só porque está ativo — confirmar versão, config, isolamento e estado dos dados. **Nunca** `docker system prune`, remover volume compartilhado ou apagar container preexistente indiscriminadamente — ver também limites de "Nunca" em [`local-runtime-authorization.md`](./local-runtime-authorization.md#requires-infra-gate-step-5).

Se Testcontainers/Docker não estiver disponível, registrar a limitação e avaliar alternativa compatível — nunca simular uma integração e depois declará-la validada.

## 6. Pirâmide de testes — execução progressiva

Interromper a progressão em falha impeditiva; adaptar à estrutura real do repo (nem toda etapa roda em todo ciclo).

| Etapa | Foco | Gate equivalente |
| ----- | ---- | ----------------- |
| A — Validações estáticas | Inspeção de config, compilação, checkstyle, dependências de teste | Read-only (passos 1–2) |
| B — Unitários | Isolados primeiro; avaliar estabilidade/duração/assertions; evitar paralelismo excessivo em máquina limitada | `./mvnw -B checkstyle:check test` (passo 4) |
| C — Integração | Só as dependências necessárias; confirmar readiness, migrações (Flyway), conectividade; cenários de sucesso/erro/recuperação | Infra gate (passo 5), `-Pintegration-tests` |
| D — End-to-end | Após estabilidade das camadas anteriores; fluxos completos (auth, persistência); dados controlados, limpeza segura | Infra gate + live proof (passo 6) |
| E — Segurança/qualidade/resiliência | Ferramentas já configuradas no projeto (Checkstyle, JaCoCo, CodeQL, Sonar, GitGuardian); timeout/falha de dependência só com ambiente isolado | `quality-gates.mdc`, `pre-push-check.sh` |
| F — Regressão/repetibilidade | Reexecutar cenários críticos; verificar independência de ordem; identificar teste instável/vazamento de recurso | Antes de "commit-ready" (passo 7) |

## 7. Coesão, coerência e qualidade dos testes

Para cada teste relevante, verificar: valida comportamento observável (não detalhe interno frágil sem motivo); dados/assertions representam o requisito; cobre caminho positivo, negativo, limite e falha pertinente; diferencia autenticação de autorização; isola usuários/papéis/permissões quando aplicável; trata corretamente transação/persistência/migração; não depende de estado compartilhado ou ordem de execução; mock não oculta erro que só apareceria em integração real; tem timeout explícito e limpeza previsível; falha de fato quando o comportamento esperado é violado.

Não inflar cobertura artificialmente para atingir percentual (JaCoCo) — priorizar lacunas que representam risco real.

## 8. Controle de carga, concorrência e limites

Definir limites conservadores para: testes simultâneos, threads de build/execução, containers ativos, tentativas de retry, timeout de inicialização de serviço, duração total da suíte, consumo de memória/CPU/disco (quando mensurável), volume de logs/artefatos.

Sem retry automático ilimitado, sem loop de espera sem timeout, sem paralelismo que dispute a mesma porta/tabela/arquivo. Retry deve ser limitado, justificado e observável — nunca para mascarar teste instável ou defeito real. Sob pressão de recursos: reduzir paralelismo, dividir a suíte em lotes, preservar evidência da limitação encontrada.

## 9. Timeouts, readiness e diagnóstico de falhas

Processo iniciado ≠ serviço pronto — validar disponibilidade por health check/readiness endpoint/verificação funcional antes de testes dependentes.

Classificar cada falha com evidência: defeito funcional/regressão; falha de compilação; config incorreta; porta ocupada; dependência indisponível; container sem recurso; falha de rede/DNS; timeout; erro de migração/persistência; instabilidade externa; teste não determinístico; falha da própria ferramenta de validação.

Coletar logs e códigos de saída relevantes. Não classificar como "ambiental" só para evitar investigar um defeito real no código.

## 10. Reprodutibilidade e rastreabilidade

Cada execução relevante deve registrar: identificador/data; commit testado; versões das ferramentas; comandos efetivamente executados; categorias de teste cobertas; resultado/duração/código de saída; recursos e serviços inicializados; portas e configs relevantes (sem segredos); falhas, limitações, tentativas de recuperação; relatórios gerados e localização; estado final dos recursos criados.

Diferenciar claramente: aprovado, reprovado, ignorado, não executado, bloqueado por infraestrutura.

## 11. Segurança e recuperação

Avançar de inspeção para ações de impacto crescente — nunca pular etapa. Antes de qualquer operação destrutiva ou disruptiva: identificar precisamente o recurso afetado; confirmar que pertence a esta execução; avaliar efeito colateral; preferir alternativa não destrutiva; pedir autorização quando houver risco a dados/processo externo (ver gates do owner).

Nunca usar credencial real de produção nem apontar teste destrutivo para ambiente compartilhado/produtivo — dados sintéticos e ambientes isolados apenas. Em interrupção, limpar seletivamente apenas os recursos criados por esta execução — nunca remover recurso desconhecido indiscriminadamente.

## 12. Critérios de conclusão

Execução só é considerada concluída quando: comandos planejados foram executados (ou a omissão está justificada); resultados foram analisados, não apenas iniciados; falhas têm classificação baseada em evidência; verificações críticas têm evidência suficiente; testes de integração usaram dependências reais quando exigido pelo cenário; recursos criados foram encerrados ou seu estado residual documentado; relatórios/logs relevantes preservados; limitações e riscos remanescentes explícitos.

Não declarar aprovação integral se teste crítico não rodou, verificação de segurança foi ignorada, ou integração foi apenas simulada.

## 13. Relatório de execução (quando a tarefa envolveu testes/infra)

1. Resumo executivo — estado geral e confiança nas evidências
2. Diagnóstico inicial — capacidade da máquina e dependências disponíveis
3. Plano executado — etapas, comandos, justificativas
4. Matriz de testes — planejados / executados / aprovados / reprovados / ignorados / bloqueados
5. Falhas encontradas — causa provável, evidência, impacto
6. Uso de recursos — limitações observadas, ajustes feitos
7. Conflitos prevenidos — portas, containers, dependências
8. Reprodutibilidade — condições para repetir a execução
9. Limpeza — recursos criados e estado final
10. Pendências — testes não executados, incertezas, riscos residuais
11. Próximos passos — priorizados por risco e custo

## 14. Regra final de decisão

**Inspecionar → diagnosticar → planejar → validar pré-condições → executar em etapas → observar → classificar → repetir o necessário → limpar com segurança → reportar evidências.**

Antes de qualquer ação de alto impacto, responder: o que será executado? por quê é necessário? quais recursos consumirá? quais serviços/portas usará? qual o risco de conflito? como uma falha será detectada? como a execução será interrompida com segurança? como os recursos criados serão identificados e limpos?

Se alguma resposta crítica estiver ausente, investigar antes de prosseguir — e, se a resposta exigir subir infraestrutura (passo 5 do gate de runtime local), pedir `ok infra` antes de agir.

**Meta:** maximizar confiabilidade, cobertura relevante e taxa de sucesso, com consumo proporcional, baixo risco operacional e evidências verificáveis — sem prometer 100% de sucesso quando há fatores não controláveis.
