package ifma.edu.br.imobiliariasystem.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Set;

//Cadastro de usuário (POST /usuarios, só ADMIN). Senha obrigatória.
public record UsuarioInput(

        @NotBlank(message = "O login é obrigatório")
        @Size(max = 50, message = "O login deve ter no máximo 50 caracteres")
        String login,

        //Texto puro só aqui, na entrada. O service transforma em hash antes de salvar.
        //Máximo 72: o BCrypt só considera os primeiros 72 bytes; o Spring recusa senhas maiores.
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres")
        String senha,

        //Nomes dos perfis: "ADMIN", "ATENDENTE", "CLIENTE"
        @NotEmpty(message = "Informe ao menos um perfil")
        Set<@NotBlank(message = "O nome do perfil não pode ser vazio") String> perfis,

        //Obrigatório só para quem tem o perfil CLIENTE. No JSON: "cliente": {"id": 1}
        @Valid
        ReferenciaInput cliente
) {}