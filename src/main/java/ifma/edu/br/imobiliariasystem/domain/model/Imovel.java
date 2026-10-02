package ifma.edu.br.imobiliariasystem.domain.model;

import ifma.edu.br.imobiliariasystem.domain.ValidationGroups;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "imoveis")
public class Imovel {

    //Só exigido quando o imóvel vem dentro de uma locação (grupo ImovelId)
    @NotNull(groups = ValidationGroups.ImovelId.class, message = "O id do imóvel é obrigatório")
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "O tipo do imóvel é obrigatório")
    @Size(max = 50, message = "O tipo do imóvel deve ter no máximo 50 caracteres")
    @Column(name = "tipo_imovel", nullable = false, length = 50)
    private String tipo_imovel;

    @NotBlank(message = "O endereço é obrigatório")
    @Size(max = 255, message = "O endereço do imóvel deve ter no máximo 255 caracteres")
    @Column(nullable = false, length = 255)
    private String endereco;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "^\\d{8}$", message = "O CEP deve conter exatamente 8 dígitos, sem traço")
    @Column(nullable = false, length = 10)
    private String cep;

    @NotNull(message = "O número de dormitórios é obrigatório")
    @PositiveOrZero(message = "O número de dormitórios não pode ser negativo")
    @Column(nullable = false)
    private Integer dormitorios;

    @NotNull(message = "O número de banheiros é obrigatório")
    @PositiveOrZero(message = "O número de banheiros não pode ser negativo")
    @Column(nullable = false)
    private Integer banheiros;

    @NotNull(message = "O número de suítes é obrigatório")
    @PositiveOrZero(message = "O número de suítes não pode ser negativo")
    @Column(nullable = false)
    private Integer suites;

    @NotNull(message = "A metragem é obrigatória")
    @Positive(message = "A metragem deve ser maior que zero")
    @Column(nullable = false)
    private Integer metragem;

    @Positive(message = "O valor sugerido do aluguel deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "O valor deve ter no máximo 10 dígitos inteiros e 2 casas decimais")
    @Column(name = "valor_aluguel_sug", precision = 12, scale = 2)
    private BigDecimal valor_aluguel_sug;

    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String obs;

    public Imovel() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo_imovel() {
        return tipo_imovel;
    }

    public void setTipo_imovel(String tipo_imovel) {
        this.tipo_imovel = tipo_imovel;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Integer getDormitorios() {
        return dormitorios;
    }

    public void setDormitorios(Integer dormitorios) {
        this.dormitorios = dormitorios;
    }

    public Integer getBanheiros() {
        return banheiros;
    }

    public void setBanheiros(Integer banheiros) {
        this.banheiros = banheiros;
    }

    public Integer getSuites() {
        return suites;
    }

    public void setSuites(Integer suites) {
        this.suites = suites;
    }

    public Integer getMetragem() {
        return metragem;
    }

    public void setMetragem(Integer metragem) {
        this.metragem = metragem;
    }

    public BigDecimal getValor_aluguel_sug() {
        return valor_aluguel_sug;
    }

    public void setValor_aluguel_sug(BigDecimal valor_aluguel_sug) {
        this.valor_aluguel_sug = valor_aluguel_sug;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    @Override 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (! (o instanceof Imovel outro)) return false;
        return id != null && Objects.equals(id, outro.id);
    }

    @Override 
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override 
    public String toString() {
        return "Imovel (id = " + id +
        ", tipo = " + tipo_imovel +
        ", endereco = " + endereco +
        ", cep = " + cep +
        ", dormitorios = " + dormitorios +
        ", banheiros = " + banheiros +
        ", suites = " + suites +
        ", metragem = " + metragem +
        ", valor sugerido = " + valor_aluguel_sug + ")";
    }
}
