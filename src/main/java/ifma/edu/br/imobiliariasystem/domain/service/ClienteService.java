package ifma.edu.br.imobiliariasystem.domain.service;

import ifma.edu.br.imobiliariasystem.domain.exception.*;
import ifma.edu.br.imobiliariasystem.domain.model.Cliente;
import ifma.edu.br.imobiliariasystem.domain.repository.ClienteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service 
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
 
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente de id " + id + "não encontrado"));
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        //garante INSERT: se o JSON trouxer um id, o save faria UPDATE em outro cliente
        cliente.setId(null);

        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new RegraNegocioException("Já existe um cliente cadastrado com este CPF");
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Integer id, Cliente dadosAtualizados) {
        //lança 404 se o cliente não existir
        buscarPorId(id);

        //Ignora o próprio cliente na verificação de CPF duplicado
        if (clienteRepository.existsByCpfAndIdNot(dadosAtualizados.getCpf(), id)) {
            throw new RegraNegocioException("Já existe outro cliente cadastrado com este CPF");
        }

        //Com o id definido, o save faz UPDATE do registro certo
        dadosAtualizados.setId(id);
        return clienteRepository.save(dadosAtualizados);
    }

    @Transactional 
    public void excluir(Integer id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);

        //Força o DELETE a ir ao banco agora. Se o cliente tiver locações,
        //o ON DELETE RESTRICT falha aqui e o handler devolve 409
        clienteRepository.flush();
    }
}
