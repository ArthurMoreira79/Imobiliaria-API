package ifma.edu.br.imobiliariasystem.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

//Dados que o cliente da API pode enviar no POST e no PUT de locação.
//Ficam de fora: id (banco/URL) e valor_total (calculado pelo service).
//Antes o valor_total precisava do @JsonProperty(READ_ONLY) na entidade; agora ele
//simplesmente não existe na entrada, então um "valor_total" enviado é ignorado.
public record LocacaoInput(

        //@Valid faz o Bean Validation entrar no record e validar o id dele
        @Valid
        @NotNull(message = "O inquilino é obrigatório")
        ReferenciaInput inquilino,

        @Valid
        @NotNull(message = "O imóvel é obrigatório")
        ReferenciaInput imovel,

        //Opcional: se não vier, o service define true (confirmada) no POST
        //e mantém o valor atual no PUT
        Boolean ativo,

        @NotNull(message = "A data de início é obrigatória")
        LocalDate data_inicio,

        //Obrigatória: sem ela não dá para calcular a quantidade de diárias
        @NotNull(message = "A data final é obrigatória")
        LocalDate data_fim,

        @NotNull(message = "O dia de vencimento é obrigatório")
        @Min(value = 1, message = "O dia de vencimento deve estar entre 1 e 31")
        @Max(value = 31, message = "O dia de vencimento deve estar entre 1 e 31")
        Integer dia_vencimento,

        @PositiveOrZero(message = "O percentual de multa não pode ser negativo")
        @DecimalMax(value = "100.00", message = "O percentual de multa não pode passar de 100%")
        @Digits(integer = 3, fraction = 2, message = "O percentual de multa deve ter no máximo 2 casas decimais")
        BigDecimal perc_multa,

        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
        String obs
) {}
