package ifma.edu.br.imobiliariasystem.domain.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_cliente", nullable = false, length = 255)
    private String nome_cliente;

    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @Column(length = 12)
    private String telefone;

    @Column(length = 100, unique = true)
    private String email;

    @Column(name = "dt_nascimento", nullable = false)
    private LocalDate dt_nascimento;

    public Cliente() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome_cliente() {
        return nome_cliente;
    }

    public void setNome_cliente(String nome_cliente) {
        this.nome_cliente = nome_cliente;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDt_nascimento() {
        return dt_nascimento;
    }

    public void setDt_nascimento(LocalDate dt_nascimento) {
        this.dt_nascimento = dt_nascimento;
    }

    @Override 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente outro)) return false;
        return id != null && Objects.equals(id, outro.id);
    }

    @Override 
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override 
    public String toString() {
        return "Cliente (id = " + id + 
        ", nome = " + nome_cliente + 
        ", cpf = " + cpf + 
        ", telefone = " + telefone +
        ", email = " + email +
        ", Data de Nascimento = " + dt_nascimento + ")";
    }
}
