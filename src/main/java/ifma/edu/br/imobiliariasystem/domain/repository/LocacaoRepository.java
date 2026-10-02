package ifma.edu.br.imobiliariasystem.domain.repository;

import ifma.edu.br.imobiliariasystem.domain.model.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocacaoRepository extends JpaRepository<Locacao, Integer> {

    //Imovel_Id: o "_" navega do atributo imovel para o id dele.
    //Usado no cadastro para ver se o imóvel já tem uma locação ativa
    boolean existsByImovel_IdAndAtivoTrue(Integer idImovel);

    //Usado na atualização: ignora a própria locação
    boolean existsByImovel_IdAndAtivoTrueAndIdNot(Integer idImovel, Integer id);
}
