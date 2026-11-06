package ifma.edu.br.imobiliariasystem.domain.service;

import ifma.edu.br.imobiliariasystem.domain.exception.*;
import ifma.edu.br.imobiliariasystem.domain.model.Imovel;
import ifma.edu.br.imobiliariasystem.domain.repository.ImovelRepository;
import ifma.edu.br.imobiliariasystem.domain.repository.LocacaoRepository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

@Service 
public class ImovelService {
    
    private final ImovelRepository imovelRepository;
    private final LocacaoRepository locacaoRepository;

    public ImovelService(ImovelRepository imovelRepository, LocacaoRepository locacaoRepository) {
        this.imovelRepository = imovelRepository;
        this.locacaoRepository = locacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Imovel> listarTodos(){
        return imovelRepository.findAll();
    }

    //Paginação: o Pageable carrega página, tamanho e ordenação; o findAll(Pageable)
    //já vem do JpaRepository e faz o LIMIT/OFFSET e o COUNT no banco
    @Transactional(readOnly = true)
    public Page<Imovel> buscarPaginado(Pageable pageable) {
        return imovelRepository.findAll(pageable);
    }

    //Query by Example: recebe um imóvel "modelo" só com os filtros informados.
    //Campos nulos são ignorados, então cada filtro é opcional
    @Transactional(readOnly = true)
    public Page<Imovel> pesquisarPorExemplo(Imovel exemplo, Pageable pageable) {
        //Textos: busca parcial (contém) e sem diferenciar maiúsculas/minúsculas.
        //dormitorios: número, então a comparação é exata
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("tipo_imovel", contains().ignoreCase())
                .withMatcher("cidade", contains().ignoreCase());

        return imovelRepository.findAll(Example.of(exemplo, matcher), pageable);
    }

    @Transactional(readOnly = true)
    public Imovel buscarPorId(Integer id) {
        return imovelRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Imóvel de id " + id + " não encontrado"));
    }

    @Transactional 
    public Imovel salvar(Imovel imovel) {
        //Garante INSERT: se o JSON trouxer um id, o save faria UPDATE em outro imóvel. 
        imovel.setId(null);

        validarRegras(imovel);

        return imovelRepository.save(imovel);
    }

    @Transactional
    public Imovel atualizar(Integer id, Imovel dadosAtualizados) {
        //lança 404 se o imóvel não existir. 
        buscarPorId(id);

        validarRegras(dadosAtualizados);

        //Com o id definido, o save faz UPDATE do registro certo.
        dadosAtualizados.setId(id);
        return imovelRepository.save(dadosAtualizados);
    }

    @Transactional
    public void excluir(Integer id) {
        Imovel imovel = buscarPorId(id);

        //Regra: imóvel com locação confirmada em andamento ou futura não pode ser excluído
        long ativasOuFuturas = locacaoRepository.contarAtivasOuFuturas(id, LocalDate.now());
        if (ativasOuFuturas > 0) {
            throw new RegraNegocioException("O imóvel de id " + id + " possui " + ativasOuFuturas
                    + " locação(ões) confirmada(s) em andamento ou futura(s) e não pode ser excluído");
        }

        imovelRepository.delete(imovel);

        //Força o DELETE a ir ao banco agora. Se o imóvel ainda tiver locações encerradas ou canceladas,
        //o ON DELETE RESTRICT preserva esse histórico: falha aqui e o handler devolve 409.
        imovelRepository.flush();
    }

    //Regras que comparam mais de um campo: o Bean Validation valida campo a campo, então esse tipo de regra fica no service.
    private void validarRegras(Imovel imovel) {
        if (imovel.getSuites() > imovel.getDormitorios()) {
            throw new RegraNegocioException("O número de suítes (" + imovel.getSuites() + ") não pode ser maior que o número de dormitórios (" + imovel.getDormitorios() + ").");
        }
    }
}