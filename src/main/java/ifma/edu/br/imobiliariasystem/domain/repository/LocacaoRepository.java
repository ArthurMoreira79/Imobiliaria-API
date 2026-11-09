package ifma.edu.br.imobiliariasystem.domain.repository;

import ifma.edu.br.imobiliariasystem.domain.model.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LocacaoRepository extends JpaRepository<Locacao, Integer> {

    //Locações confirmadas do imóvel cujo período se sobrepõe a [inicio, fim).
    //Dois períodos se sobrepõem quando um começa antes do outro terminar:
    //  existente.inicio < novo.fim  E  existente.fim > novo.inicio
    //O dia de saída fica livre: uma locação pode começar no dia em que a outra termina.
    //idIgnorado: na atualização e na confirmação, a própria locação não conta como conflito.
    //@Query porque os atributos têm "_" (data_inicio), que o Spring Data leria como navegação
    @Query("""
            SELECT l FROM Locacao l
            WHERE l.imovel.id = :idImovel
              AND l.ativo = true
              AND (:idIgnorado IS NULL OR l.id <> :idIgnorado)
              AND l.data_inicio < :fim
              AND l.data_fim > :inicio
            """)
    List<Locacao> buscarConflitos(@Param("idImovel") Integer idImovel,
                                  @Param("inicio") LocalDate inicio,
                                  @Param("fim") LocalDate fim,
                                  @Param("idIgnorado") Integer idIgnorado);

    //Locações confirmadas do imóvel que ainda não terminaram (em andamento ou futuras).
    //Usado para impedir a exclusão do imóvel
    @Query("""
            SELECT COUNT(l) FROM Locacao l
            WHERE l.imovel.id = :idImovel
              AND l.ativo = true
              AND l.data_fim >= :hoje
            """)
    long contarAtivasOuFuturas(@Param("idImovel") Integer idImovel, @Param("hoje") LocalDate hoje);

    //Locações de um inquilino. Query method: "InquilinoId" vira l.inquilino.id; (funciona porque inquilino e id não têm "_")
    List<Locacao> findByInquilinoId(Integer idInquilino);
}