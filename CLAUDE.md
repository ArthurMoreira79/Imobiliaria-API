# CLAUDE.md — imobiliariasystem

## Contexto

Projeto do **Lab 01 de Desenvolvimento Web II** (Sistemas de Informação, DCOMP/IFMA): uma API REST para um fragmento de sistema de imobiliária. O aluno é o Arthur, que está aprendendo Spring Boot com este projeto.

Requisitos do lab:
1. API com Spring Boot, Spring Data e Spring Web (REST).
2. Entidades **cliente**, **imovel** e **locacao**, cada uma com model, repository e service.
3. Controllers REST para as três entidades.
4. Verbos HTTP: GET (todos e por `{id}`), POST, PUT e, opcionalmente, DELETE.
5. Banco de dados criado por migrations com **Flyway**.
6. Validações com **Bean Validation** e tratamento centralizado de erros com `@ExceptionHandler`.

Código de referência do professor: `github.com/jcpinheiro/frete-api-2025` (pasta `frete_v3_validacao`; a `frete_v7_modelmapper` mostra uma evolução com DTOs e ModelMapper, que não é obrigatória).

## Modo de trabalho

O Arthur pediu que o Claude atue como **professor**: prefira explicar o conceito, apontar onde está o problema e fazer perguntas que levem à solução, em vez de entregar código pronto. Quando ele **pedir explicitamente** que o código seja escrito, escreva, mas explique as decisões. Responda em português.

## Stack

- Spring Boot 3, Maven, Java
- Dependências: Spring Web, Spring Data JPA, Validation, Flyway (`flyway-core` + `flyway-mysql`), MySQL Driver, DevTools
- **Sem Lombok** (getters e setters escritos à mão) e **sem Spring Security** (não é requisito)
- Banco: MySQL local, porta 3306, banco `dbimobiliaria`
- Testes manuais: Postman (coleção salva na pasta `postman/` do projeto)
- Versionamento: GitHub Desktop

## Segurança das credenciais

- As credenciais ficam no arquivo `.env` na raiz, **fora do Git** (listado no `.gitignore`). O `.env.example` tem as mesmas chaves sem valores e vai para o repositório.
- O `application.properties` importa o `.env` com `spring.config.import=optional:file:.env[.properties]` e usa `${DB_NAME}`, `${DB_USERNAME}` e `${DB_PASSWORD}`.
- **Nunca abra, exiba ou copie o conteúdo do `.env`.** Nunca escreva credenciais em nenhum arquivo versionado.
- Se aparecer `Could not resolve placeholder`, a aplicação provavelmente foi executada fora da raiz do projeto e não encontrou o `.env`.

## Banco e migrations

Local: `src/main/resources/db/migration/`. Nome: `V<n>__<descricao>.sql` (dois underlines).

| Versão | Conteúdo |
|---|---|
| V1 | `clientes` (id, nome_cliente, cpf, telefone, email, dt_nascimento NOT NULL) |
| V2 | `imoveis` (id, tipo_imovel, endereco, cep, dormitorios, banheiros, suites, metragem, valor_aluguel_sug DECIMAL(12,2), obs VARCHAR(500)) |
| V3 | `locacao` (FKs `id_inquilino` → clientes e `id_imovel` → imoveis, ambas `ON DELETE RESTRICT`; `ativo BOOLEAN`, `data_fim` opcional, `perc_multa DECIMAL(5,2)`, `valor_aluguel DECIMAL(12,2) NOT NULL`) |
| V4 | `alugueis` (FK `id_locacao` → locacao `ON DELETE RESTRICT`); **não tem entidade**, o lab não exige |
| V5 | `UNIQUE` no `cpf` de `clientes` |
| V6 | `UNIQUE` no `email` de `clientes` (NULL pode repetir) |

Regras:
- Todas as PKs são `INT AUTO_INCREMENT`; todas as FKs são `INT`.
- **Nunca editar uma migration já aplicada.** Correções vão numa nova migration com `ALTER TABLE`.
- `spring.jpa.hibernate.ddl-auto=validate`: só o Flyway altera o banco; o Hibernate apenas confere se as entidades batem com as tabelas.

## Arquitetura

```
<pacote base>/            (começa com ifma.edu — confira o pacote real)
├── domain/
│   ├── model/            entidades JPA
│   ├── repository/       interfaces JpaRepository
│   ├── service/          regras de negócio
│   └── exception/        EntidadeNaoEncontradaException, RegraNegocioException
└── api/
    ├── controller/       controllers REST
    └── exceptionhandler/ ApiExceptionHandler, ErroResposta
```

O **Cliente** é o modelo de referência: siga o mesmo padrão nas outras entidades.

## Convenções

**Entidades**
- `@Table(name = "...")` com o nome exato da tabela; `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- Mapeamento de tipos: `INT` → `Integer` (nunca `Long`), `DECIMAL` → `BigDecimal`, `DATE` → `LocalDate`, `BOOLEAN` → `Boolean`, `VARCHAR` → `String`.
- Construtor sem argumentos; `equals`/`hashCode` apenas pelo `id`.
- Bean Validation direto na entidade (estilo da v3 do professor). Todo limite de tamanho do banco deve ter uma anotação correspondente (`@Size`, `@Pattern` ou `@Digits`), para gerar 400 em vez de 500.
- CPF, telefone e CEP são guardados **só com dígitos**, sem máscara, validados com `@Pattern`.
- Campos opcionais usam anotações que ignoram nulo (`@Pattern`, `@Email`, `@Size`, `@Past`).

**Services**
- Injeção pelo construtor; `@Transactional` do pacote `org.springframework.transaction.annotation`.
- Leituras com `@Transactional(readOnly = true)`.
- `buscarPorId` lança `EntidadeNaoEncontradaException` (404).
- `salvar` faz `setId(null)` antes do `save`, para um POST nunca sobrescrever registro existente.
- Unicidade verificada com query methods: `existsByX` no cadastro e `existsByXAndIdNot` na atualização.
- `atualizar` busca o registro (404 se não existir), define o `id` e chama `save`.
- `excluir` chama `delete` seguido de `flush()`, para a violação de FK estourar dentro do método.
- Regras que envolvem mais de um campo lançam `RegraNegocioException` (409).

**Controllers**
- `ResponseEntity` em todos os endpoints; `@Valid` no `@RequestBody`.
- POST → 201 com header `Location`; GET/PUT → 200; DELETE → 204.

**Tratamento de erros** (`ApiExceptionHandler`, já pronto para todas as entidades)

| Exceção | Status |
|---|---|
| `MethodArgumentNotValidException` | 400, com lista de campos |
| `HttpMessageNotReadableException` | 400 (JSON malformado, data fora do padrão `AAAA-MM-DD`) |
| `EntidadeNaoEncontradaException` | 404 |
| `RegraNegocioException` | 409 |
| `DataIntegrityViolationException` | 409 (FK em uso, UNIQUE violado) |

## Status atual

- [x] Configuração, `.env` e migrations V1 a V6
- [x] Cliente completo e testado no Postman
- [x] Repositório publicado no GitHub
- [x] Decidido: atributos em `snake_case` (`nome_cliente`, `dt_nascimento`), iguais às colunas. Manter nas outras entidades.
- [x] Imóvel completo e testado no Postman (falta testar o 409 ao excluir imóvel em uso, depende da Locação)
- [ ] Locação
- [ ] Relatório final com todas as decisões justificadas

## Próximo passo: Imóvel

Criar `Imovel`, `ImovelRepository`, `ImovelService` e `ImovelController`. Pontos de atenção:
- `cep`: guardar só os 8 dígitos.
- `dormitorios`, `banheiros`, `suites`, `metragem`: decidir entre `@Positive` e `@PositiveOrZero`.
- `valor_aluguel_sug`: opcional, não negativo, com `@Digits` compatível com `DECIMAL(12,2)`.
- Regra de negócio no service: suítes não podem exceder dormitórios.
- Sem campo único, então o repository não precisa de query methods.

## Depois: Locação

- `inquilino` (`Cliente`) e `imovel` (`Imovel`) com `@ManyToOne` + `@JoinColumn(name = "id_inquilino")` / `@JoinColumn(name = "id_imovel")`.
- O service busca cliente e imóvel pelos IDs, reaproveitando os `buscarPorId` (404 automático).
- Regra: `dataFim`, quando informada, deve ser posterior a `dataInicio`.
- `diaVencimento` entre 1 e 31; `ativo` com valor padrão ao criar.
- Atenção à serialização JSON dos relacionamentos; se der problema, é o momento de avaliar DTOs.

## Testes

Coleção do Postman em `postman/`. Para recomeçar os testes do zero, no Workbench:

```sql
USE dbimobiliaria;
DELETE FROM clientes;
ALTER TABLE clientes AUTO_INCREMENT = 1;
```

(`TRUNCATE` falha por causa das FKs.) Quando houver locações, apague primeiro as tabelas dependentes: `alugueis`, depois `locacao`, depois `clientes` e `imoveis`.

Nos JSONs, use os nomes dos **atributos Java**, não os das colunas. Datas no formato `AAAA-MM-DD`.

## Git

- Antes de cada commit, confirmar que o `.env` não aparece nas mudanças.
- Commits pequenos, um por etapa concluída (ex.: `feat: CRUD de imóveis`).