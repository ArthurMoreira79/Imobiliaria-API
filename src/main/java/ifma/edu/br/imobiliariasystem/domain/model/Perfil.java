package ifma.edu.br.imobiliariasystem.domain.model;

import jakarta.persistence.*;
import java.util.Objects;

//Perfil de acesso: ADMIN, ATENDENTE ou CLIENTE (sem o prefixo ROLE_, que é coisa do SPRING SECURITY)
@Entity 
@Table (name = "perfis")
public class Perfil {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 30, unique = true)
    private String nome;

    public Perfil() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Perfil outro)) return false;
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Perfil (id = " + id + ", nome = " + nome + ")";
    }
}
