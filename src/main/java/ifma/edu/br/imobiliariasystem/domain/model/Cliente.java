package ifma.edu.br.imobiliariasystem.domain.model;

import ifma.edu.br.imobiliariasystem.domain.ValidationGroups;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "clientes")
public class Cliente {

    //Só exigido quando o cliente vem dentro de uma locação (grupo ClienteId)
    @NotNull(groups = ValidationGroups.ClienteId.class, message = "O id do inquilino é obrigatório")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
    @Column(name = "nome_cliente", nullable = false, length = 255)
    private String nome_cliente;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "O CPF deve conter exatamente 11 dígitos, sem pontos ou traço")
    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @Pattern(regexp = "^\\d{10,11}$", message = "O telefone deve conter DDD + número (10 ou 11 dígitos), sem máscara")
    @Column(length = 12)
    private String telefone;

    @Email(message = "E-mail inválido")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
    @Column(length = 100, unique = true)
    private String email;

    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve ser uma data passada.")
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
