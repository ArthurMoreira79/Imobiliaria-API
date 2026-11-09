package ifma.edu.br.imobiliariasystem.domain.repository;

import ifma.edu.br.imobiliariasystem.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    //login não tem "_", então o query method funciona normalmente
    Optional<Usuario> findByLogin(String login);

    //Unicidade do login (cadastro e atualização)
    boolean existsByLogin(String login);

    boolean existsByLoginAndIdNot(String login, Integer id);

    //Um cliente tem no máximo um usuário. "ClienteId" vira u.cliente.id
    boolean existsByClienteId(Integer idCliente);

    boolean existsByClienteIdAndIdNot(Integer idCliente, Integer id);
}