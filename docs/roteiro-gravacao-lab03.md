# Roteiro da gravação: Lab 03 (DTOs, autenticação e autorização)

Duração estimada: 12 a 15 minutos. Cada bloco traz **o que mostrar**, **o que enviar no Postman** e **o que falar**. As falas são tópicos, não texto para ler.

---

## Antes de gravar

1. **Banco limpo** (para os ids baterem com o roteiro): pare a aplicação e rode no Workbench
   ```sql
   DROP DATABASE dbimobiliaria;
   CREATE DATABASE dbimobiliaria;
   ```
2. Suba a aplicação. No console devem aparecer as migrations **V1 a V12**.
3. Deixe abertos:
   - **VS Code** com o projeto;
   - **Postman** com a coleção `Imobiliaria`, pasta `6 Roteiro Lab 03` expandida;
   - **Workbench** com uma aba de consulta pronta:
     ```sql
     SELECT login, senha, id_cliente FROM usuarios;
     ```
4. No Postman, mande as requisições **uma por uma** (botão Send) durante a gravação. O Runner fica para o fechamento.

---

## 1. Abertura (30 s)

**Falar:**
- O Lab 03 evolui a API da imobiliária em duas frentes: **DTOs**, para separar o que a API expõe das entidades JPA, e **Spring Security**, com autenticação HTTP Basic e autorização por perfil (ADMIN, ATENDENTE e CLIENTE).
- As regras de negócio, as validações e o tratamento de erros dos labs anteriores foram mantidos.

---

## 2. Parte 1: DTOs (3 min)

**Mostrar no VS Code:**
1. Pasta `api/dto`: um **Input** e um **Output** por entidade, todos como `record`.
2. `ClienteInput` × `ClienteOutput`:
   - o Input **não tem `id`** e concentra o Bean Validation;
   - o Output tem o `id` e nenhuma validação.
3. `LocacaoInput`:
   - **não tem `valor_total`**, que é calculado pelo service; antes era preciso um `@JsonProperty(READ_ONLY)` na entidade;
   - `ReferenciaInput` (`{"id": 1}`) substituiu os `ValidationGroups` e o `@ConvertGroup`.
4. `LocacaoOutput`: inquilino e imóvel vêm **resumidos**, sem CPF, telefone ou e-mail.
5. `ClienteMapper`: mapeamento **manual**, com um `@Component`.
6. `ClienteController`: o padrão de todo endpoint é **converte a entrada → chama o service → converte a saída**.
7. `ImovelController`: `Page.map(mapper::toOutput)` na paginação e `ImovelFiltro` (`@ModelAttribute`) no Query by Example.
8. Entidade `Cliente`: só o mapeamento do banco, sem anotações de validação.

**Enviar no Postman** (pasta `5 DTOs`):

| Requisição | Mostrar |
|---|---|
| 5.1 Id enviado no POST e ignorado | o corpo manda `"id": 1` e a resposta traz outro id |
| 5.2 Cliente 1 continua intacto | o cliente 1 não foi sobrescrito |
| 5.3 Locação mostra o inquilino resumido | `inquilino` só com `id` e `nome_cliente` |

**Falar:**
- **Por que o service continua com entidades?** DTO é assunto da API. A regra de negócio não depende do formato do JSON.
- **Por que mapeamento manual?** Ele deixa explícito o que entra e o que sai. Além disso, o ModelMapper se atrapalha com os atributos em `snake_case`.
- **Por que tirar a validação da entidade?** Para ter um único ponto de verdade. Se a regra ficasse nos dois lugares, uma falha na entidade viraria `ConstraintViolationException` e sairia um 500.

---

## 3. Banco: usuários e perfis (1 min 30 s)

**Mostrar no VS Code:**
1. `V11__create_tables_usuarios_perfis.sql`:
   - `perfis`, `usuarios` e a tabela de junção `usuarios_perfis` (N:N, chave primária composta);
   - `usuarios.id_cliente`: opcional, `UNIQUE`, com FK para `clientes`. É o vínculo que permite saber quais locações são do cliente logado.
2. `V12__insert_usuarios_iniciais.sql`: os hashes BCrypt.

**Mostrar no Workbench:** rodar `SELECT login, senha, id_cliente FROM usuarios;`

**Falar (cenário 2.4.7):**
- As senhas estão guardadas como hash BCrypt (`$2a$10$...`), nunca em texto puro.
- Os quatro usuários têm **a mesma senha** e **hashes diferentes**: é o **salt**, sorteado a cada hash e guardado dentro dele.
- `ana` e `bruno` têm `id_cliente`; `admin` e `atendente`, não.

---

## 4. Autenticação HTTP Basic (2 min)

**Mostrar no VS Code:**
1. `pom.xml`: `spring-boot-starter-security`.
2. `Usuario` e `Perfil`: `@ManyToMany` com `@JoinTable` e perfis carregados com `EAGER`.
3. `UsuarioDetailsService`: busca pelo login e devolve o hash e os perfis (`roles()` acrescenta o prefixo `ROLE_`).
4. `SecurityConfig`:
   - `csrf` desligado e sessão `STATELESS`;
   - `httpBasic`;
   - `PasswordEncoder` com BCrypt.

**Enviar no Postman** (pasta `1 Autenticacao`):

| Requisição | Esperado | Cenário |
|---|---|---|
| 1.1 Sem credenciais | 401 + header `WWW-Authenticate` | 2.4.1 |
| 1.2 Senha incorreta | 401 | 2.4.2 |
| 1.3 Usuário inexistente | 401, **mesma mensagem** do 1.2 | 2.4.2 |
| 1.4 Credenciais válidas | 200 | 2.4.3 |

**Falar:**
- Quem compara a senha é o Spring, com o `PasswordEncoder`. O nosso código nunca vê a senha em texto.
- Login inexistente e senha errada dão a mesma resposta, para não revelar quais logins existem.
- O HTTP Basic manda `login:senha` em **Base64**, que não é criptografia. Em produção, ele só deve ser usado com **HTTPS**.

---

## 5. Autorização por perfil (3 min)

**Mostrar no VS Code:**
1. `SecurityConfig`, no bloco `authorizeHttpRequests`: a matriz de permissões.
   - A **ordem importa**: vale a primeira regra que casar.
   - `DELETE /**` fica antes das regras de imóveis; por isso o atendente não exclui.
2. `SegurancaErroHandler`:
   - **401** (`AuthenticationEntryPoint`) e **403** (`AccessDeniedHandler`) no mesmo JSON do `ApiExceptionHandler`;
   - não dá para usar o `@RestControllerAdvice`, porque a segurança barra **nos filtros**, antes do controller.

**Decisões que valem citar** (pontos que a matriz do PDF não define):
- `DELETE` é só do ADMIN, porque excluir é irreversível.
- CLIENTE não consulta `/clientes`, que tem os dados pessoais de todos.
- Aluguéis ficam com ADMIN e ATENDENTE.
- Consultar locações (*"conforme regra definida"*): o ATENDENTE vê **todas**, porque o atendimento precisa achar qualquer contrato.

**Enviar no Postman** (pasta `2 Permissoes por perfil`):

| Requisição | Esperado | Cenário |
|---|---|---|
| 2.1 Atendente cadastra imóvel | 201 | 2.4.3 |
| 2.2 Cliente tenta cadastrar imóvel | 403 | 2.4.5 |
| 2.3 Cliente tenta consultar clientes | 403 | |
| 2.5 Atendente tenta excluir imóvel | 403 | 2.4.5 |
| 2.8 Atendente tenta listar usuários | 403 | 2.4.4 |
| 2.9 Cliente tenta criar administrador | 403 | 2.1.8 |
| 2.10 Admin exclui imóvel | 204 | |

(2.4, 2.6 e 2.7 podem ser pulados no vídeo.)

**Falar:**
- **401** quer dizer "não sei quem você é". **403** quer dizer "sei quem você é, mas o seu perfil não pode" (requisito 2.2.6).

---

## 6. Cada cliente só acessa as próprias locações (2 min)

**Mostrar no VS Code:**
1. `UsuarioLogado`: lê o `SecurityContextHolder` e busca o `id_cliente` do login.
2. `LocacaoService`:
   - `listarTodos` (filtro `findByInquilinoId`);
   - `verificarAcesso` dentro do `buscarPorId`;
   - no `salvar`: só para si mesmo, e nasce pendente.
3. `AcessoNegadoException` + handler 403. A `AccessDeniedException` do Spring seria engolida pelo handler genérico e viraria 500.

**Enviar no Postman** (pasta `3 Locacoes do cliente`):

| Requisição | Esperado |
|---|---|
| 3.1 Ana lista as locações | só as dela |
| 3.3 Atendente lista todas | todas |
| 3.5 Ana consulta locação do Bruno | **403** (cenário 2.4.6) |
| 3.6 Ana cadastra locação para o Bruno | **403** |
| 3.7 Ana cadastra locação para si | 201, `ativo: false` mesmo enviando `true` |
| 3.8 Atendente confirma | 200, `ativo: true` |
| 3.9 Ana tenta excluir | 403 |
| 3.10 Admin exclui a locação de teste | 204 |

**Falar:**
- A regra da URL não sabe **de quem é** a locação 2. Isso depende do dado, então a verificação fica no service. É uma defesa em camadas.
- Locação feita por cliente é uma **solicitação**. Quem confirma, e ocupa a agenda do imóvel, é a imobiliária.

---

## 7. Gerenciamento de usuários (2 min 30 s)

**Mostrar no VS Code:**
1. `UsuarioInput` (senha obrigatória) × `UsuarioAtualizacaoInput` (senha opcional) × `UsuarioOutput` (**sem senha**).
2. `UsuarioService`:
   - `passwordEncoder.encode` antes do `save`;
   - vínculo CLIENTE ↔ cliente;
   - ninguém altera os próprios perfis nem se exclui (requisito 2.3.6).

**Enviar no Postman** (pasta `4 Usuarios e senhas`):

| Requisição | Esperado | Cenário |
|---|---|---|
| 4.1 Admin lista usuários | 200, **sem campo de senha** | 2.4.8 |
| 4.2 Admin cria usuário cliente (Carla) | 201, sem senha | |
| ↳ **Workbench:** rodar o `SELECT` de novo | a Carla aparece com hash | 2.4.7 |
| 4.3 Carla entra e vê só as locações dela | 200 | |
| 4.5 Perfil CLIENTE sem cliente | 409 | |
| 4.8 Sem senha | 400 | |
| 4.9 Admin tenta alterar os próprios perfis | 403 | 2.3.6 |
| 4.10 Admin tenta excluir a si mesmo | 403 | |
| 4.11 Admin redefine a senha da Carla | 200 | |
| 4.12 Carla com a senha antiga | 401 | |
| 4.13 Carla com a senha nova | 200 | |
| 4.14 Admin exclui a Carla | 204 | |

**Falar:**
- Só o ADMIN cria usuários e não existe cadastro público. Por isso ninguém consegue se promover a administrador (requisito 2.1.8).
- O `PasswordEncoder` do cadastro é o mesmo bean que o login usa para conferir a senha.

---

## 8. Fechamento (1 min)

**No Postman:** clique com o botão direito em `6 Roteiro Lab 03` → **Run** → todas as 42 requisições verdes.

**Falar (resumo):**
- DTOs separam a API das entidades: o cliente da API não controla `id` nem `valor_total`, e as respostas não expõem dados pessoais nem senhas.
- A autenticação é feita por HTTP Basic, com os usuários no banco e as senhas em BCrypt.
- A autorização tem duas camadas: **por URL e perfil**, no `SecurityConfig`, e **por dono do dado**, no `LocacaoService`.
- 401 e 403 são distintos e saem no mesmo formato de erro do resto da API.

---

## Requisitos do PDF × onde aparecem no vídeo

| Requisito | Bloco |
|---|---|
| 1.1 a 1.8 (DTOs) | 2 |
| 2.1.1 a 2.1.7 (HTTP Basic, entidades, BCrypt) | 3 e 4 |
| 2.1.8 (sem criar admin indevidamente) | 5 (2.9) e 7 |
| 2.2.1 a 2.2.4 (regras por perfil) | 5 |
| 2.2.5 (locações de outro cliente) | 6 |
| 2.2.6 (401 × 403) | 4 e 5 |
| 2.3.1 a 2.3.6 (persistência de usuários) | 3 e 7 |
| 2.4.1 a 2.4.8 (testes) | 4, 5, 6 e 7 (2.4.7 no Workbench) |
