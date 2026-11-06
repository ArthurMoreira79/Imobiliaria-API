package ifma.edu.br.imobiliariasystem.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

//Dados que a API devolve sobre uma locação.
//Inquilino e imóvel vêm resumidos: quem quiser o cadastro completo consulta /clientes/{id}
//ou /imoveis/{id}. Isso evita expor CPF, telefone e e-mail do inquilino em toda locação.
public record LocacaoOutput(
        Integer id,
        InquilinoResumo inquilino,
        ImovelResumo imovel,
        Boolean ativo,
        LocalDate data_inicio,
        LocalDate data_fim,
        Integer dia_vencimento,
        BigDecimal perc_multa,
        BigDecimal valor_total,
        String obs
) {

    //Records aninhados: só fazem sentido dentro da resposta da locação
    public record InquilinoResumo(Integer id, String nome_cliente) {}

    public record ImovelResumo(Integer id, String tipo_imovel, String endereco, String cidade,
                               BigDecimal valor_diaria) {}
}
