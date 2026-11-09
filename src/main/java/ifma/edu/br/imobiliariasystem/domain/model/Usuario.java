package ifma.edu.br.imobiliariasystem.domain.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50, unique = true)
    private String login;

    //Hash BCrypt. Nunca vai para nenhum DTO de saída.
    @Column(nullable = false, length = 100)
    private String senha;

    //Só para usuários com perfil CLIENTE: liga o login ao cadastro em clientes.
    //OneToOne porque id_cliente é UNIQUE (um cliente tem no máximo um usuário). optional = true: pode ser NULL.
    @OneToOne(optional = true)
    @JoinColumn(name = "id_cliente", unique = true)
    private Cliente cliente;

    //N:N pela tabela de junção usuarios_perfis.
    //EAGER: toda vez que o usuário é carregado (no login), os perfis vêm junto,
    //porque o Spring Security precisa deles para decidir o que ele pode acessar.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_perfis",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_perfil")
    )
    private Set<Perfil> perfis = new HashSet<>();

    public Usuario() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Set<Perfil> getPerfis() {
        return perfis;
    }

    public void setPerfis(Set<Perfil> perfis) {
        this.perfis = perfis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario outro)) return false;
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    //A senha fica de fora de propósito: toString costuma parar em logs
    @Override
    public String toString() {
        return "Usuario (id = " + id + ", login = " + login +
                ", cliente = " + (cliente != null ? cliente.getId() : null) + ")";
    }
}