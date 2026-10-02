package ifma.edu.br.imobiliariasystem.domain.service;

import ifma.edu.br.imobiliariasystem.domain.exception.*;
import ifma.edu.br.imobiliariasystem.domain.model.Locacao;
import ifma.edu.br.imobiliariasystem.domain.repository.LocacaoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocacaoService {

    private final LocacaoRepository locacaoRepository;
    private final ClienteService clienteService;
    private final ImovelService imovelService;

    public LocacaoService(LocacaoRepository locacaoRepository, ClienteService clienteService, ImovelService imovelService) {
        this.locacaoRepository = locacaoRepository;
        this.clienteService = clienteService;
        this.imovelService = imovelService;
    }

    @Transactional(readOnly = true)
    public List<Locacao> listarTodos() {
        return locacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Locacao buscarPorId(Integer id) {
        return locacaoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Locação de id " + id + " não encontrada"));
    }

    @Transactional
    public Locacao salvar(Locacao locacao) {
        //garante INSERT: se o JSON trouxer um id, o save faria UPDATE em outra locação
        locacao.setId(null);

        //toda locação nasce ativa, a menos que o JSON diga o contrário
        if (locacao.getAtivo() == null) {
            locacao.setAtivo(true);
        }

        carregarRelacionamentos(locacao);
        validarRegras(locacao);

        if (locacao.getAtivo() && locacaoRepository.existsByImovel_IdAndAtivoTrue(locacao.getImovel().getId())) {
            throw new RegraNegocioException("O imóvel de id " + locacao.getImovel().getId() + " já possui uma locação ativa");
        }

        return locacaoRepository.save(locacao);
    }

    @Transactional
    public Locacao atualizar(Integer id, Locacao dadosAtualizados) {
        //lança 404 se a locação não existir
        Locacao atual = buscarPorId(id);

        //ativo é NOT NULL no banco: se o JSON não trouxer, mantém o valor atual
        if (dadosAtualizados.getAtivo() == null) {
            dadosAtualizados.setAtivo(atual.getAtivo());
        }

        carregarRelacionamentos(dadosAtualizados);
        validarRegras(dadosAtualizados);

        //Ignora a própria locação na verificação de imóvel já alugado
        if (dadosAtualizados.getAtivo()
                && locacaoRepository.existsByImovel_IdAndAtivoTrueAndIdNot(dadosAtualizados.getImovel().getId(), id)) {
            throw new RegraNegocioException("O imóvel de id " + dadosAtualizados.getImovel().getId() + " já possui outra locação ativa");
        }

        //Com o id definido, o save faz UPDATE do registro certo
        dadosAtualizados.setId(id);
        return locacaoRepository.save(dadosAtualizados);
    }

    @Transactional
    public void excluir(Integer id) {
        Locacao locacao = buscarPorId(id);
        locacaoRepository.delete(locacao);

        //Força o DELETE a ir ao banco agora. Se a locação tiver aluguéis,
        //o ON DELETE RESTRICT falha aqui e o handler devolve 409
        locacaoRepository.flush();
    }

    //O JSON traz só os ids ("inquilino": {"id": 1}). Aqui troca esses objetos
    //incompletos pelos registros completos do banco, reaproveitando os buscarPorId
    //(404 se o cliente ou o imóvel não existir)
    private void carregarRelacionamentos(Locacao locacao) {
        locacao.setInquilino(clienteService.buscarPorId(locacao.getInquilino().getId()));
        locacao.setImovel(imovelService.buscarPorId(locacao.getImovel().getId()));
    }

    //Regras que comparam mais de um campo
    private void validarRegras(Locacao locacao) {
        if (locacao.getData_fim() != null && !locacao.getData_fim().isAfter(locacao.getData_inicio())) {
            throw new RegraNegocioException("A data de fim (" + locacao.getData_fim()
                    + ") deve ser posterior à data de início (" + locacao.getData_inicio() + ")");
        }
    }
}
