package ifma.edu.br.imobiliariasystem.api.mapper;

import ifma.edu.br.imobiliariasystem.api.dto.ImovelFiltro;
import ifma.edu.br.imobiliariasystem.api.dto.ImovelInput;
import ifma.edu.br.imobiliariasystem.api.dto.ImovelOutput;
import ifma.edu.br.imobiliariasystem.domain.model.Imovel;
import org.springframework.stereotype.Component;

@Component
public class ImovelMapper {

    //Input -> entidade (POST e PUT). id fica null.
    public Imovel toEntity(ImovelInput input) {
        Imovel imovel = new Imovel();
        imovel.setTipo_imovel(input.tipo_imovel());
        imovel.setEndereco(input.endereco());
        imovel.setCep(input.cep());
        imovel.setCidade(input.cidade());
        imovel.setDormitorios(input.dormitorios());
        imovel.setBanheiros(input.banheiros());
        imovel.setSuites(input.suites());
        imovel.setMetragem(input.metragem());
        imovel.setValor_diaria(input.valor_diaria());
        imovel.setValor_aluguel_sug(input.valor_aluguel_sug());
        imovel.setObs(input.obs());
        return imovel;
    }

    //Filtro -> entidade "modelo" do Query by Example.
    //Campos não informados ficam null e são ignorados pelo ExampleMatcher.
    public Imovel toExemplo(ImovelFiltro filtro) {
        Imovel exemplo = new Imovel();
        exemplo.setTipo_imovel(filtro.tipo());
        exemplo.setCidade(filtro.cidade());
        exemplo.setDormitorios(filtro.dormitorios());
        return exemplo;
    }

    //entidade -> Output
    public ImovelOutput toOutput(Imovel imovel) {
        return new ImovelOutput(
                imovel.getId(),
                imovel.getTipo_imovel(),
                imovel.getEndereco(),
                imovel.getCep(),
                imovel.getCidade(),
                imovel.getDormitorios(),
                imovel.getBanheiros(),
                imovel.getSuites(),
                imovel.getMetragem(),
                imovel.getValor_diaria(),
                imovel.getValor_aluguel_sug(),
                imovel.getObs()
        );
    }
}