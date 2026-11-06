package ifma.edu.br.imobiliariasystem.api.mapper;

import ifma.edu.br.imobiliariasystem.api.dto.LocacaoInput;
import ifma.edu.br.imobiliariasystem.api.dto.LocacaoOutput;
import ifma.edu.br.imobiliariasystem.domain.model.Cliente;
import ifma.edu.br.imobiliariasystem.domain.model.Imovel;
import ifma.edu.br.imobiliariasystem.domain.model.Locacao;
import org.springframework.stereotype.Component;

@Component
public class LocacaoMapper {

    //Input -> entidade. Inquilino e imóvel saem daqui só com o id preenchido;
    //o service (carregarRelacionamentos) troca pelos registros completos do banco, com 404 se não existirem.
    public Locacao toEntity(LocacaoInput input) {
        Cliente inquilino = new Cliente();
        inquilino.setId(input.inquilino().id());

        Imovel imovel = new Imovel();
        imovel.setId(input.imovel().id());

        Locacao locacao = new Locacao();
        locacao.setInquilino(inquilino);
        locacao.setImovel(imovel);
        locacao.setAtivo(input.ativo());
        locacao.setData_inicio(input.data_inicio());
        locacao.setData_fim(input.data_fim());
        locacao.setDia_vencimento(input.dia_vencimento());
        locacao.setPerc_multa(input.perc_multa());
        locacao.setObs(input.obs());
        //valor_total fica null: quem calcula é o service
        return locacao;
    }

    public LocacaoOutput toOutput(Locacao locacao) {
        Cliente inquilino = locacao.getInquilino();
        Imovel imovel = locacao.getImovel();

        return new LocacaoOutput(
                locacao.getId(),
                new LocacaoOutput.InquilinoResumo(inquilino.getId(), inquilino.getNome_cliente()),
                new LocacaoOutput.ImovelResumo(imovel.getId(), imovel.getTipo_imovel(),
                        imovel.getEndereco(), imovel.getCidade(), imovel.getValor_diaria()),
                locacao.getAtivo(),
                locacao.getData_inicio(),
                locacao.getData_fim(),
                locacao.getDia_vencimento(),
                locacao.getPerc_multa(),
                locacao.getValor_total(),
                locacao.getObs()
        );
    }
}
