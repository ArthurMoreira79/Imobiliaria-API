package ifma.edu.br.imobiliariasystem.api.dto;

import jakarta.validation.constraints.NotNull;

//Referência a outro registro pelo id, no formato que a API já usava: "inquilino": {"id": 1}.
//Substitui os ValidationGroups: como este record só tem o id, validar ele inteiro
//é exatamente validar só o id (antes era preciso o @ConvertGroup para ignorar nome, cpf...).
public record ReferenciaInput(

        @NotNull(message = "O id é obrigatório")
        Integer id
) {}
