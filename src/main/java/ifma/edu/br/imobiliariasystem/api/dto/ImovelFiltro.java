package ifma.edu.br.imobiliariasystem.api.dto;

//Filtros opcionais da busca GET /imoveis/busca?tipo=casa&cidade=luis&dormitorios=3.
//O Spring preenche o record com os parâmetros da URL que tiverem o mesmo nome.
public record ImovelFiltro(
        String tipo,
        String cidade,
        Integer dormitorios
) {}