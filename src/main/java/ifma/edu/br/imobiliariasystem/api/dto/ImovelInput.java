package ifma.edu.br.imobiliariasystem.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

//Dados que o cliente da API pode enviar no POST e no PUT de imóvel. Sem id.
public record ImovelInput(

        @NotBlank(message = "O tipo do imóvel é obrigatório")
        @Size(max = 50, message = "O tipo do imóvel deve ter no máximo 50 caracteres")
        String tipo_imovel,

        @NotBlank(message = "O endereço é obrigatório")
        @Size(max = 255, message = "O endereço do imóvel deve ter no máximo 255 caracteres")
        String endereco,

        @NotBlank(message = "O CEP é obrigatório")
        @Pattern(regexp = "^\\d{8}$", message = "O CEP deve conter exatamente 8 dígitos, sem traço")
        String cep,

        @NotBlank(message = "A cidade é obrigatória")
        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
        String cidade,

        @NotNull(message = "O número de dormitórios é obrigatório")
        @PositiveOrZero(message = "O número de dormitórios não pode ser negativo")
        Integer dormitorios,

        @NotNull(message = "O número de banheiros é obrigatório")
        @PositiveOrZero(message = "O número de banheiros não pode ser negativo")
        Integer banheiros,

        @NotNull(message = "O número de suítes é obrigatório")
        @PositiveOrZero(message = "O número de suítes não pode ser negativo")
        Integer suites,

        @NotNull(message = "A metragem é obrigatória")
        @Positive(message = "A metragem deve ser maior que zero")
        Integer metragem,

        @NotNull(message = "O valor da diária é obrigatório")
        @Positive(message = "O valor da diária deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "A diária deve ter no máximo 10 dígitos inteiros e 2 casas decimais")
        BigDecimal valor_diaria,

        @Positive(message = "O valor sugerido do aluguel deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "O valor deve ter no máximo 10 dígitos inteiros e 2 casas decimais")
        BigDecimal valor_aluguel_sug,

        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
        String obs
) {}