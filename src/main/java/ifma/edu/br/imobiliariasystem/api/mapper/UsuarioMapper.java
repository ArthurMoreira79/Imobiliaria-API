package ifma.edu.br.imobiliariasystem.api.mapper;

import ifma.edu.br.imobiliariasystem.api.dto.ReferenciaInput;
import ifma.edu.br.imobiliariasystem.api.dto.UsuarioAtualizacaoInput;
import ifma.edu.br.imobiliariasystem.api.dto.UsuarioInput;
import ifma.edu.br.imobiliariasystem.api.dto.UsuarioOutput;
import ifma.edu.br.imobiliariasystem.domain.model.Cliente;
import ifma.edu.br.imobiliariasystem.domain.model.Perfil;
import ifma.edu.br.imobiliariasystem.domain.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioInput input) {
        return montar(input.login(), input.senha(), input.perfis(), input.cliente());
    }

    public Usuario toEntity(UsuarioAtualizacaoInput input) {
        return montar(input.login(), input.senha(), input.perfis(), input.cliente());
    }

    //Perfis e cliente saem só com o nome/id; o service troca pelos registros do banco
    //(mesmo esquema do inquilino e do imóvel na locação)
    private Usuario montar(String login, String senha, Set<String> nomesPerfis, ReferenciaInput refCliente) {
        Usuario usuario = new Usuario();
        usuario.setLogin(login.trim());
        usuario.setSenha(senha);   //ainda em texto puro: o service gera o hash

        Set<Perfil> perfis = nomesPerfis.stream()
                .map(nome -> {
                    Perfil perfil = new Perfil();
                    perfil.setNome(nome.trim().toUpperCase());
                    return perfil;
                })
                .collect(Collectors.toSet());
        usuario.setPerfis(perfis);

        if (refCliente != null) {
            Cliente cliente = new Cliente();
            cliente.setId(refCliente.id());
            usuario.setCliente(cliente);
        }
        return usuario;
    }

    public UsuarioOutput toOutput(Usuario usuario) {
        UsuarioOutput.ClienteResumo cliente = null;
        if (usuario.getCliente() != null) {
            cliente = new UsuarioOutput.ClienteResumo(
                    usuario.getCliente().getId(), usuario.getCliente().getNome_cliente());
        }

        //Ordenados para a resposta sair sempre igual (Set não tem ordem)
        return new UsuarioOutput(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getPerfis().stream().map(Perfil::getNome).sorted().toList(),
                cliente
        );
    }
}