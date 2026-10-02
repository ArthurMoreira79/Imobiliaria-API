package ifma.edu.br.imobiliariasystem.domain.service;

import ifma.edu.br.imobiliariasystem.domain.exception.*;
import ifma.edu.br.imobiliariasystem.domain.model.Imovel;
import ifma.edu.br.imobiliariasystem.domain.repository.ImovelRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service 
public class ImovelService {
    
    private final ImovelRepository imovelRepository;

    public ImovelService(ImovelRepository imovelRepository) {
        this.imovelRepository = imovelRepository;
    }

    @Transactional(readOnly = true)
    public List<Imovel> listarTodos(){
        return imovelRepository.findAll();
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
        imovelRepository.delete(imovel);

        //Força o DELETE a ir ao banco agora. Se o imóvel estiver em alguma locação, o ON DELETE RESTRICT falha aqui e o handler devolve 409.
        imovelRepository.flush();
    }

    //Regras que comparam mais de um campo: o Bean Validation valida campo a campo, então esse tipo de regra fica no service.
    private void validarRegras(Imovel imovel) {
        if (imovel.getSuites() > imovel.getDormitorios()) {
            throw new RegraNegocioException("O número de suítes (" + imovel.getSuites() + ") não pode ser maior que o número de dormitórios (" + imovel.getDormitorios() + ").");
        }
    }
}
