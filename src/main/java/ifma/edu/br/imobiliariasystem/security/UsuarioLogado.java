package ifma.edu.br.imobiliariasystem.security;

import ifma.edu.br.imobiliariasystem.domain.exception.AcessoNegadoException;
import ifma.edu.br.imobiliariasystem.domain.model.Cliente;
import ifma.edu.br.imobiliariasystem.domain.model.Usuario;
import ifma.edu.br.imobiliariasystem.domain.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

//Dá aos servicess acesso a quem está fazendo a requisição. Depois que o HTTP Basic autentica, o Spring guarda o usuário no SecurityContextHolder, que vale ssó para a requisição atual (cada requisição roda na sua própria thread).
@Component 
public class UsuarioLogado {

    private final UsuarioRepository usuarioRepository;

    public UsuarioLogado(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    //true quando o usuário tem SÓ o perfil CLIENTE.
    //Se alguém for CLIENTE e também ATENDENTE ou ADMIN, vale o perfil maior: vê tudo.
    public boolean isApenasCliente() {
        Set<String> perfis = perfis();
        return perfis.contains("ROLE_CLIENTE") && !perfis.contains("ROLE_ADMIN") && !perfis.contains("ROLE_ATENDENTE");
    }

    //Id do cliente vinculado ao login (usuarios.id_cliente).
    //Um usuário CLIENTE sem cadastro vinculado não tem locações próprias: 403.
    public Integer getIdCliente() {
        String login = autenticacao().getName();

        return usuarioRepository.findByLogin(login)
                .map(Usuario::getCliente)
                .map(Cliente::getId)
                .orElseThrow(() -> new AcessoNegadoException(
                        "O usuário '" + login + "' não está vinculado a um cadastro de cliente"));
    }

    private Set<String> perfis() {
        return autenticacao().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    private Authentication autenticacao() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    //Login de quem está fazendo a requisição
    public String getLogin() {
        return autenticacao().getName();
    }
}
