package ifma.edu.br.imobiliariasystem.domain.repository;

import ifma.edu.br.imobiliariasystem.domain.model.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface AluguelRepository extends JpaRepository<Aluguel, Integer> {
    
}
