package ifma.edu.br.imobiliariasystem.domain.repository;

import ifma.edu.br.imobiliariasystem.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository 
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    //Usado no cadastro para ver se já existe cliente com esse cpf
    boolean existsByCpf(String cpf);

    //Usado na atualização para ver se existe OUTRO cliente (id diferente) com esse cpf
    boolean existsByCpfAndIdNot(String cpf, Integer id);
}
