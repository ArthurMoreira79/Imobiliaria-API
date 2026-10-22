# imobiliariasystem

API REST de um fragmento de sistema de imobiliária, feita para os Labs 01 e 02 da disciplina Desenvolvimento Web II (Sistemas de Informação, DCOMP/IFMA).

## Tecnologias

Spring Boot 4, Spring Web, Spring Data JPA, Bean Validation, Flyway e MySQL.

## Como rodar

1. Crie o banco no MySQL: `CREATE DATABASE dbimobiliaria;`
2. Copie o `.env.example` para `.env` na raiz do projeto e preencha `DB_NAME`, `DB_USERNAME` e `DB_PASSWORD`. O `.env` não vai para o Git.
3. Rode a aplicação a partir da raiz do projeto (`./mvnw spring-boot:run`). O Flyway cria as tabelas e insere os dados iniciais (migrations V1 a V10).
4. A API sobe em `http://localhost:8080`.

A coleção do Postman está em `postman/collections/Imobiliaria`. A pasta **5 Roteiro Lab 02** tem os testes do Lab 02 em sequência, com verificações automáticas. Rode-a com o banco recém-criado.

## Endpoints

| Recurso | Endpoints |
|---|---|
| Clientes | `GET/POST /clientes`, `GET/PUT/DELETE /clientes/{id}` |
| Imóveis | `GET/POST /imoveis`, `GET/PUT/DELETE /imoveis/{id}` |
| | `GET /imoveis/paginado?page=0&size=5&sort=metragem,desc` |
| | `GET /imoveis/busca?tipo=&cidade=&dormitorios=` (Query by Example, também paginado) |
| Locações | `GET/POST /locacoes`, `GET/PUT/DELETE /locacoes/{id}` |
| | `PUT /locacoes/{id}/confirmar`, `PUT /locacoes/{id}/cancelar` |
| Aluguéis | `GET/POST /alugueis`, `GET/PUT/DELETE /alugueis/{id}` |

Nos JSONs, os campos usam os nomes dos atributos (`nome_cliente`, `data_inicio`...). Datas no formato `AAAA-MM-DD`. Relacionamentos são informados pelo id: `"inquilino": { "id": 1 }`.

## Regras de negócio

- **Cliente:** CPF e e-mail únicos.
- **Imóvel:** o número de suítes não pode passar do número de dormitórios.
- **Locação:**
  - `ativo = true` significa locação confirmada. Uma locação nasce confirmada, a menos que o JSON envie `"ativo": false` (pendente).
  - A data de fim é obrigatória e deve ser posterior à data de início.
  - O `valor_total` é calculado pelo sistema: `valor_diaria` do imóvel × quantidade de diárias. As diárias são os dias entre o início e o fim, e o dia de saída não é cobrado. O valor enviado no JSON é ignorado.
  - **Disponibilidade:** não pode haver duas locações confirmadas do mesmo imóvel com períodos sobrepostos. A verificação acontece ao cadastrar, ao atualizar e ao confirmar. Uma locação pode começar no dia em que a outra termina.
- **Exclusão de imóvel:**
  - Um imóvel com locação confirmada em andamento ou futura não pode ser excluído (regra de negócio, 409).
  - Um imóvel que só tem locações encerradas ou canceladas também não é excluído: a chave estrangeira (`ON DELETE RESTRICT`) preserva o histórico.
  - Só um imóvel que nunca foi locado pode ser excluído.
- **Aluguel:**
  - Uma cobrança nova só pode ser lançada para locação confirmada.
  - O vencimento deve estar dentro do período da locação.
  - Uma parcela existente pode ser atualizada (por exemplo, quitada) mesmo que a locação tenha sido cancelada.

## Respostas de erro

Todos os erros passam pelo `ApiExceptionHandler` e têm o mesmo formato (`status`, `titulo`, `mensagem`, `dataHora` e, na validação, `campos`).

| Situação | Status |
|---|---|
| Validação (Bean Validation), JSON malformado, parâmetro ou ordenação inválidos | 400 |
| Registro ou endpoint inexistente | 404 |
| Verbo HTTP não suportado | 405 |
| Regra de negócio ou restrição do banco (FK, UNIQUE) | 409 |
| Corpo que não é JSON | 415 |

## Limitação conhecida

Os atributos das entidades seguem os nomes das colunas (snake_case). O Spring Data interpreta `_` como navegação entre propriedades, então a ordenação da paginação não funciona para campos com underscore (`valor_diaria`, `tipo_imovel`). Ela funciona para `id`, `cidade`, `metragem`, `dormitorios`, `banheiros`, `suites`, `endereco` e `cep`.
