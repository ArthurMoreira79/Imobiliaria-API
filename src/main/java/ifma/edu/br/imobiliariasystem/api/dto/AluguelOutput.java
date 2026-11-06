package ifma.edu.br.imobiliariasystem.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

//Dados que a API devolve sobre um aluguel. A locação vem resumida (ids e período).
public record AluguelOutput(
        Integer id,
        LocacaoResumo locacao,
        LocalDate dt_vencimento,
        BigDecimal valor_pago,
        String obs
) {

    public record LocacaoResumo(Integer id, Integer id_inquilino, Integer id_imovel,
                                LocalDate data_inicio, LocalDate data_fim, Boolean ativo) {}
}
