package ifma.edu.br.imobiliariasystem.api.dto;

import java.time.LocalDate;

//Dados que a API DEVOLVE. Sem validação: quem monta este objeto é o próprio sistema.
public record ClienteOutput (
    Integer id,
    String nome_cliente,
    String cpf,
    String telefone,
    String email,
    LocalDate dt_nascimento
) {}
