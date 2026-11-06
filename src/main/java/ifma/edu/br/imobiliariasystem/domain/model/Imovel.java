package ifma.edu.br.imobiliariasystem.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "imoveis")
public class Imovel {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo_imovel", nullable = false, length = 50)
    private String tipo_imovel;

    @Column(nullable = false, length = 255)
    private String endereco;

    @Column(nullable = false, length = 10)
    private String cep;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false)
    private Integer dormitorios;

    @Column(nullable = false)
    private Integer banheiros;

    @Column(nullable = false)
    private Integer suites;

    @Column(nullable = false)
    private Integer metragem;

    //Base do cálculo automático do valor da locação (valor_diaria x quantidade de diárias)
    @Column(name = "valor_diaria", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor_diaria;

    @Column(name = "valor_aluguel_sug", precision = 12, scale = 2)
    private BigDecimal valor_aluguel_sug;

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

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public BigDecimal getValor_diaria() {
        return valor_diaria;
    }

    public void setValor_diaria(BigDecimal valor_diaria) {
        this.valor_diaria = valor_diaria;
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
        ", cidade = " + cidade +
        ", dormitorios = " + dormitorios +
        ", banheiros = " + banheiros +
        ", suites = " + suites +
        ", metragem = " + metragem +
        ", valor da diária = " + valor_diaria +
        ", valor sugerido = " + valor_aluguel_sug + ")";
    }
}
