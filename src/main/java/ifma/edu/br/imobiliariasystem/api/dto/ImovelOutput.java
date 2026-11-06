package ifma.edu.br.imobiliariasystem.api.dto;

import java.math.BigDecimal;

//Dados que a API devolve sobre um imóvel.
public record ImovelOutput(
        Integer id,
        String tipo_imovel,
        String endereco,
        String cep,
        String cidade,
        Integer dormitorios,
        Integer banheiros,
        Integer suites,
        Integer metragem,
        BigDecimal valor_diaria,
        BigDecimal valor_aluguel_sug,
        String obs
) {}