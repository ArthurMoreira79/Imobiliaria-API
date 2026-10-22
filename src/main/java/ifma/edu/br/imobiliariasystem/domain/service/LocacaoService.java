package ifma.edu.br.imobiliariasystem.domain.service;

import ifma.edu.br.imobiliariasystem.domain.exception.*;
import ifma.edu.br.imobiliariasystem.domain.model.Locacao;
import ifma.edu.br.imobiliariasystem.domain.repository.LocacaoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
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

        //ativo = confirmada. Se o JSON não disser nada, a locação já nasce confirmada
        if (locacao.getAtivo() == null) {
            locacao.setAtivo(true);
        }

        carregarRelacionamentos(locacao);
        validarPeriodo(locacao);

        //Só locações confirmadas ocupam o imóvel
        if (locacao.getAtivo()) {
            verificarDisponibilidade(locacao, null);
        }

        calcularValorTotal(locacao);
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
        validarPeriodo(dadosAtualizados);

        //Ignora a própria locação na verificação de conflito
        if (dadosAtualizados.getAtivo()) {
            verificarDisponibilidade(dadosAtualizados, id);
        }

        //Datas ou imóvel podem ter mudado: recalcula com a diária atual do imóvel
        calcularValorTotal(dadosAtualizados);

        //Com o id definido, o save faz UPDATE do registro certo
        dadosAtualizados.setId(id);
        return locacaoRepository.save(dadosAtualizados);
    }

    //PUT /locacoes/{id}/confirmar: transforma uma locação pendente em confirmada
    @Transactional
    public Locacao confirmar(Integer id) {
        Locacao locacao = buscarPorId(id);

        if (locacao.getAtivo()) {
            throw new RegraNegocioException("A locação de id " + id + " já está confirmada");
        }

        verificarDisponibilidade(locacao, id);
        locacao.setAtivo(true);
        return locacaoRepository.save(locacao);
    }

    //PUT /locacoes/{id}/cancelar: libera o período do imóvel
    @Transactional
    public Locacao cancelar(Integer id) {
        Locacao locacao = buscarPorId(id);

        if (!locacao.getAtivo()) {
            throw new RegraNegocioException("A locação de id " + id + " já está cancelada");
        }

        locacao.setAtivo(false);
        return locacaoRepository.save(locacao);
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

    //A data final precisa ser posterior à inicial: senão a quantidade de diárias seria 0 ou negativa
    private void validarPeriodo(Locacao locacao) {
        if (!locacao.getData_fim().isAfter(locacao.getData_inicio())) {
            throw new RegraNegocioException("A data de fim (" + locacao.getData_fim()
                    + ") deve ser posterior à data de início (" + locacao.getData_inicio() + ")");
        }
    }

    //Rejeita a operação se outra locação confirmada ocupa o imóvel em parte do período
    private void verificarDisponibilidade(Locacao locacao, Integer idIgnorado) {
        List<Locacao> conflitos = locacaoRepository.buscarConflitos(
                locacao.getImovel().getId(), locacao.getData_inicio(), locacao.getData_fim(), idIgnorado);

        if (!conflitos.isEmpty()) {
            Locacao conflito = conflitos.get(0);
            throw new RegraNegocioException("O imóvel de id " + locacao.getImovel().getId()
                    + " não está disponível de " + locacao.getData_inicio() + " a " + locacao.getData_fim()
                    + ": já existe a locação confirmada de id " + conflito.getId()
                    + " de " + conflito.getData_inicio() + " a " + conflito.getData_fim());
        }
    }

    //valor_total = valor_diaria do imóvel x quantidade de diárias.
    //Diárias = dias entre início e fim (o dia de saída não é cobrado, como num hotel)
    private void calcularValorTotal(Locacao locacao) {
        long diarias = ChronoUnit.DAYS.between(locacao.getData_inicio(), locacao.getData_fim());

        BigDecimal total = locacao.getImovel().getValor_diaria()
                .multiply(BigDecimal.valueOf(diarias))
                .setScale(2, RoundingMode.HALF_EVEN);

        locacao.setValor_total(total);
    }
}