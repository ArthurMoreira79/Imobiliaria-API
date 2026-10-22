package ifma.edu.br.imobiliariasystem.domain.service;

import ifma.edu.br.imobiliariasystem.domain.exception.*;
import ifma.edu.br.imobiliariasystem.domain.model.Aluguel;
import ifma.edu.br.imobiliariasystem.domain.model.Locacao;
import ifma.edu.br.imobiliariasystem.domain.repository.AluguelRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final LocacaoService locacaoService;

    public AluguelService(AluguelRepository aluguelRepository, LocacaoService locacaoService) {
        this.aluguelRepository = aluguelRepository;
        this.locacaoService = locacaoService;
    }

    @Transactional(readOnly = true)
    public List<Aluguel> listarTodos() {
        return aluguelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Aluguel buscarPorId(Integer id) {
        return aluguelRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluguel de id " + id + " não encontrado"));
    }

    @Transactional
    public Aluguel salvar(Aluguel aluguel) {
        //garante INSERT: se o JSON trouxer um id, o save faria UPDATE em outro aluguel
        aluguel.setId(null);

        carregarLocacao(aluguel);

        //Cobrança nova só para locação confirmada. Na atualização a regra não vale:
        //uma parcela antiga de locação cancelada ainda pode ser quitada
        if (!aluguel.getLocacao().getAtivo()) {
            throw new RegraNegocioException("A locação de id " + aluguel.getLocacao().getId()
                    + " não está confirmada. Não é possível lançar aluguel para ela");
        }

        validarRegras(aluguel);

        return aluguelRepository.save(aluguel);
    }

    @Transactional
    public Aluguel atualizar(Integer id, Aluguel dadosAtualizados) {
        //lança 404 se o aluguel não existir
        buscarPorId(id);

        carregarLocacao(dadosAtualizados);
        validarRegras(dadosAtualizados);

        //Com o id definido, o save faz UPDATE do registro certo
        dadosAtualizados.setId(id);
        return aluguelRepository.save(dadosAtualizados);
    }

    @Transactional
    public void excluir(Integer id) {
        Aluguel aluguel = buscarPorId(id);
        aluguelRepository.delete(aluguel);
        aluguelRepository.flush();
    }

    //Troca o {"id": 1} do JSON pela locação completa (404 se não existir)
    private void carregarLocacao(Aluguel aluguel) {
        aluguel.setLocacao(locacaoService.buscarPorId(aluguel.getLocacao().getId()));
    }

    //O vencimento precisa cair dentro do período da locação
    private void validarRegras(Aluguel aluguel) {
        Locacao locacao = aluguel.getLocacao();

        if (aluguel.getDt_vencimento().isBefore(locacao.getData_inicio())
                || aluguel.getDt_vencimento().isAfter(locacao.getData_fim())) {
            throw new RegraNegocioException("O vencimento (" + aluguel.getDt_vencimento()
                    + ") deve estar dentro do período da locação (" + locacao.getData_inicio()
                    + " a " + locacao.getData_fim() + ")");
        }
    }
}