package ifma.edu.br.imobiliariasystem.security;

import ifma.edu.br.imobiliariasystem.domain.model.Perfil;
import ifma.edu.br.imobiliariasystem.domain.model.Usuario;
import ifma.edu.br.imobiliariasystem.domain.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Ponte entre o banco e o Spring Security
//A cada requisição com HTTP Basic, o Spring chama loadUserByUsername com o login recebido, e depois compara a senha enviada com o hash devolvido aqui (usando o PasswordEncoder).
@Service 
public class UsuarioDetailsService implements UserDetailsService{
    
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override 
    @Transactional (readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não Encontrado: " + login));

        //"ADMIN" vira a autoridade "ROLE_ADMIN": o roles() acrescenta o prefixo sozinho
        String[] perfis = usuario.getPerfis().stream()
                    .map(Perfil::getNome)
                    .toArray(String[]::new);

        return User.withUsername(usuario.getLogin())
                .password(usuario.getSenha())
                .roles(perfis)
                .build();
    }
}
