package ifma.edu.br.imobiliariasystem.api.mapper;

import ifma.edu.br.imobiliariasystem.api.dto.ClienteInput;
import ifma.edu.br.imobiliariasystem.api.dto.ClienteOutput;
import ifma.edu.br.imobiliariasystem.domain.model.Cliente;
import org.springframework.stereotype.Component;

//Conversão manual entre DTOs e a entidade.
//Fica na camada api porque DTO é assunto da API; o service só conhece a entidade.
@Component 
public class ClienteMapper {
    
    //Input -> entidade(POST e PUT). O id fica null; O service decide o id.
    public Cliente toEntity(ClienteInput input) {
        Cliente cliente = new Cliente();
        cliente.setNome_cliente(input.nome_cliente());
        cliente.setCpf(input.cpf());
        cliente.setTelefone(input.telefone());
        cliente.setEmail(input.email());
        cliente.setDt_nascimento(input.dt_nascimento());
        return cliente;
    }

    //entidade -> Output (todas as respostas)
    public ClienteOutput toOutput(Cliente cliente) {
        return new ClienteOutput(
                cliente.getId(),
                cliente.getNome_cliente(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getDt_nascimento()
        );
    }
}
