# Plano de Teste — City Transporte

**Plano de Testes Manuais e de Aceitação do Usuário (UAT)**

- **Sistema:** City Transporte — Gestão de Terminal de Cargas
- **Versão:** 1.0
- **Data:** 2026-05-20
- **Tipo de teste predominante:** Manual / UAT (User Acceptance Testing)
- **Perspectiva:** Usuário final
- **Ambiente:** `http://localhost:5173` (front) + `http://localhost:8080` (API)

---

## Sumário

1. [Objetivos dos testes](#1-objetivos-dos-testes)
2. [Escopo funcional](#2-escopo-funcional)
3. [Estratégia de testes manuais](#3-estratégia-de-testes-manuais)
4. [Critérios gerais de aceitação](#4-critérios-gerais-de-aceitação)
5. [Massa de dados necessária](#5-massa-de-dados-necessária)
6. [Pré-condições gerais](#6-pré-condições-gerais)
7. [Critérios de aprovação e reprovação](#7-critérios-de-aprovação-e-reprovação)
8. [Funcionalidades — Histórias de Usuário e Cenários](#8-funcionalidades--histórias-de-usuário-e-cenários)
9. [Cenários transversais (segurança, usabilidade, condições limite)](#9-cenários-transversais)
10. [Modelo de relatório de defeito](#10-modelo-de-relatório-de-defeito)
11. [Tabela consolidada de execução](#11-tabela-consolidada-de-execução)

---

## 1. Objetivos dos testes

Validar, sob a perspectiva do usuário final, que o sistema **City Transporte** atende às necessidades operacionais de um terminal de cargas. Especificamente:

1. **Confirmar que os fluxos críticos de negócio** (check-in, execução, finalização e checkout de operações) ocorrem conforme as regras estabelecidas e sem perda de consistência entre o estado da operação e o estado físico do recurso (doca/vaga).
2. **Verificar que cada perfil de usuário** (Administrador, Supervisor, Operador, Segurança) só consegue executar as ações compatíveis com sua função, garantindo a integridade do controle de acesso.
3. **Avaliar a experiência do usuário** em termos de clareza das mensagens, navegação intuitiva, respostas visuais a sucesso e a erro, e fluidez da operação cotidiana.
4. **Validar as regras de negócio** aplicadas pelo sistema, incluindo unicidade de placas, transições de estado permitidas, vínculos obrigatórios em incidentes e impossibilidade de conflitos de alocação.
5. **Garantir a consistência dos dados** ao longo de operações de longa duração e após operações destrutivas (cancelamentos, exclusões).
6. **Identificar defeitos** antes da entrega final do projeto acadêmico.

---

## 2. Escopo funcional

### 2.1 Funcionalidades dentro do escopo

| # | Módulo | Descrição |
|---|--------|-----------|
| F01 | Autenticação | Login, logout, manutenção de sessão JWT |
| F02 | Dashboard | Painel com métricas operacionais em tempo quase real |
| F03 | Terminais | Cadastro e manutenção dos terminais físicos |
| F04 | Docas | Cadastro das docas vinculadas a um terminal, com controle de status |
| F05 | Estacionamentos | Cadastro de estacionamentos pertencentes a um terminal |
| F06 | Vagas | Cadastro de vagas de um estacionamento, com controle de status |
| F07 | Veículos | Cadastro de veículos que operam no terminal |
| F08 | Usuários | Gestão de contas, perfis e senhas |
| F09 | Tipos de Incidente | Catálogo de tipos de ocorrência com nível de gravidade |
| F10 | Operações de Carga | Fluxo completo: check-in → iniciar → finalizar → checkout |
| F11 | Documentos de Carga | Vínculo de notas fiscais, romaneios e CT-e a uma operação |
| F12 | Incidentes | Registro e encerramento de ocorrências |
| F13 | Permissões (RBAC) | Aplicação das políticas de acesso por perfil |

### 2.2 Fora do escopo deste plano

- Testes de carga, estresse e performance
- Testes automatizados de UI (Cypress, Playwright)
- Pentest formal e auditoria de segurança
- Testes em dispositivos móveis nativos
- Upload físico de arquivos (sistema armazena apenas metadados)
- Internacionalização (sistema é monolíngue PT-BR)

---

## 3. Estratégia de testes manuais

### 3.1 Abordagem

O plano adota uma abordagem **baseada em risco e em histórias de usuário**, combinando:

- **Testes exploratórios:** o testador navega livremente pelas telas verificando reações da interface.
- **Testes baseados em cenários (script-based):** execução passo a passo dos casos descritos na seção 8.
- **Testes de aceitação do usuário (UAT):** cada funcionalidade tem critérios de aceite explícitos derivados das histórias de usuário; a aprovação depende do atendimento integral.
- **Testes de regressão:** após correção de qualquer defeito, os cenários relacionados devem ser reexecutados.

### 3.2 Ciclos de execução

| Ciclo | Foco | Quando |
|-------|------|--------|
| **Smoke** | Verificar se o sistema sobe e os fluxos básicos respondem | Início de cada bateria |
| **UAT funcional** | Validar todas as histórias de usuário | Após smoke aprovado |
| **Regressão** | Reexecutar cenários afetados | Após cada correção |
| **Aceite final** | Demonstração ponta a ponta para o cliente/professor | Pré-entrega |

### 3.3 Perfis de testador

| Perfil simulado | E-mail (seed) | Senha | Função no teste |
|-----------------|---------------|-------|-----------------|
| Administrador | admin@city.com | admin123 | Executa todos os fluxos sem restrição |
| Supervisor | supervisor@city.com | sup123 | Valida operações táticas e encerramento de incidentes |
| Operador | operador@city.com | oper123 | Executa o cotidiano operacional |
| Segurança | seguranca@city.com | seg123 | Faz check-in/checkout e registra incidentes |

### 3.4 Ferramentas auxiliares

- **Navegador:** Chrome ou Firefox (versão atual), com DevTools aberto para inspecionar requisições e logs.
- **Cliente de banco:** pgAdmin ou DBeaver para confirmar consistência no Postgres.
- **Bloco de notas:** registro das evidências (prints + observações).

---

## 4. Critérios gerais de aceitação

Para que o sistema seja considerado **aceito pelo usuário final**, deve atender simultaneamente a:

1. **Funcional:** todos os cenários de prioridade **Alta** com status esperado igual a "Aprovado".
2. **Negócio:** o fluxo completo de uma operação de carga (check-in → iniciar → finalizar → checkout) executa sem inconsistência entre estado da operação e estado da doca/vaga vinculada.
3. **Segurança:** nenhum perfil consegue executar ação além do permitido pela matriz de permissões (seção 8.13).
4. **UX:** todas as ações realizadas no sistema produzem feedback visual claro ao usuário (mensagem de sucesso, mensagem de erro, atualização imediata da lista, indicador de carregamento).
5. **Consistência:** após qualquer operação, ao recarregar a página, os dados continuam no estado correto.
6. **Recuperabilidade:** ao receber um erro, o usuário consegue corrigir a entrada e tentar novamente sem precisar reiniciar o fluxo.

---

## 5. Massa de dados necessária

A massa de dados é provida pelo script `backend/src/main/resources/schema.sql`, executado uma única vez antes do início dos testes.

### 5.1 Usuários (4)

| Nome | E-mail | Perfil | Senha |
|------|--------|--------|-------|
| Administrador | admin@city.com | ADMINISTRADOR | admin123 |
| Supervisor | supervisor@city.com | SUPERVISOR | sup123 |
| Operador | operador@city.com | OPERADOR | oper123 |
| Segurança | seguranca@city.com | SEGURANCA | seg123 |

### 5.2 Terminais (2)

- **Terminal Central** — São Paulo/SP — Ativo
- **Terminal Sul** — Curitiba/PR — Ativo

### 5.3 Docas (8)

- 4 docas no Terminal Central (D-01 a D-04) com status variando entre `DISPONIVEL`, `OCUPADA` e `INTERDITADA`
- 4 docas no Terminal Sul (D-05 a D-08), todas `DISPONIVEL`

### 5.4 Estacionamentos e vagas

- 1 estacionamento no Terminal Central com 4 vagas (V-01 a V-04), todas `LIVRE`

### 5.5 Veículos (5)

Placas: `ABC1234` (CAMINHAO), `DEF5678` (CARRETA), `GHI9012` (VAN), `JKL3456` (UTILITARIO), `MNO7890` (OUTRO).

### 5.6 Tipos de incidente (5)

- Avaria de carga (ALTO)
- Vazamento (ALTO)
- Atraso na entrega (MEDIO)
- Documentação incompleta (MEDIO)
- Trânsito interno (BAIXO)

### 5.7 Reinicialização da massa

Sempre que a base ficar inconsistente:

```sql
DROP DATABASE terminal;
CREATE DATABASE terminal;
\i backend/src/main/resources/schema.sql
```

---

## 6. Pré-condições gerais

Antes de iniciar qualquer cenário:

- [ ] PostgreSQL em execução na porta 5432
- [ ] Banco `terminal` criado e populado com o `schema.sql`
- [ ] Back-end em execução na porta 8080 (`./mvnw.cmd spring-boot:run`)
- [ ] Front-end em execução na porta 5173 (`npm run dev`)
- [ ] Navegador limpo (sem token salvo) na primeira execução
- [ ] DevTools aberto na aba "Network" para inspeção dos retornos

---

## 7. Critérios de aprovação e reprovação

### 7.1 Aprovação de cenário

Um cenário é **APROVADO** quando:
- Todos os passos foram executados na ordem
- O resultado obtido é **idêntico** ao resultado esperado
- A mensagem ao usuário (sucesso ou erro) está em português correto e contextualizada
- O estado do banco após a execução é consistente com o resultado da tela

### 7.2 Reprovação de cenário

Um cenário é **REPROVADO** quando ocorre qualquer um:
- O sistema apresenta erro não tratado (tela branca, erro 500, exceção exposta)
- O resultado obtido difere do esperado
- Há divergência entre o que a tela mostra e o que está no banco
- Mensagem em inglês, com termos técnicos do back-end, ou sem contexto
- O usuário fica preso em estado inconsistente sem caminho de saída

### 7.3 Aprovação global do sistema

| Prioridade | Critério mínimo |
|------------|------------------|
| Alta | 100% dos cenários aprovados |
| Média | ≥ 90% dos cenários aprovados |
| Baixa | ≥ 70% dos cenários aprovados |

Nenhum defeito **bloqueante** (impede fluxo crítico) pode estar em aberto.

---

## 8. Funcionalidades — Histórias de Usuário e Cenários

### Legenda

- **Status Esperado** — `Aprovado` significa que o sistema se comporta conforme descrito.
- **Prioridade** — `Alta` (fluxo crítico), `Média` (relevante mas não bloqueante), `Baixa` (cosmético/marginal).

---

### 8.1 Autenticação (F01)

**História de Usuário:**
> Como **funcionário da City Transporte**, quero **acessar o sistema com meu e-mail e senha**, para **operar conforme minha função sem expor informações para pessoas não autorizadas**.

**Critérios de aceite:**
- O usuário consegue entrar no sistema com credenciais válidas e é direcionado ao dashboard.
- Tentativas com senha errada são bloqueadas com mensagem clara.
- A sessão é mantida durante a navegação e expira de forma controlada.
- O logout limpa o acesso completamente.

#### Cenário CT-AUTH-01 — Login com credenciais válidas

| Campo | Valor |
|-------|-------|
| **ID** | CT-AUTH-01 |
| **Funcionalidade** | Autenticação |
| **História** | Como usuário cadastrado, quero entrar no sistema com minhas credenciais. |
| **Prioridade** | Alta |
| **Pré-condições** | Usuário `admin@city.com` ativo no banco; navegador sem sessão prévia |
| **Passos** | 1. Acessar `http://localhost:5173/login`<br>2. Digitar `admin@city.com` no campo E-mail<br>3. Digitar `admin123` no campo Senha<br>4. Clicar no botão **Entrar** |
| **Resultado esperado** | O sistema redireciona para `/dashboard`, exibe o nome "Administrador" no canto superior e mostra as métricas operacionais. |
| **Status esperado** | Aprovado |

#### Cenário CT-AUTH-02 — Login com senha incorreta

| Campo | Valor |
|-------|-------|
| **ID** | CT-AUTH-02 |
| **Funcionalidade** | Autenticação |
| **História** | Como sistema, devo bloquear acessos com credenciais inválidas. |
| **Prioridade** | Alta |
| **Pré-condições** | Usuário `admin@city.com` existe |
| **Passos** | 1. Acessar `/login`<br>2. Digitar `admin@city.com`<br>3. Digitar `senha-errada`<br>4. Clicar em **Entrar** |
| **Resultado esperado** | Mensagem "E-mail ou senha incorretos" exibida em destaque; usuário permanece na tela de login; nenhum dado armazenado no `localStorage`. |
| **Status esperado** | Aprovado |

#### Cenário CT-AUTH-03 — Login com e-mail inexistente

| Campo | Valor |
|-------|-------|
| **ID** | CT-AUTH-03 |
| **Funcionalidade** | Autenticação |
| **História** | Como sistema, não devo revelar se o e-mail existe ou não na base. |
| **Prioridade** | Alta |
| **Pré-condições** | E-mail `inexistente@city.com` NÃO existe |
| **Passos** | 1. Acessar `/login`<br>2. Digitar `inexistente@city.com`<br>3. Digitar qualquer senha<br>4. Clicar **Entrar** |
| **Resultado esperado** | Mesma mensagem genérica "E-mail ou senha incorretos" (não diferenciar e-mail inexistente de senha errada). |
| **Status esperado** | Aprovado |

#### Cenário CT-AUTH-04 — Campos obrigatórios em branco

| Campo | Valor |
|-------|-------|
| **ID** | CT-AUTH-04 |
| **Funcionalidade** | Autenticação (validação de formulário) |
| **História** | Como sistema, devo orientar o usuário sobre campos obrigatórios. |
| **Prioridade** | Média |
| **Pré-condições** | Nenhuma |
| **Passos** | 1. Acessar `/login`<br>2. Clicar **Entrar** sem preencher nada |
| **Resultado esperado** | Validação Zod exibe erro inline em ambos os campos; nenhuma requisição é enviada à API. |
| **Status esperado** | Aprovado |

#### Cenário CT-AUTH-05 — Sessão expira após o tempo configurado

| Campo | Valor |
|-------|-------|
| **ID** | CT-AUTH-05 |
| **Funcionalidade** | Autenticação |
| **História** | Como sistema, devo invalidar sessões antigas. |
| **Prioridade** | Média |
| **Pré-condições** | Usuário logado; token JWT no `localStorage` |
| **Passos** | 1. Abrir DevTools → Application → Local Storage<br>2. Substituir o token por uma string aleatória<br>3. Tentar navegar para `/terminais` |
| **Resultado esperado** | Sistema detecta 401, limpa o storage e redireciona para `/login`. |
| **Status esperado** | Aprovado |

#### Cenário CT-AUTH-06 — Logout encerra a sessão

| Campo | Valor |
|-------|-------|
| **ID** | CT-AUTH-06 |
| **Funcionalidade** | Autenticação |
| **História** | Como usuário, quero sair do sistema ao final do expediente. |
| **Prioridade** | Alta |
| **Pré-condições** | Usuário logado |
| **Passos** | 1. Clicar no menu do usuário (canto superior)<br>2. Clicar em **Sair**<br>3. Após o redirecionamento, pressionar a seta de voltar do navegador |
| **Resultado esperado** | Após sair, ao voltar não há acesso ao dashboard; tela de login é exibida novamente; `localStorage` vazio. |
| **Status esperado** | Aprovado |

---

### 8.2 Dashboard (F02)

**História de Usuário:**
> Como **gestor do terminal**, quero **visualizar em um único painel o estado atual da operação**, para **tomar decisões rápidas sobre alocação, atendimento e ocorrências**.

**Critérios de aceite:**
- O dashboard apresenta indicadores numéricos das principais métricas.
- O status visual das docas é representado por cores intuitivas.
- Os dados se atualizam automaticamente em intervalo regular sem ação do usuário.
- O filtro por terminal modifica todas as métricas de forma consistente.

#### Cenário CT-DASH-01 — Visualização inicial do dashboard

| Campo | Valor |
|-------|-------|
| **ID** | CT-DASH-01 |
| **Funcionalidade** | Dashboard |
| **História** | Como gestor, quero abrir o sistema e ver o estado operacional. |
| **Prioridade** | Alta |
| **Pré-condições** | Usuário ADMIN logado; banco com seeds carregados |
| **Passos** | 1. Após login, observar a tela `/dashboard` |
| **Resultado esperado** | São exibidos os cards: Docas Ocupadas, Operações em Andamento, Veículos no Pátio, Tempo Médio Hoje, Incidentes Abertos. Grid de docas exibe cada doca com cor: verde (disponível), amarelo (ocupada), vermelho (interditada). |
| **Status esperado** | Aprovado |

#### Cenário CT-DASH-02 — Atualização automática de métricas

| Campo | Valor |
|-------|-------|
| **ID** | CT-DASH-02 |
| **Funcionalidade** | Dashboard (polling) |
| **História** | Como gestor, quero que o painel reflita mudanças em tempo quase real. |
| **Prioridade** | Média |
| **Pré-condições** | Dashboard aberto em uma aba; outra aba com perfil OPERADOR pronta |
| **Passos** | 1. Anotar o valor atual de "Operações em Andamento"<br>2. Em outra aba (perfil OPERADOR), iniciar uma operação que estava AGENDADA<br>3. Voltar à aba do dashboard e aguardar até 15 segundos sem clicar em nada |
| **Resultado esperado** | O contador "Operações em Andamento" incrementa em 1 sem necessidade de F5. |
| **Status esperado** | Aprovado |

#### Cenário CT-DASH-03 — Cor da doca acompanha o status real

| Campo | Valor |
|-------|-------|
| **ID** | CT-DASH-03 |
| **Funcionalidade** | Dashboard (representação visual) |
| **História** | Como gestor, quero identificar o estado de cada doca em um relance. |
| **Prioridade** | Alta |
| **Pré-condições** | Doca D-01 está `DISPONIVEL` |
| **Passos** | 1. Verificar a cor de D-01 no dashboard (deve ser verde)<br>2. Em outra aba, executar check-in atribuindo D-01<br>3. Voltar ao dashboard e aguardar a próxima atualização |
| **Resultado esperado** | D-01 muda de verde para amarelo (OCUPADA) no card visual. |
| **Status esperado** | Aprovado |

---

### 8.3 Terminais (F03)

**História de Usuário:**
> Como **administrador**, quero **cadastrar e manter os terminais físicos da empresa**, para **organizar toda a operação em torno deles**.

**Critérios de aceite:**
- Apenas administradores podem criar, editar ou excluir terminais.
- Não é possível cadastrar dois terminais com o mesmo nome.
- Não é possível excluir um terminal que tenha docas vinculadas.

#### Cenário CT-TERM-01 — Cadastrar terminal com dados válidos

| Campo | Valor |
|-------|-------|
| **ID** | CT-TERM-01 |
| **Funcionalidade** | Terminais — Criar |
| **História** | Como administrador, quero cadastrar uma nova unidade operacional. |
| **Prioridade** | Alta |
| **Pré-condições** | Usuário ADMIN logado |
| **Passos** | 1. Acessar `/terminais`<br>2. Clicar em **Novo Terminal**<br>3. Preencher Nome: "Terminal Norte", Cidade: "Manaus"<br>4. Marcar **Ativo**<br>5. Clicar em **Salvar** |
| **Resultado esperado** | Mensagem de sucesso; novo terminal aparece imediatamente na tabela sem F5; modal fecha. |
| **Status esperado** | Aprovado |

#### Cenário CT-TERM-02 — Impedir nome duplicado

| Campo | Valor |
|-------|-------|
| **ID** | CT-TERM-02 |
| **Funcionalidade** | Terminais — Validação de unicidade |
| **História** | Como sistema, devo impedir duplicidade de cadastro. |
| **Prioridade** | Alta |
| **Pré-condições** | Existe terminal "Terminal Central" |
| **Passos** | 1. Acessar `/terminais`<br>2. Clicar **Novo Terminal**<br>3. Preencher Nome: "Terminal Central"<br>4. Salvar |
| **Resultado esperado** | Mensagem clara: "Já existe um Terminal com nome 'Terminal Central'"; modal permanece aberto com os dados; nenhuma criação ocorre. |
| **Status esperado** | Aprovado |

#### Cenário CT-TERM-03 — Editar terminal mantendo o próprio nome

| Campo | Valor |
|-------|-------|
| **ID** | CT-TERM-03 |
| **Funcionalidade** | Terminais — Editar |
| **História** | Como administrador, quero corrigir dados de um terminal sem precisar renomeá-lo. |
| **Prioridade** | Média |
| **Pré-condições** | Terminal "Terminal Central" existente |
| **Passos** | 1. Clicar em **Editar** na linha do Terminal Central<br>2. Alterar Cidade para "São Paulo - Capital"<br>3. Manter o mesmo Nome<br>4. Salvar |
| **Resultado esperado** | Salva normalmente sem erro de duplicidade; cidade atualizada na lista. |
| **Status esperado** | Aprovado |

#### Cenário CT-TERM-04 — Operador tenta cadastrar terminal (acesso negado)

| Campo | Valor |
|-------|-------|
| **ID** | CT-TERM-04 |
| **Funcionalidade** | Terminais — RBAC |
| **História** | Como sistema, devo restringir o cadastro de terminais ao perfil ADMIN. |
| **Prioridade** | Alta |
| **Pré-condições** | Usuário OPERADOR logado |
| **Passos** | 1. Tentar acessar `/terminais` ou observar o menu lateral |
| **Resultado esperado** | Botão **Novo Terminal** não aparece OU aparece desabilitado. Se forçar via URL/API, sistema responde 403 com mensagem "Você não tem permissão para executar esta ação". |
| **Status esperado** | Aprovado |

#### Cenário CT-TERM-05 — Excluir terminal sem dependências

| Campo | Valor |
|-------|-------|
| **ID** | CT-TERM-05 |
| **Funcionalidade** | Terminais — Excluir |
| **História** | Como administrador, quero remover terminais desativados que não tenham vínculos. |
| **Prioridade** | Média |
| **Pré-condições** | Terminal "Terminal Norte" criado sem docas |
| **Passos** | 1. Clicar **Excluir** na linha de Terminal Norte<br>2. Confirmar no modal |
| **Resultado esperado** | Terminal sumiu da lista; mensagem "Terminal excluído". |
| **Status esperado** | Aprovado |

#### Cenário CT-TERM-06 — Excluir terminal com docas vinculadas

| Campo | Valor |
|-------|-------|
| **ID** | CT-TERM-06 |
| **Funcionalidade** | Terminais — Excluir (integridade) |
| **História** | Como sistema, devo proteger a integridade referencial. |
| **Prioridade** | Alta |
| **Pré-condições** | Terminal Central tem docas D-01 a D-04 |
| **Passos** | 1. Tentar excluir Terminal Central |
| **Resultado esperado** | Operação bloqueada com mensagem clara: "Não é possível excluir um terminal que possui docas vinculadas"; terminal continua na lista. |
| **Status esperado** | Aprovado |

---

### 8.4 Docas (F04)

**História de Usuário:**
> Como **supervisor**, quero **cadastrar as docas de cada terminal e controlar seu status**, para **otimizar a alocação de veículos e bloquear docas em manutenção**.

**Critérios de aceite:**
- Cada doca pertence obrigatoriamente a um terminal.
- O código da doca é único dentro do mesmo terminal (mas pode se repetir entre terminais diferentes).
- Operadores podem alterar status para refletir manutenção ou interdição.
- O status real da doca sempre acompanha o vínculo com operações.

#### Cenário CT-DOCA-01 — Cadastrar doca com código único no terminal

| Campo | Valor |
|-------|-------|
| **ID** | CT-DOCA-01 |
| **Funcionalidade** | Docas — Criar |
| **História** | Como supervisor, quero adicionar uma nova doca operacional. |
| **Prioridade** | Alta |
| **Pré-condições** | Supervisor logado; Terminal Central existente |
| **Passos** | 1. Acessar `/docas`<br>2. Clicar **Nova Doca**<br>3. Selecionar Terminal: "Terminal Central"<br>4. Preencher Código: "D-99"<br>5. Salvar |
| **Resultado esperado** | Doca criada com status default `DISPONIVEL`; aparece na lista agrupada por terminal. |
| **Status esperado** | Aprovado |

#### Cenário CT-DOCA-02 — Código duplicado no mesmo terminal

| Campo | Valor |
|-------|-------|
| **ID** | CT-DOCA-02 |
| **Funcionalidade** | Docas — Validação |
| **História** | Como sistema, devo impedir códigos duplicados num mesmo terminal. |
| **Prioridade** | Alta |
| **Pré-condições** | Existe D-01 no Terminal Central |
| **Passos** | 1. Clicar **Nova Doca**<br>2. Terminal: "Terminal Central"<br>3. Código: "D-01"<br>4. Salvar |
| **Resultado esperado** | Mensagem "Já existe uma Doca com código 'D-01' neste terminal"; cadastro não ocorre. |
| **Status esperado** | Aprovado |

#### Cenário CT-DOCA-03 — Mesmo código em terminais diferentes

| Campo | Valor |
|-------|-------|
| **ID** | CT-DOCA-03 |
| **Funcionalidade** | Docas — Regra de unicidade por terminal |
| **História** | Como supervisor, quero usar o mesmo padrão de nomenclatura em terminais distintos. |
| **Prioridade** | Média |
| **Pré-condições** | D-01 existe no Terminal Central; Terminal Sul não tem D-01 |
| **Passos** | 1. Criar nova doca no **Terminal Sul** com código D-01 |
| **Resultado esperado** | Cadastro bem-sucedido — a unicidade é por terminal, não global. |
| **Status esperado** | Aprovado |

#### Cenário CT-DOCA-04 — Operador interdita doca

| Campo | Valor |
|-------|-------|
| **ID** | CT-DOCA-04 |
| **Funcionalidade** | Docas — Alterar status |
| **História** | Como operador, quero marcar uma doca como interditada quando ela apresenta problema. |
| **Prioridade** | Alta |
| **Pré-condições** | OPERADOR logado; D-02 está `DISPONIVEL` |
| **Passos** | 1. Acessar `/docas`<br>2. Localizar D-02<br>3. Clicar no dropdown de status e escolher **Interditada**<br>4. Confirmar |
| **Resultado esperado** | Status muda imediatamente; ao abrir dashboard, D-02 aparece em vermelho; uma tentativa de check-in posterior bloqueia D-02. |
| **Status esperado** | Aprovado |

#### Cenário CT-DOCA-05 — Segurança não pode mudar status (acesso negado)

| Campo | Valor |
|-------|-------|
| **ID** | CT-DOCA-05 |
| **Funcionalidade** | Docas — RBAC |
| **História** | Como sistema, devo restringir alteração de status a perfis operacionais. |
| **Prioridade** | Alta |
| **Pré-condições** | SEGURANCA logado |
| **Passos** | 1. Acessar `/docas`<br>2. Observar a coluna de status |
| **Resultado esperado** | Dropdown de status aparece desabilitado ou ausente; se o usuário forçar a API, recebe 403 com mensagem clara. |
| **Status esperado** | Aprovado |

---

### 8.5 Estacionamentos e Vagas (F05, F06)

**História de Usuário:**
> Como **supervisor**, quero **cadastrar estacionamentos e suas vagas**, para **alocar veículos que não estão em doca de carga/descarga**.

**Critérios de aceite:**
- Cada estacionamento pertence a um terminal.
- Cada vaga pertence a um estacionamento e tem código único dentro dele.
- Vagas têm controle de status (LIVRE, OCUPADA, INTERDITADA).

#### Cenário CT-EST-01 — Cadastrar estacionamento

| Campo | Valor |
|-------|-------|
| **ID** | CT-EST-01 |
| **Funcionalidade** | Estacionamentos — Criar |
| **História** | Como supervisor, quero registrar uma nova área de estacionamento. |
| **Prioridade** | Média |
| **Pré-condições** | Supervisor logado; Terminal Central existente |
| **Passos** | 1. Acessar `/estacionamentos`<br>2. **Novo Estacionamento**<br>3. Terminal: Terminal Central; Nome: "Pátio B"; Capacidade: 20<br>4. Salvar |
| **Resultado esperado** | Estacionamento criado e visível na lista. |
| **Status esperado** | Aprovado |

#### Cenário CT-VAGA-01 — Cadastrar vaga em estacionamento

| Campo | Valor |
|-------|-------|
| **ID** | CT-VAGA-01 |
| **Funcionalidade** | Vagas — Criar |
| **História** | Como supervisor, quero adicionar uma vaga ao estacionamento. |
| **Prioridade** | Média |
| **Pré-condições** | Estacionamento "Pátio B" existe |
| **Passos** | 1. Acessar `/vagas`<br>2. Filtrar por "Pátio B"<br>3. Clicar **Nova Vaga**<br>4. Código: V-05; Status: LIVRE<br>5. Salvar |
| **Resultado esperado** | Vaga criada e listada sob "Pátio B". |
| **Status esperado** | Aprovado |

#### Cenário CT-VAGA-02 — Operador interdita vaga

| Campo | Valor |
|-------|-------|
| **ID** | CT-VAGA-02 |
| **Funcionalidade** | Vagas — Alterar status |
| **História** | Como operador, quero indicar que uma vaga está indisponível. |
| **Prioridade** | Média |
| **Pré-condições** | OPERADOR logado; V-01 está LIVRE |
| **Passos** | 1. Acessar `/vagas`<br>2. Em V-01 alterar status para INTERDITADA |
| **Resultado esperado** | Status atualizado; check-in subsequente que tentar usar V-01 é bloqueado. |
| **Status esperado** | Aprovado |

---

### 8.6 Veículos (F07)

**História de Usuário:**
> Como **operador**, quero **cadastrar veículos que vão usar o terminal**, para **vinculá-los às operações de carga**.

**Critérios de aceite:**
- A placa é única em todo o sistema.
- A placa é armazenada em letras maiúsculas, independentemente de como o usuário digite.
- Cada veículo tem um tipo (Caminhão, Carreta, Van, Utilitário, Outro).

#### Cenário CT-VEI-01 — Cadastrar veículo válido

| Campo | Valor |
|-------|-------|
| **ID** | CT-VEI-01 |
| **Funcionalidade** | Veículos — Criar |
| **História** | Como operador, quero registrar um veículo novo. |
| **Prioridade** | Alta |
| **Pré-condições** | Operador logado; placa "XYZ4567" não existe |
| **Passos** | 1. Acessar `/veiculos`<br>2. **Novo Veículo**<br>3. Placa: "XYZ4567"; Tipo: CAMINHAO<br>4. Salvar |
| **Resultado esperado** | Veículo aparece na lista; pode ser usado num check-in subsequente. |
| **Status esperado** | Aprovado |

#### Cenário CT-VEI-02 — Placa digitada em minúsculas é normalizada

| Campo | Valor |
|-------|-------|
| **ID** | CT-VEI-02 |
| **Funcionalidade** | Veículos — Normalização |
| **História** | Como operador, quero digitar a placa sem me preocupar com o caixa das letras. |
| **Prioridade** | Média |
| **Pré-condições** | Placa "qwe9999" não existe |
| **Passos** | 1. **Novo Veículo**<br>2. Placa: "qwe9999"<br>3. Salvar |
| **Resultado esperado** | Veículo gravado com placa "QWE9999"; aparece em maiúsculas na lista. |
| **Status esperado** | Aprovado |

#### Cenário CT-VEI-03 — Placa duplicada

| Campo | Valor |
|-------|-------|
| **ID** | CT-VEI-03 |
| **Funcionalidade** | Veículos — Unicidade |
| **História** | Como sistema, devo impedir cadastros duplicados. |
| **Prioridade** | Alta |
| **Pré-condições** | "ABC1234" já existe |
| **Passos** | 1. Tentar cadastrar veículo com placa "ABC1234" |
| **Resultado esperado** | Mensagem "Já existe um Veículo com placa 'ABC1234'"; cadastro não ocorre. |
| **Status esperado** | Aprovado |

#### Cenário CT-VEI-04 — Segurança tenta cadastrar (acesso negado)

| Campo | Valor |
|-------|-------|
| **ID** | CT-VEI-04 |
| **Funcionalidade** | Veículos — RBAC |
| **História** | Como sistema, devo restringir cadastro a perfis cadastrais. |
| **Prioridade** | Alta |
| **Pré-condições** | SEGURANCA logado |
| **Passos** | 1. Acessar `/veiculos` |
| **Resultado esperado** | Botão "Novo Veículo" não aparece; tentativa direta na API retorna 403. |
| **Status esperado** | Aprovado |

---

### 8.7 Usuários (F08)

**História de Usuário:**
> Como **administrador**, quero **gerenciar as contas de acesso da equipe**, para **manter apenas pessoas autorizadas operando o sistema e atualizar perfis quando houver mudança de função**.

**Critérios de aceite:**
- Apenas o ADMIN acessa este módulo.
- A senha sempre é armazenada criptografada (BCrypt), nunca em texto claro.
- Na edição, deixar o campo de senha em branco mantém a senha atual.
- O e-mail é único no sistema.

#### Cenário CT-USR-01 — Cadastrar novo usuário

| Campo | Valor |
|-------|-------|
| **ID** | CT-USR-01 |
| **Funcionalidade** | Usuários — Criar |
| **História** | Como administrador, quero criar uma conta para um novo colaborador. |
| **Prioridade** | Alta |
| **Pré-condições** | ADMIN logado |
| **Passos** | 1. Acessar `/usuarios`<br>2. **Novo Usuário**<br>3. Nome: "João Silva"; E-mail: "joao@city.com"; Senha: "joao123"; Perfil: OPERADOR; Ativo: marcado<br>4. Salvar |
| **Resultado esperado** | Usuário criado; consegue logar com essas credenciais; consulta direta no banco mostra senha em hash BCrypt (começando com `$2a$` ou `$2b$`). |
| **Status esperado** | Aprovado |

#### Cenário CT-USR-02 — Cadastrar sem informar senha (negativo)

| Campo | Valor |
|-------|-------|
| **ID** | CT-USR-02 |
| **Funcionalidade** | Usuários — Validação |
| **História** | Como sistema, devo exigir senha no cadastro. |
| **Prioridade** | Alta |
| **Pré-condições** | ADMIN logado |
| **Passos** | 1. **Novo Usuário**<br>2. Preencher nome, e-mail, perfil; deixar senha vazia<br>3. Salvar |
| **Resultado esperado** | Erro inline indicando senha obrigatória; nenhum registro criado. |
| **Status esperado** | Aprovado |

#### Cenário CT-USR-03 — Editar usuário sem alterar senha

| Campo | Valor |
|-------|-------|
| **ID** | CT-USR-03 |
| **Funcionalidade** | Usuários — Editar |
| **História** | Como administrador, quero corrigir o nome de um usuário sem alterar sua senha. |
| **Prioridade** | Alta |
| **Pré-condições** | Usuário "João Silva" existe |
| **Passos** | 1. **Editar** João Silva<br>2. Alterar nome para "João S. Pereira"<br>3. Deixar campo de senha em branco<br>4. Salvar<br>5. Sair do ADMIN<br>6. Logar como joao@city.com com a senha original |
| **Resultado esperado** | Nome alterado; senha continua válida; login funciona normalmente. |
| **Status esperado** | Aprovado |

#### Cenário CT-USR-04 — Editar usuário trocando a senha

| Campo | Valor |
|-------|-------|
| **ID** | CT-USR-04 |
| **Funcionalidade** | Usuários — Editar |
| **História** | Como administrador, quero resetar a senha de um colaborador. |
| **Prioridade** | Alta |
| **Pré-condições** | Usuário existe |
| **Passos** | 1. Editar usuário<br>2. Digitar nova senha "nova123"<br>3. Salvar<br>4. Logar com nova senha<br>5. Tentar logar com senha antiga |
| **Resultado esperado** | Login com "nova123" funciona; login com senha antiga falha. |
| **Status esperado** | Aprovado |

#### Cenário CT-USR-05 — Operador tenta acessar usuários (acesso negado)

| Campo | Valor |
|-------|-------|
| **ID** | CT-USR-05 |
| **Funcionalidade** | Usuários — RBAC |
| **História** | Como sistema, gestão de usuários é exclusiva do ADMIN. |
| **Prioridade** | Alta |
| **Pré-condições** | OPERADOR logado |
| **Passos** | 1. Observar o menu lateral<br>2. Tentar acessar `/usuarios` via URL |
| **Resultado esperado** | Item "Usuários" não aparece no menu; acesso direto via URL redireciona ou exibe 403; API responde 403. |
| **Status esperado** | Aprovado |

#### Cenário CT-USR-06 — E-mail duplicado

| Campo | Valor |
|-------|-------|
| **ID** | CT-USR-06 |
| **Funcionalidade** | Usuários — Unicidade |
| **História** | Como sistema, devo evitar contas duplicadas. |
| **Prioridade** | Alta |
| **Pré-condições** | admin@city.com existe |
| **Passos** | 1. **Novo Usuário** com e-mail "admin@city.com" |
| **Resultado esperado** | Mensagem "Já existe um Usuário com e-mail 'admin@city.com'"; cadastro bloqueado. |
| **Status esperado** | Aprovado |

---

### 8.8 Tipos de Incidente (F09)

**História de Usuário:**
> Como **administrador**, quero **manter o catálogo de tipos de incidente**, para **padronizar o registro de ocorrências e a classificação por gravidade**.

**Critérios de aceite:**
- Apenas ADMIN gerencia este catálogo.
- O nome de cada tipo é único.
- A gravidade deve ser um dos valores: BAIXO, MEDIO, ALTO.

#### Cenário CT-TIP-01 — Cadastrar tipo de incidente

| Campo | Valor |
|-------|-------|
| **ID** | CT-TIP-01 |
| **Funcionalidade** | Tipos de Incidente — Criar |
| **História** | Como administrador, quero adicionar uma nova categoria de ocorrência. |
| **Prioridade** | Alta |
| **Pré-condições** | ADMIN logado |
| **Passos** | 1. Acessar `/tipos-incidente`<br>2. **Novo Tipo**<br>3. Nome: "Acidente com veículo"; Descrição: "Colisão dentro do pátio"; Gravidade: ALTO<br>4. Salvar |
| **Resultado esperado** | Tipo aparece na lista e fica disponível no select de registro de incidente. |
| **Status esperado** | Aprovado |

#### Cenário CT-TIP-02 — Nome duplicado

| Campo | Valor |
|-------|-------|
| **ID** | CT-TIP-02 |
| **Funcionalidade** | Tipos de Incidente — Unicidade |
| **História** | Como sistema, devo evitar duplicidade no catálogo. |
| **Prioridade** | Média |
| **Pré-condições** | "Avaria de carga" já existe |
| **Passos** | 1. Tentar criar tipo com nome "Avaria de carga" |
| **Resultado esperado** | Mensagem clara de duplicidade; cadastro não ocorre. |
| **Status esperado** | Aprovado |

#### Cenário CT-TIP-03 — Supervisor tenta criar (acesso negado)

| Campo | Valor |
|-------|-------|
| **ID** | CT-TIP-03 |
| **Funcionalidade** | Tipos de Incidente — RBAC |
| **História** | Como sistema, gestão do catálogo é exclusiva do ADMIN. |
| **Prioridade** | Alta |
| **Pré-condições** | SUPERVISOR logado |
| **Passos** | 1. Acessar `/tipos-incidente` |
| **Resultado esperado** | Modo leitura apenas: lista visível, botões de ação ausentes; API responde 403 em POST/PUT/DELETE. |
| **Status esperado** | Aprovado |

---

### 8.9 Operações de Carga (F10) — **FLUXO CRÍTICO**

**História de Usuário:**
> Como **operador do terminal**, quero **registrar a chegada do veículo, alocá-lo numa doca ou vaga, controlar o início e o fim do carregamento/descarregamento e liberar o recurso ao final**, para **garantir o atendimento ordenado da fila e a rastreabilidade de cada operação**.

**Critérios de aceite:**
- Toda operação inicia no estado **AGENDADA** após o check-in.
- A operação só transita para **EM_ANDAMENTO** com a ação explícita "Iniciar".
- O **Finalizar** registra o horário de fim mas mantém a doca/vaga ocupada.
- O **Checkout** libera fisicamente a doca/vaga, deixando-a disponível para a próxima operação.
- O **Cancelar** funciona até a operação ser finalizada; depois disso, só checkout.
- Doca em status diferente de DISPONIVEL não pode receber check-in.
- Vaga em status diferente de LIVRE não pode receber check-in.
- Não é permitido check-in com doca **e** vaga ao mesmo tempo.

#### Cenário CT-OP-01 — Check-in em doca disponível (caminho feliz)

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-01 |
| **Funcionalidade** | Operações — Check-in |
| **História** | Como operador, quero registrar a chegada de um veículo numa doca livre. |
| **Prioridade** | Alta |
| **Pré-condições** | OPERADOR logado; veículo ABC1234 existe; D-01 está DISPONIVEL |
| **Passos** | 1. Acessar `/operacoes`<br>2. Clicar **Nova Operação**<br>3. Passo 1 do wizard: Terminal: Terminal Central; Veículo: ABC1234; Tipo: CARGA<br>4. **Próximo**<br>5. Passo 2: selecionar opção "Doca"; escolher D-01; preencher Descrição da Carga: "Eletrônicos"<br>6. **Confirmar Check-in** |
| **Resultado esperado** | Operação criada com status AGENDADA; modal fecha; nova linha aparece na tabela; D-01 muda para OCUPADA no dashboard. |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-02 — Check-in tentando usar doca ocupada (negativo)

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-02 |
| **Funcionalidade** | Operações — Validação de doca |
| **História** | Como sistema, devo impedir alocação dupla na mesma doca. |
| **Prioridade** | Alta |
| **Pré-condições** | D-01 está OCUPADA (depois do cenário anterior) |
| **Passos** | 1. **Nova Operação**<br>2. Passo 1: Veículo DEF5678; CARGA<br>3. Passo 2: tentar selecionar D-01<br>4. Confirmar |
| **Resultado esperado** | Idealmente D-01 nem aparece como opção. Se aparecer, ao confirmar surge mensagem "A doca 'D-01' não está disponível. Status atual: OCUPADA"; nenhuma operação é criada; doca permanece como está. |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-03 — Check-in em vaga livre

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-03 |
| **Funcionalidade** | Operações — Check-in em vaga |
| **História** | Como operador, quero alocar um veículo numa vaga quando todas as docas estão ocupadas. |
| **Prioridade** | Alta |
| **Pré-condições** | V-01 está LIVRE; veículo GHI9012 existe |
| **Passos** | 1. **Nova Operação**<br>2. Passo 1: GHI9012; CARGA<br>3. Passo 2: selecionar opção "Vaga"; escolher V-01<br>4. Confirmar |
| **Resultado esperado** | Operação criada AGENDADA; V-01 muda para OCUPADA. |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-04 — Tentar check-in sem doca nem vaga (negativo)

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-04 |
| **Funcionalidade** | Operações — Validação |
| **História** | Como sistema, devo exigir alocação física. |
| **Prioridade** | Alta |
| **Pré-condições** | OPERADOR logado |
| **Passos** | 1. Iniciar wizard sem selecionar doca nem vaga<br>2. Tentar avançar ou confirmar |
| **Resultado esperado** | Botão Confirmar desabilitado OU mensagem clara solicitando seleção de doca ou vaga. |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-05 — Iniciar operação agendada (caminho feliz)

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-05 |
| **Funcionalidade** | Operações — Iniciar |
| **História** | Como operador, quero marcar o início real do carregamento. |
| **Prioridade** | Alta |
| **Pré-condições** | Operação AGENDADA com D-01 alocada |
| **Passos** | 1. Localizar a operação na lista<br>2. Clicar em **Iniciar** |
| **Resultado esperado** | Status muda para EM_ANDAMENTO; data/hora de início é registrada; ao abrir o detalhe, a timeline mostra o ponto "Iniciada". |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-06 — Iniciar operação já em andamento (negativo)

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-06 |
| **Funcionalidade** | Operações — Transição inválida |
| **História** | Como sistema, devo bloquear transições impossíveis. |
| **Prioridade** | Alta |
| **Pré-condições** | Operação em EM_ANDAMENTO |
| **Passos** | 1. Tentar clicar **Iniciar** novamente (se o botão estiver disponível) ou forçar via API |
| **Resultado esperado** | Botão **Iniciar** não aparece para EM_ANDAMENTO. Se forçado via API, retorna 409 "Não é possível iniciar operação X: status atual é EM_ANDAMENTO". |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-07 — Finalizar operação em andamento

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-07 |
| **Funcionalidade** | Operações — Finalizar |
| **História** | Como operador, quero registrar o término do carregamento. |
| **Prioridade** | Alta |
| **Pré-condições** | Operação EM_ANDAMENTO |
| **Passos** | 1. Clicar **Finalizar** na operação |
| **Resultado esperado** | Status muda para FINALIZADA; horário de fim registrado; doca **continua OCUPADA** (esperando checkout); timeline atualizada. |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-08 — Checkout libera a doca

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-08 |
| **Funcionalidade** | Operações — Checkout |
| **História** | Como segurança/operador, quero liberar a doca após a saída do veículo. |
| **Prioridade** | Alta |
| **Pré-condições** | Operação FINALIZADA com D-01 alocada e OCUPADA |
| **Passos** | 1. Clicar **Checkout** na operação<br>2. Acessar `/docas` ou o dashboard |
| **Resultado esperado** | Operação permanece FINALIZADA; D-01 retorna a DISPONIVEL (verde no dashboard); operação pode ser checkout novamente sem erro (idempotência). |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-09 — Tentar checkout antes de finalizar (negativo)

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-09 |
| **Funcionalidade** | Operações — Validação |
| **História** | Como sistema, devo exigir finalização antes do checkout. |
| **Prioridade** | Alta |
| **Pré-condições** | Operação EM_ANDAMENTO |
| **Passos** | 1. Tentar clicar **Checkout** (se visível) ou via API |
| **Resultado esperado** | Botão não aparece na lista para EM_ANDAMENTO; via API, retorna 409 "A operação X ainda não foi finalizada". |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-10 — Cancelar operação agendada

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-10 |
| **Funcionalidade** | Operações — Cancelar |
| **História** | Como supervisor, quero cancelar operações que não vão acontecer. |
| **Prioridade** | Alta |
| **Pré-condições** | SUPERVISOR logado; operação AGENDADA com D-02 |
| **Passos** | 1. Clicar **Cancelar** na operação<br>2. Confirmar no modal |
| **Resultado esperado** | Status muda para CANCELADA; D-02 volta a DISPONIVEL imediatamente. |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-11 — Cancelar operação finalizada (negativo)

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-11 |
| **Funcionalidade** | Operações — Transição inválida |
| **História** | Como sistema, devo proteger operações já encerradas. |
| **Prioridade** | Alta |
| **Pré-condições** | Operação FINALIZADA |
| **Passos** | 1. Tentar cancelar |
| **Resultado esperado** | Botão **Cancelar** ausente para FINALIZADA; via API, retorna 409 "Não é possível cancelar uma operação já finalizada — utilize checkout". |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-12 — Fluxo completo ponta a ponta (UAT crítico)

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-12 |
| **Funcionalidade** | Operações — Fluxo integrado |
| **História** | Como operador, quero executar o ciclo completo de uma carga, do início ao fim, sem interrupções. |
| **Prioridade** | **Alta — caso de aceite final** |
| **Pré-condições** | Banco limpo com seeds; D-03 DISPONIVEL; veículo JKL3456 |
| **Passos** | 1. Login como operador<br>2. Check-in: JKL3456 em D-03 — confirmar que D-03 fica OCUPADA<br>3. Iniciar a operação<br>4. Adicionar 2 documentos (NOTA_FISCAL e ROMANEIO) no detalhe<br>5. Registrar 1 incidente vinculado à doca da operação<br>6. Finalizar a operação<br>7. Encerrar o incidente (logando como supervisor)<br>8. Voltar como operador e fazer checkout<br>9. Confirmar D-03 em DISPONIVEL no dashboard |
| **Resultado esperado** | Todas as transições ocorrem na ordem correta; documentos persistem; incidente aparece encerrado; doca volta ao estado original; nenhum dado inconsistente. |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-13 — Filtro de operações por status

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-13 |
| **Funcionalidade** | Operações — Listagem |
| **História** | Como gestor, quero visualizar apenas as operações em andamento. |
| **Prioridade** | Média |
| **Pré-condições** | Há operações em diferentes status |
| **Passos** | 1. Acessar `/operacoes`<br>2. Aplicar filtro Status: EM_ANDAMENTO |
| **Resultado esperado** | Lista exibe somente operações com este status; contador da página atualizado. |
| **Status esperado** | Aprovado |

#### Cenário CT-OP-14 — Detalhe da operação exibe timeline

| Campo | Valor |
|-------|-------|
| **ID** | CT-OP-14 |
| **Funcionalidade** | Operações — Detalhe |
| **História** | Como gestor, quero ver o histórico cronológico de uma operação. |
| **Prioridade** | Média |
| **Pré-condições** | Operação FINALIZADA com início e fim registrados |
| **Passos** | 1. Clicar na linha da operação |
| **Resultado esperado** | Tela de detalhe mostra: dados do veículo, doca/vaga, timeline com 3 marcos (Agendada, Iniciada, Finalizada) com data/hora, lista de documentos vinculados. |
| **Status esperado** | Aprovado |

---

### 8.10 Documentos de Carga (F11)

**História de Usuário:**
> Como **operador**, quero **anexar os documentos da carga (nota fiscal, romaneio, CT-e) a uma operação**, para **manter a rastreabilidade fiscal e operacional**.

**Critérios de aceite:**
- Documentos são vinculados sempre a uma operação existente.
- Cada documento tem: tipo, número, data de emissão e observação opcional.
- A lista de documentos aparece no detalhe da operação.

#### Cenário CT-DOC-01 — Adicionar documento à operação

| Campo | Valor |
|-------|-------|
| **ID** | CT-DOC-01 |
| **Funcionalidade** | Documentos — Adicionar |
| **História** | Como operador, quero registrar a nota fiscal de uma carga. |
| **Prioridade** | Alta |
| **Pré-condições** | Operação existente |
| **Passos** | 1. Abrir detalhe da operação<br>2. Em "Documentos", clicar **Adicionar**<br>3. Tipo: NOTA_FISCAL; Número: "NF-12345"; Data: hoje<br>4. Salvar |
| **Resultado esperado** | Documento aparece imediatamente na lista; pode ser visualizado ao retornar à página depois. |
| **Status esperado** | Aprovado |

#### Cenário CT-DOC-02 — Múltiplos documentos na mesma operação

| Campo | Valor |
|-------|-------|
| **ID** | CT-DOC-02 |
| **Funcionalidade** | Documentos — Múltiplos vínculos |
| **História** | Como operador, posso anexar quantos documentos forem necessários. |
| **Prioridade** | Média |
| **Pré-condições** | Operação com 1 documento já anexado |
| **Passos** | 1. Adicionar mais 2 documentos (ROMANEIO e CT_E) |
| **Resultado esperado** | Os 3 documentos aparecem na lista do detalhe; nenhum erro de duplicidade. |
| **Status esperado** | Aprovado |

#### Cenário CT-DOC-03 — Excluir documento (supervisor/admin)

| Campo | Valor |
|-------|-------|
| **ID** | CT-DOC-03 |
| **Funcionalidade** | Documentos — Excluir |
| **História** | Como supervisor, quero remover um documento adicionado por engano. |
| **Prioridade** | Média |
| **Pré-condições** | Documento existe; SUPERVISOR logado |
| **Passos** | 1. Acessar detalhe da operação<br>2. Clicar **Excluir** no documento<br>3. Confirmar |
| **Resultado esperado** | Documento sai da lista; ação não afeta outros documentos da operação. |
| **Status esperado** | Aprovado |

#### Cenário CT-DOC-04 — Operador tenta excluir (negativo)

| Campo | Valor |
|-------|-------|
| **ID** | CT-DOC-04 |
| **Funcionalidade** | Documentos — RBAC |
| **História** | Como sistema, devo restringir exclusão a perfis superiores. |
| **Prioridade** | Média |
| **Pré-condições** | OPERADOR logado |
| **Passos** | 1. Acessar detalhe<br>2. Procurar opção de excluir documento |
| **Resultado esperado** | Botão **Excluir** ausente ou desabilitado; via API retorna 403. |
| **Status esperado** | Aprovado |

---

### 8.11 Incidentes (F12)

**História de Usuário:**
> Como **profissional do terminal**, quero **registrar ocorrências (avarias, vazamentos, atrasos) com vínculo ao recurso afetado**, para **acompanhar o tratamento e manter histórico para auditoria**.

**Critérios de aceite:**
- Qualquer perfil autenticado pode registrar incidentes.
- Cada incidente deve ter pelo menos um vínculo (operação, doca ou vaga).
- A data do incidente não pode estar no futuro.
- Encerrar um incidente exige descrição da ação tomada.
- Apenas supervisores e administradores encerram incidentes.
- Incidentes já resolvidos não podem ser reabertos via mesmo fluxo.

#### Cenário CT-INC-01 — Registrar incidente com vínculo a doca

| Campo | Valor |
|-------|-------|
| **ID** | CT-INC-01 |
| **Funcionalidade** | Incidentes — Criar |
| **História** | Como operador, quero registrar um vazamento detectado na doca. |
| **Prioridade** | Alta |
| **Pré-condições** | OPERADOR logado; D-02 existe |
| **Passos** | 1. Acessar `/incidentes`<br>2. **Novo Incidente**<br>3. Tipo: Vazamento; Terminal: Terminal Central; Vínculo: Doca D-02; Data: hoje 14:30; Descrição: "Vazamento de óleo no chão"<br>4. Salvar |
| **Resultado esperado** | Incidente aparece na lista com status ABERTO e gravidade ALTO; surge no dashboard como aberto. |
| **Status esperado** | Aprovado |

#### Cenário CT-INC-02 — Registrar incidente com data futura (negativo)

| Campo | Valor |
|-------|-------|
| **ID** | CT-INC-02 |
| **Funcionalidade** | Incidentes — Validação |
| **História** | Como sistema, devo proteger a base de datas impossíveis. |
| **Prioridade** | Alta |
| **Pré-condições** | OPERADOR logado |
| **Passos** | 1. **Novo Incidente**<br>2. Data: amanhã<br>3. Salvar |
| **Resultado esperado** | Mensagem clara contendo "futuro" ("A data do incidente não pode estar no futuro"); cadastro bloqueado. |
| **Status esperado** | Aprovado |

#### Cenário CT-INC-03 — Registrar incidente sem nenhum vínculo (negativo)

| Campo | Valor |
|-------|-------|
| **ID** | CT-INC-03 |
| **Funcionalidade** | Incidentes — Validação |
| **História** | Como sistema, devo exigir vínculo para rastreabilidade. |
| **Prioridade** | Alta |
| **Pré-condições** | OPERADOR logado |
| **Passos** | 1. **Novo Incidente**<br>2. Preencher tipo, terminal, data e descrição<br>3. **Não selecionar** doca, vaga nem operação<br>4. Salvar |
| **Resultado esperado** | Mensagem clara mencionando "vínculo" ("É necessário informar pelo menos um vínculo: operação, doca ou vaga"); cadastro bloqueado. |
| **Status esperado** | Aprovado |

#### Cenário CT-INC-04 — Encerrar incidente aberto

| Campo | Valor |
|-------|-------|
| **ID** | CT-INC-04 |
| **Funcionalidade** | Incidentes — Encerrar |
| **História** | Como supervisor, quero registrar a ação tomada e encerrar a ocorrência. |
| **Prioridade** | Alta |
| **Pré-condições** | SUPERVISOR logado; incidente ABERTO existe |
| **Passos** | 1. Acessar `/incidentes`<br>2. Clicar **Encerrar** no incidente<br>3. No modal, preencher Ação Tomada: "Área isolada e limpa pela manutenção"<br>4. Confirmar |
| **Resultado esperado** | Status muda para RESOLVIDO; horário de encerramento preenchido; ação tomada visível ao reabrir o detalhe. |
| **Status esperado** | Aprovado |

#### Cenário CT-INC-05 — Tentar encerrar incidente já resolvido (negativo)

| Campo | Valor |
|-------|-------|
| **ID** | CT-INC-05 |
| **Funcionalidade** | Incidentes — Transição |
| **História** | Como sistema, devo impedir encerramento duplo. |
| **Prioridade** | Alta |
| **Pré-condições** | Incidente RESOLVIDO |
| **Passos** | 1. Tentar clicar **Encerrar** (se disponível) ou via API |
| **Resultado esperado** | Botão ausente para RESOLVIDO; via API retorna 409 "Incidente já encerrado". |
| **Status esperado** | Aprovado |

#### Cenário CT-INC-06 — Operador tenta encerrar (acesso negado)

| Campo | Valor |
|-------|-------|
| **ID** | CT-INC-06 |
| **Funcionalidade** | Incidentes — RBAC |
| **História** | Como sistema, devo restringir encerramento a perfis superiores. |
| **Prioridade** | Alta |
| **Pré-condições** | OPERADOR logado; incidente ABERTO |
| **Passos** | 1. Tentar localizar a ação **Encerrar** |
| **Resultado esperado** | Ação ausente ou desabilitada; via API retorna 403. |
| **Status esperado** | Aprovado |

#### Cenário CT-INC-07 — Filtros combinados na listagem

| Campo | Valor |
|-------|-------|
| **ID** | CT-INC-07 |
| **Funcionalidade** | Incidentes — Filtros |
| **História** | Como gestor, quero filtrar a lista para focar em ocorrências críticas. |
| **Prioridade** | Média |
| **Pré-condições** | Há incidentes em estados e gravidades variados |
| **Passos** | 1. Aplicar Status: ABERTO + Gravidade: ALTO + Terminal: Terminal Central |
| **Resultado esperado** | Lista retorna somente incidentes que satisfazem **todos** os filtros simultaneamente. |
| **Status esperado** | Aprovado |

---

### 8.12 Tela de detalhe da operação (integração F10+F11+F12)

#### Cenário CT-DET-01 — Detalhe agrega operação, documentos e incidentes

| Campo | Valor |
|-------|-------|
| **ID** | CT-DET-01 |
| **Funcionalidade** | Operação — Detalhe consolidado |
| **História** | Como gestor, quero ver tudo o que aconteceu numa operação numa única tela. |
| **Prioridade** | Alta |
| **Pré-condições** | Operação com 2 documentos e 1 incidente vinculado |
| **Passos** | 1. Acessar `/operacoes/{id}` |
| **Resultado esperado** | Página exibe: dados do veículo, terminal, doca/vaga, timeline, lista de documentos, lista de incidentes (ou indicação de quantos estão vinculados). |
| **Status esperado** | Aprovado |

---

### 8.13 Permissões e perfis (F13) — Matriz transversal

**História de Usuário:**
> Como **administrador**, quero **que o sistema imponha automaticamente as fronteiras de cada perfil**, para **garantir que cada funcionário só execute o que sua função permite**.

#### Matriz consolidada de permissões

| Ação | ADMIN | SUPERVISOR | OPERADOR | SEGURANCA |
|------|:-----:|:----------:|:--------:|:---------:|
| Login + visualizar telas | ✅ | ✅ | ✅ | ✅ |
| CRUD de Terminais / Tipos de Incidente / Usuários | ✅ | ❌ | ❌ | ❌ |
| CRUD de Docas / Estacionamentos / Vagas | ✅ | ✅ | ❌ | ❌ |
| Alterar status de doca/vaga | ✅ | ✅ | ✅ | ❌ |
| CRUD de Veículos | ✅ | ✅ | ✅ | ❌ |
| Check-in / Checkout | ✅ | ❌ | ✅ | ✅ |
| Iniciar / Finalizar operação | ✅ | ✅ | ✅ | ❌ |
| Cancelar operação | ✅ | ✅ | ❌ | ❌ |
| Registrar incidente | ✅ | ✅ | ✅ | ✅ |
| Encerrar incidente | ✅ | ✅ | ❌ | ❌ |

#### Cenário CT-RBAC-01 — Verificação completa por perfil

| Campo | Valor |
|-------|-------|
| **ID** | CT-RBAC-01 |
| **Funcionalidade** | RBAC — Verificação consolidada |
| **História** | Como sistema, devo aplicar a matriz de permissões em todas as telas. |
| **Prioridade** | Alta |
| **Pré-condições** | Os 4 perfis disponíveis |
| **Passos** | Para cada perfil, logar e: 1. Verificar quais itens aparecem no menu lateral<br>2. Tentar cada ação marcada como ❌ na matriz<br>3. Verificar resposta da interface (botão oculto/desabilitado) e da API (403) |
| **Resultado esperado** | Todas as ❌ resultam em ação bloqueada na interface E no back-end. |
| **Status esperado** | Aprovado |

---

## 9. Cenários transversais

### 9.1 Segurança

| ID | Cenário | Resultado esperado |
|----|---------|---------------------|
| CT-SEG-01 | Acessar `/dashboard` sem token | Redireciona para `/login` |
| CT-SEG-02 | Adulterar um caractere do token JWT no `localStorage` | Próxima requisição retorna 401 e força login |
| CT-SEG-03 | Logar como SEGURANCA e tentar abrir `/usuarios` via URL direta | Redireciona ou exibe 403 |
| CT-SEG-04 | Inserir texto com aspas simples e tags HTML no campo "Descrição" do incidente | Texto é persistido literalmente; ao exibir, não executa script (sem XSS) |
| CT-SEG-05 | Conferir no banco que `senha_hash` está em formato BCrypt | Valores começam com `$2a$` ou `$2b$` |

### 9.2 Usabilidade e navegação

| ID | Cenário | Resultado esperado |
|----|---------|---------------------|
| CT-UX-01 | Após cada ação de sucesso, surge feedback visual ao usuário | Toast ou mensagem inline sempre presente |
| CT-UX-02 | Indicador de carregamento durante chamadas à API | Botão fica em loading; usuário não consegue duplo-clicar |
| CT-UX-03 | Mensagens de erro são exibidas em PT-BR e contextualizadas | Sem stack trace, sem termos como "constraint", "exception" |
| CT-UX-04 | Navegação por teclado funciona em campos de formulário | Tab move entre campos; Enter submete |
| CT-UX-05 | Modais fecham com Esc | Ao pressionar Esc, modal fecha sem salvar |
| CT-UX-06 | Layout permanece consistente em 1280×720 (laptop padrão) | Sem sobreposição de elementos, sem scroll horizontal |

### 9.3 Condições limite e entradas inválidas

| ID | Cenário | Resultado esperado |
|----|---------|---------------------|
| CT-LIM-01 | Nome de terminal com 1 caractere | Aceito (não há limite mínimo definido) ou erro inline claro se houver |
| CT-LIM-02 | Nome de terminal com 256 caracteres | Erro inline indicando tamanho máximo |
| CT-LIM-03 | Placa de veículo com formato Mercosul ("ABC1D23") | Aceita (sistema é permissivo) |
| CT-LIM-04 | Placa com caracteres especiais ("ABC@123") | Erro inline orientando formato |
| CT-LIM-05 | Descrição de incidente em branco | Erro inline obrigatoriedade |
| CT-LIM-06 | Filtro de operações em página inexistente (`page=999`) | Retorna lista vazia, sem erro |
| CT-LIM-07 | Dashboard com banco vazio (sem docas) | Exibe contadores em 0; taxa de ocupação = 0%; sem divisão por zero |

### 9.4 Recuperação de erro

| ID | Cenário | Resultado esperado |
|----|---------|---------------------|
| CT-REC-01 | Submeter formulário com erro de validação | Usuário consegue corrigir o campo e submeter novamente sem reabrir o modal |
| CT-REC-02 | API ficar indisponível durante uso (parar o back-end) | Mensagem clara de falha de comunicação; ao voltar a API, próxima ação funciona |
| CT-REC-03 | Tentar duas vezes a mesma ação destrutiva (duplo-clique) | Ação executa uma única vez (botão fica em loading) |

---

## 10. Modelo de relatório de defeito

```
ID do defeito: BUG-XXX
Cenário de origem: CT-XX-XX
Data de detecção: AAAA-MM-DD
Detectado por: <nome do testador>
Perfil de teste: <ADMIN | SUPERVISOR | OPERADOR | SEGURANCA>

Severidade: Bloqueante | Alta | Média | Baixa
Reprodutibilidade: Sempre | Intermitente | Uma vez

Descrição:
  <descrição objetiva e curta do problema>

Passos para reproduzir:
  1.
  2.
  3.

Resultado obtido:
  <o que o sistema fez de fato>

Resultado esperado:
  <o que deveria ter acontecido conforme o cenário>

Evidências:
  - Captura de tela: <arquivo>
  - Log do navegador (Console): <trecho>
  - Resposta da API: <JSON ou status>
  - traceId (se houver): <valor>

Ambiente:
  - Sistema operacional:
  - Navegador + versão:
  - Versão do código (commit):

Status: Aberto | Em correção | Reaberto | Fechado
```

---

## 11. Tabela consolidada de execução

Marcar com ✅ aprovado, ❌ reprovado, ⏭️ não aplicável, ⬜ não executado.

| ID | Funcionalidade | Prioridade | Status | Observação |
|----|----------------|:----------:|:------:|------------|
| CT-AUTH-01 | Autenticação | Alta | ⬜ | |
| CT-AUTH-02 | Autenticação | Alta | ⬜ | |
| CT-AUTH-03 | Autenticação | Alta | ⬜ | |
| CT-AUTH-04 | Autenticação | Média | ⬜ | |
| CT-AUTH-05 | Autenticação | Média | ⬜ | |
| CT-AUTH-06 | Autenticação | Alta | ⬜ | |
| CT-DASH-01 | Dashboard | Alta | ⬜ | |
| CT-DASH-02 | Dashboard | Média | ⬜ | |
| CT-DASH-03 | Dashboard | Alta | ⬜ | |
| CT-TERM-01 | Terminais | Alta | ⬜ | |
| CT-TERM-02 | Terminais | Alta | ⬜ | |
| CT-TERM-03 | Terminais | Média | ⬜ | |
| CT-TERM-04 | Terminais | Alta | ⬜ | |
| CT-TERM-05 | Terminais | Média | ⬜ | |
| CT-TERM-06 | Terminais | Alta | ⬜ | |
| CT-DOCA-01 | Docas | Alta | ⬜ | |
| CT-DOCA-02 | Docas | Alta | ⬜ | |
| CT-DOCA-03 | Docas | Média | ⬜ | |
| CT-DOCA-04 | Docas | Alta | ⬜ | |
| CT-DOCA-05 | Docas | Alta | ⬜ | |
| CT-EST-01 | Estacionamentos | Média | ⬜ | |
| CT-VAGA-01 | Vagas | Média | ⬜ | |
| CT-VAGA-02 | Vagas | Média | ⬜ | |
| CT-VEI-01 | Veículos | Alta | ⬜ | |
| CT-VEI-02 | Veículos | Média | ⬜ | |
| CT-VEI-03 | Veículos | Alta | ⬜ | |
| CT-VEI-04 | Veículos | Alta | ⬜ | |
| CT-USR-01 | Usuários | Alta | ⬜ | |
| CT-USR-02 | Usuários | Alta | ⬜ | |
| CT-USR-03 | Usuários | Alta | ⬜ | |
| CT-USR-04 | Usuários | Alta | ⬜ | |
| CT-USR-05 | Usuários | Alta | ⬜ | |
| CT-USR-06 | Usuários | Alta | ⬜ | |
| CT-TIP-01 | Tipos de Incidente | Alta | ⬜ | |
| CT-TIP-02 | Tipos de Incidente | Média | ⬜ | |
| CT-TIP-03 | Tipos de Incidente | Alta | ⬜ | |
| CT-OP-01 | Operações — Check-in | Alta | ⬜ | |
| CT-OP-02 | Operações — Validação | Alta | ⬜ | |
| CT-OP-03 | Operações — Check-in vaga | Alta | ⬜ | |
| CT-OP-04 | Operações — Validação | Alta | ⬜ | |
| CT-OP-05 | Operações — Iniciar | Alta | ⬜ | |
| CT-OP-06 | Operações — Transição | Alta | ⬜ | |
| CT-OP-07 | Operações — Finalizar | Alta | ⬜ | |
| CT-OP-08 | Operações — Checkout | Alta | ⬜ | |
| CT-OP-09 | Operações — Validação | Alta | ⬜ | |
| CT-OP-10 | Operações — Cancelar | Alta | ⬜ | |
| CT-OP-11 | Operações — Transição | Alta | ⬜ | |
| CT-OP-12 | Operações — Fluxo completo | **Alta** | ⬜ | UAT crítico |
| CT-OP-13 | Operações — Filtros | Média | ⬜ | |
| CT-OP-14 | Operações — Detalhe | Média | ⬜ | |
| CT-DOC-01 | Documentos | Alta | ⬜ | |
| CT-DOC-02 | Documentos | Média | ⬜ | |
| CT-DOC-03 | Documentos | Média | ⬜ | |
| CT-DOC-04 | Documentos | Média | ⬜ | |
| CT-INC-01 | Incidentes | Alta | ⬜ | |
| CT-INC-02 | Incidentes | Alta | ⬜ | |
| CT-INC-03 | Incidentes | Alta | ⬜ | |
| CT-INC-04 | Incidentes | Alta | ⬜ | |
| CT-INC-05 | Incidentes | Alta | ⬜ | |
| CT-INC-06 | Incidentes | Alta | ⬜ | |
| CT-INC-07 | Incidentes | Média | ⬜ | |
| CT-DET-01 | Detalhe operação | Alta | ⬜ | |
| CT-RBAC-01 | Permissões | Alta | ⬜ | |
| CT-SEG-01 a 05 | Segurança | Alta | ⬜ | |
| CT-UX-01 a 06 | Usabilidade | Média | ⬜ | |
| CT-LIM-01 a 07 | Condições limite | Média | ⬜ | |
| CT-REC-01 a 03 | Recuperação | Média | ⬜ | |

---

## Resumo final

| Indicador | Valor |
|-----------|-------|
| Total de cenários documentados | 70+ |
| Funcionalidades cobertas | 13 |
| Histórias de usuário | 13 |
| Cenários positivos | ~ 40 |
| Cenários negativos | ~ 25 |
| Cenários transversais | ~ 20 |

**Tempo estimado para execução completa:** 1 testador, ~ 12 a 16 horas distribuídas em 3 a 4 dias.

---

*Documento mantido junto ao código-fonte. Última atualização: 2026-05-20.*
