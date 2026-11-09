package ifma.edu.br.imobiliariasystem.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Set;

//Atualização de usuário (PUT /usuarios/{id}, só ADMIN).
//Igual ao UsuarioInput, mas a senha é opcional: se não vier, o hash atual é mantido.
public record UsuarioAtualizacaoInput(

        @NotBlank(message = "O login é obrigatório")
        @Size(max = 50, message = "O login deve ter no máximo 50 caracteres")
        String login,

        //@Size ignora null: sem senha passa; com senha, precisa ter 6 a 72 caracteres
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres")
        String senha,

        @NotEmpty(message = "Informe ao menos um perfil")
        Set<@NotBlank(message = "O nome do perfil não pode ser vazio") String> perfis,

        @Valid
        ReferenciaInput cliente
) {}