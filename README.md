# Imobiliária API

> API REST de um sistema de imobiliária, com clientes, imóveis, locações e aluguéis, autenticação e permissões por perfil. Desenvolvida nos Labs 01, 02 e 03 da disciplina **Desenvolvimento Web II** (Sistemas de Informação, DCOMP/IFMA).

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?logo=flyway&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?logo=postman&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)

---

## Destaques

- **Regras de negócio reais na camada de serviço:** a API impede locações sobrepostas do mesmo imóvel, calcula o valor da locação automaticamente e protege o histórico na exclusão de imóveis.
- **Autenticação e autorização em duas camadas:** HTTP Basic com senhas em BCrypt, permissões por perfil (ADMIN, ATENDENTE, CLIENTE) e restrição por dono do dado. Um cliente só vê as próprias locações.
- **DTOs com mappers:** as entidades JPA nunca saem da API. Campos como `id` e `valor_total` não podem ser enviados pelo cliente, e a senha nunca aparece nas respostas.
- **Erros padronizados:** todas as respostas de erro têm o mesmo formato, incluindo as que o Spring Security barra antes de chegar ao controller.
- **Banco versionado com Flyway** (12 migrations) e **coleção do Postman** com os cenários de teste de cada lab.

---

## Sumário

- [Tecnologias](#tecnologias)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Como rodar](#como-rodar)
- [Autenticação](#autenticação)
- [Permissões](#permissões)
- [Endpoints](#endpoints)
- [DTOs](#dtos)
- [Regras de negócio](#regras-de-negócio)
- [Respostas de erro](#respostas-de-erro)
- [Testes com Postman](#testes-com-postman)
- [Limitação conhecida](#limitação-conhecida)

---

## Tecnologias

| Camada | Tecnologias |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4 · Spring Web MVC |
| Persistência | Spring Data JPA · MySQL · Flyway |
| Validação | Bean Validation |
| Segurança | Spring Security (HTTP Basic + BCrypt) |
| Testes manuais | Postman |

---

## Estrutura do projeto

```
src/main/java/ifma/edu/br/imobiliariasystem/
├── api/
│   ├── controller/          # endpoints REST (Cliente, Imovel, Locacao, Aluguel, Usuario)
│   ├── controlleradvice/    # ApiExceptionHandler e formato padrão de erro
│   ├── dto/                 # records de entrada (Input) e saída (Output)
│   └── mapper/              # conversão manual entre DTO e entidade
├── domain/
│   ├── model/               # entidades JPA
│   ├── repository/          # interfaces Spring Data
│   ├── service/             # regras de negócio
│   └── exception/           # exceções de domínio (404, 409, 403)
└── security/                # SecurityConfig, usuário logado e erros de segurança

src/main/resources/
├── application.properties   # lê as credenciais do .env
└── db/migration/            # migrations Flyway (V1 a V12)

postman/collections/Imobiliaria/   # coleção de requisições e roteiros de teste
```

---

## Como rodar

**Pré-requisitos:** Java 21 e MySQL.

1. Crie o banco no MySQL:
   ```sql
   CREATE DATABASE dbimobiliaria;
   ```
2. Copie o `.env.example` para `.env` na raiz do projeto e preencha as variáveis. O `.env` não vai para o Git.
   ```properties
   DB_NAME=dbimobiliaria
   DB_USERNAME=seu_usuario
   DB_PASSWORD=sua_senha
   ```
3. Rode a aplicação a partir da raiz do projeto:
   ```bash
   ./mvnw spring-boot:run
   ```
   O Flyway cria as tabelas e insere os dados iniciais (migrations V1 a V12).
4. A API sobe em `http://localhost:8080`. Todas as requisições exigem login (veja [Autenticação](#autenticação)).

---

## Autenticação

A API usa **HTTP Basic**: cada requisição envia login e senha no header `Authorization`, e o servidor não guarda sessão. Os usuários e perfis ficam no banco, e as senhas são guardadas com hash **BCrypt**, nunca em texto puro.

A migration V12 cria quatro usuários de teste, todos com a senha `senha123`:

| Login | Perfil | Cliente vinculado |
|---|---|---|
| `admin` | ADMIN | |
| `atendente` | ATENDENTE | |
| `ana` | CLIENTE | Ana Beatriz Souza (id 1) |
| `bruno` | CLIENTE | Bruno Carvalho (id 2) |

> O HTTP Basic envia as credenciais apenas codificadas em Base64, que não é criptografia. Fora do ambiente de desenvolvimento, use sempre HTTPS.

---

## Permissões

| Funcionalidade | ADMIN | ATENDENTE | CLIENTE |
|---|---|---|---|
| Gerenciar usuários e perfis (`/usuarios`) | Sim | Não | Não |
| Consultar imóveis | Sim | Sim | Sim |
| Cadastrar e atualizar imóveis | Sim | Sim | Não |
| Consultar, cadastrar e atualizar clientes | Sim | Sim | Não |
| Consultar locações | Todas | Todas | Apenas as próprias |
| Cadastrar locações | Sim | Sim | Apenas para si (nasce pendente) |
| Atualizar, confirmar e cancelar locações | Sim | Sim | Não |
| Aluguéis (cobranças) | Sim | Sim | Não |
| Excluir qualquer recurso | Sim | Não | Não |

A autorização funciona em duas camadas:

1. **Por URL e perfil**, no `SecurityConfig`. As regras são avaliadas em ordem, e vale a primeira que casar com a requisição.
2. **Por dono do dado**, no `LocacaoService`. Um CLIENTE só lista, consulta e cadastra locações em que ele é o inquilino. O vínculo entre login e cliente é a coluna `usuarios.id_cliente`.

Regras do gerenciamento de usuários:

- Só o ADMIN cria usuários. Não existe cadastro público, então ninguém se promove a administrador.
- A senha é enviada em texto, transformada em hash pelo `PasswordEncoder` antes de ser salva e nunca aparece nas respostas.
- Um usuário com perfil CLIENTE precisa estar vinculado a um cliente, e cada cliente tem no máximo um usuário. Usuários sem esse perfil não podem ter vínculo.
- Ninguém altera os próprios perfis nem exclui o próprio usuário, inclusive o ADMIN.
- No `PUT`, a senha é opcional. Se não for enviada, a senha atual é mantida.

---

## Endpoints

| Recurso | Endpoints |
|---|---|
| Clientes | `GET/POST /clientes`, `GET/PUT/DELETE /clientes/{id}` |
| Imóveis | `GET/POST /imoveis`, `GET/PUT/DELETE /imoveis/{id}` |
| | `GET /imoveis/paginado?page=0&size=5&sort=metragem,desc` (máximo de 20 itens por página) |
| | `GET /imoveis/busca?tipo=&cidade=&dormitorios=` (Query by Example, também paginado) |
| Locações | `GET/POST /locacoes`, `GET/PUT/DELETE /locacoes/{id}` |
| | `PUT /locacoes/{id}/confirmar`, `PUT /locacoes/{id}/cancelar` |
| Aluguéis | `GET/POST /alugueis`, `GET/PUT/DELETE /alugueis/{id}` |
| Usuários | `GET/POST /usuarios`, `GET/PUT/DELETE /usuarios/{id}` |

Nos JSONs, os campos usam os nomes dos atributos (`nome_cliente`, `data_inicio`...). Datas no formato `AAAA-MM-DD`. Relacionamentos são informados pelo id: `"inquilino": { "id": 1 }`.

Exemplo de cadastro de usuário (só ADMIN):

```json
{
  "login": "carla",
  "senha": "carla123",
  "perfis": ["CLIENTE"],
  "cliente": { "id": 3 }
}
```

---

## DTOs

Os controllers recebem e devolvem DTOs (`record`, no pacote `api/dto`). As entidades JPA ficam restritas às camadas internas.

- **Input:** define o que o cliente da API pode enviar e concentra o Bean Validation. Não tem `id` nem valores calculados (`valor_total`). Por isso, esses campos são ignorados se vierem no JSON.
- **Output:** define o que a API devolve. Os relacionamentos vêm resumidos: na locação, o inquilino aparece só com `id` e `nome_cliente`, sem CPF, telefone ou e-mail. Usuários nunca saem com a senha.
- **Mappers** (`api/mapper`): conversão manual entre DTO e entidade. Os services continuam recebendo e devolvendo entidades, e as regras de negócio não dependem do formato do JSON.

---

## Regras de negócio

- **Cliente:** CPF e e-mail únicos.
- **Imóvel:** o número de suítes não pode passar do número de dormitórios.
- **Locação:**
  - `ativo = true` significa locação confirmada. Uma locação nasce confirmada, a menos que o JSON envie `"ativo": false` (pendente). Locações cadastradas por um CLIENTE sempre nascem pendentes e são confirmadas pela imobiliária.
  - A data de fim é obrigatória e deve ser posterior à data de início.
  - O `valor_total` é calculado pelo sistema: `valor_diaria` do imóvel × quantidade de diárias. As diárias são os dias entre o início e o fim, e o dia de saída não é cobrado.
  - **Disponibilidade:** não pode haver duas locações confirmadas do mesmo imóvel com períodos sobrepostos. A verificação acontece ao cadastrar, ao atualizar e ao confirmar. Uma locação pode começar no dia em que a outra termina.
- **Exclusão de imóvel:**
  - Um imóvel com locação confirmada em andamento ou futura não pode ser excluído (regra de negócio, 409).
  - Um imóvel que só tem locações encerradas ou canceladas também não é excluído: a chave estrangeira (`ON DELETE RESTRICT`) preserva o histórico.
  - Só um imóvel que nunca foi locado pode ser excluído.
- **Aluguel:**
  - Uma cobrança nova só pode ser lançada para locação confirmada.
  - O vencimento deve estar dentro do período da locação.
  - Uma parcela existente pode ser atualizada (por exemplo, quitada) mesmo que a locação tenha sido cancelada.
- **Cliente com usuário:** não pode ser excluído (FK `usuarios.id_cliente`, 409).

---

## Respostas de erro

Todos os erros têm o mesmo formato (`status`, `titulo`, `mensagem`, `dataHora` e, na validação, `campos`). Os erros da API passam pelo `ApiExceptionHandler`. Os de segurança passam pelo `SegurancaErroHandler`, porque são barrados nos filtros do Spring Security, antes de chegar ao controller.

| Situação | Status |
|---|---|
| Validação (Bean Validation), JSON malformado, parâmetro ou ordenação inválidos | 400 |
| Sem credenciais, login inexistente ou senha errada | 401 |
| Perfil sem permissão, ou acesso a locação de outro cliente | 403 |
| Registro, perfil ou endpoint inexistente | 404 |
| Verbo HTTP não suportado | 405 |
| Regra de negócio ou restrição do banco (FK, UNIQUE) | 409 |
| Corpo que não é JSON | 415 |

---

## Testes com Postman

A coleção está em `postman/collections/Imobiliaria`. Ela usa o usuário `admin` por padrão e tem uma pasta por recurso (Clientes, Imóveis, Locações, Aluguéis), além dos roteiros de cada lab:

- **5 Roteiro Lab 02:** paginação, Query by Example, cálculo do valor, disponibilidade, exclusão de imóvel, aluguéis e erros gerais. Rode em sequência com o banco recém-criado, porque os ids dependem da ordem.
- **6 Roteiro Lab 03:** autenticação, permissões por perfil, locações do cliente, usuários e senhas, e DTOs. Cada requisição informa o próprio usuário. A pasta exclui tudo o que cria, então pode rodar depois do Lab 02 ou mais de uma vez.

---

## Limitação conhecida

Os atributos das entidades seguem os nomes das colunas (snake_case). O Spring Data interpreta `_` como navegação entre propriedades, então a ordenação da paginação não funciona para campos com underscore (`valor_diaria`, `tipo_imovel`). Ela funciona para `id`, `cidade`, `metragem`, `dormitorios`, `banheiros`, `suites`, `endereco` e `cep`.

---

## Autor

Desenvolvido por [Arthur Moreira](https://github.com/ArthurMoreira79) · [LinkedIn](https://www.linkedin.com/in/arthur-moreira79)

Distribuído sob a licença [MIT](LICENSE).
