package ifma.edu.br.imobiliariasystem.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

//Dados que o cliente da API pode enviar no POST e no PUT de aluguel (parcela). Sem id.
public record AluguelInput(

        //No JSON: "locacao": {"id": 1}
        @Valid
        @NotNull(message = "A locação é obrigatória")
        ReferenciaInput locacao,

        @NotNull(message = "A data de vencimento é obrigatória")
        LocalDate dt_vencimento,

        //Nulo significa que a parcela não foi paga
        @PositiveOrZero(message = "O valor pago não pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "O valor pago deve ter no máximo 10 dígitos inteiros e 2 casas decimais")
        BigDecimal valor_pago,

        @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
        String obs
) {}
