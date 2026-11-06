package ifma.edu.br.imobiliariasystem.api.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

//Dados que o cliente da API PODE enviar no POST e no PUT.
//Não tem id: quem define o id é o banco (POST) ou a URL (PUT).
public record ClienteInput(

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
    String nome_cliente,

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "O CPF deve conter exatamente 11 dígitos, sem pontos ou traço")
    String cpf,

    @Pattern(regexp = "^\\d{10,11}$", message = "O telefone deve conter DDD + número (10 ou 11 dígitos), sem máscara")
    String telefone,

    @Email(message = "E-mail inválido")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
    String email,

    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve ser uma data passada.")
    LocalDate dt_nascimento

) {} 