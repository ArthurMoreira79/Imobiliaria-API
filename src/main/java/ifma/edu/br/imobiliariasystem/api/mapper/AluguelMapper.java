package ifma.edu.br.imobiliariasystem.api.mapper;

import ifma.edu.br.imobiliariasystem.api.dto.AluguelInput;
import ifma.edu.br.imobiliariasystem.api.dto.AluguelOutput;
import ifma.edu.br.imobiliariasystem.domain.model.Aluguel;
import ifma.edu.br.imobiliariasystem.domain.model.Locacao;
import org.springframework.stereotype.Component;

@Component
public class AluguelMapper {

    //Input -> entidade. A locação sai só com o id; o service carrega a completa (404 se não existir).
    public Aluguel toEntity(AluguelInput input) {
        Locacao locacao = new Locacao();
        locacao.setId(input.locacao().id());

        Aluguel aluguel = new Aluguel();
        aluguel.setLocacao(locacao);
        aluguel.setDt_vencimento(input.dt_vencimento());
        aluguel.setValor_pago(input.valor_pago());
        aluguel.setObs(input.obs());
        return aluguel;
    }

    public AluguelOutput toOutput(Aluguel aluguel) {
        Locacao locacao = aluguel.getLocacao();

        return new AluguelOutput(
                aluguel.getId(),
                new AluguelOutput.LocacaoResumo(
                        locacao.getId(),
                        locacao.getInquilino().getId(),
                        locacao.getImovel().getId(),
                        locacao.getData_inicio(),
                        locacao.getData_fim(),
                        locacao.getAtivo()),
                aluguel.getDt_vencimento(),
                aluguel.getValor_pago(),
                aluguel.getObs()
        );
    }
}
